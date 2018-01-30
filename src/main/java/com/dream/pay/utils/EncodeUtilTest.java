package com.dream.pay.utils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

/**
 * @Author mengzhenbin
 * @Since 2018/1/30
 */
public class EncodeUtilTest {
    public static void main(String[] args) throws Exception {
        String msg = "我爱你";
        System.out.println("转换前：" + msg);
        //==Base64==
        String base64Str = BASE64Util.base64Encode(msg.getBytes());
        System.out.println("Base64转换后：" + base64Str);
        System.out.println("Base64解码后：" + new String(BASE64Util.base64Decode(base64Str)));
        //==Md5==
        String md5Base64Str = MD5Util.md5Base64(msg);
        System.out.println("Md5后Base编码转换后：" + md5Base64Str);
        //==Des==
        byte[] des = DESUtil.encryptMode(msg.getBytes(),"");
        System.out.println("【DES加密后】：" + new String(des));
        byte[] myMsgArr = DESUtil.decryptMode(des,"");
        System.out.println("【DES解密后】：" + new String(myMsgArr));
        String desBase64Str = DESUtil.encryptModeBase64(msg.getBytes(),"");
        System.out.println("Des后Base64编码转换后：" + desBase64Str);
        System.out.println("Base64解码后Des转换后：" + new String(DESUtil.decryptModeBase64(desBase64Str,"")));
        //==Aes==
        String aesKey = "123456";
        String aesEnStr = AESUtil.aesEncrypt(msg, aesKey);
        System.out.println("【AES加密Base64编码后】：" + aesEnStr);
        String aesDeStr = AESUtil.aesDecrypt(aesEnStr, aesKey);
        System.out.println("【Base64解码AES解密后】：" + aesDeStr);
        //==Rsa==
        HashMap<String, Object> keys = RSAUtil.getKeys();
        RSAPublicKey publicKey = (RSAPublicKey) keys.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) keys.get("private");
        //RSAPublicKey pubKey = RSAUtil.getPublicKey(publicKey.getModulus().toString(), publicKey.getPublicExponent().toString());
        //RSAPrivateKey priKey = RSAUtil.getPrivateKey(privateKey.getModulus().toString(), privateKey.getPrivateExponent().toString());
        String rsaEnStr = RSAUtil.encryptByPublicKey(msg, publicKey);
        System.out.println("【RSA公钥加密后】：" + rsaEnStr);
        String rsaDeStr = RSAUtil.decryptByPrivateKey(rsaEnStr, privateKey);
        System.out.println("【RSA私钥解密后】：" + rsaDeStr);
    }
}
