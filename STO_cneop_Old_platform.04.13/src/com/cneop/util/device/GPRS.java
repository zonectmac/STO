package com.cneop.util.device;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ʹ��ʱ��Ҫ��Ȩ�ޣ� <uses-permission
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

	// �ƶ����ݿ����͹ر�
	private void setMobileDataStatus(Context context, boolean enabled)

	{

		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ConnectivityManager��

		Class<?> conMgrClass = null;

		// ConnectivityManager���е��ֶ�
		Field iConMgrField = null;
		// IConnectivityManager�������
		Object iConMgr = null;
		// IConnectivityManager��
		Class<?> iConMgrClass = null;
		// setMobileDataEnabled����
		Method setMobileDataEnabledMethod = null;
		try {

			// ȡ��ConnectivityManager��
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// ȡ��ConnectivityManager���еĶ���Mservice
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// ����mService�ɷ���
			iConMgrField.setAccessible(true);
			// ȡ��mService��ʵ������IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// ȡ��IConnectivityManager��
			iConMgrClass = Class.forName(iConMgr.getClass().getName());

			// ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

			// ����setMobileDataEnabled�����Ƿ�ɷ���
			setMobileDataEnabledMethod.setAccessible(true);
			// ����setMobileDataEnabled����
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
