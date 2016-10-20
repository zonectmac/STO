package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.NextStationService;
import com.cneop.stoExpress.dao.NextStationXmlService;
import com.cneop.util.PromptUtils;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class NextStationValidate extends DataValidate implements IDataValidate {

	NextStationService nextStationService;

	public NextStationValidate(Context context) {

		super(context);
		nextStationService = new NextStationService(context);
	}

	@SuppressWarnings("static-access")
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

	@SuppressWarnings("static-access")
	@Override
	public boolean vlidateInputData(EditText et) {
		boolean flag = true;
		if (et.getTag() == null) {
			flag = false;
			PromptUtils.getInstance().showAlertDialogHasFeel(context, "站点名异常！", null, EVoiceType.fail, 0);
			et.setText("");
			controlUtil.setEditVeiwFocus(et);
		}
		return flag;
	}

	// 查找站点
	private String queryStationName(String stationNo) {

		com.cneop.stoExpress.model.NextStation stationModel = nextStationService.getNextStation(stationNo);
		String stationName = "";
		if (stationModel != null) {
			stationName = stationModel.getStationName();
		}
		return stationName;
	}

}
