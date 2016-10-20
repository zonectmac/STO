package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.model.ScanDataModel;

class DC extends BaseScanData {

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {
	 
		if (dataList != null && dataList.size() > 0) {
			String operDate=getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDateFormate=new E3ScanDataFormat();
				e3ScanDateFormate.setScanCode(scanDataModel.getScanCode());
				e3ScanDateFormate.setScanTime(scanDataModel.getScanTime());
				e3ScanDateFormate.setBarcode(scanDataModel.getBarcode());
				e3ScanDateFormate.setScanUser(scanDataModel.getScanUser());
				e3ScanDateFormate.setOperDate(operDate);
				e3ScanDateFormate.setDeviceId(deviceId);
				e3DataList.add(e3ScanDateFormate);
			}
		}
	}

}
