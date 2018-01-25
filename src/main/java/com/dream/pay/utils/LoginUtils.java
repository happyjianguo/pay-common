package com.dream.pay.utils;

import com.dream.pay.bean.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption 登陆检验工具类<br/>
 */
public class LoginUtils {
    public static boolean hasLogin(HttpServletRequest request) {
        String cookie = CookieUtils.findCookie(request, "user");

        if (cookie == null) {
            return false;
        }

        Base64.Decoder decoder = Base64.getDecoder();
        String decode = new String(decoder.decode(cookie), StandardCharsets.UTF_8);
        UserInfo user = JsonUtil.fromJson(decode, UserInfo.class);

        return user != null;
    }

    public static UserInfo getUser(HttpServletRequest request) {
        String cookie = CookieUtils.findCookie(request, "user");

        if (cookie == null) {
            return null;
        }

        Base64.Decoder decoder = Base64.getDecoder();
        String decode = new String(decoder.decode(cookie), StandardCharsets.UTF_8);

        return JsonUtil.fromJson(decode, UserInfo.class);
    }


    public static void setLoginUser(HttpServletResponse response, UserInfo user) {
        Base64.Encoder encoder = Base64.getEncoder();
        String original = JsonUtil.toJson(user);
        String encoded = encoder.encodeToString(original.getBytes(StandardCharsets.UTF_8));
        CookieUtils.addCookie(response, "user", encoded);

    }
}
