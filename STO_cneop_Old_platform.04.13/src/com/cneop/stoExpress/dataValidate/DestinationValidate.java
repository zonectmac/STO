package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.DestinationXmlService;
import com.cneop.stoExpress.model.DestinationStation;
import com.cneop.util.PromptUtils;

import android.content.Context;
import android.widget.EditText;

public class DestinationValidate extends DataValidate implements IDataValidate {

	DestinationXmlService destinationService;

	public DestinationValidate(Context context) {
		super(context);
		destinationService = new DestinationXmlService(context);
	}

	@SuppressWarnings("static-access")
	@Override
	public void showName(EditText et) {
		if (et.getTag() == null) {
			String provinceNo = et.getText().toString().trim();
			if (!strUtil.isNullOrEmpty(provinceNo)) {
				String province = queryProvince(provinceNo);
				System.out.println("======province====" + province);
				if (!strUtil.isNullOrEmpty(province)) {
					et.setText(province);
					et.setTag(provinceNo);
				} else {
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "目的地异常！", null, EVoiceType.fail, 0);
					et.setText("");
					et.setTag(null);
					controlUtil.setEditVeiwFocus(et);
				}
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {
		if (et.getTag() != null) {
			String provinceNo = et.getTag().toString().trim();
			et.setText(provinceNo);
			et.setTag(null);
		}
	}

	@Override
	public boolean vlidateInputData(EditText et) {
		boolean flag = true;
		if (et.getText().toString().trim().length() <= 0) {

			flag = false;
		}
		return flag;
	}

	// 查找目的地
	private String queryProvince(String provinceNo) {
		String province = "";
		DestinationStation destinationModel = destinationService.getStationModel(provinceNo.trim());
		if (destinationModel != null) {
			province = destinationModel.getProvince();
			System.out.println("======province===" + province);
		}
		return province;
	}

}
