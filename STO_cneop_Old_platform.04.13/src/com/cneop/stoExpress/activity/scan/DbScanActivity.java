package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DbScanActivity extends ScanBaseActivity implements OnClickListener {

	private final String flightKey = "flight";
	private EditText etFlight;
	private EditText etRouteNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dbscan);
		setTitle("到包扫描");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null);
		TextView tvBarcode = (TextView) this.findViewById(R.id.tv_barcode_tvBarcode);
		tvBarcode.setText("包号");
		etFlight = bindEditText(R.id.et_db_scan_etFlightNo, null);
		etRouteNo = bindEditText(R.id.et_db_scan_etRouteNo, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		btnAdd.setOnClickListener(this);
	}

	/**
	 * 扫描数据
	 */
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		if (new math().CODE1(barcode)) {
			etBarcode.setText(barcode);
			addRecord();
		} else {
			Toast.makeText(getApplicationContext(), "非法条码", 1).show();
		}
	}

	@Override
	protected void initListView() {
		lvx = (ListViewEx) this.findViewById(R.id.lv_db_scan_lvList);
		lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, flightKey, routeNoKey }, new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2, R.id.tv_list_item_three_a_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {
		// 航班跟路由暂时屏蔽掉,日后需升级
		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		// TextView tvHead2 = (TextView)
		// this.findViewById(R.id.tv_list_head_three_tvhead2);
		// TextView tvHead3 = (TextView)
		// this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("包号");
		// tvHead2.setText("航班");
		// tvHead3.setText("路由");
		controlUtil.setControlLayoutWidth(tvHead1, 130, DbScanActivity.this);
		// controlUtil.setControlLayoutWidth(tvHead2, 130, DbScanActivity.this);
		// controlUtil.setControlLayoutWidth(tvHead3, 100, DbScanActivity.this);
	}

	@Override
	protected void initScanCode() {
		scanType = EScanType.DB;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {
		controlUtil.setEditVeiwFocus(etBarcode);
		// if (!validateInput()) {
		// return;
		// }
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String flightNo = etFlight.getText().toString().trim();
		String routeNo = etRouteNo.getText().toString().trim();
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setFlightCode(flightNo);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(DbScanActivity.this).add(scanDataModel);
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(flightKey, flightNo);
		map.put(routeNoKey, routeNo);
		lvx.add(map, barcode, barcodeKey);
		scanCount++;
		updateView();
	}

	// @Override
	// protected boolean validateInput() {
	// return validateScanBarcode(etBarcode);
	// }

	@Override
	public void onClick(View arg0) {

		if (new math().CODE1(etBarcode.getText().toString().trim())) {
			etBarcode.setText(etBarcode.getText().toString().trim());
			addRecord();
		} else {
			Toast.makeText(getApplicationContext(), "非法条码", 1).show();
		}
	}

	@Override
	protected boolean validateInput() {
		// TODO Auto-generated method stub
		return false;
	}

}
