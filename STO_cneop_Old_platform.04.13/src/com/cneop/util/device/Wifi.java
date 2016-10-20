package com.cneop.util.device;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

public class Wifi {
	private Wifi() {
	}

	private WifiManager mWifiManager;

	private static Wifi INSTANCE;
	private static Object INSTANCE_LOCK = new Object();

	public static Wifi getInstance(Context context) {
		if (INSTANCE == null) {
			synchronized (INSTANCE_LOCK) {

				INSTANCE = new Wifi();
			}
			INSTANCE.init(context);
		}
		return INSTANCE;
	}

	private void init(Context _context) {
		context = _context;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	private Context context;

	public boolean Open() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		return true;
		// return mWifiManager.getWifiState();
	}

	public void DoConfig() {
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		context.startActivity(intent);

		// int sdkVersion = android.os.Build.VERSION.SDK_INT;
		// if (sdkVersion >= 14) {
		// context.startActivity(new
		// Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		// } else {
		// Intent intent = new Intent("/");
		// ComponentName cm = new ComponentName("com.android.settings",
		// "com.android.settings.WirelessSettings");
		// intent.setComponent(cm);
		// intent.setAction("android.intent.action.VIEW");
		// context.startActivity(intent);
		// }
	}

	public boolean isConnected() {
		ConnectivityManager cm;
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ? true : false;

	}

	public void Close() {

		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);

		}
	}

}
