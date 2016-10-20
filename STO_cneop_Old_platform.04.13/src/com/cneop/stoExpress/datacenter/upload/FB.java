package com.cneop.stoExpress.datacenter.upload;

import java.util.List;
import com.cneop.stoExpress.model.ScanDataModel;

class FB extends BaseScanData {

	boolean isAddFlag = false;

	public void setAddFlag() {
		isAddFlag = true;
	}

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {

		if (dataList != null && dataList.size() > 0) {
			String operDate = getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDataFormat = new E3ScanDataFormat();
				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
				e3ScanDataFormat.setCode(scanDataModel.getNextStationCode());
				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
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
				e3ScanDataFormat.setOperDate(operDate);
				e3ScanDataFormat.setAbnormalCode(scanDataModel.getRouteCode());
				e3ScanDataFormat.setDeviceId(deviceId);
				e3DataList.add(e3ScanDataFormat);
			}
		}
	}

}
