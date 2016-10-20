package com.cneop.util.scan;

import java.util.Timer;
import java.util.TimerTask;

import com.cneop.util.VibratorUtil;
import com.cneop.util.scan.V6ScanManager.IScanResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;

public class NewScanManager implements IScan {

	private Vibrator mvibrator;
	private boolean g_mvibrator;
	private int scanRawResID;
	private static final int MESSAGE_BARCODE = 1011;
	private ScanBroadcastReceiver sanBroadcast = new ScanBroadcastReceiver();
	private IScanResult scanResultHandler = null;
	private Context context;
	private Timer mTimer = null;// 锟斤拷时锟斤拷
	private boolean isContinue = false;
	private long last_scan_time = System.currentTimeMillis();;
	private boolean isContinueOpened = false;// 锟斤拷扫锟角凤拷锟斤拷359996020119782
	private boolean isDoingOtherOpt = false;// 锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟秸ｏ拷锟斤拷扫锟借不锟斤拷锟秸广播

	private boolean isPowerStat = false;

	public void setisDoingOtherOpt(boolean isDoingOtherOpt) {
		this.isDoingOtherOpt = isDoingOtherOpt;
	}

	public boolean getisDoingOtherOpt() {
		return isDoingOtherOpt;
	}

	private void initCheckContinueScanTimer() {
		if (mTimer != null) {
			return;
		}
		mTimer = new Timer();
		TimerTask timerTast = new TimerTask() {
			@Override
			public void run() {
				long cur_time = System.currentTimeMillis();
				if (cur_time - last_scan_time >= 10000 && isContinueOpened) {
					stopScan();
				}

			}
		};
		if (mTimer != null)
			mTimer.schedule(timerTast, 0, 15000);
	}

	private static NewScanManager _instance;

	public static NewScanManager getInstance() {
		if (_instance == null) {
			_instance = new NewScanManager();
		}
		return _instance;
	}

	public void setPower(boolean isPowerOn) {
		Intent intent = new Intent("ACTION_BAR_SCANCFG");
		intent.putExtra("EXTRA_SCAN_POWER", isPowerOn ? 1 : 0);
		// context.sendBroadcast(intent);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isPowerStat = isPowerOn;
	}

	public void init(boolean isVibrator, int scanRawResID, Context context) {

		this.g_mvibrator = isVibrator;
		this.scanRawResID = scanRawResID;
		// pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		// mBarcodeScan = new BarcodeScan(context);
		this.context = context;

		setPower(true);

		// MediaPlayerUtil.getInstance().init(context);

		// //寮�鍚壂鎻�
		Intent intent;
		//
		//
		//
		// // 鐩存帴濉厖妯″紡
		intent = new Intent("ACTION_BAR_SCANCFG");
		intent.putExtra("EXTRA_SCAN_MODE", 3);// 閲囩敤api鐨勬柟寮�
		intent.putExtra("EXTRA_SCAN_AUTOENT", 0);// 涓嶅厑璁告崲琛�
		context.sendBroadcast(intent);
		//
		// // 鍏佽鑷姩鎹㈣
		// intent = new Intent("ACTION_BAR_SCANCFG");

		// context.sendBroadcast(intent);
		//
		// // 鎵撳紑澹伴煶鎻愮ず
		// intent = new Intent("ACTION_BAR_SCANCFG");
		// intent.putExtra("EXTRA_SCAN_NOTY_SND", 1);
		// context.sendBroadcast(intent);
		//
		// // 鎵撳紑鎸姩鎻愮ず
		// intent = new Intent("ACTION_BAR_SCANCFG");
		// intent.putExtra("EXTRA_SCAN_NOTY_VIB", 1);
		// context.sendBroadcast(intent);
		//
		// // 鎵撳紑鎸囩ず鐏彁绀�
		// intent = new Intent("ACTION_BAR_SCANCFG");
		// intent.putExtra("EXTRA_SCAN_NOTY_LED", 1);
		// context.sendBroadcast(intent);
	}

	public void setIsVibrator(boolean isVibrator) {
		this.g_mvibrator = isVibrator;
	}

	private void regReceiver() {
		try {
			if (this.context == null)
				return;
			IntentFilter intentFilter = new IntentFilter("ACTION_BAR_SCAN");
			context.registerReceiver(sanBroadcast, intentFilter);
		} catch (Exception ex) {

		}
	}

