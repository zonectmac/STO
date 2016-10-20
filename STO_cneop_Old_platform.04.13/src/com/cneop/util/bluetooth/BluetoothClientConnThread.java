package com.cneop.util.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

@SuppressLint("NewApi")
public class BluetoothClientConnThread extends Thread {

	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	public BluetoothAdapter adapter;
	final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

	public BluetoothClientConnThread(BluetoothDevice device) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		UUID uuid = UUID.fromString(SPP_UUID);
		// tmp = device.createRfcommSocketToServiceRecord(uuid);
		try {
			Method m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
			try {
				tmp = (BluetoothSocket) m.invoke(device, Integer.valueOf(2));
			} catch (Exception e) {
				// Log.e(TAG, "invoke failed", e);
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// Log.e(TAG, "method failed", e);
			e.printStackTrace();
		}
		mmSocket = tmp;
	}

	@Override
	public void run() {
		// Cancel discovery because it will slow down the connection
		// mBluetoothAdapter.cancelDiscovery();
		adapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();

			BluetoothCommunThread connect = new BluetoothCommunThread(mmSocket);
			connect.start();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException closeException) {
				closeException.printStackTrace();
			}
			return;
		}

		// Do work to manage the connection (in a separate thread)
		// manageConnectedSocket(mmSocket);
	}

	/*
	 * 2.3²»Ö§³Ö
	 */
	public void cancel() {
		try {
			if (mmSocket != null && mmSocket.isConnected())
				mmSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
