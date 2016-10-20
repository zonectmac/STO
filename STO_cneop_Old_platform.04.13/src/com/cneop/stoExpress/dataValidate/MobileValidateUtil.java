package com.cneop.stoExpress.dataValidate;

import android.content.Context;
import android.widget.EditText;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.util.CheckUtils;
import com.cneop.util.PromptUtils;

public class MobileValidateUtil extends DataValidate {
	public MobileValidateUtil(Context context) {
		super(context);
	}

	/**
	 * 验证手机号
	 * 
	 * @param et
	 * @param isNull
	 *            是否可为空
	 * @return
	 */
	public boolean validateMobilePhone(EditText et, boolean isNull) {
		boolean flag = true;
		if (et != null) {
			String phone = et.getText().toString().trim().trim();
			if (!strUtil.isNullOrEmpty(phone)) {
				if (!CheckUtils.isMobilePhone(phone)) {
					controlUtil.setEditVeiwFocus(et);
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "手机号码不正确！", null, EVoiceType.fail, 0);
					flag = false;
				}
			} else {
				if (!isNull) {
					controlUtil.setEditVeiwFocus(et);
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "请输入手机号！", null, EVoiceType.fail, 0);
					flag = false;
				}
			}
		}
		return flag;
	}
}
