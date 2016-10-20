package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.DestinationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class PickupScanNophoneActivity extends ScanBaseActivity {

	Button btnSelStation;
	EditText etDestination;
	TextView tvStation;
	RadioButton rdoGoods;
	DestinationValidate destinationValidate;
	boolean isAddrecordEnable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_pickupnophone_scan);
		setTitle("收件扫描");
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
		if (etBarcode != null) {
			if (barcode.length() <= 7) {
				controlUtil.setEditVeiwFocus(etDestination);
				etDestination.setText(barcode.trim());
				controlUtil.setEditVeiwFocus(etBarcode);

				return;
			}

			etBarcode.setText(barcode);
			boolean flag = new math().CODE2(barcode);
			if (flag) {
				// if (etDestination.getText().toString().trim().length() <= 0)
				// {
				// Toast.makeText(getApplicationContext(), "目的地不能为空",
				// Toast.LENGTH_SHORT).show();
				// etDestination.requestFocus();
				// etBarcode.setText("");
				// } else {
				// addRecord();
				// }
				addRecord();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
				etBarcode.requestFocus();
			}
		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		rdoGoods = (RadioButton) findViewById(R.id.rdo_goods_category_rbGoods);
		tvStation = (TextView) findViewById(R.id.tv_next_station_tvNextStation);
		tvStation.setText("目的地");
		etDestination = bindEditText(R.id.et_next_station_etNextStation, null, null);
		btnSelStation = bindButton(R.id.btn_next_station_btnSelStation);

		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		destinationValidate = new DestinationValidate(PickupScanNophoneActivity.this);
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_pickupnop_lvBarcodeList);
		lvx.inital(R.layout.list_item_three, new String[] { barcodeKey, destinationKey, itemCategoryKey }, new int[] { R.id.tv_list_item_three_tvhead1, R.id.tv_list_item_three_tvhead2, R.id.tv_list_item_three_tvhead3, });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("单号");
		tvHead2.setText("目的地 ");
		tvHead3.setText("物品类型");
		tvHead1.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(PickupScanNophoneActivity.this, 130), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);
		if (barcode.length() < GlobalParas.getGlobalParas().getSiteNoMaxLen()) {
			controlUtil.setEditVeiwFocus(etDestination);
			etDestination.setText(barcode);
			controlUtil.setEditVeiwFocus(etBarcode);
		} else {
			etBarcode.setText(barcode);
			addRecord();
		}
	};

	@Override
	protected void initScanCode() {

		scanType = EScanType.SJ;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_next_station_btnSelStation:
			openSlecotr(EDownType.Destination);
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etBarcode != null) {
				boolean flag = new math().CODE2(etBarcode.getText().toString().trim());
				if (flag) {
					// if (etDestination.getText().toString().trim().length() <=
					// 0) {
					// Toast.makeText(getApplicationContext(), "目的地不能为空",
					// Toast.LENGTH_SHORT).show();
					// etDestination.requestFocus();
					// etBarcode.setText("");
					// } else {
					// addRecord();
					// }
					addRecord();
				} else {
					// Toast.makeText(getApplicationContext(), "非法条码",
					// Toast.LENGTH_SHORT).show();
					PromptUtils.getInstance().showAlertDialogHasFeel(PickupScanNophoneActivity.this, "非法条码", null, EVoiceType.fail, 0);

					etBarcode.setText("");
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void addRecord() {

		isAddrecordEnable = true;
		controlUtil.setEditVeiwFocus(etBarcode);
		if (isAddrecordEnable == false) {
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
		String destinationNo = "";
		if (etDestination.getTag() != null) {
			destinationNo = etDestination.getTag().toString().trim();
			;
		}
		String destinationName = etDestination.getText().toString().trim();
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}

		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setDestinationSiteCode(destinationNo);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		scanDataModel.setExpressType(this.initValue);

		// 添加到插入队列中
		AddScanDataThread.getIntance(PickupScanNophoneActivity.this).add(scanDataModel);
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(destinationKey, destinationName);
		map.put(itemCategoryKey, goodsType.toString().trim());
		lvx.add(map);

		scanCount++;
		etDestination.setText("");
		updateView();
	}

	@Override
	protected boolean validateInput() {

		if (etDestination.getText().toString().length() > 0) {
			if (!destinationValidate.vlidateInputData(etDestination)) {
				return false;
			}
		}
		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				String destination = etDestination.getText().toString();
				destinationValidate.showName(etDestination);
				if (!strUtil.isNullOrEmpty(destination)) {
					if (!destinationValidate.vlidateInputData(etDestination)) {
						isAddrecordEnable = false;
						return;
					}
				}
			}
			break;
		case R.id.et_next_station_etNextStation:
			if (hasFocus) {
				destinationValidate.restoreNo(etDestination);
			}
			break;
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		switch (selType) {
		case Destination:
			etDestination.setText(res_2);
			etDestination.setTag(res_1);

			controlUtil.setEditVeiwFocus(etBarcode);
			break;
		default:
			break;
		}
	};
}
