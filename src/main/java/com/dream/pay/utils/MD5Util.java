package com.dream.pay.utils;

import java.security.MessageDigest;

/**
 * @Author mengzhenbin
 * @Since 2018/1/30
 */
public class MD5Util {
    /**
     * 获取byte[]的md5值
     *
     * @param bytes byte[]
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return md.digest();
    }

    /**
     * 获取字符串md5值
     *
     * @param msg
     * @return md5
     * @throws Exception
     */
    public static byte[] md5(String msg) throws Exception {
        return md5(msg.getBytes());
    }

    /**
     * 获取字符串md5值
     *
     * @param msg
     * @return md5
     * @throws Exception
     */
    public static String md5Hex(String msg) throws Exception {
        byte[] messageDigest = md5(msg.getBytes());
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换为 十六进制 数
        for (int i = 0; i < messageDigest.length; i++) {
            String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }

    /**
     * 结合base64实现md5加密
     *
     * @param msg 待加密字符串
     * @return 获取md5后转为base64
     * @throws Exception
     */
    public static String md5Base64(String msg) throws Exception {
        return BASE64Util.base64Encode(md5(msg));
    }
}