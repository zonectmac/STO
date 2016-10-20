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
import com.cneop.stoExpress.dataValidate.RouteValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.device.DeviceUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ZCFJActivity extends ScanBaseActivity {

	private EditText etCarLotNumber;
	private EditText etNextStation;
	private EditText etRoute;
	private Button btnSelNextStation;
	private Button btnSelRoute;
	private TextView tvNextStation;
	private TextView tvSecondStation;
	private ViewStub vsStub;
	private NextStationValidate stationValidate;
	private RouteValidate routeValidate;
	private String routeZcKey = "routezc";
	private DeviceUtil deviceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_zcfj);
		setTitle("装车发件");
		super.onCreate(savedInstanceState);
		super.scannerPower = true;
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	OnKeyListener onKey = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (v.getId()) {
			case R.id.et_carlotnumber_etCarLotNumber:
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					if (validateCarLotNumber((EditText) v)) {
						etCarLotNumber.clearFocus();
						if (etNextStation != null) {
							etNextStation.requestFocus();
						} else {
							if (etRoute != null) {
								etRoute.requestFocus();
							}
						}
					}
				}
				break;
			default:
				break;
			}
			return false;
		}
	};

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		if (etRoute != null) {
			try {
				if (new math().cqh(barcode)) {
					etCarLotNumber.setText(barcode);
					controlUtil.setEditVeiwFocus(etRoute);
					return;
				} else if (barcode.length() >= 7 && barcode.length() <= 10) {
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
					PromptUtils.getInstance().showToastHasFeel("单号非法", this, EVoiceType.fail, 0);
					return;
				}
				addRecord();

			} catch (Exception e) {
				// TODO: handle exception
			}
			// 网点跟中兴通用一个界面需要区分
		} else if (etNextStation != null) {
			try {
				if (new math().cqh(barcode)) {
					etCarLotNumber.setText(barcode);
					controlUtil.setEditVeiwFocus(etNextStation);
					return;
				} else if (barcode.length() == 6 && etNextStation != null) {
					controlUtil.setEditVeiwFocus(etNextStation);
					etNextStation.setText(barcode);
					if (etNextStation.getText().toString().length() > 0) {
						controlUtil.setEditVeiwFocus(etBarcode);
					} else {
						controlUtil.setEditVeiwFocus(etBarcode);
					}
					return;
				} else if (new math().CODE1(barcode)) {
					etBarcode.setText(barcode);
				} else {
					PromptUtils.getInstance().showToastHasFeel("单号非法", this, EVoiceType.fail, 0);
					controlUtil.setEditVeiwFocus(etBarcode);
					return;
				}
				addRecord();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etCarLotNumber = bindEditText(R.id.et_carlotnumber_etCarLotNumber, null, null);// 车签号
		// String digits = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZ";
		// etCarLotNumber.setKeyListener(DigitsKeyListener.getInstance(digits));
		etCarLotNumber.setOnKeyListener(onKey);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);// 单号
		etBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);

		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		if (!strUtil.isNullOrEmpty(initValue) && initValue.equals(routeZcKey)) { // 路由号
			vsStub = (ViewStub) this.findViewById(R.id.vs_zcfj_vsRoute);
			vsStub.inflate();
			etRoute = bindEditText(R.id.et_route_etRoute, null, null);
			btnSelRoute = bindButton(R.id.btn_route_btnSelRoute);
			tvNextStation = (TextView) this.findViewById(R.id.tv_route_tvNextDestination);
			tvSecondStation = (TextView) this.findViewById(R.id.tv_route_tvSecondDestination);
		} else { // 目的地
			vsStub = (ViewStub) this.findViewById(R.id.vs_zcfj_vsNextStation);
			vsStub.inflate();
			etNextStation = bindEditText(R.id.et_next_station_etNextStation, null, null);
			btnSelNextStation = bindButton(R.id.btn_next_station_btnSelStation);
		}
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		deviceUtil = new DeviceUtil(ZCFJActivity.this);
		deviceUtil.SetKeyboardChangeState(true);
		if (!strUtil.isNullOrEmpty(initValue) && initValue.equals(routeZcKey)) {
			routeValidate = new RouteValidate(ZCFJActivity.this, tvNextStation, tvSecondStation);
		} else {
			stationValidate = new NextStationValidate(ZCFJActivity.this);
		}
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_next_station_btnSelStation:
			openSlecotr(EDownType.NextStation);
			break;
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

		if (selType == EDownType.NextStation) {
			etNextStation.setText(res_2);
			etNextStation.setTag(res_1);
			ControlUtil.setEditVeiwFocus(etBarcode);
		} else if (selType == EDownType.Route) {
			etRoute.setText(res_1);
			etRoute.setTag(res_1);
			tvNextStation.setText(res_2);
			tvSecondStation.setText(res_3);
			tvNextStation.setTag(res_4); // 下一站转索引成数字形式
			ControlUtil.setEditVeiwFocus(etBarcode);

		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_zcfj_scan_lvBarcodeList);
		lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, carLotNumberKey, nextStatoinKey },
				new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2,
						R.id.tv_list_item_three_a_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("单号");
		tvHead2.setText("车签号");
		if (!strUtil.isNullOrEmpty(initValue) && initValue.equals(routeZcKey)) {
			tvHead3.setText("路由号");
		} else {
			tvHead3.setText("下一站");
		}
		tvHead1.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(ZCFJActivity.this, 130), LayoutParams.WRAP_CONTENT));
		tvHead2.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(ZCFJActivity.this, 130), LayoutParams.WRAP_CONTENT));
		tvHead3.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(this, 106.5f), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void initScanCode() {

		// 上传数据参数
		scanType = EScanType.ZC;
		uploadType = EUploadType.scanData;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void addRecord() {

		if (stationValidate != null) {
			stationValidate.showName(etNextStation);
			// if (etNextStation.getText().length() > 0) {
			if (!stationValidate.vlidateInputData(etNextStation)) {
				return;
				// }
			}
		}
		if (routeValidate != null) {
			routeValidate.showName(etRoute);
			if (!routeValidate.vlidateInputData(etRoute)) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "路由异常！", null, EVoiceType.fail, 0);

				return;
			}
		}
		if (!strUtil.isNullOrEmpty(initValue) && initValue.equals(routeZcKey)) { // 路由号
			if (!new math().cqh(etCarLotNumber.getText().toString().trim())) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "车签号非法", null, EVoiceType.fail, 0);
				return;
			} else if (!new math().luyouhao(etRoute.getText().toString().trim())) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "路由号非法", null, EVoiceType.fail, 0);
				return;
			} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "单号非法", null, EVoiceType.fail, 0);
				return;
			}
		} else {
			if (!new math().cqh(etCarLotNumber.getText().toString().trim())) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "车签号非法", null, EVoiceType.fail, 0);
				return;
			} else if (etNextStation.getText().toString().trim().length() <= 0) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "下一站非法", null, EVoiceType.fail, 0);
				return;
			} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
				PromptUtils.getInstance().showAlertDialogHasFeel(this, "单号非法", null, EVoiceType.fail, 0);
				return;
			}
		}

		String carLotNumber = etCarLotNumber.getText().toString().trim();
		String stationName = "";
		String nextStationId = "";
		String routeNo = "";
		// 2016.4.18.日网点号：
		if (!strUtil.isNullOrEmpty(initValue) && initValue.equals(routeZcKey)) {
			;
		} else {
			stationName = etNextStation.getText().toString().trim();
			nextStationId = etNextStation.getTag().toString().trim();
		}
		if (etRoute != null) {
			routeNo = etRoute.getText().toString().trim();
			nextStationId = tvNextStation.getTag().toString();

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
		scanDataModel.setCarLotNumber(carLotNumber);
		scanDataModel.setNextStationCode(nextStationId);
		scanDataModel.setRouteCode(routeNo);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(ZCFJActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(carLotNumberKey, carLotNumber);
		if (stationValidate != null) {
			map.put(nextStatoinKey, stationName);
		} else {
			map.put(nextStatoinKey, routeNo);
		}
		lvx.add(map);

		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	// -----------------------------------------------------------------------------------
	@Override
	protected boolean validateInput() {

		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:
			if (hasFocus) {
				deviceUtil.setCurrentInputMethod(0);
				// 给车签号加头部STO
				if (!validateCarLotNumber(etCarLotNumber)) {
					return;
				} else if (stationValidate != null) {
					stationValidate.restoreNo(etNextStation);
				} else if (routeValidate != null) {
					routeValidate.restoreNo(etRoute);
				}
			}
			break;
		case R.id.et_route_etRoute:
			if (hasFocus) {
				deviceUtil.setCurrentInputMethod(0);
				// 给车签号加头部STO
				if (!validateCarLotNumber(etCarLotNumber)) {
					return;
				}
				if (stationValidate != null) {
					stationValidate.restoreNo(etNextStation);
				}
				if (routeValidate != null) {
					routeValidate.restoreNo(etRoute);
				}

			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				deviceUtil.setCurrentInputMethod(0);
				if (!validateCarLotNumber(etCarLotNumber)) {
					return;
				}
				if (stationValidate != null) {
					stationValidate.showName(etNextStation);
					if (!stationValidate.vlidateInputData(etNextStation)) {
						return;
					}

				}
				if (routeValidate != null) {
					routeValidate.showName(etRoute);
					if (!routeValidate.vlidateInputData(etRoute)) {
						if (etRoute.getText().toString().length() <= 0)
							PromptUtils.getInstance().showAlertDialogHasFeel(this, "路由非法", null, EVoiceType.fail, 0);

						return;
					}
				}
			}
			break;
		}
	}
}
