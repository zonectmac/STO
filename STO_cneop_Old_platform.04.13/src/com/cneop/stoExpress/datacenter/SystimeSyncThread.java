package com.cneop.stoExpress.datacenter;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.util.StrUtil;

public class SystimeSyncThread extends Thread {
	public interface ISyncSuc {
		public void OnHandleResult(boolean isSuc, String msg, int what);

	}

	public static final int TIME_SYNC_WHAT = 1013;// 同步时间的what
	public static final int HANDLE_SYNC_WHAT = 1013;// 手动更新的what
	StrUtil strUtil;
	private Context context;

	public SystimeSyncThread(Context context) {
		this.setName("SystimeSyncThread");
		strUtil = new StrUtil();
		this.context = context;
	}

	private static ISyncSuc systimeSyncSuc = null;

	public static void setISystimeSyncSuc(ISyncSuc _sync) {
		systimeSyncSuc = _sync;
	}

	public static String year = "";
	public static String month = "";
	public static String day = "";
	public static String hour = "";
	public static String minute = "";
	public static String second = "";

	public long getTimeMillis() {
		Calendar c = Calendar.getInstance();
		String time = UPtime;// 2016/03/03 14:24:02
		if (time.length() > 0) {
			year = time.substring(0, 4);

			month = time.substring(5, 7);

			day = time.substring(8, 10);

			hour = time.substring(11, 13);

			minute = time.substring(14, 16);
			second = time.substring(17, 19);
			c.set(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
			return c.getTimeInMillis();
		}
		return c.getTimeInMillis();
	}

	public void settime() {
		// 新大陆更改系统时间广播
		String ACTION1 = "com.sf.fss.SETTIME_ACTION";
		Intent it = new Intent(ACTION1);
		it.putExtra("time", String.valueOf(getTimeMillis()));
		context.sendBroadcast(it);

		// ----------------------------------------------------------------------------------------------------
		// S3更改系统时间广播
		Intent intent = new Intent("ACTION_USER_SETDATEANDTIME");
		intent.putExtra("USER_SETDATEANDTIME_YEAR", Integer.valueOf(year));
		intent.putExtra("USER_SETDATEANDTIME_MONTH", Integer.valueOf(month));
		intent.putExtra("USER_SETDATEANDTIME_DAY", Integer.valueOf(day));
		intent.putExtra("USER_SETDATEANDTIME_HOUR", Integer.valueOf(hour));
		intent.putExtra("USER_SETDATEANDTIME_MINUTE", Integer.valueOf(minute));
		context.sendBroadcast(intent);

		SharedPreferences preferences = context.getSharedPreferences("timer", context.MODE_PRIVATE);
		preferences.edit().putString("time", SystimeSyncThread.year + "-" + SystimeSyncThread.month + "-" + SystimeSyncThread.day + " " + SystimeSyncThread.hour + ":" + SystimeSyncThread.minute + ":" + SystimeSyncThread.second).commit();
		System.out.println("==========preferences \t" + preferences.getString("time", ""));
	}

	public String UPtime;

	@Override
	public void run() {
		super.run();

		String serverTime = MSDServer.getInstance(context).getServerTime();
		UPtime = serverTime;
		if (UPtime.length() > 0) {
			settime();
		}
		if (StrUtil.isNullOrEmpty(serverTime)) {
			if (systimeSyncSuc != null) {
				systimeSyncSuc.OnHandleResult(false, "时间同步失败", TIME_SYNC_WHAT);
				return;
			}
		}
		// 2016/03/03 14:14:47
		if (!strUtil.isNullOrEmpty(serverTime)) {
			if (serverTime.contains("/")) {
				serverTime = serverTime.replace('/', '-').replace(' ', '-').replace(':', '-');
			}
			String[] time = serverTime.split("-");
			if (systimeSyncSuc != null) {
				systimeSyncSuc.OnHandleResult(true, "时间同步成功", TIME_SYNC_WHAT);
				return;
			}
		}
		return;

	}

}
