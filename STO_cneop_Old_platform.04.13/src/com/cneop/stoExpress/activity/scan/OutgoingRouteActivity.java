package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.RouteValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.math.math;

import android.view.View;
import android.view.ViewStub;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 指定路由发件
 */
public class OutgoingRouteActivity extends OutgoingBaseActivity {

	private RouteValidate routeValidate;

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		vsFirst = (ViewStub) this.findViewById(R.id.vs_outgoing_vsRoute);
		vsFirst.inflate();
		vsGoods = (ViewStub) this.findViewById(R.id.vs_outgoing_vsCagegory);
		vsGoods.inflate();
		rdoGoods = (RadioButton) this.findViewById(R.id.rdo_goods_category_rbGoods);
		etRoute = bindEditText(R.id.et_route_etRoute, null, null);
		btnSelRoute = bindButton(R.id.btn_route_btnSelRoute);
		tvNextStation = (TextView) this.findViewById(R.id.tv_route_tvNextDestination);
		tvSecondStation = (TextView) this.findViewById(R.id.tv_route_tvSecondDestination);
	}

	@Override
	protected void initListView() {

		findViewById(R.id.et_vehicleId_etVehicleId).setVisibility(View.GONE);
		lvx = (ListViewEx) this.findViewById(R.id.lv_outgoing_lvBarcodeList);
		lvx.inital(R.layout.list_item_five,
				new String[] { barcodeKey, weightKey, routeNoKey, vehicleIdKey, itemCategoryKey },
				new int[] { R.id.tv_list_item_five_tvhead1, R.id.tv_list_item_five_tvhead2,
						R.id.tv_list_item_five_tvhead3, R.id.tv_list_item_five_tvhead4,
						R.id.tv_list_item_five_tvhead5 });
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		routeValidate = new RouteValidate(this, tvNextStation, tvSecondStation);
		expressType = initValue;
	}

	@Override
	protected void setHeadTitle() {

		vsListHead = (ViewStub) this.findViewById(R.id.vs_outgoing_vsHead5);
		vsListHead.inflate();
		TextView tvColumn1 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead1);
		TextView tvColumn2 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead2);
		TextView tvColumn3 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead3);
		TextView tvColumn4 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead4);
		TextView tvColumn5 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead5);
		tvColumn1.setText("单号");
		tvColumn2.setText("重量");
		tvColumn3.setText("路由号");
		tvColumn4.setText("车辆ID");
		tvColumn5.setText("物品类别");
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
			etBarcode.requestFocus();
		}
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_route_etRoute:
			if (hasFocus) {
				// 还原编号
				routeValidate.restoreNo(etRoute);
			}
			break;

		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				// 显示路由信息
				routeValidate.showName(etRoute);
				if (!routeValidate.vlidateInputData(etRoute)) {
					etRoute.requestFocus();
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "路由名异常！", null, EVoiceType.fail, 0);
					return;
				}
			}
		}
	}

	@Override
	protected void addRecord() {

		// controlUtil.setEditVeiwFocus(etBarcode);
		routeValidate.showName(etRoute);
		if (!routeValidate.vlidateInputData(etRoute)) {
			PromptUtils.getInstance().showAlertDialogHasFeel(this, "路由名异常！", null, EVoiceType.fail, 0);
			return;
		}

		if (etRoute.getText().toString().length() <= 0) {
			Toast.makeText(getApplicationContext(), "路由号不能为空", 1).show();
			etRoute.requestFocus();
			return;
		} else {
			etBarcode.setText(etBarcode.getText().toString().trim());
			boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
			if (!flag) {
				// Toast.makeText(getApplicationContext(), "非法条码", 1).show();
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "非法条码！", null, EVoiceType.fail, 0);
				etBarcode.setText("");
				etBarcode.requestFocus();
				return;
			}
		}

		String routeNo = etRoute.getTag().toString().trim();
		String routeName = etRoute.getText().toString().trim();
		String nextStationId = tvNextStation.getTag().toString();
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}
		String weightStr = "";
		String weight = "";
		if (tvWeight != null) {
			weightStr = tvWeight.getText().toString();

			boolean isWeightScan = AppContext.getAppContext().getBluetoothIsOpen();
			if (isWeightScan) {
				if (rdoGoods != null && rdoGoods.isChecked()) {
					// 货样
					if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
						PromptUtils.getInstance().showAlertDialogHasFeel(OutgoingRouteActivity.this,
								"电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
						return;
					}
				} else if (rdoGoods != null && !rdoGoods.isChecked()) {
					// 非货样
					if (strUtil.isNullOrEmpty(weightStr) || weightStr.startsWith("-")) {
						weightStr = "0.2kg";
					} else {
						double t = Double.parseDouble(weightStr.substring(0, weightStr.length() - 2));
						if (t < 0.2) {
							weightStr = "0.2kg";
						}
					}
				} else {
					if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
						PromptUtils.getInstance().showAlertDialogHasFeel(OutgoingRouteActivity.this,
								"电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
						return;
					}
				}
				weight = weightStr.substring(0, weightStr.length() - 2);
			}
		}
		String vehicleId = etVehicleId.getText().toString().trim();
		// String weightStr = tvWeight.getText().toString().trim();
		String barcode = etBarcode.getText().toString().trim();
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setNextStationCode(nextStationId);
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setExpressType(expressType);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setWeight(weight);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(OutgoingRouteActivity.this).add(scanDataModel);
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(weightKey, weightStr);
		map.put(routeNoKey, routeName);
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
		return super.validateInput();
	}
}
