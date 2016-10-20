package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.BarcodeScan;
import android.os.Build;
import android.os.Bundle;
import android.serialport.ScantrigCon;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cneop.stoExpress.cneops.R;

public class LockScreenActivity extends Activity {
	private static List<Activity> activity = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 此时相当于viewgroup添加view,按menu键时外层view退出堆
		setContentView(R.layout.activity_lock_screen);
		super.onCreate(savedInstanceState);
		activity.add(this);
		if (Build.MODEL.equals("CN-S3")) {
			mBarcodeScan = new BarcodeScan(this);
			mBarcodeScan.open();
		}
	}

	/**
	 * 解锁
	 */
	private void unLockScreen() {
		// 关闭外层Activity
		for (int i = 0; i < activity.size(); i++) {
			activity.get(i).finish();
		}
		activity.clear();
	}

	private BarcodeScan mBarcodeScan;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Toast.makeText(getApplicationContext(), "请按MENU解锁", Toast.LENGTH_SHORT).show();
			return true;
		case KeyEvent.KEYCODE_MENU:
			unLockScreen();
			return true;
		case 139:
			// Toast.makeText(getApplicationContext(), "中间键 139", 1).show();
			sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
			break;
		case 140:
			// Toast.makeText(getApplicationContext(), "右侧键 141", 1).show();
			sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
			break;
		case 141:
			// Toast.makeText(getApplicationContext(), "左侧键 140", 1).show();
			sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_MUTE:
		case KeyEvent.KEYCODE_CTRL_LEFT:
			if (Build.MODEL.equals("CN-S3")) {
				if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("")) {
					mBarcodeScan.open();
					mBarcodeScan.scanning();
				} else if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("连扫")) {
					mBarcodeScan.setScannerContinuousMode();// 连扫
				}

			}
		}
		return super.onKeyDown(keyCode, event);

	}
}
