package com.cneop.util;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

/*
 * 震子类
 */
public class VibratorUtil {


	private Vibrator vib;

	private VibratorUtil(Context act) {
		vib = (Vibrator) act.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private static VibratorUtil Intance;

	public static VibratorUtil getIntance(Context context) {
		if (Intance == null) {
			Intance = new VibratorUtil(context);
		}
		return Intance;
	}

	public static VibratorUtil getIntance() {
		return Intance;
	}

	/*
	 * 2.3不支持 振动
	 */
	public void startVibrator(double seconds) {
		if (vib == null) {
			return;
		}
		// if (vib.hasVibrator()) {
		if (seconds <= 0) {
			return;
		}
		vib.vibrate((int) (seconds * 1000));
		// }
	}

	/*
	 * 震动 pattern [静止时长，震动时长]
	 */
	public void startVibrator(long[] pattern, boolean isRepeat) {
		if (vib == null) {
			return;
		}
		// if (vib.hasVibrator()) {
		vib.vibrate(pattern, isRepeat == true ? 1 : -1);
		// }
	}

	/*
	 * 停止
	 */
	public void StopVibrator() {
		if (vib == null) {
			return;
		}
		// if (vib.hasVibrator()) {
		vib.cancel();
		// }
	}
}
