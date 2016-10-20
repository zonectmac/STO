package com.cneop.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * ����ʱ���ܵ�ProgressDialog
 * 
 * @author Administrator
 * 
 */
public class ProgressDialogEx extends ProgressDialog {
	public static final String TAG = "ProgressDialogEx";
	private long mTimeOut = 0;// Ĭ��timeOutΪ0�����޴�
	private OnTimeOutListener mTimeOutListener = null;// timeOut��Ĵ�����
	private Timer mTimer = null;// ��ʱ��
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
	 * ����timeOut���ȣ��ʹ�����
	 * 
	 * @param t
	 *            timeoutʱ�䳤��
	 * @param timeOutListener
	 *            ��ʱ��Ĵ�����
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
	 * ͨ����̬Create�ķ�ʽ����һ��ʵ������
	 * 
	 * @param context
	 * @param time
	 *            timeoutʱ�䳤��(��λ�ǣ�ms)
	 * @param listener
	 *            timeOutListener ��ʱ��Ĵ�����
	 * @return MyProgressDialogEx ����
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
	 * ����ʱ�ĵĽӿ�
	 * 
	 */
	public interface OnTimeOutListener {

		/**
		 * ��ProgressDialogEx��ʱʱ���ô˷���
		 */
		abstract public void onTimeOut(ProgressDialogEx dialog);
	}
}
