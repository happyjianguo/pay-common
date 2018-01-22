package com.dream.pay.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption Http调用工具<br />
 * 调用失败抛RuntimeException<br />
 * 开启重试：HttpUtil.enableRetry(true)，读接口或幂等接口可以开启，否则需要谨慎使用
 */
public class HttpUtil {

    private static final Log log = LogFactory.getLog(HttpUtil.class);
    private static final Properties properties = System.getProperties();
    private static final String HTTP_TIMEOUT_PROPERTY = "system.http.timeout";
    private static final String HTTP_LOG_LENGTH_PROPERTY = "system.http.log.length";
    private static final int HTTP_TIMEOUT_DEFAULT = 5000; // 默认超时时间：毫秒
    private static final int LOG_LENGTH_DEFAULT = 500; // 默认LOG字符长度
    private static final String CHARSET_DEFAULT = "UTF-8"; // 默认字符集
    private static final String HTTP_RETRY_COUNT_PROPERTY = "system.http.retry.count"; // http重试次数
    private static final int HTTP_RETRY_COUNT_DEFAULT = 3; // 默认重试3次
    // 默认记录的返回结果长度，debug级别完整记录
    private static final int LOG_LENGTH = NumberUtils.toInt(properties.getProperty(HTTP_LOG_LENGTH_PROPERTY),
            LOG_LENGTH_DEFAULT);
    // 默认重试次数
    private static final int HTTP_RETRY_COUNT = NumberUtils.toInt(properties.getProperty(HTTP_RETRY_COUNT_PROPERTY),
            HTTP_RETRY_COUNT_DEFAULT);
    // 默认字符集：UTF-8
    private static final Charset CHARSET = Charset.forName(CHARSET_DEFAULT);
    private static final CloseableHttpClient httpClient;
    private static final CloseableHttpClient httpClientRetry;
    // 默认超时时间：毫秒
    private static int HTTP_TIMEOUT = NumberUtils.toInt(properties.getProperty(HTTP_TIMEOUT_PROPERTY),
            HTTP_TIMEOUT_DEFAULT); // 毫秒
    // 是否开启重试
    private static ThreadLocal<Boolean> httpRetry = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    static {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT)
                .setSocketTimeout(HTTP_TIMEOUT).build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

