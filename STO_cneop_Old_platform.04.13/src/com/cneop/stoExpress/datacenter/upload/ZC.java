package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.model.ScanDataModel;

class ZC extends BaseScanData {

	FJ fj;

	public ZC() {
		super();
	    fj = new FJ();
	}

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {

		if (dataList != null && dataList.size() > 0) {
			String operDate = getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDataFormat = new E3ScanDataFormat();
				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
				e3ScanDataFormat.setCode(scanDataModel.getCarLotNumber());
				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
				e3ScanDataFormat.setBarcode(scanDataModel.getBarcode());
				e3ScanDataFormat.setScanUser(scanDataModel.getScanUser());
				e3ScanDataFormat.setOperDate(operDate);
				e3ScanDataFormat.setDeviceId(deviceId);
				e3DataList.add(e3ScanDataFormat);
			}
//			 fj.setDeviceId(deviceId);
//			 fj.setDataSource(dataList);
//			 if (fj.e3DataList != null && fj.e3DataList.size() > 0) {
//				 for (E3ScanDataFormat model : fj.e3DataList) {
//					 model.setScanCode(EScanType.FJ.value());
//					 e3DataList.add(model);
//				 	}
//			 }
		}
		
	}
	
	
	protected void setDataSource_FJ(List<ScanDataModel> dataList) {

		
			 fj.setDeviceId(deviceId);
			 fj.setDataSource(dataList);
			 if (fj.e3DataList != null && fj.e3DataList.size() > 0) {
				 for (E3ScanDataFormat model : fj.e3DataList) {
					 model.setScanCode(EScanType.FJ.value());
					 e3DataList_ZCFJ.add(model);
				 	}
			 }
		
		
	}
	
	

}
