package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.NextStationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.math.math;

/*
 * 指定目的地发件
 */
public class OutgoingStationActivity extends OutgoingBaseActivity {

	private TextView tvColumn1;
	private TextView tvColumn2;
	private TextView tvColumn3;
	private TextView tvColumn4;
	private TextView tvColumn5;
	private NextStationValidate stationValidate;

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		vsFirst = (ViewStub) this.findViewById(R.id.vs_outgoing_vsNextStation);
		vsFirst.inflate();
		if (GlobalParas.getGlobalParas().getProgramRole() != EProgramRole.station) {
			vsGoods = (ViewStub) this.findViewById(R.id.vs_outgoing_vsCagegory);
			vsGoods.inflate();
			rdoGoods = (RadioButton) this.findViewById(R.id.rdo_goods_category_rbGoods);
		}
		etNextStation = bindEditText(R.id.et_next_station_etNextStation, null, null);
		btnSelNextStation = bindButton(R.id.btn_next_station_btnSelStation);
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		stationValidate = new NextStationValidate(this);
		expressType = initValue;
		LinearLayout layout = (LinearLayout) findViewById(R.id.cheliang);
		layout.setVisibility(View.GONE);
	}

	@Override
	protected void initListView() {
		lvx = (ListViewEx) this.findViewById(R.id.lv_outgoing_lvBarcodeList);
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.station) {
			lvx.inital(R.layout.list_item_four, new String[] { barcodeKey, weightKey, nextStatoinKey, vehicleIdKey }, new int[] { R.id.tv_list_item_four_tvhead1, R.id.tv_list_item_four_tvhead2, R.id.tv_list_item_four_tvhead3, R.id.tv_list_item_four_tvhead4 });
		} else {
			lvx.inital(R.layout.list_item_five, new String[] { barcodeKey, weightKey, nextStatoinKey, vehicleIdKey, itemCategoryKey }, new int[] { R.id.tv_list_item_five_tvhead1, R.id.tv_list_item_five_tvhead2, R.id.tv_list_item_five_tvhead3, R.id.tv_list_item_five_tvhead4, R.id.tv_list_item_five_tvhead5 });
		}
	}

	@Override
	protected void setHeadTitle() {
		if (GlobalParas.getGlobalParas().getProgramRole() == EProgramRole.station) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_outgoing_vsHead4);
			vsListHead.inflate();
			tvColumn1 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead1);
			tvColumn2 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead2);
			tvColumn3 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead3);
			tvColumn4 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead4);
			tvColumn1.setText("单号");
			tvColumn2.setText("重量");
			tvColumn3.setText("下一站");
			tvColumn4.setText("车辆ID");
			tvColumn4.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(OutgoingStationActivity.this, 76), LayoutParams.WRAP_CONTENT));
		} else {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_outgoing_vsHead5);
			vsListHead.inflate();
			tvColumn1 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead1);
			tvColumn2 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead2);
			tvColumn3 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead3);
			tvColumn4 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead4);
			tvColumn5 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead5);
			tvColumn1.setText("单号");
			tvColumn2.setText("重量");
			tvColumn3.setText("下一站");
			tvColumn4.setText("车辆ID");
			tvColumn5.setText("物品类别");
		}
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_next_station_btnSelStation:
			openSlecotr(EDownType.NextStation);
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etNextStation.getText().toString().trim().equals("")) {
				PromptUtils.getInstance().showToastHasFeel("下一站不能为空", this, EVoiceType.fail, 0);
				etNextStation.requestFocus();
			} else {
				etBarcode.setText(etBarcode.getText().toString().trim());
				boolean flag = new math().CODE1(etBarcode.getText().toString());
				if (flag) {
					addRecord();
				} else {
					PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
					etBarcode.setText("");
					etBarcode.requestFocus();
				}
			}
			break;

		}
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:// 下一站
			if (hasFocus) {
				stationValidate.restoreNo(etNextStation);
			}
			break;
		case R.id.et_vehicleId_etVehicleId:
			stationValidate.showName(etNextStation);
			etBarcode.requestFocus();
			break;
		case R.id.et_barcode_etBarcode:// 单号
			if (hasFocus) {
				stationValidate.showName(etNextStation);
				if (!stationValidate.vlidateInputData(etNextStation)) {
					return;
				}
				if (!validateVehicleId(etVehicleId)) {
					return;
				}
			}
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.NextStation) {
			etNextStation.setText(res_2);
			etNextStation.setTag(res_1);
			controlUtil.setEditVeiwFocus(etVehicleId);
		}
	}

	@Override
	protected void addRecord() {

		if (etNextStation != null) {
			if (etNextStation.getText().toString().length() <= 0)
				return;
		}
		stationValidate.showName(etNextStation);
		if (!stationValidate.vlidateInputData(etNextStation)) {
			return;
		}

		if (GlobalParas.getGlobalParas().getProgramRole() != EProgramRole.station && GlobalParas.getGlobalParas().getProgramRole() != EProgramRole.center) {
			if (etVehicleId.getText().toString().length() <= 0)
				return;
		}

		if (etBarcode.getText().toString().length() <= 0)
			return;
		boolean flag = new math().CODE1(etBarcode.getText().toString());
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
			etBarcode.setText("");
			etBarcode.requestFocus();
		}

		String stationNo = etNextStation.getTag().toString().trim();
		String stationName = etNextStation.getText().toString().trim();
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods != null && rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}
		String vehicleId = etVehicleId.getText().toString().trim();

		String weightStr = "";
		if (tvWeight != null) {

			weightStr = tvWeight.getText().toString().trim();
		}

		String barcode = etBarcode.getText().toString().trim();
		String weight = "";
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			if (rdoGoods != null && rdoGoods.isChecked()) {
				// 货样
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(OutgoingStationActivity.this, "电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
					return;
				}
			} else if (rdoGoods != null && !rdoGoods.isChecked()) {
				// 非货样
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.startsWith("-")) {
					weightStr = "0.2kg";
				} else {
					// OutgoingRouteActivity
					double t = Double.parseDouble(weightStr.substring(0, weightStr.length() - 2));
					if (t < 0.2) {
						weightStr = "0.2kg";
					}
				}
			} else {
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(OutgoingStationActivity.this, "电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
					return;
				}
			}
			weight = weightStr.substring(0, weightStr.length() - 2);
		}

		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setNextStationCode(stationNo);
		if (rdoGoods != null) {
			scanDataModel.setSampleType(goodsType.value());
		}
		scanDataModel.setExpressType(expressType);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setWeight(weight);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(OutgoingStationActivity.this).add(scanDataModel);
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(weightKey, weightStr);
		map.put(nextStatoinKey, stationName);
		map.put(vehicleIdKey, vehicleId);
		if (rdoGoods != null) {
			map.put(itemCategoryKey, goodsType.toString().trim());
		}
		lvx.add(map);

		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}
}
