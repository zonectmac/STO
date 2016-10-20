package com.cneop.util.bluetooth;

import com.cneop.util.AppContext;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

	String strPsw = "0";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
			BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			strPsw = AppContext.getAppContext().getBluetoothCode();
			// byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
			// device.setPin(pinBytes);
			Log.i("tag11111", "ddd");
			try {
				ClsUtils.setPin(btDevice.getClass(), btDevice, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
