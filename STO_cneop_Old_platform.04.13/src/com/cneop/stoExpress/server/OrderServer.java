package com.cneop.stoExpress.server;

import java.util.HashMap;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.OrderAlertActivity;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.download.DownloadManager;
import com.cneop.stoExpress.model.Order;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.SoundUtil;
import com.cneop.util.VibratorUtil;

public class OrderServer extends Service {
	private OrderDownThread orderDownThread = null;
	private Object objLock = new Object();
	private DownloadManager downloadManager;
	private boolean exitFalg = false;
	private BrocastUtil brocastUtil;
	private int downTimeSpan = 5;
	private NotificationManager nm;
	private Context context;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		brocastUtil = new BrocastUtil(this);
		downloadManager = new DownloadManager(this, GlobalParas.getGlobalParas().getStationName(), GlobalParas.getGlobalParas().getUserNo(), GlobalParas.getGlobalParas().getDeviceId());
		// 启动线程定时下载订单
		orderDownThread = new OrderDownThread();
		orderDownThread.setName("OrderDownThread");
		orderDownThread.isDaemon();// 设置为守护进程
		orderDownThread.start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		synchronized (objLock) {
			objLock.notify();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		exitFalg = true;
		synchronized (objLock) {
			objLock.notify();
		}
		try {
			orderDownThread.join(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderDownThread = null;
	}

	private class OrderDownThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!exitFalg) {
				// todo:网络是否连通
				try {
					brocastUtil.sendDownloadPicStatus(true);
					int t = downloadManager.getOrder();
					brocastUtil.sendOrderDownResultBrocast(t);
					Thread.sleep(1000);
					if (t != -2) {
						List<Order> orderList = downloadManager.getOrderStatus();
						if (orderList != null && orderList.size() > 0) {
							HashMap<String, Order> mapList = new HashMap<String, Order>();
							for (Order order : orderList) {
								if (!mapList.containsKey(order.getLogisticid())) {
									mapList.put(order.getLogisticid(), order);
								}
							}
							GlobalParas.getGlobalParas().setOrderList(mapList);
						}
					}
					if (t > 0) {
						sendNotification();
					}
					brocastUtil.sendDownloadPicStatus(false);
					if (exitFalg) {
						break;
					}
					synchronized (objLock) {
						objLock.wait(downTimeSpan * 60 * 1000);
					}
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 发送通知
	 */
	private void sendNotification() {
		SoundUtil.getIntance(context).PlayVoice(EVoiceType.neworder);
		VibratorUtil.getIntance(context).startVibrator(3);
		Notification mNotification = new Notification();
		mNotification.icon = R.drawable.order_down;
		mNotification.tickerText = "订单通知";
		mNotification.when = System.currentTimeMillis();
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(context, OrderAlertActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		mNotification.setLatestEventInfo(context, "订单提醒", "您有新的订单！", pendingIntent);
		nm.notify(1, mNotification);
	}
}
