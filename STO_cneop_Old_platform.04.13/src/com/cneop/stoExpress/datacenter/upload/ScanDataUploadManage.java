package com.cneop.stoExpress.datacenter.upload;

import java.util.ArrayList;
import java.util.List;
import android.R.integer;
import android.content.Context;
import android.widget.Toast;
import com.cneop.stoExpress.common.Enums.EDownError;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUploadResult;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.model.ScanDataModel;

public class ScanDataUploadManage {

	private String syncStatus = "0";// 未上传
	private ScanDataService scanDataService;
	private String deviceId; // 巴枪ID
	private int uploadSize;
	private Context context;

	public ScanDataUploadManage(Context context, String deviceId, int uploadSize) {

		this.deviceId = deviceId;
		scanDataService = new ScanDataService(context);
		this.uploadSize = uploadSize;
		this.context = context;
	}

	/*
	 * 扫描数据上传
	 */
	public EUploadResult UploadScanData(EScanType scanType, ESiteProperties siteProperties) {// 1，区分上传数据

		EUploadResult uploadResult = EUploadResult.none;
		try {
			@SuppressWarnings("unchecked")
			Class<BaseScanData> onwClass = (Class<BaseScanData>) Class.forName("com.cneop.stoExpress.datacenter.upload." + scanType.toString().trim());
			BaseScanData baseScanData = onwClass.newInstance();
			// 设置巴枪ID
			baseScanData.setDeviceId(deviceId);
			// 获得扫描数据
			List<ScanDataModel> list = scanDataService.GetScanData(scanType.value(), String.valueOf(siteProperties.value()), syncStatus, String.valueOf(uploadSize));
			// -------------------------------------------------------------------
			if (baseScanData instanceof ZD) {
				// 增加发件记录
				List<ScanDataModel> packageList = getDataListByPackage(list, scanType, siteProperties);
				setBarcode(packageList);// 设置包号
				List<ScanDataModel> templist = new ArrayList<ScanDataModel>();
				for (ScanDataModel model : list) {
					templist.add(model);
				}
				for (ScanDataModel model : packageList) {
					model.setExpressType("0");
					templist.add(model);
				}
				((ZD) baseScanData).setDataSource_FJ(templist);  // 把单填入
			} else if (baseScanData instanceof ZB) {
				// 三个文件四条记录
				// 增加发件、发包记录 ZB,FJ,FB
				List<ScanDataModel> packageList = getDataListByPackage(list, scanType, siteProperties);
				setBarcode(packageList);// 设置包号
				((ZB) baseScanData).setFBSource(packageList);
				((ZB) baseScanData).setFJSource(packageList);
			} else if (baseScanData instanceof ZC) {
				// 装车发件，应该形成两个文件；此处增加一个FJ。
				((ZC) baseScanData).setDataSource_FJ(list); // e3DataList_ZCFJ
			}
			// --------------------------------------------------携带参数
			UpContentStructure cs = new UpContentStructure();
			cs.setScanType(scanType.toString().trim());
			cs.setExpressType(siteProperties.toString().trim());
			cs.setDataList(baseScanData.getScanData(list));
			// ----------------------------------------------------------
			if (baseScanData instanceof ZB) {
				cs.setDataList_FJ(baseScanData.getScanData_HKFJ(list));
				cs.setDataList_FB(baseScanData.getScanData_HKFB(list));
			}
			if (baseScanData instanceof ZC) {
				cs.setDataList_FJ(baseScanData.getScanData_ZCFJ(list));
			}
			if (baseScanData instanceof ZD) {
				cs.setDataList_FJ(baseScanData.getScanData_ZDFJ(list));
			}
			// ---------------------------------------------------------
			String result = "";
			try {
				result = MSDServer.getInstance(context).uploadData(cs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				String errorMsg = e.getMessage();
				if (errorMsg.contains("网络异常")) {
					Toast.makeText(context, "网络发生异常...", 1).show();
					result = EDownError.netError.toString().trim();
				}
			}
			if (EDownError.noError.toString().trim().equals(result)) {
				// 更新数据库状态
				int t = scanDataService.updateStatus(list);
				if (t > 0) {
					uploadResult = EUploadResult.ok;
				}
			} else if (EDownError.netError.toString().trim().equals(result)) {
				uploadResult = EUploadResult.neterror;
			} else {
				uploadResult = EUploadResult.fail;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return uploadResult;
	}

	/*
	 * 将单号设置成包号
	 */
	private void setBarcode(List<ScanDataModel> list) {

		for (int i = 0; i < list.size(); i++) {
			ScanDataModel model = list.get(i);
			model.setBarcode(model.getPackageCode());
			list.set(i, model);
		}
	}

	/*
	 * 装包数据
	 */
	private List<ScanDataModel> getDataListByPackage(List<ScanDataModel> list, EScanType scanType, ESiteProperties siteProperties) {

		String minId = String.valueOf(list.get(0).getId());
		String maxId = String.valueOf(list.get(list.size() - 1).getId());
		List<ScanDataModel> packageList = scanDataService.getDataGroupByPackage(scanType.value(), String.valueOf(siteProperties.value()), minId, maxId);

		return packageList;
	}

	/*
	 * 未上传数据数量
	 */
	public int getUnUploadCount(EScanType scanType, ESiteProperties siteProperties) {

		int count = 0; // 总记录数
		count = scanDataService.getCount(scanType.value(), String.valueOf(siteProperties.value()), syncStatus);
		return count;
	}
}
