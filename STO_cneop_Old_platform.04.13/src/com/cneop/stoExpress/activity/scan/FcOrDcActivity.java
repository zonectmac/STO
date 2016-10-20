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
import com.cneop.stoExpress.dataValidate.NextStationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.device.DeviceUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class FcOrDcActivity extends ScanBaseActivity {

	private EditText etNextStation;
	private Button btnSelNextStation;
	private EditText etVehicleId;
	private NextStationValidate stationValidate;
	private ViewStub vsListHead;
	private TextView tvHead1;
	private TextView tvHead2;
	private TextView tvHead3;
	DeviceUtil deviceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_fc_or_dc);
		setTitle("到车");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		if (initValue.equals("fc")) {
			if (barcode.length() == 6) {
				controlUtil.setEditVeiwFocus(etNextStation);
				etNextStation.setText(barcode);
				if (etVehicleId.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etBarcode);
				} else {
					controlUtil.setEditVeiwFocus(etVehicleId);
				}
				return;
			} else if (new math().cqh(barcode)) {
				etBarcode.setText(barcode);
			} else {
				PromptUtils.getInstance().showToastHasFeel("条码非法", this, EVoiceType.fail, 0);
				return;
			}

			addRecord();

		} else if (initValue.equals("dc")) {
			if (new math().cqh(barcode)) {
				etBarcode.setText(barcode);
				addRecord();
			} else {
				PromptUtils.getInstance().showToastHasFeel("车签号非法", this, EVoiceType.fail, 0);
			}
		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		if (initValue.equals("fc")) { // 发车,初始化组件
			ViewStub vsNextStation = (ViewStub) this.findViewById(R.id.vs_dc_or_fc_vsNextStation);
			vsNextStation.inflate();
			ViewStub vsVehicleId = (ViewStub) this.findViewById(R.id.vs_dc_or_fc_vsVehicleId);
			vsVehicleId.inflate();
			etNextStation = bindEditText(R.id.et_next_station_etNextStation, null, null);
			etVehicleId = bindEditText(R.id.et_vehicleId_etVehicleId, null, null);
			etVehicleId.setInputType(InputType.TYPE_CLASS_NUMBER);// 只能输入数字
			btnSelNextStation = bindButton(R.id.btn_next_station_btnSelStation);
			etVehicleId.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
		}
		TextView tvBarcode = (TextView) this.findViewById(R.id.tv_barcode_tvBarcode);
		tvBarcode.setText("车签号");
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		etBarcode.setOnKeyListener(onKey);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		deviceUtil = new DeviceUtil(FcOrDcActivity.this);
		deviceUtil.SetKeyboardChangeState(true);
		if (initValue.equals("fc")) {
			setTitle("发车");
			stationValidate = new NextStationValidate(FcOrDcActivity.this);
		}
		etBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);

		if (initValue.equals("fc")) {
			// 要调试
			if (barcode.length() == GlobalParas.getGlobalParas().getNextSiteLen() && etNextStation.hasFocus()) {
				etNextStation.setText(barcode);
				controlUtil.setEditVeiwFocus(etVehicleId);
			} else if (barcode.length() == GlobalParas.getGlobalParas().getCarLotNumberLen()
					&& etVehicleId.hasFocus()) {
				etVehicleId.setText(barcode);
				controlUtil.setEditVeiwFocus(etBarcode);
			} else {
				etBarcode.setText(barcode);
				addRecord();
			}
		} else {
			etBarcode.setText(barcode);
			addRecord();
		}
	};

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
			if (initValue.equals("fc")) {
				if (etNextStation.getText().toString().trim().length() <= 0) {
					Toast.makeText(getApplicationContext(), "下一站非法", 1).show();
				} else if (etVehicleId.getText().toString().trim().length() != 6) {
					Toast.makeText(getApplicationContext(), "车辆ID非法", 1).show();
				} else if (!new math().cqh(etBarcode.getText().toString().trim())) {
					// Toast.makeText(getApplicationContext(), "车签号非法",
					// 1).show();
					PromptUtils.getInstance().showToastHasFeel("车签号非法", this, EVoiceType.fail, 0);// 加了声音提示（可以自由选择）
				} else {
					addRecord();
				}
			} else {
				if (new math().CODE1(etBarcode.getText().toString().trim())) {
					PromptUtils.getInstance().showToastHasFeel("单号非法", this, EVoiceType.fail, 0);
				} else {
					addRecord();
				}
			}

			break;

		default:
			break;
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
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_fc_or_dc_scan_lvBarcodeList);
		if (initValue.equals("fc")) {
			lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, nextStatoinKey, vehicleIdKey },
					new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2,
							R.id.tv_list_item_three_a_tvhead3 });
		} else {
			// 到车
			lvx.inital(R.layout.list_item_one, new String[] { barcodeKey },
					new int[] { R.id.tv_list_item_one_tvhead1 });
		}
	}

	@Override
	protected void setHeadTitle() {

		if (initValue.equals("fc")) {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_fc_or_dc_vsListHead3);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
			tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
			tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
			tvHead1.setText("车签号");
			tvHead2.setText("下一站");
			tvHead3.setText("车辆ID");
			// tvHead3.setVisibility(View.GONE);
			tvHead1.setLayoutParams(
					new LayoutParams(ScreenUtil.dip2px(FcOrDcActivity.this, 130), LayoutParams.WRAP_CONTENT));
			tvHead2.setLayoutParams(
					new LayoutParams(ScreenUtil.dip2px(FcOrDcActivity.this, 130), LayoutParams.WRAP_CONTENT));
			tvHead3.setLayoutParams(
					new LayoutParams(ScreenUtil.dip2px(FcOrDcActivity.this, 106.5f), LayoutParams.WRAP_CONTENT));
		} else {
			vsListHead = (ViewStub) this.findViewById(R.id.vs_fc_or_dc_vsListHead1);
			vsListHead.inflate();
			tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_one_tvhead1);
			tvHead1.setText("车签号");
			tvHead1.setLayoutParams(
					new LayoutParams(ScreenUtil.dip2px(FcOrDcActivity.this, 310), LayoutParams.WRAP_CONTENT));
		}
	}

	@Override
	protected void initScanCode() {

		if (initValue.equals("fc")) {
			scanType = EScanType.FC;
		} else {
			scanType = EScanType.DC;
		}
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		if (!validateInput()) {
			return;
		}
		String nextStationNo = "";
		String nextStationName = "";
		String vehicleId = "";
		String carLotNumber = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(carLotNumber, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("车签号：" + carLotNumber + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		if (initValue.equals("fc")) {
			stationValidate.showName(etNextStation);
			if (!stationValidate.vlidateInputData(etNextStation)) {
				return;
			}
			nextStationNo = etNextStation.getTag().toString().trim();
			nextStationName = etNextStation.getText().toString().trim();
			vehicleId = etVehicleId.getText().toString().trim();
		}
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, carLotNumber);
		if (initValue.equals("fc")) {
			map.put(vehicleIdKey, vehicleId);
			map.put(nextStatoinKey, nextStationName);
		}
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setNextStationCode(nextStationNo);
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setBarcode(carLotNumber);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(FcOrDcActivity.this).add(scanDataModel);
		lvx.add(map);
		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (initValue.equals("fc")) {
			if (!stationValidate.vlidateInputData(etNextStation)) {
				return false;
			}
			if (etVehicleId.getText().toString().length() <= 0) {
				PromptUtils.getInstance().showToastHasFeel("车辆ID非法", this, EVoiceType.fail, 0);
				return false;
			}
		}

		if (!validateCarLotNumber(etBarcode)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		//
		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:
			if (hasFocus) {
				// stationValidate.showName(etNextStation);
				deviceUtil.setCurrentInputMethod(0);
				stationValidate.restoreNo(etNextStation);
				if (initValue.equals("fc")) {
					if (etBarcode.getText().toString().length() > 0)
						validateCarLotNumber(etBarcode);
				}
			}
			break;
		case R.id.et_vehicleId_etVehicleId:
			if (hasFocus) {
				stationValidate.showName(etNextStation);
				if (!stationValidate.vlidateInputData(etNextStation)) {
					return;
				}
				if (initValue.equals("fc")) {
					if (etBarcode.getText().toString().length() > 0) {
						validateCarLotNumber(etBarcode);
					}
				}
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				if (initValue.equals("fc")) {
					if (etVehicleId.getText().toString().length() != 6) {
						PromptUtils.getInstance().showAlertDialogHasFeel(this, "车辆ID非法", null, EVoiceType.fail, 0);
						etVehicleId.setText("");
						return;
					}
					stationValidate.showName(etNextStation);
					if (!stationValidate.vlidateInputData(etNextStation)) {
						return;
					}
				}
			}
		}
	}

	OnKeyListener onKey = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.et_barcode_etBarcode:
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					if (validateCarLotNumber((EditText) v)) {
						etBarcode.clearFocus();

					}

				}
				break;
			default:
				break;
			}
			return false;

		}
	};
}
