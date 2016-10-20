package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.RouteXmlService;
import com.cneop.stoExpress.model.Route;
import com.cneop.util.PromptUtils;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

public class RouteValidate extends DataValidate implements IDataValidate {

	RouteXmlService routeService;
	TextView tvNextStation;
	TextView tvSecondStaton;

	public RouteValidate(Context context, TextView tvNextStation, TextView tvSecondStation) {
		super(context);
		routeService = new RouteXmlService(context);
		this.tvNextStation = tvNextStation;
		this.tvSecondStaton = tvSecondStation;
	}

	@Override
	public void showName(EditText et) {

		if (et.getTag() == null) {
			boolean flag = true;
			String routeNo = et.getText().toString().trim().trim();
			if (!strUtil.isNullOrEmpty(routeNo)) {
				Route routeModel = queryRoute(routeNo);
				if (routeModel != null) {
					et.setTag(routeNo);
					et.setText(routeNo);
					tvNextStation.setText(routeModel.getNextStationName());
					tvNextStation.setTag(routeModel.getNextStationId());
					tvSecondStaton.setText(routeModel.getSecondStationName());
					flag = false;
				} else {
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "路由号异常！", null, EVoiceType.fail, 0);
					controlUtil.setEditVeiwFocus(et);
				}
			} else {
				PromptUtils.getInstance().showToastHasFeel("路由号不能为空！", context, EVoiceType.fail, 0);
				et.requestFocus();
			}
			if (flag) {
				tvNextStation.setText("");
				tvSecondStaton.setText("");
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {
		if (et.getTag() != null) {
			String routeNo = et.getTag().toString().trim();
			et.setText(routeNo);
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
		}
		return flag;
	}

	private Route queryRoute(String routeNo) {
		Route model = routeService.getRoute(routeNo);
		return model;
	}

}
