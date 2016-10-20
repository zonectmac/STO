package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.util.CheckUtils;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

public class AutoUpdateSetActivity extends CommonTitleActivity {

	RadioGroup radio;
	EditText etTime;

	Button btnOk, btnBack;
	SysConfigService sysConfigService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.layout_updateset);
		setTitle("上传设置");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		radio = (RadioGroup) findViewById(R.id.rg);
		etTime = (EditText) findViewById(R.id.autotime);

		btnOk = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);

		btnOk.setOnClickListener(ocl);
		btnBack.setOnClickListener(ocl);

		radio.setOnCheckedChangeListener(listener);
	}

	@Override
	protected void initializeValues() {
		sysConfigService = new SysConfigService(AutoUpdateSetActivity.this);
		int autoUploadTimeSpiltlStr = GlobalParas.getGlobalParas().getAutoUploadTimeSpilt();
		if (autoUploadTimeSpiltlStr > 0) {
			radio.check(R.id.upauto);
			etTime.setText(String.valueOf(autoUploadTimeSpiltlStr));
		} else {
			radio.check(R.id.uphands);
			etTime.setText("");
		}

		super.initializeValues();
	}

	private void SaveUpSet() {

		int iTimes = 0;
		if (radio.getCheckedRadioButtonId() == R.id.upauto) {
			String time = etTime.getText().toString().trim();
			if (!CheckUtils.isNumeric(time)) {
				PromptUtils.getInstance().showToast("时间必须输入数字", AutoUpdateSetActivity.this);
				return;
			}
			iTimes = Integer.parseInt(time);
			if (iTimes < 5 || iTimes > 60) {
				PromptUtils.getInstance().showToast("设置的时间不能少于5分钟，且不能大于60分钟", AutoUpdateSetActivity.this);
				return;
			}
		}

		GlobalParas.getGlobalParas().setAutoUploadTimeSpilt(iTimes);
		com.cneop.stoExpress.model.SysConfig model = new com.cneop.stoExpress.model.SysConfig();
		model.setConfig_desc("自动上传的时间间隔(单位:min");
		model.setConfig_name(ESysConfig.autoUploadTimeSpilt.toString().trim());
		model.setConfig_value(String.valueOf(iTimes));
		model.setLasttime(new java.util.Date());

		List<com.cneop.stoExpress.model.SysConfig> lstModels = new ArrayList<com.cneop.stoExpress.model.SysConfig>();
		lstModels.add(model);
		sysConfigService.addRecord(lstModels);

		PromptUtils.getInstance().showToast("保存成功", AutoUpdateSetActivity.this);
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// SaveUpSet();
	// }

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.bottom_2_btnOk:
				SaveUpSet();
				break;
			case R.id.bottom_2_btnBack:
				back();
				break;
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			if (checkedId == R.id.upauto) {
				etTime.setEnabled(true);
			}
			if (checkedId == R.id.uphands) {
				etTime.setEnabled(false);
			}
		}
	};
}
