package com.cneop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;

public class DateUtil {

	/*
	 * �������ʱ���ַ���
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
	 * ���ʱ���ʽ
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
	 * ��ʱ�� ת��yyyyMMddHHmmss
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
		Date date = new Date();// ȡʱ��
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, days);// ��������������һ��.����������,������ǰ�ƶ�
		date = calendar.getTime(); // ���ʱ���������������һ��Ľ��
		return date;
	}

	public static String addDay(int days, String format) {
		Date date = addDay(days);
		return getStrDate(date, format);
	}
}
