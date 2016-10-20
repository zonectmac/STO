package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.model.ScanDataModel;

class PJ extends BaseScanData {

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {
	 
		if(dataList!=null&&dataList.size()>0){
			String operDate=getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDataFormat=new E3ScanDataFormat();
				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
				e3ScanDataFormat.setCode(scanDataModel.getCourier());
				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
				e3ScanDataFormat.setBarcode(scanDataModel.getBarcode());
				e3ScanDataFormat.setScanUser(scanDataModel.getScanUser());
				e3ScanDataFormat.setOperDate(operDate);
				e3ScanDataFormat.setDeviceId(deviceId);
				e3DataList.add(e3ScanDataFormat);
			}
		}
	}

}