	private void unRegReceiver() {
		try {
			if (this.context == null)
				return;
			context.unregisterReceiver(sanBroadcast);

		} catch (Exception ex) {

		}
	}

	/**
	 * 
	 * @param keyCode
	 *            :扫锟斤拷锟終eycode
	 * @return
	 */
	public boolean scan(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_MUTE:
		case KeyEvent.KEYCODE_CTRL_LEFT:
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_F9:
			if (event.getRepeatCount() == 0) {
				if (isContinue) {
					if (isContinueOpened) {
						stopScan();
					} else {
						startContinueScan();
						last_scan_time = System.currentTimeMillis();
					}
				} else {
					// mBarcodeScan.scanning();
					Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
					// intent.putExtra("timeout", 3);
					this.context.sendBroadcast(intent);
				}

				return true;
			}
		}
		return false;
	}

	public void setScanResultHandler(IScanResult scanResult) {
		this.scanResultHandler = scanResult;
		unRegReceiver();
		regReceiver();
	}

	/**
	 * 停止扫锟借，锟斤拷锟截碉拷
	 */
	public void stop() {

		stopTimer();

		unRegReceiver();

		// context.unregisterReceiver(sanBroadcast);

	}

	private void switchScanModel(boolean isContinue) {
		Intent intent = new Intent("ACTION_BAR_SCANCFG");
		intent.putExtra("EXTRA_TRIG_MODE", isContinue ? 1 : 0);// 1:杩炴壂,0:鍗曟壂
																// ,浠栦滑鐨勬帴鍙ｆ槸涓嶆槸寮�
		context.sendBroadcast(intent);

		// try {
		// Thread.sleep(300);
		//
		// setPower(false);
		//
		// Thread.sleep(100);
		// setPower(true);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// ;

	}

	/**
	 * 锟斤拷锟斤拷扫锟斤拷模式锟斤拷锟斤拷扫/锟斤拷扫
	 * 
	 * @param isContinue
	 *            :true为锟斤拷扫
	 */
	public void setScanMode(boolean isContinue) {
		this.isContinue = isContinue;
		switchScanModel(isContinue);

		if (isContinue) {
			initCheckContinueScanTimer();

		}

		else {
			stopTimer();
			stopScan();

		}
	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mTimer = null;
	}

	/**
	 * 锟斤拷始锟斤拷扫(锟斤拷锟斤拷锟斤拷)
	 */
	private void startContinueScan() {
		isContinueOpened = true;
		switchScanModel(true);
		// mBarcodeScan.setScannerContinuousMode();
	}

	private void stopScan() {
		// setPower(false);
		//
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// setPower(true);
		isContinueOpened = false;
		// mBarcodeScan.scannerContinusousModeShutdown();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_BARCODE:
				last_scan_time = System.currentTimeMillis();

				if (scanResultHandler != null) {
					scanResultHandler.HandleScanResult((String) msg.obj);
					if (g_mvibrator) {
						setVibratortime(50);
					}
				}
				break;

			}
		}
	};

	private class ScanBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (getisDoingOtherOpt()) {
				return;
			}
			String action = intent.getAction();
			// if (action.equals(BarcodeScan.ACTION_BAR_SCAN)) {

			String text = intent.getStringExtra("EXTRA_SCAN_DATA");
			if (text == null)
				return;

			if (text.contains("\r\n"))
				text = text.substring(0, text.length() - 2);// 锟斤拷通扫锟斤拷锟斤拷证去锟斤拷锟斤拷尾锟斤拷\r\n

			text = text.trim();

			if (text.equals("")) {
				return;
			}

			Message m = Message.obtain(mHandler, MESSAGE_BARCODE);
			m.obj = text.trim();
			mHandler.sendMessage(m);
			// }
		}

	}

	/**
	 * 震动时间
	 * 
	 * @param times
	 */
	private void setVibratortime(int times) {
		if (g_mvibrator)
			VibratorUtil.getIntance(context).startVibrator(0.1);
		// mvibrator.vibrate(times);
	}

	@Override
	public void setHomeKeyEnable(boolean isEn, Context context) {
		Intent intent = new Intent("com.android.action.HOMEKEY_SWITCH_STATE");
		intent.putExtra("enable", isEn);
		context.sendBroadcast(intent);

	}

	@Override
	public void setNoticeEnable(boolean isEn, Context context) {
		Intent intent = new Intent("com.android.action.STATUSBAR_SWITCH_STATE");
		intent.putExtra("enable", isEn);
		context.sendBroadcast(intent);

	}

}