        // 异常处理逻辑
        // 1.httpcomponets.
        // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e279
        // 2.common httpclient
        // http://hc.apache.org/httpclient-3.x/exception-handling.html
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {

                boolean toRetry = false;

                // connection refuse
                // 服务器端丢掉请求
                // 指定时间与服务器连接超时
                // 这几种异常，开启重试
                if (e instanceof ConnectException || e instanceof NoHttpResponseException
                        || e instanceof ConnectTimeoutException) {
                    toRetry = true;
                }

                if (toRetry) {
                    // 超过最大重试次数
                    if (executionCount >= HTTP_RETRY_COUNT) {
                        log.error(new StringBuilder().append("HTTP调用异常[").append(e.getClass().getSimpleName())
                                .append("], 但重试次数已达最大次数[").append(executionCount).append("], 不再处理.").toString());
                        return false;
                    } else {
                        log.warn(new StringBuilder().append("HTTP调用异常[").append(e.getClass().getSimpleName())
                                .append("], 发起第[").append(executionCount).append("]次重试.").toString());
                    }
                } else {
                    HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
                    if (!clientContext.isRequestSent()) {
                        log.warn(new StringBuilder().append("HTTP调用异常[").append(e.getClass().getSimpleName())
                                .append("], 但请求未发送, 发起第[").append(executionCount).append("]次重试.").toString());
                        toRetry = true;
                    } else {
                        log.warn(new StringBuilder().append("HTTP调用异常[").append(e.getClass().getSimpleName())
                                .append("], 非可重试异常.").toString());
                    }
                }

                return toRetry;
            }
        };

        httpClientRetry = HttpClients.custom().setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler)
                .build();
    }

    /**
     * 是否开启http调用重试
     * <p>
     * 本设置后对本线程的所有HttpUtil调用都有效
     *
     * @param retry
     */
    public static void enableRetry(boolean retry) {
        httpRetry.set(retry);
    }

    /**
     * 发起HTTP GET请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认字符集:UTF-8, 默认超时时间:5000ms
     *
     * @param url url地址
     * @return
     */
    public static String get(String url) {
        return get(url, null, null, 0);
    }

    /**
     * 发起HTTP GET请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认超时时间:5000ms
     *
     * @param url     url地址
     * @param charset 字符集
     * @return
     */
    public static String get(String url, Charset charset) {
        return get(url, null, charset, 0);
    }

    /**
     * 发起HTTP GET请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认字符集:UTF-8
     *
     * @param url     url地址
     * @param timeout 超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String get(String url, int timeout) {
        return get(url, null, null, timeout);
    }

    /**
     * 发起HTTP GET请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认字符集:UTF-8, 默认超时时间:5000ms
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static String get(String url, Map<String, String> paramMap) {
        return get(url, paramMap, null, 0);
    }

    /**
     * 发起HTTP GET请求<br />
     * 调用失败抛RuntimeException，不会抛任何异常
     *
     * @param url      url地址
     * @param paramMap 参数
     * @param charset  字符集
     * @param timeout  超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String get(String url, Map<String, String> paramMap, Charset charset, int timeout) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("HttpUtil.get failure: url is missing");
        }

        if (charset == null) {
            charset = CHARSET;
        }

        List<BasicNameValuePair> nameValuePair = getNameValuePairList(paramMap);
        if (nameValuePair.size() > 0) {
            url += "?" + URLEncodedUtils.format(nameValuePair, charset);
        }

        log.info("GET " + url);

        HttpGet httpGet = new HttpGet(url);
        if (timeout > 0) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .build();

            httpGet.setConfig(requestConfig);
        }

        return executeHttpClient(httpGet, charset);
    }

    /**
     * 发起HTTP POST请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认字符集:UTF-8, 默认超时时间:5000ms
     *
     * @param url      url地址
     * @param paramMap 参数
     * @return
     */
    public static String post(String url, Map<String, String> paramMap) {
        return post(url, paramMap, null, 0);
    }

    /**
     * 发起HTTP POST请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认字符集:UTF-8
     *
     * @param url      url地址
     * @param paramMap 参数
     * @param timeout  超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String post(String url, Map<String, String> paramMap, int timeout) {
        return post(url, paramMap, null, timeout);
    }

    /**
     * 发起HTTP POST请求<br />
     * 调用失败抛RuntimeException<br />
     * 默认超时时间:5000ms
     *
     * @param url      url地址
     * @param paramMap 参数
     * @param charset  字符集
     * @return
     */
    public static String post(String url, Map<String, String> paramMap, Charset charset) {
        return post(url, paramMap, charset, 0);
    }

    /**
     * 发起HTTP POST请求<br />
     * 调用失败抛RuntimeException<br />
     *
     * @param url      url地址
     * @param paramMap 参数
     * @param charset  字符集
     * @param timeout  超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String post(String url, Map<String, String> paramMap, Charset charset, int timeout) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("HttpUtil.post failure: url is missing");
        }

        if (charset == null) {
            charset = CHARSET;
        }

        HttpPost httpPost = createHttpPost(url, timeout);
        List<BasicNameValuePair> nameValuePair = getNameValuePairList(paramMap);

        if (nameValuePair.size() > 0) {
            log.info("POST " + url + ", data: " + URLEncodedUtils.format(nameValuePair, charset));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, charset));
        }

        return executeHttpClient(httpPost, charset);
    }

    /**
     * 发起HTTP POST请求<br/>
     * ContentType: application/json<br/>
     * 默认字符集:UTF-8, 默认超时时间:5000ms
     *
     * @param url        url地址
     * @param jsonString json字符串
     * @return
     */
    public static String postJson(String url, String jsonString) {
        return postJson(url, jsonString, null, 0);
    }

    /**
     * 发起HTTP POST请求<br />
     * ContentType: application/json
     *
     * @param url        url地址
     * @param jsonString json字符串
     * @param charset    字符集
     * @param timeout    超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String postJson(String url, String jsonString, Charset charset, int timeout) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        if (charset == null) {
            charset = CHARSET;
        }

        HttpPost httpPost = createHttpPost(url, timeout);
        StringEntity entity = new StringEntity(jsonString, ContentType.create("application/json", charset));

        log.info("POST " + url + ", data: " + jsonString);

        httpPost.setEntity(entity);

        return executeHttpClient(httpPost, charset);
    }

    /**
     * 发起HTTP POST请求<br/>
     * ContentType: application/xml<br/>
     * 默认字符集:UTF-8, 默认超时时间:5000ms
     *
     * @param url       url地址
     * @param xmlString xml字符串
     * @return
     */
    public static String postXml(String url, String xmlString) {
        return postXml(url, xmlString, null, 0);
    }

    /**
     * 发起HTTP POST请求<br />
     * ContentType: application/xml
     *
     * @param url       url地址
     * @param xmlString xml字符串
     * @param charset   字符集
     * @param timeout   超时时间,单位ms，小于等于0则默认5000ms
     * @return
     */
    public static String postXml(String url, String xmlString, Charset charset, int timeout) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        if (charset == null) {
            charset = CHARSET;
        }

        HttpPost httpPost = createHttpPost(url, timeout);
        StringEntity entity = new StringEntity(xmlString, ContentType.create("application/xml", charset));

        log.info("POST " + url + ", data: " + xmlString);

        httpPost.setEntity(entity);

        return executeHttpClient(httpPost, charset);
    }

    private static HttpPost createHttpPost(String url, int timeout) {
        HttpPost httpPost = new HttpPost(url);
        if (timeout > 0) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .build();

            httpPost.setConfig(requestConfig);
        }

        return httpPost;
    }

    private static List<BasicNameValuePair> getNameValuePairList(Map<String, String> paramMap) {

        if (MapUtils.isEmpty(paramMap)) {
            return new ArrayList<BasicNameValuePair>(0);
        }

        List<BasicNameValuePair> nameValuePair = new ArrayList<BasicNameValuePair>(paramMap.size());
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                nameValuePair.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }

        return nameValuePair;
    }

    /**
     * 执行http请求操作<br/>
     * 计时、写log
     *
     * @param request
     * @param charset
     * @return
     */
    private static String executeHttpClient(HttpRequestBase request, Charset charset) {

        try {
            StopWatch clock = new StopWatch();
            clock.start();
            CloseableHttpResponse response = httpRetry.get() ? httpClientRetry.execute(request)
                    : httpClient.execute(request);
            clock.stop();

            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();

            String result = "";
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }

            EntityUtils.consume(entity);
            response.close();

            StringBuilder logString = new StringBuilder();
            logString.append(request.getMethod());

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                logString.append(" ok, elapsed[").append(clock.getTime()).append("]ms, result: ");

                // 如果调用结果长度在LOG_LENGTH，则全部记录
                if (result.length() < LOG_LENGTH) {
                    log.info(logString.append(result));
                } else {
                    // 如果是debug级别，则记录完整调用结果
                    if (log.isDebugEnabled()) {
                        log.debug(logString.append(result));
                    } else { // 否则只记录长度在LOG_LENGTH之内的调用结果
                        logString.append(result.substring(0, LOG_LENGTH)).append("...");
                        log.info(logString);
                    }
                }
            } else {
                request.abort();
                logString.append(" failure, elapsed[").append(clock.getTime()).append("]ms, status: ")
                        .append(statusLine);

                log.error(logString);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(request.getMethod() + "[" + request.getURI() + "] failure", e);
        }
    }
}
