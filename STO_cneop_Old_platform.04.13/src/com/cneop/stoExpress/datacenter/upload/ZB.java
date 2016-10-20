package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.model.ScanDataModel;

class ZB extends BaseScanData {
	FJ fj;
	FB fb;
	List<ScanDataModel> fjDataList;

	public ZB() {
		super();
		fj = new FJ();
		fb = new FB();
	}

	public void setFJSource(List<ScanDataModel> fjDataList) {
		this.fjDataList = fjDataList;
		fj.setDeviceId(deviceId);
	}
	public void setFBSource(List<ScanDataModel> fbDataList) {
		fb.setDeviceId(deviceId);
		fb.setAddFlag();
		fb.setDataSource(fbDataList);
	}

	@Override
	protected void setDataSource(List<ScanDataModel> dataList) {
		if (dataList != null && dataList.size() > 0) {
			String operDate = getOperDate();
			for (ScanDataModel scanDataModel : dataList) {
				E3ScanDataFormat e3ScanDataFormat = new E3ScanDataFormat();
				e3ScanDataFormat.setScanCode(scanDataModel.getScanCode());
				e3ScanDataFormat.setCode(scanDataModel.getPackageCode());
				e3ScanDataFormat.setScanTime(scanDataModel.getScanTime());
				e3ScanDataFormat.setExpressType(scanDataModel.getExpressType());
				e3ScanDataFormat.setBarcode(scanDataModel.getBarcode());
				e3ScanDataFormat.setScanUser(scanDataModel.getScanUser());
				e3ScanDataFormat.setOperDate(operDate);
				e3ScanDataFormat.setAbnormalCode(scanDataModel.getRouteCode());
				e3ScanDataFormat.setVehicleNum(scanDataModel.getVehicleNumber());
				e3ScanDataFormat.setDeviceId(deviceId);
				e3DataList.add(e3ScanDataFormat);
			}
			fj.setDataSource(dataList);
			fj.setAddFlag();
			fj.setDataSource(fjDataList);
			
			
			if (fj.e3DataList != null && fj.e3DataList.size() > 0) {
				for (E3ScanDataFormat model : fj.e3DataList) {
					model.setScanCode(EScanType.FJ.value());
				//	e3DataList.add(model);
					e3DataList_HKFJ.add(model);
				}
			}
			
			
			
			if (fb.e3DataList != null && fb.e3DataList.size() > 0) {
				for (E3ScanDataFormat model : fb.e3DataList) {
					model.setScanCode(EScanType.FB.value());
					//e3DataList.add(model);
					e3DataList_HKFB.add(model);
				}
			}
		}
	}

}
