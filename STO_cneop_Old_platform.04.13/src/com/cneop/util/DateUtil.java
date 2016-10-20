package com.cneop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;

public class DateUtil {

	/*
	 * 获得日期时间字符串
	 */
	public static String getDateTimeByPattern(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(new Date());
		return dateStr;
	}

	public static String getStrDate(java.util.Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 获得时间格式
	 * 
	 * @param timeStr
	 * @return
	 */
	public static Date getDateFormStr(String timeStr, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date d = null;
		try {
			d = simpleDateFormat.parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	/*
	 * 将时间 转成yyyyMMddHHmmss
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatTimeStr(String timeStr, String pattern) {
		String newTimeStr = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		Date d = null;
		if (!timeStr.contains("/") && timeStr.length() == 19) {
			dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			try {
				d = dateFormat.parse(timeStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			d = new Date(timeStr);
		}
		dateFormat.applyPattern(pattern);
		newTimeStr = dateFormat.format(d);
		return newTimeStr;
	}

	public static Date addDay(int days) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, days);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		return date;
	}

	public static String addDay(int days, String format) {
		Date date = addDay(days);
		return getStrDate(date, format);
	}
}
