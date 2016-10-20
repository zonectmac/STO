package com.cneop.util.device;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 使用时，要加权限： <uses-permission
 * android:name="android.permission.ACCESS_NETWORK_STATE" /> <uses-permission
 * android:name="android.permission.CHANGE_NETWORK_STATE" />
 * 
 * @author Administrator
 * 
 */
public class GPRS {
	private static GPRS INSTANCE;
	private static Object INSTANCE_LOCK = new Object();
	private Context context;

	private GPRS() {

	}

	private void init(Context context) {
		this.context = context;
	}

	public static GPRS getInstance(Context context) {

		if (INSTANCE == null) {
			synchronized (INSTANCE_LOCK) {
				INSTANCE = new GPRS();
				INSTANCE.init(context);
			}
		}
		return INSTANCE;
	}

	public boolean Open() {
		setMobileDataStatus(context, true);
		// ConnectivityManager cm = (ConnectivityManager) context
		// .getSystemService(Context.CONNECTIVITY_SERVICE);

		// NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		// boolean isConnected = activeNetwork.isConnectedOrConnecting();

		return true;
	}

	public void Close() {

		if (isConnected())

			setMobileDataStatus(context, false);
	}

	// 移动数据开启和关闭
	private void setMobileDataStatus(Context context, boolean enabled)

	{

		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ConnectivityManager类

		Class<?> conMgrClass = null;

		// ConnectivityManager类中的字段
		Field iConMgrField = null;
		// IConnectivityManager类的引用
		Object iConMgr = null;
		// IConnectivityManager类
		Class<?> iConMgrClass = null;
		// setMobileDataEnabled方法
		Method setMobileDataEnabledMethod = null;
		try {

			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象Mservice
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());

			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

			// 设置setMobileDataEnabled方法是否可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);

		}

		catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (NoSuchFieldException e) {

			e.printStackTrace();
		}

		catch (SecurityException e) {
			e.printStackTrace();

		} catch (NoSuchMethodException e)

		{
			e.printStackTrace();
		}

		catch (IllegalArgumentException e) {

			e.printStackTrace();
		}

		catch (IllegalAccessException e) {

			e.printStackTrace();
		}

		catch (InvocationTargetException e)

		{

			e.printStackTrace();

		}

	}

	public boolean isConnected() {
		ConnectivityManager cm;
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ? true : false;
	}

}
