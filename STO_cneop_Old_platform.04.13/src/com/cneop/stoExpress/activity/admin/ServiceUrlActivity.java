package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.List;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.stoExpress.util.SystemInitUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServiceUrlActivity extends CommonTitleActivity {
	EditText etServiceUrl;// ������IP��ַ
	EditText etUpgrageUrl;// ����IP��ַ
	EditText serviceUrl_etSmsInfoUrl;// �������Ϣ���ص�ַ
	EditText serviceUrl_etSmsSendUrl;// ���ŷ��͵�ַ
	EditText serviceUrl_etMdDownloadUrl;// �ռ��浥����¼���ص�ַ
	Button btnConfirm;
	Button btnBack;
	SystemInitUtil systemInitUtil;
	StrUtil strUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_service_url);
		setTitle("�����ַ");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		btnConfirm = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
		etServiceUrl = bindEditText(R.id.serviceUrl_etDataAccessUrl, null, null);
		etUpgrageUrl = bindEditText(R.id.serviceUrl_etUpgradeUrl, null, null);
		serviceUrl_etSmsInfoUrl = bindEditText(R.id.serviceUrl_etSmsInfoUrl, null, null);
		serviceUrl_etSmsSendUrl = bindEditText(R.id.serviceUrl_etSmsSendUrl, null, null);
		serviceUrl_etMdDownloadUrl = super.bindEditText(R.id.serviceUrl_etMdDownloadUrl, null);
		systemInitUtil = new SystemInitUtil(ServiceUrlActivity.this);
		strUtil = new StrUtil();
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		etServiceUrl.setText(GlobalParas.getGlobalParas().getDataAccessUrl());// http://222.66.109.144:22230/PDATransferWS.svc
		etUpgrageUrl.setText(GlobalParas.getGlobalParas().getUpgradeUrl());// http://222.66.109.144/posupgrade/
		serviceUrl_etSmsInfoUrl.setText(GlobalParas.getGlobalParas().getSmsInfoUrl());// http://222.66.39.194:8809/BqSms.ashx
		serviceUrl_etSmsSendUrl.setText(GlobalParas.getGlobalParas().getSmsSendUrl());// http://222.66.39.194:8809/BqSms.ashx
		serviceUrl_etMdDownloadUrl.setText(GlobalParas.getGlobalParas().getSjMdDownloadUrl());// http://140.206.185.9:22210/stLogisticsPlatform_track/releaseRecordQuery.action
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			saveConfig();
			break;
		case R.id.bottom_2_btnBack:
			back();
			break;
		}
	}

	@Override
	public String toString() {
		return "ServiceUrlActivity [etServiceUrl=" + etServiceUrl + ", etUpgrageUrl=" + etUpgrageUrl + ", serviceUrl_etSmsInfoUrl=" + serviceUrl_etSmsInfoUrl + ", serviceUrl_etSmsSendUrl=" + serviceUrl_etSmsSendUrl + ", serviceUrl_etMdDownloadUrl=" + serviceUrl_etMdDownloadUrl + ", btnConfirm=" + btnConfirm + ", btnBack=" + btnBack + ", systemInitUtil=" + systemInitUtil + ", strUtil=" + strUtil + ", okEvent=" + okEvent + "]";
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// saveConfig();
	// }

	private void saveConfig() {

		// ��֤
		String dataAccessUrl = etServiceUrl.getText().toString().trim();
		String upgradeUrl = etUpgrageUrl.getText().toString().trim();
		String smsInfo = serviceUrl_etSmsInfoUrl.getText().toString().trim();
		String smsSend = serviceUrl_etSmsSendUrl.getText().toString().trim();
		String sjMdUrl = this.serviceUrl_etMdDownloadUrl.getText().toString().trim();
		if (strUtil.isNullOrEmpty(sjMdUrl)) {
			PromptUtils.getInstance().showToastHasFeel("�ռ��浥���ͼ�¼���ص�ַ����Ϊ�գ�", ServiceUrlActivity.this, EVoiceType.fail, 0);

			return;
		}

		if (!validateInput(dataAccessUrl, upgradeUrl, smsInfo, smsSend)) {
			return;
		}
		List<SysConfig> configList = new ArrayList<SysConfig>();

		SysConfig model = new SysConfig();
		model.setConfig_name(ESysConfig.serviceIp.toString().trim());
		model.setConfig_value(dataAccessUrl);
		configList.add(model);

		model = new SysConfig();
		model.setConfig_name(ESysConfig.upgradeIp.toString().trim());
		model.setConfig_value(upgradeUrl);
		configList.add(model);

		model = new SysConfig();
		model.setConfig_name(ESysConfig.smsInfo.toString().trim());
		model.setConfig_value(smsInfo);
		configList.add(model);

		model = new SysConfig();
		model.setConfig_name(ESysConfig.smsSend.toString().trim());
		model.setConfig_value(smsSend);
		configList.add(model);

		model = new SysConfig();
		model.setConfig_name(ESysConfig.MdDownloadUrl.toString().trim());
		model.setConfig_value(smsSend);
		model.setConfig_desc("�ռ��浥���ͼ�¼���ص�ַ");
		configList.add(model);

		if (systemInitUtil.replaceSystemSet(configList)) {
			GlobalParas.getGlobalParas().setDataAccessUrl(dataAccessUrl);
			GlobalParas.getGlobalParas().setUpgradeUrl(upgradeUrl);
			GlobalParas.getGlobalParas().setSmsInfoUrl(smsInfo);
			GlobalParas.getGlobalParas().setSmsSendUrl(smsSend);
			GlobalParas.getGlobalParas().setSjMdDownloadUrl(sjMdUrl);

			PromptUtils.getInstance().showAlertDialog(ServiceUrlActivity.this, "�޸ĳɹ���", okEvent);
		} else {
			PromptUtils.getInstance().showAlertDialog(ServiceUrlActivity.this, "�޸�ʧ�ܣ�", okEvent);
		}
	}

	private OnClickListener okEvent = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			back();
		}
	};

	private boolean validateInput(String dataAccessUrl, String upgradeUrl, String smsInfoUrl, String smsSendUrl) {
		boolean flag = true;
		if (strUtil.isNullOrEmpty(dataAccessUrl)) {
			PromptUtils.getInstance().showToastHasFeel("������IP��ַ����Ϊ�գ�", ServiceUrlActivity.this, EVoiceType.fail, 0);
			flag = false;
			return flag;
		}
		if (strUtil.isNullOrEmpty(upgradeUrl)) {
			PromptUtils.getInstance().showToastHasFeel("����IP��ַ����Ϊ�գ�", ServiceUrlActivity.this, EVoiceType.fail, 0);
			flag = false;
			return flag;
		}
		if (strUtil.isNullOrEmpty(smsInfoUrl)) {
			PromptUtils.getInstance().showToastHasFeel("��������ص�ַ����Ϊ�գ�", ServiceUrlActivity.this, EVoiceType.fail, 0);
			flag = false;
			return flag;
		}
		if (strUtil.isNullOrEmpty(smsSendUrl)) {
			PromptUtils.getInstance().showToastHasFeel("���ŷ��͵�ַ����Ϊ�գ�", ServiceUrlActivity.this, EVoiceType.fail, 0);
			flag = false;
			return flag;
		}
		return flag;
	}

	private boolean validateInput(String dataAccessUrl, String upgradeUrl) {
		boolean flag = true;
		if (strUtil.isNullOrEmpty(dataAccessUrl)) {
			PromptUtils.getInstance().showToast("������IP��ַ����Ϊ�գ�", ServiceUrlActivity.this);
			flag = false;
			return flag;
		}
		if (strUtil.isNullOrEmpty(upgradeUrl)) {
			PromptUtils.getInstance().showToast("����IP��ַ����Ϊ�գ�", ServiceUrlActivity.this);
			flag = false;
			return flag;
		}
		return flag;
	}
}
