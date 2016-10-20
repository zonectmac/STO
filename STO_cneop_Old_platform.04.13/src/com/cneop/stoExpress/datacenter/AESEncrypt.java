package com.cneop.stoExpress.datacenter;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

public class AESEncrypt {

	private static String key = "Guz(%&hj7x89H$yuBI0456FtmaT5&fvH";
	private static String iv = "E4ghj*Ghg7!rNIfb";

	/**
	 * ¼ÓÃÜ
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("NewApi") public static String encrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(Charset.forName("ASCII")), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(Charset.forName("ASCII")));
			
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(data.getBytes("utf-8"));

			return Base64.encodeToString(encrypted, Base64.NO_WRAP);

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ½âÃÜ
	 * 
	 * @param data
	 * @return
	 */
	@SuppressLint("NewApi") public static String decrypt(String data) {
		try {

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(Charset.forName("ASCII")), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(Charset.forName("ASCII")));

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			// ·´BASE64×Ö·û´®
			byte[] cncrypted1 = Base64.decode(data, Base64.NO_WRAP);
			byte[] original = cipher.doFinal(cncrypted1);
			String originalStr = new String(original, Charset.forName("utf-8"));
			return originalStr;
		} catch (Exception e) {
			return "";
		}
	}
}
