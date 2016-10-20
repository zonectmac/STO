package com.cneop.stoExpress.datacenter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EAddThreadOpt;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.SJMDSendedRecordsDao;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.model.SJMDSendedRecordsVO;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.CheckUtils;

public class AddScanDataThread extends Thread {

	private List<Object> scanDataList;// 添加集合
	private List<Object> executeList;// 执行列表
	private static Object List_LOCK = new Object();
	public Handler addScanDataHandler;
	private ScanDataService scanDataService;
	private MsgSendService msgSendService;
	private BrocastUtil brocastUtil;
	private SJMDSendedRecordsDao sJMDSendedRecordsDao;

	private static Context context;
	private static String ADD_THREAD_TAG = "AddScanDataThread";

	private AddScanDataThread(Context context) {
		scanDataList = new ArrayList<Object>();
		executeList = new ArrayList<Object>();
		scanDataService = new ScanDataService(context);
		brocastUtil = new BrocastUtil(context);
		msgSendService = new MsgSendService(context);
	}

	public static AddScanDataThread addScanDataIntance;

	/*
	 * 获得实例
	 */
	public static AddScanDataThread getIntance(Context contextParam) {
		if (addScanDataIntance == null) {
			addScanDataIntance = new AddScanDataThread(contextParam);
			context = contextParam;
		}
		return addScanDataIntance;
	}

	/*
	 * 启动线程
	 */
	public void startAddScanData() {
		if (addScanDataIntance.getState() == Thread.State.NEW) {
			addScanDataIntance.start();
		}
	}

	/*
	 * 添加记录
	 */
	public void add(Object obj) {
		synchronized (List_LOCK) {
			scanDataList.add(obj);
		}
		addScanDataHandler.sendEmptyMessage(0);
	}

