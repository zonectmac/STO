package com.cneop.stoExpress.activity;

import com.cneop.stoExpress.activity.admin.AdminMainActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.UserXmlService;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.datacenter.msd.TransferParam;
import com.cneop.stoExpress.model.User;
import com.cneop.stoExpress.util.SystemInitUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.device.Imei;
import com.cneop.util.device.NetworkUtil;
import com.cneop.util.device.Wifi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends CommonTitleActivity {
	private EditText et_login_username;// �û���
	private EditText et_login_pwd;// ����
	private TextView tv_login_machineID;// ��ǹID
	private TextView tv_login_site;
	private Button btn_login;
	private TextView tv_login_tel;// �ͷ��绰
	private UserXmlService userService;
	private StrUtil strUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		setTitle("��¼");
		super.onCreate(savedInstanceState);
		System.out.println("model \t" + Build.MODEL);
		/**
		 * NLS-MT60 CN-S3
		 */
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Runtime.getRuntime().gc();
	}

	protected void initializeComponent() {
		super.initializeComponent();
		et_login_username = bindEditText(R.id.et_login_username, null, null);// �û���
		et_login_pwd = bindEditText(R.id.et_login_pwd, null, null);// ����

		tv_login_machineID = (TextView) findViewById(R.id.tv_login_machineID);// ��ǹID
		tv_login_site = (TextView) this.findViewById(R.id.tv_login_site);// ������
		btn_login = bindButton(R.id.btn_login);// ��½
		bindButton(R.id.btn_sysconfig);// ϵͳ����
		tv_login_tel = (TextView) this.findViewById(R.id.tv_login_tel);// �ͷ��绰
		et_login_pwd.setOnKeyListener(new View.OnKeyListener() {
			// ENT��½
			@Override
			public boolean onKey(View arg0, int code, KeyEvent arg2) {
				if (code == KeyEvent.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_DOWN) {
					doLogin();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		new SystemInitUtil(this).loadSystemInit();
		refreshStation();
		tv_login_tel.setText("�ͷ��绰:18521430100 \t ����" + GlobalParas.getGlobalParas().getVersion().replace("V", ""));
		tv_login_machineID.setText(Imei.getImei(this));// ��ȡ��ǹID
		GlobalParas.getGlobalParas().setDeviceId(Imei.getImei(this));
		userService = new UserXmlService(this);
		strUtil = new StrUtil();
		// --------------------------------------------------------------
		if (StrUtil.isNullOrEmpty(Imei.getImei(this))) {
			PromptUtils.getInstance().showToastHasFeel("��ǹIDΪ��,�ݲ���ʹ��,������ϵ�ۺ���Ա!!!", this, EVoiceType.fail, 0.1);
			tv_login_machineID.setText(Imei.getImei(this));// ��ȡ��ǹID
			btn_login.setEnabled(false);
			return;
		}
		// -------------------------------------------------------------------------------
		TransferParam transferParam = new TransferParam();
		transferParam.setStationID(GlobalParas.getGlobalParas().getStationId());
		transferParam.setPdaId(GlobalParas.getGlobalParas().getDeviceId());
		transferParam.setEnterpriseID(GlobalParas.getGlobalParas().getCompanyCode());
		transferParam.setDefaultLogic(false);
		transferParam.setCompress(true);
		transferParam.setVersion(GlobalParas.getGlobalParas().getVersion());
		// -----------------------------------------------------------------------------------------
		MSDServer.getInstance(this).setParam(transferParam);

	}

	public void showmsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	// ˢ��վ������
	private void refreshStation() {
		tv_login_site.setText(
				GlobalParas.getGlobalParas().getStationId() + "\t" + GlobalParas.getGlobalParas().getStationName());
	}

	private String adminNo = "000000";
	private String adminName = "����Ա";
	private String adminPwd = "718527";
	private String exitNo = "88888888";
	private String exitPwd = "159357";

	@SuppressWarnings("static-access")
	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_login_username:
			if (hasFocus) {
				if (et_login_username.getTag() != null) {
					et_login_username.setText(et_login_username.getTag().toString());
					et_login_username.setTag(null);
				}
			}
			break;
		case R.id.et_login_pwd:
			if (hasFocus) {
				String username = et_login_username.getText().toString().trim();
				if (!strUtil.isNullOrEmpty(username)) {
					if (username.equals(adminNo)) {
						et_login_username.setTag(adminNo);
						et_login_username.setText(adminName);
					} else if (username.equals(exitNo)) {
						et_login_username.setTag(exitNo);
						et_login_username.setText("�˳�ϵͳ");
					} else {
						User userModel = userService
								.getUserByNo(GlobalParas.getGlobalParas().getStationId() + username);
						if (userModel != null) {
							et_login_username.setTag(username);
							et_login_username.setText(userModel.getUserName());
						}
					}
				}
			}

			break;
		}
	}

	/**
	 * ��½��ϵͳ����
	 */
	@Override
	protected void uiOnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			doLogin();
			break;
		case R.id.btn_sysconfig: {
			Intent intent = new Intent(this, SystemConfigActivity.class);
			intent.putExtra("isLoginSet", true);
			startActivity(intent);
		}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// �ӹ���Ա�����˳������վ����ʾ
		refreshStation();
	}

	/**
	 * ��¼
	 */
	private void doLogin() {
		String userNo = "";
		if (et_login_username.getTag() == null) {
			userNo = et_login_username.getText().toString().trim();
		} else {
			userNo = et_login_username.getTag().toString().trim();
		}
		String pwd = et_login_pwd.getText().toString().trim().trim();
		StringBuilder errorMsg = new StringBuilder();
		// ----------------------------�û���Ϊ��------------------------------------
		if (!validateInput(userNo, errorMsg)) {
			PromptUtils.getInstance().showToast(errorMsg.toString().trim(), LoginActivity.this);
			return;
		}
		// ---------------------------------------- ����Ա����
		if (userNo.equals(adminNo)) {
			if (pwd.equals(adminPwd)) {
				GlobalParas.getGlobalParas().setUserRole(EUserRole.other);
				EProgramRole eRole = GlobalParas.getGlobalParas().getProgramRole();
				GlobalParas.getGlobalParas().setSitePropertyForManagerSearch(eRole);

				Intent intent = new Intent(this, AdminMainActivity.class);
				startActivityForResult(intent, 0);
			} else {
				errorMsg.append("�������");
				PromptUtils.getInstance().showToast(errorMsg.toString().trim(), LoginActivity.this);

			}
		}
		// -------------------------------------------------�˳�app------------------
		// else if (userNo.equals(exitNo) && pwd.equals(exitPwd)) {
		// userService.closeDB();
		// this.finish();
		// System.exit(0);
		// return;
		// }
		else if (userNo.equals(exitNo)) {
			if (pwd.equals(exitPwd)) {
				userService.closeDB();
				this.finish();
				System.exit(0);
				return;
			} else {
				errorMsg.append("�������");
				PromptUtils.getInstance().showToast(errorMsg.toString().trim(), LoginActivity.this);
			}

		}
		// ��½��֤
		else {
			User userModel = userService.getUserByNo(GlobalParas.getGlobalParas().getStationId() + userNo);// 900000,0010
			if (userModel == null) {
				errorMsg.append("�û������ڣ�");
				PromptUtils.getInstance().showToast(errorMsg.toString().trim(), this);
			} else {
				if (!pwd.equals(userModel.getPassword())) {
					errorMsg.append("�������");
					PromptUtils.getInstance().showToast(errorMsg.toString().trim(), LoginActivity.this);
				} else {
					// // ��ס�û���
					GlobalParas.getGlobalParas().setUserName(userModel.getUserName());
					// ���滺��
					GlobalParas.getGlobalParas().setUserNo(GlobalParas.getGlobalParas().getStationId() + userNo);
					startActivityByRole();// ���ݽ�ɫ����
				}
			}

		}
		et_login_username.requestFocus();
		et_login_pwd.setText("");
		et_login_username.setText("");
	}

	/*
	 * ���ݽ�ɫ����
	 */
	private void startActivityByRole() {
		switch (GlobalParas.getGlobalParas().getProgramRole()) {// station
		case other:
			PromptUtils.getInstance().showToast("�����������ز������������½������Ա���������أ�", this);
			break;
		case station:
			startUserRoleSelActivity(EProgramRole.station);
			break;
		case center:
			startMainActivity(EUserRole.centerScaner, EProgramRole.center);
			break;
		case air:
			startUserRoleSelActivity(EProgramRole.air);
			break;
		}
	}

	/*
	 * �û���ɫ
	 */
	private EUserRole getUserRole(String userType) {
		if (userType.equals("1")) {
			return EUserRole.business;
		}
		return EUserRole.scaner;
	}

	/*
	 * ����������
	 */
	private void startMainActivity(EUserRole userRole, EProgramRole programRole) {
		GlobalParas.getGlobalParas().setUserRole(userRole);
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.putExtra("exitTip", true);
		intent.putExtra("parentId", userRole.value());
		intent.putExtra("roleId", programRole.value());
		intent.putExtra("parentId2", EUserRole.other.value());
		startActivity(intent);
	}

	/*
	 * �����û���ɫѡ�����
	 */
	private void startUserRoleSelActivity(EProgramRole programRole) {
		// station(1), center(2), air(3), other(0);
		Bundle bundle = new Bundle();
		bundle.putSerializable("programRole", programRole);
		// ----------------------------------------------------
		startActivity(new Intent(this, UserRoleActivity.class).putExtras(bundle));
		overridePendingTransition(R.anim.activity_open, 0);
	}

	private boolean validateInput(String userNo, StringBuilder msg) {

		boolean flag = true;
		if (strUtil.isNullOrEmpty(userNo)) {
			msg.append("�������û�����");
			flag = false;
		}
		return flag;
	}

	private Handler wifiNoticeHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1023: {
				Toast.makeText(LoginActivity.this, "WIFIδ���ӣ�����ѡ��WIFI����������������", Toast.LENGTH_LONG).show();
				Wifi.getInstance(LoginActivity.this).DoConfig();
			}
				break;
			case 1024: {
				NetworkUtil.getInstance(LoginActivity.this).wifiConfigHandler = null;
			}
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// return true ���Լ�����. return false ��ϵͳ����
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// return true;// �����κδ���
		} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
			doLogin();
		}
		return false;
	}
}
