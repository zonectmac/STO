package com.cneop.stoExpress.dataValidate;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.UserXmlService;
import com.cneop.stoExpress.model.User;
import com.cneop.util.PromptUtils;

import android.content.Context;
import android.widget.EditText;

public class UserValidate extends DataValidate implements IDataValidate {

	UserXmlService userService;
	String stationId;

	public UserValidate(Context context, String stationId) {
		super(context);
		userService = new UserXmlService(context);
		this.stationId = stationId;
	}

	@Override
	public void showName(EditText et) {
		// if (et.getText().toString().length() <= 0) {
		// PromptUtils.getInstance().showAlertDialogHasFeel(context, "员工编号异常！",
		// null, EVoiceType.fail, 0);
		// et.requestFocus();
		// return;
		// }
		// 查找用户
		if (et.getText().toString().length() <= 0) {
			PromptUtils.getInstance().showAlertDialogHasFeel(context, "员工编号异常！", null, EVoiceType.fail, 0);
			et.requestFocus();
			return;
		}
		if (et.getTag() == null) {
			String userNo = et.getText().toString().trim();
			if (!strUtil.isNullOrEmpty(userNo)) {
				if (userNo.length() <= 6) {
					userNo = stationId + userNo;
				}
				String userName = queryUser(userNo);
				if (!strUtil.isNullOrEmpty(userName)) {
					et.setText(userName);
					et.setTag(userNo);
				} else {
					PromptUtils.getInstance().showAlertDialogHasFeel(context, "员工编号异常！", null, EVoiceType.fail, 0);
					et.setText("");
					et.requestFocus();

				}
			}
		}
	}

	@Override
	public void restoreNo(EditText et) {
		if (et.getTag() != null) {
			if (et.getTag().toString().length() > 6) {
				String userNo = et.getTag().toString().trim();
				userNo = userNo.substring(6);
				et.setText(userNo);
				et.setTag(null);
			}
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

	// 查找用户
	private String queryUser(String userNo) {
		String userName = "";
		User userModel = userService.getUserByNo(userNo.trim());
		if (userModel != null) {
			userName = userModel.getUserName();
		}
		return userName;
	}

}
