package com.dream.pay.nsq;

import com.dream.pay.nsq.config.NsqConfig;
import com.dream.pay.nsq.decode.DefaultNsqMessageDecoder;
import com.dream.pay.nsq.decode.NsqMessageDecoder;
import com.dream.pay.nsq.retry.CirculateRetryPolicy;
import com.dream.pay.nsq.retry.NsqRequeueException;
import com.dream.pay.nsq.retry.NsqRetryHandler;
import com.dream.pay.utils.TracerContextUtils;
import com.youzan.nsq.client.Consumer;
import com.youzan.nsq.client.ConsumerImplV2;
import com.youzan.nsq.client.entity.NSQConfig;
import com.youzan.nsq.client.entity.NSQMessage;
import com.youzan.nsq.client.exception.NSQException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * Nsq 消费者.
 * <pre>
 * 主要是从properties属性文件中获取配置内容,给消费者和生产者使用.
 * </pre>
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
@Data
@Slf4j
public class NsqConsumer implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    /**
     * 消息监听器
     **/
    Listener listener;
    /**
     * 消息过滤器链,只要其中的过滤器要过滤掉消息,就不会继续向下执行 optional
     */
    List<Filter> filters;

    /**
     * Nsq客户端配置
     */
    NsqConfig nsqConfig;
    /**
     * 主题
     */
    String topic;
    /**
     * 通道
     */
    String channel;

    /**
     * 消息重试处理器
     */
    NsqRetryHandler retry = new CirculateRetryPolicy();

    /**
     * 消息解析器
     */
    NsqMessageDecoder decoder = new DefaultNsqMessageDecoder();


    /**
     * NSQ 消费者
     */
    private Consumer consumer;

    /**
     * NSQ 消费者是否自动ack，默认为true； 设置为false，表示采用手动ack
     */
    private boolean autoFinish = true;

    /**
     * 是否采用顺序模式消费，默认为非顺序模式
     */
    private boolean ordered = false;


    /**
     * 把listener对象放入ThreadLocal中,后边需要使用到可以使用.
     */
    public static ThreadLocal<Listener> CTX_LISTENER = new ThreadLocal<>();

    /**
     * 启动
     **/
    public void start() {

        //构造配置
        NSQConfig config = new NSQConfig();
        config.setLookupAddresses(nsqConfig.getServerAddresses());

        if (nsqConfig.getThreadPoolSize4IO() > 0) {
            config.setThreadPoolSize4IO(nsqConfig.getThreadPoolSize4IO());
        }

        if (nsqConfig.getMsgTimeoutInMillisecond() > 0) {
            config.setMsgTimeoutInMillisecond(nsqConfig.getMsgTimeoutInMillisecond());
        }

        if (nsqConfig.getConnectTimeoutInMillisecond() > 0) {
            config.setConnectTimeoutInMillisecond(nsqConfig.getConnectTimeoutInMillisecond());
        }

        if (nsqConfig.getQueryTimeoutInMillisecond() > 0) {
            config.setQueryTimeoutInMillisecond(nsqConfig.getQueryTimeoutInMillisecond());
        }


        config.setOrdered(ordered);

        config.setConsumerName(channel);

        //消费者,订阅主题
        consumer = new ConsumerImplV2(config, (NSQMessage message) -> {
            try {
                TracerContextUtils.genTracerId();
                execute(message);
            } finally {
                TracerContextUtils.clearTracerId();
            }
        });

        try {
            //设置手动／自动 ack
            consumer.setAutoFinish(autoFinish);

            consumer.subscribe(topic);

            //启动
            consumer.start();
        } catch (Exception e) {
            log.error("[消息消费者]-启动失败,topic={},channel={}", topic, channel, e);
            throw new RuntimeException("[消息消费者]-启动失败,topic=" + topic + ",channel=" + channel, e);
        }
    }

    /**
     * 消息处理
     *
     * @param message 消息体
     */
    protected void execute(NSQMessage message) {

        //1.解码消息体
        Object bodyObj = decoder.decode(message, this);
        if (bodyObj == null) {
            log.error("[消息消费者]-解码失败,请检查格式是否为json,topic={},channel={},message={}", topic, channel, ReflectionToStringBuilder.reflectionToString(message));

            setMessageFinish(message);

            return;
        }
        //2.消息被过滤就不继续执行
        if (isFilter(bodyObj)) {
            log.warn("[消息消费者]-被过滤,topic={},channel={},message={}", topic, channel, message);

            setMessageFinish(message);

            return;
        }
        log.info("[消息消费者]-开始处理消息,topic={},channel={},message={}", topic, channel, message);
        //3. 处理消息
        try {
            CTX_LISTENER.set(listener);
            //3.1. 调用监听器方法
            listener.onEvent(bodyObj);

            setMessageFinish(message);

        } catch (NsqRequeueException e) {
            //3.2   消息重试异常处理,频次调整等
            handleRequeueException(e, message, bodyObj);
        } catch (Exception e) {
            log.error("[消息消费者]-处理消息未知异常,消息不重发,topic={},channel={},message={}", topic, channel, ReflectionToStringBuilder.reflectionToString(message), e);
        } finally {
            CTX_LISTENER.remove();
        }
    }

    /**
     * 根据当前 autoFinish 模式，ack消息
     *
     * @param message
     */
    private void setMessageFinish(NSQMessage message) {
        if (!autoFinish) {
            try {
                consumer.finish(message);
            } catch (NSQException e) {
                log.error("[消息消费者]-业务成功，消息手动ack异常，消息可能会重发，也可能不重发,topic={},channel={},message={}", topic, channel, ReflectionToStringBuilder.reflectionToString(message), e);
            }
        }
    }


    //消息重试异常处理
    private void handleRequeueException(NsqRequeueException nse, NSQMessage originMessage, Object bodyObj) {

        boolean isRequeue = false;

        if (retry != null) {
            isRequeue = retry.handle(originMessage, bodyObj, nse);
        } else {
            log.error("[消息消费者]-重发异常,但重发策略不存在,请配置,topic={},channel={},originMessage={},bodyObj={}", topic, channel, originMessage, bodyObj);
        }
        //抛异常消息继续重发
        if (isRequeue) {
            throw nse;
        }
    }

    /**
     * 过滤消息
     */
    private boolean isFilter(Object bodyObj) {
        if (CollectionUtils.isEmpty(filters)) {
            return false;
        }
        for (Filter filter : filters) {
            if (filter.filter(bodyObj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 释放资源
     */
    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("[消息消费者]-开始初始化,topic={},channel={},listener={},decoder={}", topic, channel, listener.getClass().getSimpleName(), decoder.getClass().getSimpleName());
        start();
        log.info("[消息消费者]-启动成功,topic={},channel={}", topic, channel);
    }
}
