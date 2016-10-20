package com.cneop.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 带超时功能的ProgressDialog
 * 
 * @author Administrator
 * 
 */
public class ProgressDialogEx extends ProgressDialog {
	public static final String TAG = "ProgressDialogEx";
	private long mTimeOut = 0;// 默认timeOut为0即无限大
	private OnTimeOutListener mTimeOutListener = null;// timeOut后的处理器
	private Timer mTimer = null;// 定时器
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (mTimeOutListener != null) {
				mTimeOutListener.onTimeOut(ProgressDialogEx.this);
				dismiss();
			}
		}
	};

	public ProgressDialogEx(Context context) {
		super(context);

	}

	/**
	 * 设置timeOut长度，和处理器
	 * 
	 * @param t
	 *            timeout时间长度
	 * @param timeOutListener
	 *            超时后的处理器
	 */
	public void setTimeOut(long t, OnTimeOutListener timeOutListener) {
		mTimeOut = t;
		if (timeOutListener != null) {
			this.mTimeOutListener = timeOutListener;
		}
	}

	@Override
	protected void onStop() {

		super.onStop();
		if (mTimer != null) {

			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void onStart() {

		super.onStart();
		if (mTimeOut != 0) {
			mTimer = new Timer();
			TimerTask timerTast = new TimerTask() {
				@Override
				public void run() {

					// dismiss();
					Message msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);
				}
			};
			if (mTimer != null)
				mTimer.schedule(timerTast, mTimeOut);
		}

	}

	/**
	 * 通过静态Create的方式创建一个实例对象
	 * 
	 * @param context
	 * @param time
	 *            timeout时间长度(单位是：ms)
	 * @param listener
	 *            timeOutListener 超时后的处理器
	 * @return MyProgressDialogEx 对象
	 */
	public static ProgressDialogEx createProgressDialogEx(Context context, long time, String title, String message, OnTimeOutListener listener) {
		ProgressDialogEx ProgressDialogEx = new ProgressDialogEx(context);
		if (time != 0) {
			ProgressDialogEx.setTimeOut(time, listener);
		}
		ProgressDialogEx.setTitle(title);
		ProgressDialogEx.setMessage(message);
		return ProgressDialogEx;
	}

	@Override
	public void setMessage(CharSequence message) {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.setMessage(message);

	}

	/**
	 * 
	 * 处理超时的的接口
	 * 
	 */
	public interface OnTimeOutListener {

		/**
		 * 当ProgressDialogEx超时时调用此方法
		 */
		abstract public void onTimeOut(ProgressDialogEx dialog);
	}
}
