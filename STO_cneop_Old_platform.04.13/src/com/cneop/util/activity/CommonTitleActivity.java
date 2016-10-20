package com.cneop.util.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.stoExpress.cneops.R;
import com.cneop.util.StrUtil;
import com.cneop.util.model.Enums.EToolbarUploadStatus;
import com.cneop.util.scan.V6ScanManager.IScanResult;

public class CommonTitleActivity extends BaseActivity {

	private ImageView toolbar_imgvStatus;
	private TextView toolbar_txttittle, toolbar_tvUnupload, toolbar_tvUnuploadmsg;
	private int iImageIndex = 0;
	protected int delIndex = -1;
	protected final boolean DEBUG = true;
	private Timer timerToolbarStatus = null;// 工具栏的图片
	private String mStrTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUnuploadCount();
		setMsgUnuploadCount();
		setUploadStatus();
		registerReceiver(titleStatusReceiver, new IntentFilter(BrocastUtil.TITLE_STATUS_CHANGE_BROCAST));
	}

	// 状态栏广播接收器
	private BroadcastReceiver titleStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isUpdateUnUploadCount = intent.getBooleanExtra(BrocastUtil.TITLE_IS_UPDATE_UNUPLOAD_COUNT, true);
			if (isUpdateUnUploadCount) {
				setUnuploadCount();
			}
			boolean isUpdatePicStatus = intent.getBooleanExtra(BrocastUtil.TITLE_IS_UPDATE_PIC_STATUS, true);
			if (isUpdatePicStatus) {
				setUploadStatus();
			}
			boolean isUpdateUnUploadMsgCount = intent.getBooleanExtra(BrocastUtil.TITLE_IS_UPDATE_UNUPLOAD_MSGCOUNT, true);
			if (isUpdateUnUploadMsgCount) {
				setMsgUnuploadCount();
				setUnuploadCount();
			}
		}
	};

	public void setUnuploadCount() {

		String countStr = "";
		if (GlobalParas.getGlobalParas().getOrderUnUploadCount() > 0) {
			countStr = "订单:" + GlobalParas.getGlobalParas().getOrderUnUploadCount();
		} else if (GlobalParas.getGlobalParas().getUnUploadCount() > 0) {
			countStr = GlobalParas.getGlobalParas().getUnUploadCount() + "票";
		} else if (GlobalParas.getGlobalParas().getPicUnUploadCount() > 0) {
			countStr = "图片:" + GlobalParas.getGlobalParas().getPicUnUploadCount();
		}
		toolbar_tvUnupload.setText(String.valueOf(countStr));// 票
	}

	private void setMsgUnuploadCount() {

		if (GlobalParas.getGlobalParas().getMsgUnUploadCount() > 0) {
			toolbar_tvUnuploadmsg.setText("信:" + GlobalParas.getGlobalParas().getMsgUnUploadCount());
		} else {
			toolbar_tvUnuploadmsg.setText("");// 信
		}

	}

	public void setUploadStatus() {

		// 获得当前状态
		final EToolbarUploadStatus eStatus = getCurrentUploadStatus();
		// 正常则停止定时器
		if (eStatus == EToolbarUploadStatus.Normal) {// 正常
			toolbar_imgvStatus.setImageResource(R.drawable.down_up_normal);
			toolbar_imgvStatus.invalidate();
			if (timerToolbarStatus != null) {
				timerToolbarStatus.cancel();
				timerToolbarStatus = null;
			}
			return;
		}

		if (timerToolbarStatus == null) {
			timerToolbarStatus = new Timer();
			final int imgMaxArrange = (eStatus == EToolbarUploadStatus.DownAndUploading) ? 3 : 4;// 此值是由图片数量决定的(如：上传且下载时，只有3个可显示的图片，其他情况有4个)
			timerToolbarStatus.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {

					Message mesasge = new Message();
					mesasge.arg1 = eStatus.value();// 如果子类的Activity，需要用到handle时，可以共用，以此参数区分
					mesasge.what = iImageIndex;
					// handler.sendMessage(mesasge);
					iImageIndex++;
					if (iImageIndex >= imgMaxArrange) {
						iImageIndex = 0;
					}
				}
			}, 0, 1000);
		}
	}

	@Override
	public void setContentView(int paramInt) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(paramInt);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, com.cneop.stoExpress.cneops.R.layout.activity_toolbar);
		toolbar_imgvStatus = (ImageView) findViewById(R.id.toolbar_imgvStatus);// 图标
		toolbar_txttittle = (TextView) findViewById(R.id.toolbar_txttittle);// 标题
		toolbar_tvUnupload = (TextView) findViewById(R.id.toolbar_tvUnupload);// 票
		toolbar_tvUnuploadmsg = (TextView) findViewById(R.id.toolbar_tvUnuploadmsg);// 信
	}

	public void setTitle(String text) {

		this.mStrTitle = text;
		toolbar_txttittle.setText(this.mStrTitle);// 设置标题
	}

	@Override
	protected void onDestroy() {
		try {
			if (timerToolbarStatus != null) {
				timerToolbarStatus.cancel();
				timerToolbarStatus = null;
			}
			unregisterReceiver(titleStatusReceiver);
			super.onDestroy();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * 获得当前上传状态
	 */
	private EToolbarUploadStatus getCurrentUploadStatus() {

		EToolbarUploadStatus eStatus = EToolbarUploadStatus.Normal;// 正常

		if (GlobalParas.getGlobalParas().isDownloading() == false && GlobalParas.getGlobalParas().isUploading() == false) {
			eStatus = EToolbarUploadStatus.Normal;
		} else if (GlobalParas.getGlobalParas().isDownloading() == true && GlobalParas.getGlobalParas().isUploading() == true) {
			eStatus = EToolbarUploadStatus.DownAndUploading;
		} else if (GlobalParas.getGlobalParas().isDownloading() == true) {
			eStatus = EToolbarUploadStatus.Downing;
		} else if (GlobalParas.getGlobalParas().isUploading() == true) {
			eStatus = EToolbarUploadStatus.Uploading;
		}
		return eStatus;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.arg1 > 3) { // 如果是其他子类要共用此handle，在要在此处改写
				return;
			}
			EToolbarUploadStatus uploadStatus = EToolbarUploadStatus.valueOf(msg.arg1);
			int imageIndex = msg.what;
			doShowDownAndUpImg(uploadStatus, imageIndex);
			super.handleMessage(msg);
		}
	};

	private void doShowDownAndUpImg(EToolbarUploadStatus uploadStatus, int imageIndex) {

		int resId = R.drawable.down_up_normal;
		switch (uploadStatus) {
		case Downing:
			switch (imageIndex) {
			case 0:
				resId = R.drawable.download0;
				break;
			case 1:
				resId = R.drawable.download1;
				break;
			case 2:
				resId = R.drawable.download2;
				break;
			case 3:
				resId = R.drawable.download3;
				break;
			}
			break;
		case DownAndUploading:
			switch (imageIndex) {
			case 0:
				resId = R.drawable.network_downloading;
				break;
			case 1:
				resId = R.drawable.network_up_dow;
				break;
			case 2:
				resId = R.drawable.network_uploading;
				break;
			}
			break;
		case Uploading:
			switch (imageIndex) {
			case 0:
				resId = R.drawable.upload0;
				break;
			case 1:
				resId = R.drawable.upload1;
				break;
			case 2:
				resId = R.drawable.upload2;
				break;
			case 3:
				resId = R.drawable.upload3;
				break;
			}
			break;
		}
		toolbar_imgvStatus.setImageResource(resId);
		toolbar_imgvStatus.invalidate();
	}

	protected IScanResult handleScanData = new IScanResult() {

		@Override
		public void HandleScanResult(String barcode) {

			barcode = StrUtil.FilterSpecial(barcode);
			setScanData(barcode);
		}
	};

	protected void setScanData(String bar) {

	}
}
