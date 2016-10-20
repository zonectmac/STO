package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class LeaveWarehouseActivity extends ScanBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_leave_warehouse);
		setTitle("留仓");
		super.onCreate(savedInstanceState);
		super.scannerPower = true;
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		if (etBarcode != null) {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(barcode);
			if (flag) {
				addRecord();
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
			}
		}
	}

	@Override
	protected void initListView() {
		lvx = (ListViewEx) this.findViewById(R.id.lv_leave_warehouse_lvBarcode);
		lvx.inital(R.layout.list_item_one, new String[] { barcodeKey }, new int[] { R.id.tv_list_item_one_tvhead1 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_one_tvhead1);
		tvHead1.setText("单号");
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.LC;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);
		etBarcode.setText(barcode);
		addRecord();
	};

	@Override
	protected void addRecord() {

		if (!validateInput()) {
			return;
		}
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(LeaveWarehouseActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		lvx.add(map);

		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (!validateScanBarcode(etBarcode)) {
			return false;
		}
		return true;
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			// --------------------------------------------------------
			if (etBarcode != null) {// 单号
				boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
				if (flag) {
					addRecord();
				} else {
					PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
					etBarcode.setText("");
				}
			}
			// ----------------------------------------------------------
			break;
		}
	}

}
