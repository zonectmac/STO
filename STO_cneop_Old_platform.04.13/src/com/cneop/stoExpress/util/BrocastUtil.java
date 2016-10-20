package com.cneop.stoExpress.util;

import android.content.Context;
import android.content.Intent;

import com.cneop.stoExpress.common.Enums.EAddThreadOpt;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.GlobalParas;

/*
 * 广播发送
 */
public class BrocastUtil {
	private Context context;
	public static String TITLE_STATUS_CHANGE_BROCAST = "com.cneop.titleStatusChange"; // 标题广播
	public static String TITLE_IS_UPDATE_UNUPLOAD_COUNT = "updateUnUploadCount"; // 是否更新未上传数量

	public static String TITLE_IS_UPDATE_UNUPLOAD_MSGCOUNT = "updateUnUploadMsgCount"; // 是否更新短信未上传数量
	public static String BLUETOOTH_SCALE_WEIGHT_BROCAST = "bluetooth.scale.weightbrocast";
	public static String TITLE_IS_UPDATE_PIC_STATUS = "updatePicStatus"; // 是否更新上传状态图标
	public static String WEIGHT = "weight";// 重量
	public static String ADD_SCANDATA_ERROR_BROCAST = "com.cneop.scanError"; // 扫描错误提示广播
	public static String ADD_ISREPEAT = "isRepeat"; // 是否重复
	public static String ADD_ERRORMSG = "errorMsg"; // 错误信息
	public static String ADD_TYPE = "addtype";//
	public static String ADD_THREAD_OPT_TYPE = "AddThreadOpt";// 保存数据 的线程操作类型
	public static String ORDER_DOWN_BROCAST = "com.cneop.orderdown";// 订单下载
	public static String ORDER_RESULT = "orderResult";// 订单下载结果

	public static String SCREEN_UPDATE = "updateScreen";// 更新上传界面

	public static String ACTION_INTERCEPT_HOME_KEYS = "ACTION_INTERCEPT_HOME_KEYS";// 屏蔽home键广播
	public static String INTERCEPT_HOME_KEYS_ENABLE = "INTERCEPT_HOME_KEYS_ENABLE";// key

	public static String ACTION_USER_SETDATEANDTIME = "ACTION_USER_SETDATEANDTIME";// 同步时间

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
	 * 是否屏蔽home键广播true屏蔽，false允许
	 */
	public void sendHomeEnableBrocast(boolean isEnable) {
		Intent intent = new Intent(ACTION_INTERCEPT_HOME_KEYS);
		intent.putExtra(INTERCEPT_HOME_KEYS_ENABLE, isEnable);
		context.sendBroadcast(intent);
	}

	/*
	 * 送放插入异常广播：重复和插入数据库错误
	 */
	public void sendAddErrorBrocast(boolean isRepeat, String errorMsg) {
		Intent intent = new Intent(ADD_SCANDATA_ERROR_BROCAST);
		intent.putExtra(ADD_ISREPEAT, isRepeat);
		intent.putExtra(ADD_ERRORMSG, errorMsg);
		context.sendBroadcast(intent);
	}

	/*
	 * 发送标题状态改变广播：包括下传下载状态，未上传数量
	 */
	public void sendTitleChangeBrocast(boolean isUpdateCount, boolean isUpdatePicStatus) {
		Intent intent = new Intent(TITLE_STATUS_CHANGE_BROCAST);
		intent.putExtra(TITLE_IS_UPDATE_UNUPLOAD_COUNT, isUpdateCount);// false
		intent.putExtra(TITLE_IS_UPDATE_UNUPLOAD_MSGCOUNT, isUpdateCount);// false
		intent.putExtra(TITLE_IS_UPDATE_PIC_STATUS, isUpdatePicStatus);// true
		context.sendBroadcast(intent);
	}

	// 发送下载状态广播
	public void sendDownloadPicStatus(boolean isDownloading) {
		GlobalParas.getGlobalParas().setDownloading(isDownloading);
		sendTitleChangeBrocast(false, true);
	}

	// 发送上传状态广播
	public void sendUploadPicStatus(boolean isUploading) {
		GlobalParas.getGlobalParas().setUploading(isUploading);
		sendTitleChangeBrocast(false, true);
	}

	// 未上传数改变
	public void sendUnUploadCountChange(int count) {
		GlobalParas.getGlobalParas().setUnUploadCount(count);
		sendTitleChangeBrocast(true, false);
	}

	/**
	 * 
	 * 送放插入异常广播：重复和插入数据库错误
	 * 
	 * @param isRepeat
	 * @param errorMsg
	 * @param addType
	 *            1:扫描数据;2:短信数据 ====>对应EAddType中的值
	 * @param threadOpt
	 *            :对应EAddThreadOpt中的值
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

	// 未上传数改变
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
	 * 发送蓝牙重量广播
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
