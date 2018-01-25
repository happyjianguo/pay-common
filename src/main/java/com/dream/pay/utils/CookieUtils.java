package com.dream.pay.utils;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @author mengzhenbin
 * @version V1.0
 * @sine 2017年3月1日
 * @descrption Cookie处理工具类<br/>
 */
@Slf4j
public class CookieUtils {
    public static final int MAX_AGE = 60 * 60 * 24 * 7;


    public static final String DEFAULT_ENCODING = "UTF-8";


    private CookieUtils() {
    }


    /**
     * @param response
     * @param key
     * @param value
     */
    public static void addCookie(HttpServletResponse response, String key, String value) {
        addCookie(response, key, value, MAX_AGE);

    }

    public static void deleteCookie(HttpServletResponse response, String key) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * @param response
     * @param key
     * @param value
     * @param maxAge
     */
    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge) {
        try {
            value = URLEncoder.encode(value, DEFAULT_ENCODING);

            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            log.error("CookieUtils.addCookie encode error", e);
        } catch (Exception e) {
            log.error("CookieUtils.addCookie error", e);
        }
    }

    /**
     * @param request
     * @param key
     * @return
     */
    public static String findCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        try {
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (key.equals(cookie.getName())) {
                        return URLDecoder.decode(cookie.getValue(), DEFAULT_ENCODING);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("CookieUtils.addCookie decode error", e);
        } catch (Exception e) {
            log.error("CookieUtils.addCookie error", e);
        }
        return null;
    }
}
