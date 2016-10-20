package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.dao.StationXmlService;
import com.cneop.stoExpress.model.Station;
import com.cneop.util.PromptUtils;

import android.content.Context;
import android.widget.EditText;

public class StationValidate extends DataValidate implements IDataValidate {

	StationXmlService stationService;

	public StationValidate(Context context) {
		super(context);
		stationService = new StationXmlService(context);
	}

	@Override
	public void showName(EditText et) {
	 
		if (et.getTag() == null) {
			String stationNo = et.getText().toString().trim().trim();
			if (!strUtil.isNullOrEmpty(stationNo)) {
				String stationName = queryStationName(stationNo);
				if (!strUtil.isNullOrEmpty(stationName)) {
					et.setText(stationName);
					et.setTag(stationNo);
				}
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {
	 
		if (et.getTag() != null) {
			String stationNo = et.getTag().toString().trim().trim();
			et.setText(stationNo);
			et.setTag(null);
		}
	}

	@Override
	public boolean vlidateInputData(EditText et) {
	 
		boolean flag = true;
		if (et.getTag() == null) {
			flag = false;
			controlUtil.setEditVeiwFocus(et);
			et.setText("");
			PromptUtils.getInstance().showToast("站点名异常！", context);
		}
		return flag;
	}

	// 查找站点
	private String queryStationName(String stationNo) {
	 
		Station stationModel = stationService.getStationModel(stationNo);
		String stationName = "";
		if (stationModel != null) {
			stationName = stationModel.getStationName();
		}
		return stationName;
	}

}
