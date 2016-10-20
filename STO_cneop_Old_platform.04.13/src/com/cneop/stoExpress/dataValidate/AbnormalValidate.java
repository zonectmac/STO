package com.cneop.stoExpress.dataValidate;

import android.content.Context;
import android.widget.EditText;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.AbnormalXmlService;
import com.cneop.stoExpress.model.Abnormal;
import com.cneop.util.PromptUtils;

public class AbnormalValidate extends DataValidate implements IDataValidate {

	EditText etDesc;
	AbnormalXmlService abnromalService;

	public AbnormalValidate(Context context, EditText et) {

		super(context);
		this.etDesc = et;
		abnromalService = new AbnormalXmlService(context);
	}

	@SuppressWarnings("static-access")
	@Override
	public void showName(EditText et) {

		if (et.getTag() == null) {
			boolean flag = true;
			String abnormalNo = et.getText().toString();
			if (!strUtil.isNullOrEmpty(abnormalNo)) {
				String abnromalDesc = getAbnormalDesc(abnormalNo);
				if (!strUtil.isNullOrEmpty(abnromalDesc)) {
					et.setTag(abnormalNo);
					this.etDesc.setText(abnromalDesc);
					flag = false;
				}
			}
			if (flag) {
				this.etDesc.setText("");
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {

		if (et.getTag() != null) {
			et.setTag(null);
		}
	}

	@Override
	public boolean vlidateInputData(EditText et) {

		boolean flag = true;
		if (et.getTag() == null) {
			flag = false;
			controlUtil.setEditVeiwFocus(et);
			PromptUtils.getInstance().showToast("问题类型异常！", context);
		}
		return flag;
	}

	private String getAbnormalDesc(String abnormalNo) {

		Abnormal abnormalModel = abnromalService.getAbnormal(abnormalNo);
		if (abnormalModel != null) {
			if (abnormalModel.getAttribute().equals("网点") && GlobalParas.getGlobalParas().getStationId().equals("900000")) {
				return abnormalModel.getReasonDesc();
			} else if (abnormalModel.getAttribute().equals("中心") && GlobalParas.getGlobalParas().getStationId().equals("900001")) {
				return abnormalModel.getReasonDesc();
			} else if (abnormalModel.getAttribute().equals("全部")) {
				// abnormalModel.getAttribute().equals("全部") &&
// GlobalParas.getGlobalParas().getStationId().equals("900002") ||
// abnormalModel.getAttribute().equals("中心") &&
// GlobalParas.getGlobalParas().getStationId().equals("900001") ||
// abnormalModel.getAttribute().equals("网点") &&
// GlobalParas.getGlobalParas().getStationId().equals("900000")
				return abnormalModel.getReasonDesc();
			} else {
				return "";
			}
		}
		return "";// 自取件
	}
}
