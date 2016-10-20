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
	 * ��֤�ֻ���
	 * 
	 * @param et
	 * @param isNull
	 *            �Ƿ��Ϊ��
	 * @return
	 */
	public boolean validateMobilePhone(EditText et, boolean isNull) {
		boolean flag = true;
		if (et != null) {
			String phone = et.getText().toString().trim().trim();
			if (!strUtil.isNullOrEmpty(phone)) {
				if (!CheckUtils.isMobilePhone(phone)) {
					controlUtil.setEditVeiwFocus(et);
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "�ֻ����벻��ȷ��", null, EVoiceType.fail, 0);
					flag = false;
				}
			} else {
				if (!isNull) {
					controlUtil.setEditVeiwFocus(et);
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "�������ֻ��ţ�", null, EVoiceType.fail, 0);
					flag = false;
				}
			}
		}
		return flag;
	}
}
