package com.cneop.util.scan;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.BarcodeScan;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;

/**
 * 扫锟斤拷头锟斤拷锟斤拷 1)锟斤拷锟斤拷锟斤拷贸锟斤拷锟缴�,锟斤拷锟�15锟斤拷未锟斤拷锟秸碉拷锟斤拷莼锟斤拷远锟酵Ｖ癸拷锟斤拷锟斤拷,同时锟斤拷锟�
 * 2)
 * 
 * @author Administrator
 * 
 */
public class V6ScanManager implements IScan {
	public interface IScanResult {

		public void HandleScanResult(String barcode);
	}

	private PowerManager pm;
	private BarcodeScan mBarcodeScan;
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
	private boolean isContinueOpened = false;// 锟斤拷扫锟角凤拷锟斤拷
	private boolean isDoingOtherOpt = false;// 锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟秸ｏ拷锟斤拷扫锟借不锟斤拷锟秸广播

	private V6ScanManager() {

	}

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

	private static V6ScanManager _instance;

	public static V6ScanManager getInstance() {
		if (_instance == null) {
			_instance = new V6ScanManager();
		}
		return _instance;
	}

	/**
	 * 锟斤拷始锟斤拷扫锟斤拷头
	 * 
	 * @param isVibrator
	 *            :锟角凤拷锟斤拷
	 * @param scanRawResID
	 *            :扫锟斤拷锟斤拷锟斤拷 锟斤拷锟斤拷源锟斤拷ID,锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷-1
	 * 
	 */
	public void init(boolean isVibrator, int scanRawResID, Context context) {
		if (mBarcodeScan != null) {
			return;
		}

		this.g_mvibrator = isVibrator;
		this.scanRawResID = scanRawResID;
		pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mBarcodeScan = new BarcodeScan(context);
		this.context = context;

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BarcodeScan.ACTION_BAR_SCAN);
		context.registerReceiver(sanBroadcast, intentFilter);
		pm.setScanningGunPowerOnroOff(1);
		// MediaPlayerUtil.getInstance().init(context);
	}

	public void setIsVibrator(boolean isVibrator) {
		this.g_mvibrator = isVibrator;
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
			if (event.getRepeatCount() == 0) {
				if (isContinue) {
					if (isContinueOpened) {
						stopScan();
					} else {
						startContinueScan();
						last_scan_time = System.currentTimeMillis();
					}
				} else {
					mBarcodeScan.scanning();
				}

				return true;
			}
		}
		return false;
	}

	public void setScanResultHandler(IScanResult scanResult) {
		this.scanResultHandler = scanResult;
	}

	/**
	 * 停止扫锟借，锟斤拷锟截碉拷
	 */
	public void stop() {
		if (mBarcodeScan == null) {
			return;
		}
		stopTimer();
		pm.setScanningGunPowerOnroOff(0);
		mBarcodeScan.close();
		context.unregisterReceiver(sanBroadcast);
		mBarcodeScan = null;
	}

	/**
	 * 锟斤拷锟斤拷扫锟斤拷模式锟斤拷锟斤拷扫/锟斤拷扫
	 * 
	 * @param isContinue
	 *            :true为锟斤拷扫
	 */
	public void setScanMode(boolean isContinue) {
		this.isContinue = isContinue;

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
		mBarcodeScan.setScannerContinuousMode();
	}

	private void stopScan() {
		isContinueOpened = false;
		mBarcodeScan.scannerContinusousModeShutdown();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_BARCODE:
				last_scan_time = System.currentTimeMillis();
				if (scanResultHandler != null) {
					scanResultHandler.HandleScanResult((String) msg.obj);
					// if (scanRawResID != -1) {
					// MediaPlayerUtil.getInstance().play(scanRawResID, false);
					// }

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
			if (action.equals(BarcodeScan.ACTION_BAR_SCAN)) {
				String text = intent.getStringExtra(BarcodeScan.EXTRA_SCAN_DATA);
				if (text.contains("\r\n"))
					text = text.substring(0, text.length() - 2);// 锟斤拷通扫锟斤拷锟斤拷证去锟斤拷锟斤拷尾锟斤拷\r\n
				Message m = Message.obtain(mHandler, MESSAGE_BARCODE);
				m.obj = text;
				mHandler.sendMessage(m);
			}
		}

	}

	@SuppressLint("NewApi")
	private void setVibratortime(int times) {
		if (mvibrator.hasVibrator() && g_mvibrator)
			mvibrator.vibrate(times);
	}

	@Override
	public void setHomeKeyEnable(boolean isEn, Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNoticeEnable(boolean isEn, Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPower(boolean isPowerOn) {
		// TODO Auto-generated method stub

	}

}
