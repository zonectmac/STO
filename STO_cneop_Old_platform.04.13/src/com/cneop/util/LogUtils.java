package com.cneop.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogUtils {
	public enum eLevel{
		verbose,
		
	}

	private static String DIR = "";
	private static String FILE_PATH = "";
	public final static String LEVEL_VERBOSE = "verbose";
	public final static String LEVEL_DEBUG = "debug";
	public final static String LEVEL_INFO = "info";
	public final static String LEVEL_WARN = "warn";
	public final static String LEVEL_ERROR = "error";
	static {

		// String path=getApplicationContext().getPackageResourcePath();
		// DIR = Constant.APP_STORAGE_DIR;
		// TODO  
		DIR = System.getProperty("java.class.path");
		FILE_PATH = DIR + "/log.txt";

	}

	public static String getException(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString().trim();
	}

	private static String getCurrentTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		return simpleDateFormat.format(new Date());
	}

	/**
	 * 默认以info等级且不换行写入日志
	 * 
	 * @param tag
	 * @param throwable
	 */
	public static void write(String tag, Throwable throwable) {
		write(tag, LEVEL_INFO, throwable, false);
	}

	/**
	 * 默认以info等级且不换行写入日志
	 * 
	 * @param tag
	 * @param throwable
	 */
	public static void write(String tag, String throwable) {
		write(tag, LEVEL_INFO, throwable, false);
	}

	/**
	 * 默认不换行写入日志
	 * 
	 * @param tag
	 * @param level
	 * @param throwable
	 */
	public static void write(String tag, String level, String throwable) {
		write(tag, level, throwable, false);
	}

	/**
	 * 默认不换行写入日志
	 * 
	 * @param tag
	 * @param level
	 * @param throwable
	 */
	public static void write(String tag, String level, Throwable throwable) {
		write(tag, level, throwable, false);
	}

	/**
	 * 默认以info等级写入日志
	 * 
	 * @param tag
	 * @param throwable
	 * @param seq
	 */
	public static void write(String tag, Throwable throwable, boolean seq) {
		write(tag, LEVEL_INFO, throwable, seq);
	}

	/**
	 * 默认以info等级写入日志
	 * 
	 * @param tag
	 * @param throwable
	 * @param seq
	 */
	public static void write(String tag, String throwable, boolean seq) {
		write(tag, LEVEL_INFO, throwable, seq);
	}

	/**
	 * 将异常信息写入log文件中
	 * 
	 * @param tag
	 *            类TAG
	 * @param level
	 *            log等级分别为verbose,debug，info，warn，error
	 * @param throwable
	 *            异常Throwable对象
	 * @param seq
	 *            是否换行
	 */
	public static void write(String tag, String level, Throwable throwable,
			boolean seq) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}
		File file = new File(DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(FILE_PATH, true);
			if (seq) {
				fw.write("\n\r————————–\r\n");
			}
			fw.write("[" + tag + "]" + getCurrentTime() + "(" + level + "):"
					+ getException(throwable) + "\r\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fw = null;
			file = null;
		}
	}

	/**
	 * 将异常信息写入log文件中
	 * 
	 * @param tag
	 *            类TAG
	 * @param level
	 *            log等级分别为verbose,debug，info，warn，error
	 * @param throwable
	 *            异常Throwable对象的字符串
	 * @param seq
	 *            是否换行
	 */
	public static void write(String tag, String level, String throwable,
			boolean seq) {
		Log.i(tag, throwable + "");
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}
		File file = new File(DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		initLog();
		FileWriter fw = null;
		try {
			fw = new FileWriter(FILE_PATH, true);
			if (seq) {
				fw.write("\n\r————————–\r\n");
			}
			fw.write("[" + tag + "]" + getCurrentTime() + "(" + level + "):"
					+ throwable + "\r\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fw = null;
			file = null;
		}
	}

	private static void initLog() {
		File file = new File(FILE_PATH);
		Calendar last = Calendar.getInstance();
		last.setTimeInMillis(file.lastModified());
		Calendar now = Calendar.getInstance();
		// System.out.println(last.get(Calendar.YEAR) + "-"
		// + last.get(Calendar.MONTH) + "-" + last.get(Calendar.DATE));
		// System.out.println(now.get(Calendar.YEAR) + "-"
		// + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DATE));
		if (last.get(Calendar.YEAR) < now.get(Calendar.YEAR)
				&& last.get(Calendar.MONTH) < now.get(Calendar.MONTH)
				&& last.get(Calendar.DATE) < now.get(Calendar.DATE)) {
			file.setLastModified(System.currentTimeMillis());
			System.out.println("log.txt reload");
			FileWriter fw = null;
			try {
				fw = new FileWriter(FILE_PATH, false);
				fw.write("");
				fw.flush();
				fw.close();
				file.setLastModified(System.currentTimeMillis());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fw = null;
				file = null;
			}
		}
	}
}
