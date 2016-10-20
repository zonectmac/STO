package com.cneop.util.device;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class DeviceUtil {
	public static int getLeftKeyCode() {
		return KeyEvent.KEYCODE_F8;
	}

	Context context;
	static final String KEYBOARD_CHANGE = "com.android.disable.keyboard.change";
	static final String CUSTOM_KEYBOARD_CHANGE = "com.android.custom.keyboard.change";
	static final String ICON_CHANGE = "com.idatachina.keystate.iconchange";

	public DeviceUtil(Context context) {
		this.context = context;
	}

	/**
	 * ÆôÓÃ/½ûÖ¹Êý×Ö×ÖÄ¸ÇÐ»»¼ü , state£º true,ÆôÓÃ£¬false ½ûÖ¹
	 * 
	 * @param state
	 */
	public void SetKeyboardChangeState(boolean state) {
		Intent intent = new Intent(KEYBOARD_CHANGE);
		intent.putExtra(KEYBOARD_CHANGE, state);
		context.sendBroadcast(intent);
	}

	/**
	 * 0 Êý×Ö, 1 Ð¡Ð´, 2 ´óÐ´
	 * 
	 * @param state
	 */
	public void setCurrentInputMethod(int state) {
		Intent intent = new Intent(CUSTOM_KEYBOARD_CHANGE);
		intent.putExtra("mode", state);
		context.sendBroadcast(intent);
		intent = new Intent(ICON_CHANGE);
		intent.putExtra("keystate", state);
		context.sendBroadcast(intent);
	}

	public void setSystemTime(String timeStr) {
		Intent intent = new Intent("com.System.TimeSetting");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		if (!timeStr.contains("/") && timeStr.length() == 19) {
			try {
				date = dateFormat.parse(timeStr);
			} catch (ParseException e) {

				e.printStackTrace();
			}
		} else {
			date = new Date(timeStr);
		}
		intent.putExtra("year", date.getYear() + 1900);
		String machineVer = android.os.Build.BRAND;
		if (machineVer.equalsIgnoreCase("alps")) { // A2W
			intent.putExtra("month", date.getMonth() + 1);
		} else {
			intent.putExtra("month", date.getMonth());
		}
		intent.putExtra("day", date.getDate());
		intent.putExtra("hour", date.getHours());
		intent.putExtra("minute", date.getMinutes());
		intent.putExtra("second", date.getSeconds());
		context.sendBroadcast(intent);
	}

}
