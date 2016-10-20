package com.cneop.stoExpress.datacenter;

import java.io.File;

import com.cneop.stoExpress.common.Enums.EPicType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUploadResult;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.upload.ImageUploadManage;
import com.cneop.stoExpress.datacenter.upload.MsgUploadManage;
import com.cneop.stoExpress.datacenter.upload.OrderUploadManage;
import com.cneop.stoExpress.datacenter.upload.ScanDataUploadManage;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.file.FileUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class UploadThread extends Thread {

	public static UploadThread uploadThread;
	public Handler uploadHandler;
	public static String uploadTypeKey = "uploadType";
	public static String scanTypeKey = "scanType";
	public static String sitePropertiesKey = "siteProperties";
	public static String imageTypeKey = "imageType";
	private int uploadSize = 200;// ÿ���ϴ�Ʊ��
	private BrocastUtil brocastUtil;
	private StrUtil strUtil;
	ScanDataUploadManage scanDataUploadManage;
	public ImageUploadManage imageUploadManage;
	public MsgUploadManage msgUploadManage;
	private OrderUploadManage orderUploadManage;
	private EScanType[] scanTypeArray = { EScanType.SJ, EScanType.FJ, EScanType.DJ, EScanType.PJ, EScanType.LC,
			EScanType.YN, EScanType.QS, EScanType.ZD, EScanType.ZB, EScanType.FB, EScanType.DB, EScanType.FC,
			EScanType.DC, EScanType.ZC };

	private UploadThread(Context context) {
		brocastUtil = new BrocastUtil(context);
		strUtil = new StrUtil();
		scanDataUploadManage = new ScanDataUploadManage(context, GlobalParas.getGlobalParas().getDeviceId(),
				uploadSize);
		imageUploadManage = new ImageUploadManage(GlobalParas.getGlobalParas().getImageUploadUrl(),
				GlobalParas.getGlobalParas().getStationId(), GlobalParas.getGlobalParas().getUserNo(),
				GlobalParas.getGlobalParas().getCompanyCode());
		msgUploadManage = new MsgUploadManage(GlobalParas.getGlobalParas().getSmsSendUrl(), context);
		orderUploadManage = new OrderUploadManage(context);
	}

	/*
	 * ����
	 */
	public static UploadThread getIntance(Context context) {
		if (uploadThread == null) {
			uploadThread = new UploadThread(context);
		}
		return uploadThread;
	}

	/*
	 * �����߳�
	 */
	public void startUpload() {
		if (uploadThread.getState() == Thread.State.NEW) {
			uploadThread.start();
		}
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void run() {
		this.setName("UploadThread");
		Looper.prepare();
		uploadHandler = new Handler() {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void handleMessage(Message msg) {
				// �������ݲ���
				try {
					// ���͹㲥�����ϴ�ͼ��
					// brocastUtil.sendUploadPicStatus(true);
					Bundle b = msg.getData();// Bundle[{siteProperties=C_N,scanType=ZD,uploadType=scanData}]
					EUploadType uploadType = (EUploadType) b.getSerializable(uploadTypeKey);// scanData
					switch (uploadType) {
					case scanData:
						EScanType scanType = (EScanType) b.getSerializable(scanTypeKey);// ****�����ϴ�����
						ESiteProperties siteProperties = (ESiteProperties) b.getSerializable(sitePropertiesKey);
						if (scanType == EScanType.ALL) {
							for (int i = 0; i < scanTypeArray.length; i++) {
								uploadData(scanTypeArray[i], siteProperties);
								Thread.sleep(200);
							}
						} else {
							uploadData(scanType, siteProperties);
						}
						break;
					case msg:
						uploadMsg();
						break;
					case pic:
						EPicType picType = (EPicType) b.getSerializable(imageTypeKey);
						uploadImage(picType);
						break;
					case order:
						uploadOrder();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// ���͹㲥�����ϴ�ͼ��
					brocastUtil.sendUploadPicStatus(false);
					// ���͹㲥�����ϴ�ͼ��
					brocastUtil.sendScreenUpdate(true);
				}

			}
		};
		Looper.loop();
		// }
	}

	/**
	 * �ϴ�����
	 */
	protected void uploadOrder() {
		int count = orderUploadManage.uploadOrder();
		if (count > 0) {
			brocastUtil.sendUnUploadCountChange(-count, EUploadType.order);
		}
	}

	/**
	 * �ϴ�ͼƬ
	 * 
	 * @param picType
	 */
	protected void uploadImage(EPicType picType) {

		String unUploadImagePath = "";
		String uploadedImagePath = "";
		if (picType == EPicType.sign) {
			unUploadImagePath = GlobalParas.getGlobalParas().getSignUnUploadPath();
			uploadedImagePath = GlobalParas.getGlobalParas().getSignUploadPath();
		} else if (picType == EPicType.problem) {
			unUploadImagePath = GlobalParas.getGlobalParas().getProblemUnUploadPath();
			uploadedImagePath = GlobalParas.getGlobalParas().getProblemUploadPath();
		}
		File unUploadFile = new File(unUploadImagePath);
		File[] unUploadFiles = unUploadFile.listFiles();
		if (unUploadFiles != null && unUploadFiles.length > 0) {
			for (int i = 0; i < unUploadFiles.length; i++) {
				String absoluteFilePath = unUploadFiles[i].getAbsolutePath();
				int t = imageUploadManage.uploadImage(absoluteFilePath, String.valueOf(picType.value()));
				if (t > 0) {
					// ��SD���ű��ݣ�û��ֱ��ɾ��
					if (!strUtil.isNullOrEmpty(uploadedImagePath)) {
						String barcode = absoluteFilePath.substring(absoluteFilePath.lastIndexOf("/") + 1,
								absoluteFilePath.lastIndexOf("."));
						FileUtil.copyFile(absoluteFilePath, uploadedImagePath + barcode + ".jpg");
					}
					unUploadFiles[i].delete();
					// δ�ϴ����ı�
					brocastUtil.sendUnUploadCountChange(-t, EUploadType.pic);
				}
			}
		}
	}

	/**
	 * ɨ�������ϴ�
	 */
	protected void uploadData(EScanType scanType, ESiteProperties siteProperties) {
		int count = scanDataUploadManage.getUnUploadCount(scanType, siteProperties);// 1
		int num = count / uploadSize;
		int lastTimeUploadCount = count % uploadSize;// ���һ���ϴ�������//1
		if (lastTimeUploadCount != 0) {
			num++;
		}
		int uploadCount = 0;
		for (int i = 0; i < num; i++) {
			// ok
			EUploadResult uploadResult = scanDataUploadManage.UploadScanData(scanType, siteProperties);
			if (uploadResult == EUploadResult.ok) {
				// ���͹㲥,���¼�¼
				uploadCount = uploadSize;
				if (i == num - 1 && lastTimeUploadCount != 0) {
					uploadCount = lastTimeUploadCount;
				}
				// δ�ϴ����ı�
				brocastUtil.sendUnUploadCountChange(-uploadCount, EUploadType.scanData);
			}

		}
		if (scanType == EScanType.YN) {// ������ϴ��������ǩ�գ���Ҫ���ͼƬ�Ƿ����
			uploadImage(EPicType.problem);
		} else if (scanType == EScanType.QS) {
			uploadImage(EPicType.sign);
		}
	}

	/**
	 * �ϴ�����
	 */
	protected void uploadMsg() {
		int count = msgUploadManage.uploadMsgData();
		if (count > 0) {
			// δ�ϴ����ı�
			brocastUtil.sendUnUploadCountChange(-count, EUploadType.msg);
		}
	}

}
