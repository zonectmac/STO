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

	private String syncStatus = "0";// δ�ϴ�
	private ScanDataService scanDataService;
	private String deviceId; // ��ǹID
	private int uploadSize;
	private Context context;

	public ScanDataUploadManage(Context context, String deviceId, int uploadSize) {

		this.deviceId = deviceId;
		scanDataService = new ScanDataService(context);
		this.uploadSize = uploadSize;
		this.context = context;
	}

	/*
	 * ɨ�������ϴ�
	 */
	public EUploadResult UploadScanData(EScanType scanType, ESiteProperties siteProperties) {// 1�������ϴ�����

		EUploadResult uploadResult = EUploadResult.none;
		try {
			@SuppressWarnings("unchecked")
			Class<BaseScanData> onwClass = (Class<BaseScanData>) Class.forName("com.cneop.stoExpress.datacenter.upload." + scanType.toString().trim());
			BaseScanData baseScanData = onwClass.newInstance();
			// ���ð�ǹID
			baseScanData.setDeviceId(deviceId);
			// ���ɨ������
			List<ScanDataModel> list = scanDataService.GetScanData(scanType.value(), String.valueOf(siteProperties.value()), syncStatus, String.valueOf(uploadSize));
			// -------------------------------------------------------------------
			if (baseScanData instanceof ZD) {
				// ���ӷ�����¼
				List<ScanDataModel> packageList = getDataListByPackage(list, scanType, siteProperties);
				setBarcode(packageList);// ���ð���
				List<ScanDataModel> templist = new ArrayList<ScanDataModel>();
				for (ScanDataModel model : list) {
					templist.add(model);
				}
				for (ScanDataModel model : packageList) {
					model.setExpressType("0");
					templist.add(model);
				}
				((ZD) baseScanData).setDataSource_FJ(templist);  // �ѵ�����
			} else if (baseScanData instanceof ZB) {
				// �����ļ�������¼
				// ���ӷ�����������¼ ZB,FJ,FB
				List<ScanDataModel> packageList = getDataListByPackage(list, scanType, siteProperties);
				setBarcode(packageList);// ���ð���
				((ZB) baseScanData).setFBSource(packageList);
				((ZB) baseScanData).setFJSource(packageList);
			} else if (baseScanData instanceof ZC) {
				// װ��������Ӧ���γ������ļ����˴�����һ��FJ��
				((ZC) baseScanData).setDataSource_FJ(list); // e3DataList_ZCFJ
			}
			// --------------------------------------------------Я������
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
				if (errorMsg.contains("�����쳣")) {
					Toast.makeText(context, "���緢���쳣...", 1).show();
					result = EDownError.netError.toString().trim();
				}
			}
			if (EDownError.noError.toString().trim().equals(result)) {
				// �������ݿ�״̬
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
	 * ���������óɰ���
	 */
	private void setBarcode(List<ScanDataModel> list) {

		for (int i = 0; i < list.size(); i++) {
			ScanDataModel model = list.get(i);
			model.setBarcode(model.getPackageCode());
			list.set(i, model);
		}
	}

	/*
	 * װ������
	 */
	private List<ScanDataModel> getDataListByPackage(List<ScanDataModel> list, EScanType scanType, ESiteProperties siteProperties) {

		String minId = String.valueOf(list.get(0).getId());
		String maxId = String.valueOf(list.get(list.size() - 1).getId());
		List<ScanDataModel> packageList = scanDataService.getDataGroupByPackage(scanType.value(), String.valueOf(siteProperties.value()), minId, maxId);

		return packageList;
	}

	/*
	 * δ�ϴ���������
	 */
	public int getUnUploadCount(EScanType scanType, ESiteProperties siteProperties) {

		int count = 0; // �ܼ�¼��
		count = scanDataService.getCount(scanType.value(), String.valueOf(siteProperties.value()), syncStatus);
		return count;
	}
}
