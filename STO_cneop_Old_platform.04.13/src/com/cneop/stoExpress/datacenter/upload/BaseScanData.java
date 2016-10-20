package com.cneop.stoExpress.datacenter.upload;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cneop.stoExpress.model.ScanDataModel;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
abstract class BaseScanData {

	protected List<E3ScanDataFormat> e3DataList;
	protected List<E3ScanDataFormat> e3DataList_HKFJ; // ���շ�����.�򺽿�1����¼3���ļ�
	protected List<E3ScanDataFormat> e3DataList_HKFB; // ���շ�����.�򺽿�1����¼3���ļ�
	protected List<E3ScanDataFormat> e3DataList_ZCFJ;
	protected List<E3ScanDataFormat> e3DataList_ZDFJ; // װ��������

	protected String deviceId;// ��ǹID

	public BaseScanData() {
		e3DataList = new ArrayList<E3ScanDataFormat>();

		e3DataList_HKFJ = new ArrayList<E3ScanDataFormat>();
		e3DataList_HKFB = new ArrayList<E3ScanDataFormat>();
		e3DataList_ZCFJ = new ArrayList<E3ScanDataFormat>();
		e3DataList_ZDFJ = new ArrayList<E3ScanDataFormat>();
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/*
	 * ��ò���ʱ��
	 */
	protected String getOperDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String operTime = simpleDateFormat.format(new Date());
		return operTime;
	}

	/*
	 * ����E3ɨ������
	 */
	public List<String> getScanData(List<ScanDataModel> dataList) {
		List<String> dataStrList = new ArrayList<String>();
		setDataSource(dataList);
		if (e3DataList != null && e3DataList.size() > 0) {
			for (E3ScanDataFormat e3ScanDataModel : e3DataList) {
				dataStrList.add(e3ScanDataModel.toString().trim());
			}
		}
		return dataStrList;
	}

	// ��ú��շ��� �ļ� �õļ�¼��
	public List<String> getScanData_HKFB(List<ScanDataModel> dataList) {

		List<String> dataStrList = new ArrayList<String>();
		// setDataSource(dataList);
		if (e3DataList_HKFB != null && e3DataList_HKFB.size() > 0) {
			for (E3ScanDataFormat e3ScanDataModel : e3DataList_HKFB) {
				dataStrList.add(e3ScanDataModel.toString().trim());
			}
		}
		return dataStrList;
	}

	// ���װ������ �ļ� �õļ�¼��
	public List<String> getScanData_ZCFJ(List<ScanDataModel> dataList) {

		List<String> dataStrList = new ArrayList<String>();
		// setDataSource(dataList);
		if (e3DataList_ZCFJ != null && e3DataList_ZCFJ.size() > 0) {
			for (E3ScanDataFormat e3ScanDataModel : e3DataList_ZCFJ) {
				dataStrList.add(e3ScanDataModel.toString().trim());
			}
		}
		return dataStrList;
	}

	// ��ú��շ��� �ļ��õļ�¼����
	public List<String> getScanData_HKFJ(List<ScanDataModel> dataList) {

		List<String> dataStrList = new ArrayList<String>();
		// setDataSource(dataList);
		if (e3DataList_HKFJ != null && e3DataList_HKFJ.size() > 0) {
			for (E3ScanDataFormat e3ScanDataModel : e3DataList_HKFJ) {
				dataStrList.add(e3ScanDataModel.toString().trim());
			}
		}
		return dataStrList;
	}

	// ���װ������ �ļ� �õļ�¼��
	public List<String> getScanData_ZDFJ(List<ScanDataModel> dataList) {

		List<String> dataStrList = new ArrayList<String>();
		// setDataSource(dataList);
		if (e3DataList_ZDFJ != null && e3DataList_ZDFJ.size() > 0) {
			for (E3ScanDataFormat e3ScanDataModel : e3DataList_ZDFJ) {
				dataStrList.add(e3ScanDataModel.toString().trim());
			}
		}
		return dataStrList;
	}

	protected abstract void setDataSource(List<ScanDataModel> dataList);
}
