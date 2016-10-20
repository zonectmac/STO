package com.cneop.stoExpress.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.BaseActivity;
import com.cneop.util.scan.ScanManager;
import com.cneop.util.scan.V6ScanManager.IScanResult;

public class CustomDialogActivity extends BaseActivity {
	EditText etContent;
	Button btnDel;
	Button btnCancel;
	StrUtil strUtil;
	public static String BARCODEKEY = "barcode";
	public static final int resultCode = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_custom_dialog);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected IScanResult handleScanData = new IScanResult() {
		@Override
		public void HandleScanResult(String barcode) {
			// barcode = StrUtil.FilterSpecial(barcode);
			etContent.setText(barcode);
		}

	};

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		etContent = bindEditText(R.id.et_custom_dialog_etContent, null);
		btnCancel = bindButton(R.id.btn_custom_dialog_btnCancel);
		btnDel = bindButton(R.id.btn_custom_dialog_btnDel);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		strUtil = new StrUtil();
		Intent intent = getIntent();
		String barcode = intent.getStringExtra(BARCODEKEY);
		if (!strUtil.isNullOrEmpty(barcode)) {
			etContent.setText(barcode);
		}
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_custom_dialog_btnCancel:
			back();
			break;
		case R.id.btn_custom_dialog_btnDel:
			delRecord();
			break;
		}
	}

	private void delRecord() {
		String barcode = etContent.getText().toString();
		if (strUtil.isNullOrEmpty(barcode)) {
			PromptUtils.getInstance().showAlertDialogHasFeel(CustomDialogActivity.this, "请输入或扫描要删除的单号！", null, EVoiceType.fail, 0);
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(BARCODEKEY, barcode);
		setResult(resultCode, intent);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if (lockLongPressKey) {
				return true;
			}

			if (event.getRepeatCount() == 0) {
				event.startTracking();
			}
		}

		if (ScanManager.getInstance().getScanner().scan(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};

	private boolean lockLongPressKey;// 是否长按 // 在扫描页面时,屏蔽声音增大,或减少的长按键

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			lockLongPressKey = true;
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

}
