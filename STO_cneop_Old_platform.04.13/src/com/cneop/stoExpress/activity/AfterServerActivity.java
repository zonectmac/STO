package com.cneop.stoExpress.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.cneop.stoExpress.cneops.R;
import com.cneop.util.activity.CommonTitleActivity;

public class AfterServerActivity extends CommonTitleActivity {
	private WebView wbHelp;
	private Button btnTop, btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_after_server);
		setTitle("售后服务");
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		wbHelp = (WebView) findViewById(R.id.wv_afterser);
		btnTop = bindButton(R.id.bottom_2_btnOk);
		btnTop.setText("顶部");
		btnBack = bindButton(R.id.bottom_2_btnBack);
		WebSettings settings = wbHelp.getSettings();
		settings.setTextSize(WebSettings.TextSize.NORMAL);
		wbHelp.loadUrl("file:///android_asset/HELP.HTM");
	}

	@SuppressLint("NewApi")
	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			wbHelp.loadUrl("file:///android_asset/HELP.HTM");
			break;
		case R.id.bottom_2_btnBack:
			finish();
			break;
		}
	}

}
