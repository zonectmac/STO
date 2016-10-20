package com.cneop.stoExpress.server;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.OrderAlertActivity;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.util.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class OrderAlertServer extends Service {
	private Object objLock = new Object();
	private boolean exitFalg = false;
	private int alertTimeSpan = 10;
	private NotificationManager nm;
	private Context context;
	private AlertThread alertThread;
	private OrderService orderDao;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		orderDao = new OrderService(this);
		context = getApplicationContext();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		alertThread = new AlertThread();
		alertThread.setName("orderAlertThread");
		alertThread.isDaemon();
		alertThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		exitFalg = true;
		synchronized (objLock) {
			objLock.notify();
		}
		try {
			alertThread.join(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		alertThread = null;
	}

	private class AlertThread extends Thread {

		@Override
		public void run() {

			super.run();
			while (!exitFalg) {
				try {
					int count = orderDao.getOrderCount(GlobalParas.getGlobalParas().getUserNo());
					if (count > 0) {
						// 发通知提醒
						sendNotifition();
					}
					if (exitFalg) {
						break;
					}
					synchronized (objLock) {
						objLock.wait(alertTimeSpan * 60 * 1000);
					}
				} catch (Exception e) {

				}
			}
		}

	}

	private void sendNotifition() {
		SoundUtil.getIntance(context).PlayVoice(EVoiceType.unfinishorder);
		VibratorUtil.getIntance(context).startVibrator(3);
		Notification mNotification = new Notification();
		mNotification.icon = R.drawable.order_down;
		mNotification.tickerText = "订单通知";
		mNotification.when = System.currentTimeMillis();
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(context, OrderAlertActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		mNotification.setLatestEventInfo(context, "订单提醒", "您有未处理的订单！", pendingIntent);
		nm.notify(1, mNotification);
	}

}