	@Override
	public void run() {
		this.setName("addScanDataThread");
		Looper.prepare();
		addScanDataHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				synchronized (List_LOCK) {
					executeList.addAll(0, scanDataList);
					scanDataList.removeAll(executeList);
				}
				if (executeList.size() > 0) {
					for (Object obj : executeList) {
						if (obj instanceof ScanDataModel) {
							ScanDataModel model = (ScanDataModel) obj;
							addScanData(model);
						} else if (obj instanceof MsgSend) {
							MsgSend msgSendModel = (MsgSend) obj;
							addMsg(msgSendModel);
						}
					}
					executeList.clear();
				}
			}
		};
		Looper.loop();
	}

	public boolean isExists(ScanDataModel model) {
		boolean isRepeat = false;
		String condition = "";
		if (model.getScanCode().equals(EScanType.SJ.value())) {
			condition = " and courier='" + model.getCourier() + "'";
		}
		// 单号,scanType.value()， 员工编号
		if (scanDataService.isBarcodeRepeat(model.getBarcode(), model.getScanCode(), condition) > 0) {
			isRepeat = true;

		}
		return isRepeat;
	}

	/**
	 * 增加扫描数据
	 * 
	 * @param model
	 */
	private void addScanData(ScanDataModel model) {
		// 判断是否重复
		// 业务员和扫描员收件可重复扫描，用courier字段来判断
		if (isExists(model)) {
			// 重复,界面提示发送广播
			brocastUtil.sendAddErrorBrocast(true, model.getBarcode(), 1, EAddThreadOpt.RepeatWhenAdd);
		} else if (model.getScanCode().equals(EScanType.SJ.value())) {
			doAddRecord(model);// 此处必须先保存到DB中
			if (GlobalParas.getGlobalParas().isMdControlIsOpen() && !doHandleSJMD(model.getBarcode())) {// 如果不在发放记录范围内，则提示，
				brocastUtil.sendAddErrorBrocast(true, model.getBarcode(), 1, EAddThreadOpt.SJMD);
			}

		} else {
			// 执行插入
			doAddRecord(model);
		}
	}

	private void doAddRecord(ScanDataModel model) {
		if (scanDataService.addRecord(model) > 0) {
			// 插入成功，更新界面记录显示
			brocastUtil.sendUnUploadCountChange(1, EUploadType.scanData);
		} else {
			// 失败，界面提示发送广播
			brocastUtil.sendAddErrorBrocast(false, "");
		}
	}

	/**
	 * 判断是否在收件面单发放记录的范围内（仅适用于业务员与扫描员的 收件）
	 * 
	 * @return
	 */
	private boolean doHandleSJMD(String barcode) {
		boolean flag = false;
		List<Object> lstModels = GlobalParas.getGlobalParas().getLstSJMDSenedModels();
		try {
			if (sJMDSendedRecordsDao == null) {
				sJMDSendedRecordsDao = new SJMDSendedRecordsDao(context);
			}
			SJMDSendedRecordsVO model = new SJMDSendedRecordsVO();

			if (lstModels == null) {
				String[] words = new String[] { "billcodebegin is not null and billcodebegin!='" };
				lstModels = sJMDSendedRecordsDao.getListObj(words, null, model);

				GlobalParas.getGlobalParas().setLstSJMDSenedModels(lstModels);
			}
			long billcodebegin = 0;
			long billcodeend = 0;
			long longBarcode = 0;
			if (lstModels.size() > 0) {
				for (Object obj : lstModels) {
					model = (SJMDSendedRecordsVO) obj;

					if (CheckUtils.isNumeric(model.getBillcodebegin()) && CheckUtils.isNumeric(model.getBillcodeend()) && CheckUtils.isNumeric(barcode)) {
						longBarcode = Long.valueOf(barcode);
						billcodebegin = Long.valueOf(model.getBillcodebegin());
						billcodeend = Long.valueOf(model.getBillcodeend());
						if (longBarcode >= billcodebegin && longBarcode <= billcodeend) {
							flag = true;
							break;
						}
						return flag;
					}

					// 条码不是纯数字的情况
					char[] charArrayBegin = model.getBillcodebegin().toCharArray();
					char[] charArrayEnd = model.getBillcodeend().toCharArray();
					if (charArrayBegin.length != charArrayEnd.length) {
						StringBuilder sbMsg = new StringBuilder();
						sbMsg.append("收件发放记录开始-结束单号长度不一致，分别是:");
						sbMsg.append(model.getBillcodebegin()).append(",");
						sbMsg.append(model.getBillcodeend()).append(",");

						Log.w(ADD_THREAD_TAG, sbMsg.toString());
						continue;
					}

					StringBuilder sbCommonPre = new StringBuilder();
					int commIndex = 0;
					for (int charIndex = 0; charIndex < charArrayBegin.length; charIndex++) {
						if (!String.valueOf(charArrayBegin[charIndex]).equals(String.valueOf(charArrayEnd[charIndex]))) {
							commIndex = charIndex;
							break;
						}
						sbCommonPre.append(charArrayBegin[charIndex]);
					}

					if (!barcode.substring(0, commIndex).equals(sbCommonPre.toString())) {
						continue;
					}
					String BarcodeBegin = model.getBillcodebegin();
					String BarcodeEnd = model.getBillcodeend();

					String endBarcode = barcode.substring(commIndex, barcode.length() - commIndex);
					String endBarBegin = BarcodeBegin.substring(commIndex, BarcodeBegin.length() - commIndex);
					String endBarEnd = BarcodeEnd.substring(commIndex, BarcodeEnd.length() - commIndex);

					if (!CheckUtils.isNumeric(endBarcode) || !CheckUtils.isNumeric(endBarBegin) || !CheckUtils.isNumeric(endBarEnd)) {
						continue;
					}

					long barcodeBegin = Long.valueOf(endBarcode);// row["BarcodeBegin"].ToString();
					long barcodeEnd = Long.valueOf(endBarBegin);
					long barcodeScan = Long.valueOf(endBarcode);
					if (barcodeScan >= barcodeBegin && barcodeScan <= barcodeEnd) {
						flag = true;
						break;
					}

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return flag;
	}

	/**
	 * 插入短信
	 * 
	 * @param model
	 */
	private void addMsg(MsgSend model) {
		// 查找是否存在相同的手机号和服务点
		String[] words = { " and issynchronization=0 and barcode=?", " and phone=?", " and serverNo=?" };
		String[] selectArgs = { model.getBarcode(), model.getPhone(), model.getServerNo() };
		Object obj = msgSendService.getSingleObj(words, selectArgs, new MsgSend());
		if (obj == null) {
			// 不存在则新增
			if (msgSendService.addRecord(model) > 0) {
				// 插入成功，更新界面记录显示
				brocastUtil.sendUnUploadCountChange(1, EUploadType.msg);
			} else {
				// 失败，界面提示发送广播
				brocastUtil.sendAddErrorBrocast(false, "");
			}
		}
		if (obj != null) {
			brocastUtil.sendAddErrorBrocast(true, model.getBarcode(), 2, EAddThreadOpt.RepeatWhenAdd);
		}
	}

}
