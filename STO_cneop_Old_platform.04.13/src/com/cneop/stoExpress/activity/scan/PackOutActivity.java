package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.RouteValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PackOutActivity extends ScanBaseActivity {

	private EditText etRoute;
	private Button btnRoute;
	private RadioButton rdoGoods;
	private EditText etVehicleNum;
	private EditText etPackNo;
	private TextView tvNextStation;
	private TextView tvSecondStation;
	private String expressType = "";
	private RouteValidate routeValidate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_pack_out);
		setTitle("装包发件");
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
			if (barcode.length() > 6 && barcode.length() <= 10) {
				controlUtil.setEditVeiwFocus(etRoute);
				etRoute.setText(barcode);
				if (etRoute.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etPackNo);
				} else {
					controlUtil.setEditVeiwFocus(etPackNo);
				}
				return;

			} else if ((new math().daihao(barcode)) && etPackNo.hasFocus()) {
				etPackNo.setText(barcode);
				etBarcode.requestFocus();
				return;
			} else if (new math().CODE1(barcode)) {
				etBarcode.setText(barcode);
				etBarcode.requestFocus();
			} else {
				Toast.makeText(getApplicationContext(), "单号非法", 1).show();
			}
			addRecord();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		etRoute = bindEditText(R.id.et_route_etRoute, null, null);
		btnRoute = bindButton(R.id.btn_route_btnSelRoute);
		rdoGoods = (RadioButton) this.findViewById(R.id.rdo_goods_category_rbGoods);
		etPackNo = bindEditText(R.id.et_pack_number_etPackNo, null, null);
		etVehicleNum = bindEditText(R.id.et_vehicleId_etVehicleId, null, null);
		etVehicleNum.setVisibility(View.GONE);
		findViewById(R.id.tv_vehicleId_tvVehicleId).setVisibility(View.GONE);
		tvNextStation = (TextView) this.findViewById(R.id.tv_route_tvNextDestination);
		tvSecondStation = (TextView) this.findViewById(R.id.tv_route_tvSecondDestination);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		routeValidate = new RouteValidate(this, tvNextStation, tvSecondStation);
		expressType = initValue;
	}

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
			if (!new math().luyouhao(etRoute.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "路由号非法", 1).show();
				etRoute.requestFocus();
			} else if (!new math().CODE1(etPackNo.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "包号非法", 1).show();
				etPackNo.requestFocus();
			} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "单号非法", 1).show();
			} else {
				addRecord();
			}
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
			etPackNo.requestFocus();

		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_pack_out_scan_lvBarcodeList);
		lvx.inital(R.layout.list_item_five_a,
				new String[] { barcodeKey, packageNoKey, routeNoKey, vehicleIdKey, itemCategoryKey },
				new int[] { R.id.tv_list_item_five_a_tvhead1, R.id.tv_list_item_five_a_tvhead2,
						R.id.tv_list_item_five_a_tvhead3, R.id.tv_list_item_five_a_tvhead4,
						R.id.tv_list_item_five_a_tvhead5 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead3);
		TextView tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead4);
		TextView tvHead5 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead5);
		tvHead1.setText("单号");
		tvHead2.setText("包号");
		tvHead3.setText("路由号");
		tvHead4.setText("车辆ID");
		tvHead5.setText("物品类别");
		controlUtil.setControlLayoutWidth(tvHead2, 130, PackOutActivity.this);
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.ZB;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		routeValidate.showName(etRoute);
		if (!routeValidate.vlidateInputData(etRoute)) {
			return;
		}
		if (!validateInput()) {
			return;
		}
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String nextStationId = tvNextStation.getTag().toString();
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}
		String vehicleId = etVehicleNum.getText().toString().trim();
		String packNo = etPackNo.getText().toString().trim();
		String routeNo = etRoute.getText().toString().trim();

		if (barcode.equals(packNo)) {
			PromptUtils.getInstance().showToast("不能同时将自己装包！", this);
			return;
		}

		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setPackageCode(packNo);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setNextStationCode(nextStationId);
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(PackOutActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(packageNoKey, packNo);
		map.put(routeNoKey, routeNo);
		map.put(vehicleIdKey, vehicleId);
		map.put(itemCategoryKey, goodsType.toString().trim());

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
		if (!validateVehicleId(etVehicleNum)) {
			return false;
		}
		if (!validateBarcode(etPackNo, EBarcodeType.packageNo, packageNoValidateErrorTip)) {
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
		case R.id.et_vehicleId_etVehicleId:
			if (hasFocus) {
				routeValidate.showName(etRoute);
				if (!routeValidate.vlidateInputData(etRoute)) {
					Toast.makeText(getApplicationContext(), "路由号非法", 1).show();
					return;
				}
			}
			break;
		case R.id.et_pack_number_etPackNo:
			if (hasFocus) {
				routeValidate.showName(etRoute);
				if (!routeValidate.vlidateInputData(etRoute)) {
					return;
				}
				if (!validateVehicleId(etVehicleNum)) {
					return;
				}
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				if (!new math().daihao(etPackNo.getText().toString().trim())) {
					etPackNo.requestFocus();
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "袋号非法！ ", null, EVoiceType.fail, 0);
					etPackNo.setText("");
					return;
				}
				routeValidate.showName(etRoute);
				if (!routeValidate.vlidateInputData(etRoute)) {
					return;
				}
				if (!validateVehicleId(etVehicleNum)) {
					return;
				}
				if (!validateBarcode(etPackNo, EBarcodeType.packageNo, packageNoValidateErrorTip)) {
					return;
				}
			}
			break;
		}
	}

}
