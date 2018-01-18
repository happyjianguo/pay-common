package com.dream.pay.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Charsets;

/**
 * 摘要、签名工具<br/>
 * 包含MD5、KEY、RSA等算法
 * 
 * @author chenjianchunjs
 * 
 */
public class SignUtil {

	/**
	 * 生成字符串md5<br/>
	 * 默认编码: UTF-8
	 * 
	 * @param string
	 *            字符串
	 * @return md5串
	 */
	public static String md5(String string) {
		return md5(string, null);
	}

	/**
	 * 生成字符串md5
	 * 
	 * @param string
	 *            字符串
	 * @param charset
	 *            编码
	 * @return md5串
	 */
	public static String md5(String string, Charset charset) {
		if (StringUtils.isBlank(string)) {
			return null;
		}
		if (charset == null) {
			return DigestUtils.md5Hex(string);
		}

		return DigestUtils.md5Hex(string.getBytes(charset));
	}

	/**
	 * 
	 * @param string
	 *            字符串
	 * @param algorithm
	 *            摘要算法名称
	 * @return
	 */
	public static String digest(String string, String algorithm) {
		return digest(string, algorithm, null);
	}

	/**
	 * 使用指定的算法名称和编码方式生成摘要
	 * 
	 * @param string
	 *            字符串
	 * @param algorithm
	 *            摘要算法名称
	 * @param charset
	 *            编码
	 * @return
	 */
	public static String digest(String string, String algorithm, Charset charset) {
		if (StringUtils.isBlank(string)) {
			return null;
		}
		if (charset == null) {
			charset = Charsets.UTF_8;
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] digestData = md.digest(string.getBytes(charset));

			return Hex.encodeHexString(digestData);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(algorithm + " digest exception", e);
		}
	}

	/**
	 * 加载PKCS12证书KeyStore
	 * 
	 * @param file
	 *            PKCS12证书文件
	 * @param password
	 *            证书密码
	 * @return
	 */
	public static KeyStore loadPKCS12KeyStore(String file, String password) {
		try {
			FileInputStream fis = new FileInputStream(file);
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(fis, password.toCharArray());

			fis.close();
			return keyStore;
		} catch (Exception e) {
			throw new RuntimeException("load pkcs12 keystore exception", e);
		}
	}

	/**
	 * 获取私钥
	 * 
	 * @param key
	 *            base64编码的私钥字符串
	 * @return
	 */
	public static PrivateKey getPrivateKey(String key) {
		byte[] keyBytes = Base64.decodeBase64(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("get private key exception", e);
		}
	}

	/**
	 * 从PKCS12格式证书文件里获取私钥<br/>
	 * PKCS12证书里即包含私钥，也包含公钥<br/>
	 * 证书文件后缀一般是：pfx、p12等
	 * 
	 * @param file
	 *            私钥文件
	 * @param password
	 *            密码
	 * @return
	 */
	public static PrivateKey getPrivateKeyPKCS12(String file, String password) {
		try {
			KeyStore keyStore = loadPKCS12KeyStore(file, password);
			while (keyStore.aliases().hasMoreElements()) {
				String alias = keyStore.aliases().nextElement();

				Key key = keyStore.getKey(alias, password.toCharArray());

				if (key instanceof PrivateKey) {
					return (PrivateKey) key;
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException("get pkcs12 private key exception", e);
		}

	}

	/**
	 * 获取公钥
	 * 
	 * @param key
	 *            base64编码的公钥字符串
	 * @return
	 */
	public static PublicKey getPublicKey(String key) {
		try {
			byte[] keyBytes = Base64.decodeBase64(key);

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("get public key exception", e);
		}
	}

	/**
	 * 从公钥证书文件里获取公钥<br/>
	 * 证书文件后缀一般是：cer、crt、der等
	 * 
	 * @param file
	 *            公钥文件
	 * @return
	 */
	public static PublicKey getPublicKeyX509(String file) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			FileInputStream fis = new FileInputStream(file);
			Certificate cert = certificateFactory.generateCertificate(fis);

			return cert.getPublicKey();
		} catch (Exception e) {
			throw new RuntimeException("get x509 public key exception", e);
		}
	}

	/**
	 * 从PKCS12格式证书文件里获取公钥<br/>
	 * PKCS12证书里即包含私钥，也包含公钥<br/>
	 * 证书文件后缀一般是：pfx、p12等
	 * 
	 * @param file
	 *            私钥文件
	 * @return
	 */
	public static PublicKey getPublicKeyPKCS12(String file, String password) {
		try {
			KeyStore keyStore = loadPKCS12KeyStore(file, password);
			String alias = null;
			if (keyStore.aliases().hasMoreElements()) {
				alias = keyStore.aliases().nextElement();
			}

			Certificate cert = keyStore.getCertificate(alias);

			return cert.getPublicKey();
		} catch (Exception e) {
			throw new RuntimeException("get pkcs12 public key exception", e);
		}
	}

	/**
	 * RSA签名
	 * 
	 * @param string
	 *            待签名字符串
	 * @param privateKey
	 *            私钥
	 * @param charset
	 *            编码
	 * @return base64编码的签名串
	 */
	public static String signRSA(String string, PrivateKey privateKey,
			Charset charset) {
		try {
			Signature signature = Signature
					.getInstance("SHA1WithRSA");
			signature.initSign(privateKey);
			signature.update(string.getBytes(charset));
			byte[] signed = signature.sign();

			return Base64.encodeBase64String(signed);
		} catch (Exception e) {
			throw new RuntimeException("RSA signature exception", e);
		}
	}

	/**
	 * 验证RSA签名
	 * 
	 * @param sign
	 *            base64编码的签名字符串
	 * @param string
	 *            待验证字符串
	 * @param publicKey
	 *            公钥
	 * @param charset
	 *            编码
	 * @return
	 */
	public static boolean verifyRSA(String sign, String string,
			PublicKey publicKey, Charset charset) {
		byte[] decodeSign = Base64.decodeBase64(sign);
		try {
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(publicKey);
			signature.update(string.getBytes(charset));

			return signature.verify(decodeSign);
		} catch (Exception e) {
			throw new RuntimeException("RSA verify sign exception", e);
		}
	}

	/**
	 * RSA解密<br/>
	 * 返回的数据编码：UTF-8
	 * 
	 * @param string
	 *            RSA加密字符串
	 * @param privateKey
	 *            私钥
	 * @return
	 */
	public static String decryptRSA(String string, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			InputStream ins = new ByteArrayInputStream(
					Base64.decodeBase64(string));
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			// RSA解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
			byte[] buf = new byte[128];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(cipher.doFinal(block));
			}

			return new String(writer.toByteArray(), Charsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("decrypt rsa exception", e);
		}
	}

	/**
	 * RSA解密<br/>
	 * 返回的数据编码：UTF-8
	 * 
	 * @param string
	 *            RSA加密字符串
	 * @param key
	 *            base64编码的密钥字符串
	 * @return
	 */
	public static String decryptRSA(String string, String key) {
		PrivateKey privateKey = getPrivateKey(key);

		return decryptRSA(string, privateKey);
	}
}