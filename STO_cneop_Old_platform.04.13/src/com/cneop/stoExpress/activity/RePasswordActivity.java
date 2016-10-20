package com.cneop.stoExpress.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.UserXmlService;
import com.cneop.stoExpress.model.User;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

public class RePasswordActivity extends CommonTitleActivity {

	private EditText etoldpassword, etnewpassword, etsurepassword;
	private Button btnSure, btnBack;

	private UserXmlService userService;
	private StrUtil strUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_repassword);
		setTitle("修改密码");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		etoldpassword = bindEditText(R.id.lly_oldpassword, null);
		etnewpassword = bindEditText(R.id.lly_newpassword, null);
		etsurepassword = bindEditText(R.id.lly_surepassword, null);

		btnSure = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		userService = new UserXmlService(RePasswordActivity.this);
		strUtil = new StrUtil();
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnBack:
			finish();
			break;
		case R.id.bottom_2_btnOk:
			save();
		}
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// save();
	// }

	private void save() {

		String oldpwd = etoldpassword.getText().toString().trim();
		String newpwd = etnewpassword.getText().toString().trim();
		String surepwd = etsurepassword.getText().toString().trim();

		User userModel = userService.getUserByNo(GlobalParas.getGlobalParas().getUserNo());

		if (strUtil.isNullOrEmpty(oldpwd) && strUtil.isNullOrEmpty(userModel.getPassword())) {

		} else if (!(oldpwd.equals(userModel.getPassword()))) {
			PromptUtils.getInstance().showToast("原始密码错误", RePasswordActivity.this);
			return;
		}

		if (strUtil.isNullOrEmpty(newpwd)) {
			PromptUtils.getInstance().showToast("请输入新密码", RePasswordActivity.this);
			return;
		}

		if (strUtil.isNullOrEmpty(surepwd)) {
			PromptUtils.getInstance().showToast("请输入确认密码", RePasswordActivity.this);
			return;
		}

		if (!newpwd.equals(surepwd)) {
			PromptUtils.getInstance().showToast("两次输入的密码不致，请重新输入！", RePasswordActivity.this);
			return;
		}

		if (userService.updatePwd(newpwd, GlobalParas.getGlobalParas().getUserNo()) > 0) {
			PromptUtils.getInstance().showToast("密码修改成功", RePasswordActivity.this);
		} else {
			PromptUtils.getInstance().showToast("密码修改失败", RePasswordActivity.this);
		}
	}

	// @Override
	// protected boolean doKeyCode_Back() {
	//
	// PromptUtils.getInstance().showAlertDialog(this, "确定要退出吗？", new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface arg0, int arg1) {
	//
	// back();
	// }
	// }, null);
	// return false;
	// }
}
