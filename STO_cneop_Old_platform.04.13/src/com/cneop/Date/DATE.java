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
			 * 2016-3-3:16:42 ���ʱ�䱣������
			 */
			Date today = new Date();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = f.parse(f.format(today));// ��ǰϵͳʱ��
			Date dt2 = f.parse(context.getSharedPreferences("timer", context.MODE_PRIVATE).getString("time", ""));// ͬ��ʱ��
			if (dt1.getTime() < dt2.getTime()) {
				context.sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));// �ر�ɨ��ͷ����ֹɨ��
				Toast.makeText(context.getApplicationContext(), "��ǹʱ�����������ͬ��ʱ����ٽ��в���!", 1).show();
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
