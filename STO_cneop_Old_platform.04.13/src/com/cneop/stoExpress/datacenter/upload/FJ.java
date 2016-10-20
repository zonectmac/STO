package com.cneop.stoExpress.datacenter.upload;

import java.util.List;
import com.cneop.stoExpress.model.ScanDataModel;

/*
 * ·¢¼þ
 */
class FJ extends BaseScanData {

	boolean isAddFlag = false;
	private List<ScanDataModel> fjDataList;

	public void setAddFlag() {
		isAddFlag = true;
	}

	public void setFJSource(List<ScanDataModel> fjDataList) {
		this.fjDataList = fjDataList;
	}

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {
		if (dataList != null && dataList.size() > 0) {
			String operTime = getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDataFormat = new E3ScanDataFormat();
				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
				e3ScanDataFormat.setCode(scanDataModel.getNextStationCode());
				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
				e3ScanDataFormat.setSampleType(scanDataModel.getSampleType());
				e3ScanDataFormat.setBarcode(scanDataModel.getBarcode());
				String expressType = scanDataModel.getExpressType();
				if (!"1".equals(expressType)) {
					if (isAddFlag) {
						if ("900".equals(scanDataModel.getBarcode().substring(0, 3))) {
							expressType = "0";
						}
					}
				}
				e3ScanDataFormat.setExpressType(expressType);
				e3ScanDataFormat.setScanUser(scanDataModel.getScanUser());
				e3ScanDataFormat.setOperDate(operTime);
				e3ScanDataFormat.setAbnormalCode(scanDataModel.getRouteCode());
				e3ScanDataFormat.setVehicleNum(scanDataModel.getVehicleNumber());
				e3ScanDataFormat.setWeight(scanDataModel.getWeight());
				e3ScanDataFormat.setDeviceId(deviceId);
				e3DataList.add(e3ScanDataFormat);
			}
		}
	}

//	protected void setDataSource_FJ(List<ScanDataModel> dataList) {
//		if (dataList != null && dataList.size() > 0) {
//			String operTime = getOperDate();
//			for (ScanDataModel scanDataModel : dataList) {
//				E3ScanDataFormat e3ScanDataFormat = new E3ScanDataFormat();
//				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
//				e3ScanDataFormat.setCode(scanDataModel.getNextStationCode());
//				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
//				e3ScanDataFormat.setSampleType(scanDataModel.getSampleType());
//				e3ScanDataFormat.setBarcode(scanDataModel.getBarcode());
//				String expressType = scanDataModel.getExpressType();
//				if (!"1".equals(expressType)) {
//					if (isAddFlag) {
//						if ("900".equals(scanDataModel.getBarcode().substring(0, 3))) {
//							expressType = "0";
//						}
//					}
//				}
//				e3ScanDataFormat.setExpressType(expressType);
//				e3ScanDataFormat.setScanUser(scanDataModel.getScanUser());
//				e3ScanDataFormat.setOperDate(operTime);
//				e3ScanDataFormat.setAbnormalCode(scanDataModel.getRouteCode());
//				e3ScanDataFormat.setVehicleNum(scanDataModel.getVehicleNumber());
//				e3ScanDataFormat.setWeight(scanDataModel.getWeight());
//				e3ScanDataFormat.setDeviceId(deviceId);
//				e3DataList_ZCFJ.add(e3ScanDataFormat);
//			}
//		}
//	}

	
	
}
