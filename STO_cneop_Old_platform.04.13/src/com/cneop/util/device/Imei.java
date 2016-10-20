package com.cneop.util.device;

import com.cneop.util.StrUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Imei {
	public static String getImei(Context context) {
		@SuppressWarnings("static-access")
		TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		if (tm.getDeviceId() == null) {
			return "巴枪ID为空";
			// return "770098156987532";
		} else {
			Log.i("============id \t", tm.getDeviceId());
			return tm.getDeviceId();// 359996020119782
		}
	}

	/**
	 * 此方法必须要7月9号的系统才能获取到
	 * 
	 * @return
	 */
	public static String getSerialNo() {
		String str = android.os.Build.SERIAL;
		if (!StrUtil.isNullOrEmpty(str)) {
			str = str.replace("\r", "").trim();
			str = str.replace("\n", "");
		}
		return str;
	}
}
