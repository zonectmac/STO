package com.cneop.util;

import com.cneop.stoExpress.datacenter.SystimeSyncThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * ����״̬�����ı�Ĺ㲥 ��Ҫ��Ȩ��:INTERNET,ACCESS_NETWORK_STATE
 * 
 * @author Administrator
 * 
 */
public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		ConnectivityManager connectivityManager =

		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				State state = networkInfos[i].getState();
				if (NetworkInfo.State.CONNECTED == state) {
					SystimeSyncThread.setISystimeSyncSuc(null);
					new com.cneop.stoExpress.datacenter.SystimeSyncThread(context).start();
					System.out.println("------------> Network is ok");
					return;
				}
			}
		}

		// û��ִ��return,��˵����ǰ����������

		// System.out.println("------------> Network is validate");
	}

}
