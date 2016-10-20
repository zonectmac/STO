package com.cneop.stoExpress.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.BaseActivity;
import com.cneop.util.scan.ScanManager;

public class ScanModleActivity extends BaseActivity {

	Button btnConfirm;
	Button btnBack;
	RadioButton rbSingle;
	RadioButton rbContinue;
	SysConfigService sysConfigService;
	CheckBox cbIsVibrator;
	CheckBox cbIsLockScreen;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_scan_modle);
		scannerPower = true;
		preferences = getSharedPreferences("scaner", Context.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {

		// TODO Auto-generated method stub
		scannerPower = true;
		setScannerPower();
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		btnBack = bindButton(R.id.bottom_2_btnBack);
		btnConfirm = bindButton(R.id.bottom_2_btnOk);
		rbContinue = (RadioButton) this.findViewById(R.id.rb_scan_module_rbContinue);
		rbSingle = (RadioButton) this.findViewById(R.id.rb_scan_module_rbSingle);
		cbIsVibrator = (CheckBox) this.findViewById(R.id.cb_scan_module_chkVibrator);
		cbIsLockScreen = (CheckBox) this.findViewById(R.id.cb_scan_module_chkLockScreen);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		String scannermode = preferences.getString("sm", "单扫");
		if (scannermode.equals("单扫")) {
			rbSingle.setChecked(true);
		} else {
			rbContinue.setChecked(true);
		}
		boolean isOpen = AppContext.getAppContext().getOpenVibrator();

		cbIsVibrator.setChecked(isOpen);
		boolean isLock = AppContext.getAppContext().getIsLockScreen();
		cbIsLockScreen.setChecked(isLock);
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnBack:
			// startActivity(new Intent(getApplicationContext(),
			// SystemConfigActivity.class));
			back();
			break;
		case R.id.bottom_2_btnOk:
			saveConfig();
			// startActivity(new Intent(getApplicationContext(),
			// SystemConfigActivity.class));
			back();
			break;
		}
	}

	private void saveConfig() {

		if (rbSingle.isChecked()) {
			preferences.edit().putString("sm", "单扫").commit();
			ScanManager.getInstance().getScanner().setScanMode(false);
			getSharedPreferences("s3", MODE_PRIVATE).edit().putString("0", "").commit();
		} else {
			preferences.edit().putString("sm", "连扫").commit();
			ScanManager.getInstance().getScanner().setScanMode(true);
			getSharedPreferences("s3", MODE_PRIVATE).edit().putString("0", "连扫").commit();
		}
		boolean isOpen = cbIsVibrator.isChecked();
		AppContext.getAppContext().setOpenVibrator(isOpen);
		ScanManager.getInstance().getScanner().setIsVibrator(isOpen);
		// 锁屏
		AppContext.getAppContext().setIsLockScreen(cbIsLockScreen.isChecked());
		PromptUtils.getInstance().showToastHasFeel("保存成功！", ScanModleActivity.this, EVoiceType.normal, 0);
		finish();

	}

	@Override
	protected void doLeftButton() {

		super.doLeftButton();
		saveConfig();
	}

}
