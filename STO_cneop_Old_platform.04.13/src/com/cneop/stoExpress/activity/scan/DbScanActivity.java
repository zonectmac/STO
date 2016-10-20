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
		setTitle("����ɨ��");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null);
		TextView tvBarcode = (TextView) this.findViewById(R.id.tv_barcode_tvBarcode);
		tvBarcode.setText("����");
		etFlight = bindEditText(R.id.et_db_scan_etFlightNo, null);
		etRouteNo = bindEditText(R.id.et_db_scan_etRouteNo, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		btnAdd.setOnClickListener(this);
	}

	/**
	 * ɨ������
	 */
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		if (new math().CODE1(barcode)) {
			etBarcode.setText(barcode);
			addRecord();
		} else {
			Toast.makeText(getApplicationContext(), "�Ƿ�����", 1).show();
		}
	}

	@Override
	protected void initListView() {
		lvx = (ListViewEx) this.findViewById(R.id.lv_db_scan_lvList);
		lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, flightKey, routeNoKey }, new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2, R.id.tv_list_item_three_a_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {
		// �����·����ʱ���ε�,�պ�������
		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		// TextView tvHead2 = (TextView)
		// this.findViewById(R.id.tv_list_head_three_tvhead2);
		// TextView tvHead3 = (TextView)
		// this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("����");
		// tvHead2.setText("����");
		// tvHead3.setText("·��");
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
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
			return;
		}
		String flightNo = etFlight.getText().toString().trim();
		String routeNo = etRouteNo.getText().toString().trim();
		// �������ݿ�
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setFlightCode(flightNo);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// ��ӵ����������
		AddScanDataThread.getIntance(DbScanActivity.this).add(scanDataModel);
		// ���½���
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
			Toast.makeText(getApplicationContext(), "�Ƿ�����", 1).show();
		}
	}

	@Override
	protected boolean validateInput() {
		// TODO Auto-generated method stub
		return false;
	}

}
