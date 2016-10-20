package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.List;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.dao.StationXmlService;
import com.cneop.stoExpress.model.Station;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.stoExpress.util.SystemInitUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.LogUtils.eLevel;
import com.cneop.util.activity.CommonTitleActivity;

import android.R.color;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class RoleConfigActivity extends CommonTitleActivity {

	Button btnOk;
	Button btnBack;
	Spinner spinner;
	CheckBox chk;
	CheckBox role_config_chksjMd_control;
	ArrayAdapter arrayAdapter;
	Station stationModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_role_config);
		setTitle("��ɫ����");
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		btnBack = bindButton(R.id.bottom_2_btnBack);
		btnOk = bindButton(R.id.bottom_2_btnOk);
		chk = (CheckBox) this.findViewById(R.id.role_config_chkDeviceRole);

		role_config_chksjMd_control = (CheckBox) findViewById(R.id.role_config_chksjMd_control);
		spinner = (Spinner) this.findViewById(R.id.role_config_spnProgramRole);
		spinner.setOnItemSelectedListener(selectedEvent);
		arrayAdapter = ArrayAdapter.createFromResource(RoleConfigActivity.this, R.array.program_role_choose, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arrayAdapter);
		spinner.setVisibility(View.VISIBLE);
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		chk.setChecked(true);
		StationXmlService stationService = new StationXmlService(RoleConfigActivity.this);
		stationModel = new Station();
		stationModel = (Station) stationService.getSingleObj(new String[] { " and stationId=?" }, new String[] { GlobalParas.getGlobalParas().getStationId() }, stationModel);
		int position = getPosition(GlobalParas.getGlobalParas().getProgramRoleStr());
		spinner.setSelection(position, true);// ���ó�ʼѡ��
		role_config_chksjMd_control.setChecked(GlobalParas.getGlobalParas().isMdControlIsOpen());

	}

	/*
	 * �����SPINNER�ڵ��е�λ��
	 */
	private int getPosition(String programRoleStr) {
		int index = 0;
		if (programRoleStr.equals("����")) {
			index = 1;
		} else if (programRoleStr.equals("����")) {
			index = 2;
		} else if (programRoleStr.equals("����")) {
			index = 3;
		}
		return index;
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

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// saveConfig();
	// }

	private OnItemSelectedListener selectedEvent = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String selProgramRoleStr = arrayAdapter.getItem(arg2).toString().trim();
			boolean isNotice = false;
			// ��֤��վ���Ƿ���ڸ� ��ɫ
			if (stationModel != null) {
				if (selProgramRoleStr.equals("����")) {
					if (stationModel.getAttribute().equals("����") || stationModel.getAttribute().equals("���ĺ���")) {
						isNotice = true;
					}
				} else if (selProgramRoleStr.equals("����")) {
					if (stationModel.getAttribute().equals("����") || stationModel.getAttribute().equals("���㺽��")) {
						isNotice = true;
					}
				} else if (selProgramRoleStr.equals("����")) {
					if (stationModel.getAttribute().equals("����") || stationModel.getAttribute().equals("����")) {
						isNotice = true;
					}
				}
				if (isNotice) {
					showNotice(selProgramRoleStr);
					spinner.setSelection(0, true);
				}
			}
		}

		private void showNotice(String programRoleStr) {
			PromptUtils.getInstance().showAlertDialog(RoleConfigActivity.this, "��վ��û�С�" + programRoleStr + "��Ȩ��", null);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private void saveConfig() {
		SystemInitUtil systemInitUtil = new SystemInitUtil(RoleConfigActivity.this);
		List<SysConfig> sysConfigList = new ArrayList<SysConfig>();
		// ���������ݿ���
		String programRoleStr = arrayAdapter.getItem(spinner.getSelectedItemPosition()).toString().trim();
		String selectOperatorStatus = chk.isChecked() == true ? "��" : "��";
		String mdControlIsOpen = role_config_chksjMd_control.isChecked() ? "1" : "0";

		SysConfig model = new SysConfig(ESysConfig.programRole.toString().trim(), programRoleStr);
		sysConfigList.add(model);
		model = new SysConfig(ESysConfig.allowSelectOper.toString().trim(), selectOperatorStatus);
		sysConfigList.add(model);

		// �Ƿ����浥�ܿ�
		model = new SysConfig(ESysConfig.MdControlIsOpen.toString().trim(), mdControlIsOpen);
		sysConfigList.add(model);

		if (systemInitUtil.replaceSystemSet(sysConfigList)) {
			// ���»���
			GlobalParas.getGlobalParas().setAllowSelectOper(chk.isChecked());
			GlobalParas.getGlobalParas().setProgramRoleStr(programRoleStr);
			systemInitUtil.setProgramRole(GlobalParas.getGlobalParas().getStationId(), programRoleStr);
			GlobalParas.getGlobalParas().setMdControlIsOpen(role_config_chksjMd_control.isChecked());
			
			PromptUtils.getInstance().showAlertDialog(RoleConfigActivity.this, "����ɹ�!", okEvent);
			SharedPreferences preferences = getSharedPreferences("is_user", Context.MODE_PRIVATE);
			if (chk.isChecked()) {
				preferences.edit().putBoolean("user", true).commit();
			} else {
				preferences.edit().putBoolean("user", false).commit();
			}
		} else {
			PromptUtils.getInstance().showAlertDialog(RoleConfigActivity.this, "����ʧ�ܣ�", null);
		}
	}

	private OnClickListener okEvent = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			back();
		}
	};

}
