package com.cneop.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {

	/*
	 * 左对齐，空格补齐
	 */
	public static String padLeft(String s, int length) {
		if (s != null) {
			byte[] bs = new byte[length];
			byte[] ss = s.getBytes();
			System.out.println("abc" + s);
			Arrays.fill(bs, (byte) 32);
			System.arraycopy(ss, 0, bs, 0, ss.length);
			return new String(bs);
		} else {
			byte[] bs = new byte[length];
			Arrays.fill(bs, (byte) 32);
			return new String(bs);
		}
	}

	/*
	 * 获得汉字个数
	 */
	public static int getGBCount(String str) {
		int count = 0;
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		return count;
	}

	/*
	 * 字符串是否为NULL或空
	 */
	public static boolean isNullOrEmpty(String str) {
		boolean flag = false;
		if (str == null) {
			flag = true;
		} else {
			if ("".equals(str)) {
				flag = true;
			}
		}
		return flag;
	}

	/*
	 * 将yyyy-MM-dd HH:mm:ss 转成yyyyMMddHHmmss
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatTimeStr(String timeStr) {
		String newTimeStr = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = dateFormat.parse(timeStr);
			dateFormat.applyPattern("yyyyMMddHHmmss");
			newTimeStr = dateFormat.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newTimeStr;
	}

	/*
	 * 数组变成字符串
	 */
	public static String arrayToString(String[] words) {
		StringBuilder strSb = new StringBuilder("  ");
		if (words != null && words.length > 0) {
			for (int i = 0; i < words.length; i++) {
				strSb.append(words[i]);
			}
		}
		return strSb.toString();
	}

	// / <summary>
	// / 过滤特殊字符
	// / 如果字符串为空，直接返回。
	// / </summary>
	// / <param name="str">需要过滤的字符串</param>
	// / <returns>过滤好的字符串</returns>
	public static String FilterSpecial(String str) {
		if (str == "") {
			return str;
		} else {
			str = str.replace("'", "");
			str = str.replace("<", "");
			str = str.replace(">", "");
			str = str.replace("%", "");
			str = str.replace("'delete", "");
			str = str.replace("''", "");
			str = str.replace("\"\"", "");
			str = str.replace(",", "");
			str = str.replace(".", "");
			str = str.replace(">=", "");
			str = str.replace("=<", "");
			str = str.replace("-", "");
			str = str.replace("_", "");
			str = str.replace(";", "");
			str = str.replace("||", "");
			str = str.replace("[", "");
			str = str.replace("]", "");
			str = str.replace("&", "");
			str = str.replace("#", "");
			str = str.replace("/", "");
			str = str.replace("-", "");
			str = str.replace("|", "");
			str = str.replace("?", "");
			str = str.replace(">?", "");
			str = str.replace("?<", "");
			str = str.replace(" ", "").replace(")", "").replace("＄", "").replace("$", "");
			str = str.replace("\r", "").replace("@", "").replace("*", "").replace("(", "");
			;
			;
			str = str.replace("\n", "").replace("=", "").replace("？", "");
			return str;
			// '<', '>', '&', '=', '+', '?' ,'？'
		}
	}
}
