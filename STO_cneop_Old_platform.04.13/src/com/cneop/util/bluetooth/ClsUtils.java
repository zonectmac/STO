package com.cneop.util.bluetooth;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClsUtils {

	/**
	 * 配对
	 * 
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	static public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		createBondMethod.invoke(btDevice);
		// return returnValue.booleanValue();
		return true;
	}

	/**
	 * 取消配对
	 * 
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	static public boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 设置PIN码
	 * 
	 * @param btClass
	 * @param btDevice
	 * @param str
	 * @return
	 * @throws Exception
	 */
	static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception {
		try {
			Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[] { byte[].class });
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[] { str.getBytes() });
			Log.e("returnValue", "" + returnValue);
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	/**
	 * 取消用户输入
	 * 
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	static public boolean cancelPairingUserInput(Class btClass, BluetoothDevice btDevice) throws Exception {
		Method cancelPairingMethod = btClass.getMethod("cancelPairingUserInput");
		Boolean returnValue = (Boolean) cancelPairingMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 取消配对进程
	 * 
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	static public boolean cancelBondProcess(Class btClass, BluetoothDevice device) throws Exception {
		Method cancelBondProcessMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) cancelBondProcessMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 * 连接
	 * 
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	static public BluetoothSocket connect(Class btClass, BluetoothDevice device) throws Exception {
		Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
		BluetoothSocket tmp = (BluetoothSocket) m.invoke(device, 1);
		return tmp;
	}

}
