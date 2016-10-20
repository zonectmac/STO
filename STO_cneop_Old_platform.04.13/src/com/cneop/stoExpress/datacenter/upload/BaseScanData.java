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
	protected List<E3ScanDataFormat> e3DataList_HKFJ; // 航空发件用.因航空1条记录3个文件
	protected List<E3ScanDataFormat> e3DataList_HKFB; // 航空发包用.因航空1条记录3个文件
	protected List<E3ScanDataFormat> e3DataList_ZCFJ;
	protected List<E3ScanDataFormat> e3DataList_ZDFJ; // 装袋发件。

	protected String deviceId;// 把枪ID

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
	 * 获得操作时间
	 */
	protected String getOperDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String operTime = simpleDateFormat.format(new Date());
		return operTime;
	}

	/*
	 * 生成E3扫描数据
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

	// 获得航空发包 文件 用的记录条
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

	// 获得装车发件 文件 用的记录条
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

	// 获得航空发件 文件用的记录条。
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

	// 获得装袋发件 文件 用的记录条
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
