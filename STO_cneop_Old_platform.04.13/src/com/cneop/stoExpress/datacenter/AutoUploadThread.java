package com.cneop.stoExpress.datacenter;

import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.common.GlobalParas;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

public class AutoUploadThread extends Thread {

	private AutoUploadThread(Context context) {
		this.context = context;
		this.setName("AutoUploadThread");
	}

	private Context context;
	private boolean exitFlag = false;

	private static AutoUploadThread instance;
	private static Object syncObject = new Object();

	public static AutoUploadThread getInsance(Context context) {
		if (instance == null) {
			instance = new AutoUploadThread(context);
		}
		return instance;
	}

	/**
	 * 启动线程
	 */
	public void startWork() {
		if (instance.getState() == Thread.State.NEW) {
			instance.start();
		}
		exitFlag = false;
	}

	@Override
	public void run() {

		super.run();
		while (true) {
			if (GlobalParas.getGlobalParas().getAutoUploadTimeSpilt() <= 0) {
				exitFlag = true;
				break;
			}
			if (UploadThread.getIntance(context).uploadHandler == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				continue;
			}

			Message uploadMsg = UploadThread.getIntance(context).uploadHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putSerializable(UploadThread.uploadTypeKey, EUploadType.scanData);
			bundle.putSerializable(UploadThread.scanTypeKey, EScanType.ALL);
			bundle.putSerializable(UploadThread.sitePropertiesKey, GlobalParas.getGlobalParas().getSiteProperties());
			uploadMsg.setData(bundle);
			UploadThread.getIntance(context).uploadHandler.sendMessage(uploadMsg);
			try {

				synchronized (syncObject) {
					syncObject.wait(GlobalParas.getGlobalParas().getAutoUploadTimeSpilt() * 60 * 1000);
				}
				if (exitFlag) {
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 退出
	 */
	public void exit() {
		exitFlag = true;
		synchronized (syncObject) {
			syncObject.notify();
		}
		try {
			this.join(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instance = null;
	}

}
