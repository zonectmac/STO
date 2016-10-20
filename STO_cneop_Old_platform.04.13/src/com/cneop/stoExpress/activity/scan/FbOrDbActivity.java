package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.RouteValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FbOrDbActivity extends ScanBaseActivity {

	EditText etRoute;
	Button btnRoute;
	TextView tvNextStation;
	TextView tvSecondStation;
	RouteValidate routeValidate;
	private String expressType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_fb_or_db);
		setTitle("发包");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * 扫描数据
	 */

	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		try {
			int codelength = barcode.length();
			if (codelength >= 7 && codelength <= 10) {
				controlUtil.setEditVeiwFocus(etRoute);
				etRoute.setText(barcode);
				if (etRoute.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etBarcode);
				} else {
					controlUtil.setEditVeiwFocus(etBarcode);
				}
				return;
			} else if (new math().CODE1(barcode)) {
				etBarcode.setText(barcode);
				etBarcode.requestFocus();
			} else {
				Toast.makeText(getApplicationContext(), "非法单号", 1).show();
				return;
			}
			addRecord();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etRoute = bindEditText(R.id.et_route_etRoute, null, null);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);

		tvNextStation = (TextView) findViewById(R.id.tv_route_tvNextDestination);
		tvSecondStation = (TextView) findViewById(R.id.tv_route_tvSecondDestination);

		btnRoute = bindButton(R.id.btn_route_btnSelRoute);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		String[] t = initValue.split("-");
		if (t.length > 1) {
			expressType = t[1];
		}
		if (t[0].equals("db")) {
			setTitle("到包");
		}
		routeValidate = new RouteValidate(FbOrDbActivity.this, tvNextStation, tvSecondStation);
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);
		if (barcode.substring(0, 6).equals(GlobalParas.getGlobalParas().getStationId())) {
			controlUtil.setEditVeiwFocus(etRoute);
			etRoute.setText(barcode);
			controlUtil.setEditVeiwFocus(etBarcode);
		} else {
			etBarcode.setText(barcode);
			addRecord();
		}
	};

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_route_btnSelRoute:
			openSlecotr(EDownType.Route);
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			addRecord();
			break;
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.Route) {
			etRoute.setText(res_1);
			etRoute.setTag(res_1);
			tvNextStation.setText(res_2);
			tvSecondStation.setText(res_3);

			tvNextStation.setTag(res_4);

			ControlUtil.setEditVeiwFocus(this.etBarcode);
		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_fbdb_scan_lvBarcodeList);
		lvx.inital(R.layout.list_item_two, new String[] { barcodeKey, routeNoKey },
				new int[] { R.id.tv_list_item_two_tvhead1, R.id.tv_list_item_two_tvhead2 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
		tvHead1.setText("单号");
		tvHead2.setText("路由号");
		controlUtil.setControlLayoutWidth(tvHead1, 150, FbOrDbActivity.this);
	}

	@Override
	protected void initScanCode() {

		String[] t = initValue.split("-");
		if (t[0].equals("fb")) {
			scanType = EScanType.FB;
		} else if (t[0].equals("db")) {
			scanType = EScanType.DB;
		}
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		if (!new math().luyouhao(etRoute.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), "路由号非法", 1).show();
			return;
		} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), "单号非法", 1).show();
			return;
		}

		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String routeNo = etRoute.getText().toString().trim();
		String nextStationId = tvNextStation.getTag().toString();

		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setNextStationCode(nextStationId);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(FbOrDbActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(routeNoKey, routeNo);
		lvx.add(map);
		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (!routeValidate.vlidateInputData(etRoute)) {
			return false;
		}
		if (!validateScanBarcode(etBarcode)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_route_etRoute:
			if (hasFocus) {
				routeValidate.restoreNo(etRoute);
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				routeValidate.showName(etRoute);

				if (!routeValidate.vlidateInputData(etRoute)) {
					Toast.makeText(getApplicationContext(), "路由号非法", 1).show();

					return;
				}
			}
			break;
		default:
			break;
		}
	}

}
