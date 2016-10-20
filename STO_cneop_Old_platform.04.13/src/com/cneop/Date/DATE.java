package com.cneop.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DATE {
	private static boolean TIME = false;

	public static boolean guard_time(Context context) {
		try {
			/**
			 * 2016-3-3:16:42 添加时间保护功能
			 */
			Date today = new Date();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = f.parse(f.format(today));// 当前系统时间
			Date dt2 = f.parse(context.getSharedPreferences("timer", context.MODE_PRIVATE).getString("time", ""));// 同步时间
			if (dt1.getTime() < dt2.getTime()) {
				context.sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));// 关闭扫描头，禁止扫描
				Toast.makeText(context.getApplicationContext(), "巴枪时间错误，请联网同步时间后再进行操作!", 1).show();
				TIME = false;
			} else {
				context.sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
				TIME = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context.getApplicationContext(), e.toString() + "\t" + e.getMessage(), 1).show();
			e.printStackTrace();
		}
		return TIME;
	}
}
