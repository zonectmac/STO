package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.OrderAbnormalService;
import com.cneop.stoExpress.model.OrderAbnormal;
import com.cneop.util.PromptUtils;
import android.content.Context;
import android.widget.EditText;

public class OrderAbnormalValidate extends DataValidate implements
		IDataValidate {

	EditText etDesc;
	OrderAbnormalService orderAbnormalService;

	public OrderAbnormalValidate(Context context, EditText et) {
		super(context);
		this.etDesc = et;
		orderAbnormalService = new OrderAbnormalService(context);
	}

	@Override
	public void showName(EditText et) {
	 
		if (et.getTag() == null) {
			boolean flag = true;
			String abnormalNo = et.getText().toString().trim().trim();
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

	private String getAbnormalDesc(String abnormalNo) {
	 
		String abnormalDesc = "";
		OrderAbnormal abnormalModel = orderAbnormalService
				.getOrderAbnormal(abnormalNo.trim());
		if (abnormalModel != null) {
			abnormalDesc = abnormalModel.getReasonDesc();
		}
		return abnormalDesc;
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
		String errMsg = "";
		if (strUtil.isNullOrEmpty(et.getText().toString().trim().trim())) {
			flag = false;
			errMsg = "请选择打回原因！";
		} else if (et.getTag() == null) {
			flag = false;
			errMsg = "打回原因不存在，请重新输入！";
		}
		if (!flag) {
			controlUtil.setEditVeiwFocus(et);
			PromptUtils.getInstance().showAlertDialogHasFeel(context, errMsg,
					null, EVoiceType.fail, 0);
		}
		return flag;
	}

}
