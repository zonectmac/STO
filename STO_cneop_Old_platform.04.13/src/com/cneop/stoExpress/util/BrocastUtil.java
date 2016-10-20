package com.cneop.stoExpress.util;

import android.content.Context;
import android.content.Intent;

import com.cneop.stoExpress.common.Enums.EAddThreadOpt;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.GlobalParas;

/*
 * �㲥����
 */
public class BrocastUtil {
	private Context context;
	public static String TITLE_STATUS_CHANGE_BROCAST = "com.cneop.titleStatusChange"; // ����㲥
	public static String TITLE_IS_UPDATE_UNUPLOAD_COUNT = "updateUnUploadCount"; // �Ƿ����δ�ϴ�����

	public static String TITLE_IS_UPDATE_UNUPLOAD_MSGCOUNT = "updateUnUploadMsgCount"; // �Ƿ���¶���δ�ϴ�����
	public static String BLUETOOTH_SCALE_WEIGHT_BROCAST = "bluetooth.scale.weightbrocast";
	public static String TITLE_IS_UPDATE_PIC_STATUS = "updatePicStatus"; // �Ƿ�����ϴ�״̬ͼ��
	public static String WEIGHT = "weight";// ����
	public static String ADD_SCANDATA_ERROR_BROCAST = "com.cneop.scanError"; // ɨ�������ʾ�㲥
	public static String ADD_ISREPEAT = "isRepeat"; // �Ƿ��ظ�
	public static String ADD_ERRORMSG = "errorMsg"; // ������Ϣ
	public static String ADD_TYPE = "addtype";//
	public static String ADD_THREAD_OPT_TYPE = "AddThreadOpt";// �������� ���̲߳�������
	public static String ORDER_DOWN_BROCAST = "com.cneop.orderdown";// ��������
	public static String ORDER_RESULT = "orderResult";// �������ؽ��

	public static String SCREEN_UPDATE = "updateScreen";// �����ϴ�����

	public static String ACTION_INTERCEPT_HOME_KEYS = "ACTION_INTERCEPT_HOME_KEYS";// ����home���㲥
	public static String INTERCEPT_HOME_KEYS_ENABLE = "INTERCEPT_HOME_KEYS_ENABLE";// key

	public static String ACTION_USER_SETDATEANDTIME = "ACTION_USER_SETDATEANDTIME";// ͬ��ʱ��

	public BrocastUtil(Context context) {
		this.context = context;
	}

	/*
	 * 
	 */
	public void sendSysTimeSyncBrocast(int year, int month, int day, int hour, int minute) {
		Intent intent = new Intent(ACTION_USER_SETDATEANDTIME);
		intent.putExtra("USER_SETDATEANDTIME_YEAR", year);
		intent.putExtra("USER_SETDATEANDTIME_MONTH", month);
		intent.putExtra("USER_SETDATEANDTIME_DAY", day);
		intent.putExtra("USER_SETDATEANDTIME_HOUR", hour);
		intent.putExtra("USER_SETDATEANDTIME_MINUTE", minute);
		context.sendBroadcast(intent);
	}

	/*
	 * �Ƿ�����home���㲥true���Σ�false����
	 */
	public void sendHomeEnableBrocast(boolean isEnable) {
		Intent intent = new Intent(ACTION_INTERCEPT_HOME_KEYS);
		intent.putExtra(INTERCEPT_HOME_KEYS_ENABLE, isEnable);
		context.sendBroadcast(intent);
	}

	/*
	 * �ͷŲ����쳣�㲥���ظ��Ͳ������ݿ����
	 */
	public void sendAddErrorBrocast(boolean isRepeat, String errorMsg) {
		Intent intent = new Intent(ADD_SCANDATA_ERROR_BROCAST);
		intent.putExtra(ADD_ISREPEAT, isRepeat);
		intent.putExtra(ADD_ERRORMSG, errorMsg);
		context.sendBroadcast(intent);
	}

	/*
	 * ���ͱ���״̬�ı�㲥�������´�����״̬��δ�ϴ�����
	 */
	public void sendTitleChangeBrocast(boolean isUpdateCount, boolean isUpdatePicStatus) {
		Intent intent = new Intent(TITLE_STATUS_CHANGE_BROCAST);
		intent.putExtra(TITLE_IS_UPDATE_UNUPLOAD_COUNT, isUpdateCount);// false
		intent.putExtra(TITLE_IS_UPDATE_UNUPLOAD_MSGCOUNT, isUpdateCount);// false
		intent.putExtra(TITLE_IS_UPDATE_PIC_STATUS, isUpdatePicStatus);// true
		context.sendBroadcast(intent);
	}

	// ��������״̬�㲥
	public void sendDownloadPicStatus(boolean isDownloading) {
		GlobalParas.getGlobalParas().setDownloading(isDownloading);
		sendTitleChangeBrocast(false, true);
	}

	// �����ϴ�״̬�㲥
	public void sendUploadPicStatus(boolean isUploading) {
		GlobalParas.getGlobalParas().setUploading(isUploading);
		sendTitleChangeBrocast(false, true);
	}

	// δ�ϴ����ı�
	public void sendUnUploadCountChange(int count) {
		GlobalParas.getGlobalParas().setUnUploadCount(count);
		sendTitleChangeBrocast(true, false);
	}

	/**
	 * 
	 * �ͷŲ����쳣�㲥���ظ��Ͳ������ݿ����
	 * 
	 * @param isRepeat
	 * @param errorMsg
	 * @param addType
	 *            1:ɨ������;2:�������� ====>��ӦEAddType�е�ֵ
	 * @param threadOpt
	 *            :��ӦEAddThreadOpt�е�ֵ
	 */
	public void sendAddErrorBrocast(boolean isRepeat, String errorMsg, int addType, EAddThreadOpt threadOpt) {
		Intent intent = new Intent(ADD_SCANDATA_ERROR_BROCAST);
		intent.putExtra(ADD_ISREPEAT, isRepeat);
		intent.putExtra(ADD_ERRORMSG, errorMsg);
		intent.putExtra(ADD_TYPE, addType);
		intent.putExtra(ADD_THREAD_OPT_TYPE, threadOpt.value());

		context.sendBroadcast(intent);
	}

	public void sendScreenUpdate(boolean isUpdateScreen) {
		Intent intent = new Intent(SCREEN_UPDATE);
		intent.putExtra(SCREEN_UPDATE, isUpdateScreen);
		context.sendBroadcast(intent);
	}

	// δ�ϴ����ı�
	public void sendUnUploadCountChange(int count, EUploadType uploadType) {
		switch (uploadType) {
		case scanData:
			GlobalParas.getGlobalParas().setUnUploadCount(count);
			break;
		case pic:
			GlobalParas.getGlobalParas().setPicUnUploadCount(count);
			break;
		case msg:
			GlobalParas.getGlobalParas().setMsgUnUploadCount(count);
			break;
		case order:
			GlobalParas.getGlobalParas().setOrderUnUploadCount(count);
			break;
		default:
			break;
		}
		sendTitleChangeBrocast(true, false);
	}

	/**
	 * �������������㲥
	 * 
	 * @param weight
	 */
	public void sendWeightBrocast(String weight) {
		Intent intent = new Intent(BLUETOOTH_SCALE_WEIGHT_BROCAST);
		intent.putExtra(WEIGHT, weight);
		context.sendBroadcast(intent);
	}

	public void sendOrderDownResultBrocast(int result) {
		Intent intent = new Intent(ORDER_DOWN_BROCAST);
		intent.putExtra(ORDER_RESULT, result);
		context.sendBroadcast(intent);

	}
}
