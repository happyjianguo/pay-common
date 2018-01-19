package com.dream.pay.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * @author zhenbinmeng
 */
public class DESUtil {
    private static final Log log = LogFactory.getLog(DESUtil.class);

    // 定义加密算法，DESede(即3DES)
    private static final String DESede = "DESede";

    private static final String PASSWORD_CRYPT_KEY = "2015mengzhenbinStudyForSecurity@DESede";

    /**
     * 加密方法
     *
     * @param src 源数据的字节数组
     * @return
     */
    public static byte[] encryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), DESede); // 生成密钥
            Cipher c1 = Cipher.getInstance(DESede); // 实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, deskey); // 初始化为加密模式
            return c1.doFinal(src);
        } catch (Exception e) {
            log.error("DesUtil.encryptMode error", e);
        }
        return null;
    }

    /**
     * 加密方法(BASE64编码返回)
     *
     * @param src 源数据的字节数组
     * @return
     */
    public static String encryptModeBase64(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), DESede); // 生成密钥
            Cipher c1 = Cipher.getInstance(DESede); // 实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, deskey); // 初始化为加密模式
            return BASE64Util.base64Encode(c1.doFinal(src));
        } catch (Exception e) {
            log.error("DesUtil.encryptMode error", e);
        }
        return null;
    }

    /**
     * 解密函数
     *
     * @param src 密文的字节数组
     * @return
     */
    public static byte[] decryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), DESede);
            Cipher c1 = Cipher.getInstance(DESede);
            c1.init(Cipher.DECRYPT_MODE, deskey); // 初始化为解密模式
            return c1.doFinal(src);
        } catch (Exception e) {
            log.error("DesUtil.decryptMode error", e);
        }
        return null;
    }

    /**
     * 解密函数(BASE64解码后解密)
     *
     * @param src 密文的字节数组
     * @return
     */
    public static String decryptModeBase64(String src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), DESede);
            Cipher c1 = Cipher.getInstance(DESede);
            c1.init(Cipher.DECRYPT_MODE, deskey); // 初始化为解密模式
            return new String(c1.doFinal(BASE64Util.base64Decode(src)));
        } catch (Exception e) {
            log.error("DesUtil.decryptMode error", e);
        }
        return null;
    }

    /*
     * 根据字符串生成密钥字节数组
     *
     * @param keyStr 密钥字符串
     *
     * @return
     *
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes("UTF-8"); // 将字符串转成字节数组
        /*
		 * 执行数组拷贝 System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
		 */
        if (key.length > temp.length) {
            // 如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            // 如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

    public static void main(String[] args) {

        System.out.println(new String(DESUtil.decryptModeBase64("DTB3fac9aMCrGXpX40wdlQ==")));

        String wechatSignKey = "SIWJ2XBWWQWHD9FS";
        String wechatSignKeyMi = DESUtil.encryptModeBase64(wechatSignKey.getBytes());
        System.out.println("Des后Base64编码转换后：" + wechatSignKeyMi);
        System.out.println("Base64解码后Des转换后：" + new String(DESUtil.decryptModeBase64(wechatSignKeyMi)));
        System.out.println("############################################");

        System.out.println(
                "=============================================================================================");
        System.out.print("财付通WAP(1217388701)=====");//
        System.out.println(
                new String(DESUtil.decryptModeBase64("4C2x5eM+JSGLtHH0yWzVnUgugHzKOpTcBRsCEHK0u15RLBFvMizF2Q==")));
        System.out.println(
                "=============================================================================================");
        System.out.print("财付通(1213044701)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("V33VL2U1gI+HRLsfZROcPzaCiuCv5C+MEzQzRFxlJFFRLBFvMizF2Q==")));
        System.out.println(
                "=============================================================================================");
        System.out.print("块钱(1000651725101)=====");
        System.out.println(new String(DESUtil.decryptModeBase64("NXCfb6du7Z09qdSL3MFLa1EsEW8yLMXZ")));
        System.out.println(
                "=============================================================================================");
        System.out.print("微信扫码和微信公账(1312747401||wx8be4aaecf78a1934)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("B/n9pCssu57LFQsJwU/DKqqUePf1RVyAfNn4uJ1qnwxRLBFvMizF2Q==")));
        System.out.print("微信app(1312967101||wx862db9d365a49f09)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("VgZrM0zM78QfWh0lcr1MWrUKNOE+Eu7R8nd4iX7lejBRLBFvMizF2Q==")));
        System.out.println(
                "=============================================================================================");
        System.out.print("支付宝拍卖(2088301773687981||zhifubao-pm@dangdang.com)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("uT3B0SnHWGlIqZoNEur4MweGHOtDHSBwSjZN0cWW6VFRLBFvMizF2Q==")));
        System.out.print("支付宝无线(2088901524471441||zhifubao-wx@dangdang.com)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("m9vkL4k1vP3vlTCNwMljXR6CidVjVUCsGsamVNpPE/JRLBFvMizF2Q==")));
        System.out.print("支付宝PC(2088421631345065||zhifubao@dangdang.com)=====");
        System.out.println(
                new String(DESUtil.decryptModeBase64("diE+xwMC8sD8ldB68ijZ+uAF/iONHX7ETb/hJ/v7UG1RLBFvMizF2Q==")));
        System.out.println(
                "=============================================================================================");

    }
}