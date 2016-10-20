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
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BaggingOutActivity extends ScanBaseActivity {

	public static String ZD_FJ = "";
	EditText etNextStation;
	Button btnNextStation;
	EditText etVehicleId;
	EditText etBag;
	NextStationValidate stationValidate;
	private String expressType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_bagging_out);
		setTitle("装袋发件");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		ZD_FJ = "ZD_FJ";
	}

	@Override
	protected void onPause() {

		// 需要上传两个文件ZD,FJ,具体参考上传格式
		// TODO Auto-generated method stub
		super.onPause();
		ZD_FJ = "";
	}

	@Override
	protected void onRestart() {

		// TODO Auto-generated method stub
		super.onRestart();
		ZD_FJ = "ZD_FJ";
	}

	/**
	 * 扫描数据
	 */
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		try {
			int codelength = barcode.length();
			if (codelength == 6) {
				controlUtil.setEditVeiwFocus(etNextStation);
				etNextStation.setText(barcode);

				if (etBag.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etBarcode);
				} else {
					controlUtil.setEditVeiwFocus(etBag);
				}
				return;
			}

			else if (new math().daihao(barcode) && etBag.hasFocus()) {
				etBag.setText(barcode);
				etBarcode.requestFocus();
				return;
			} else if (new math().CODE1(barcode)) {
				if (etBag.getText().toString().length() <= 0) {
					PromptUtils.getInstance().showToastHasFeel("袋号为空", this, EVoiceType.fail, 0);
					return;

				} else {
					etBarcode.setText(barcode);
					etBarcode.requestFocus();
				}

			} else {
				PromptUtils.getInstance().showToastHasFeel("非法单号", this, EVoiceType.fail, 0);
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
		etNextStation = bindEditText(R.id.et_next_station_etNextStation, null, null);
		btnNextStation = bindButton(R.id.btn_next_station_btnSelStation);
		etVehicleId = bindEditText(R.id.et_vehicleId_etVehicleId, null, null);
		etVehicleId.setVisibility(View.INVISIBLE);
		findViewById(R.id.tv_vehicleId_tvVehicleId).setVisibility(View.INVISIBLE);
		etBag = bindEditText(R.id.et_package_number_etPackageNo, null, null);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		stationValidate = new NextStationValidate(BaggingOutActivity.this);
		expressType = initValue;
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
			if (etNextStation.getText().toString().trim().length() <= 0) {
				// Toast.makeText(getApplicationContext(), "下一站非法",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("下一站非法", this, EVoiceType.fail, 0);
			} else if (!new math().daihao(etBag.getText().toString().trim())) {
				// Toast.makeText(getApplicationContext(), "袋号非法",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("袋号非法", this, EVoiceType.fail, 0);
			} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
				// Toast.makeText(getApplicationContext(), "单号非法",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("非法单号", this, EVoiceType.fail, 0);
			} else {
				addRecord();
			}
			break;
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.NextStation) {
			etNextStation.setText(res_2);
			etNextStation.setTag(res_1);
			ControlUtil.setEditVeiwFocus(etVehicleId);
			etBag.requestFocus();
		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_bagging_out_lvBarcodeList);
		lvx.inital(R.layout.list_item_four_a, new String[] { barcodeKey, packageNoKey, nextStatoinKey, vehicleIdKey },
				new int[] { R.id.tv_list_item_four_a_tvhead1, R.id.tv_list_item_four_a_tvhead2,
						R.id.tv_list_item_four_a_tvhead3, R.id.tv_list_item_four_a_tvhead4 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead3);
		TextView tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead4);
		tvHead1.setText("单号");
		tvHead2.setText("袋号");
		tvHead3.setText("下一站");
		tvHead4.setText("车辆ID");
		tvHead2.setLayoutParams(
				new LayoutParams(ScreenUtil.dip2px(BaggingOutActivity.this, 120), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.ZD;
		uploadType = EUploadType.scanData;
	}

	public static String xiayizhan = "";
	public static String daihao = "";

	@Override
	protected void addRecord() {

		if (etNextStation.getText().toString().length() > 0) {
			stationValidate.showName(etNextStation);
			if (!stationValidate.vlidateInputData(etNextStation)) {
				return;
			}
		}

		if (!validateInput()) {
			return;
		}
		String barcode = etBarcode.getText().toString().trim();// 单号
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String stationNo = etNextStation.getText().toString().trim();// 000053,下一站
		xiayizhan = etNextStation.getTag().toString();
		String vehicleId = etVehicleId.getText().toString().trim();// 登录密码
		String packageNo = etBag.getText().toString().trim();// 袋号
		daihao = packageNo;
		String stationName = etNextStation.getText().toString().trim();// 广东石龙站
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setNextStationCode(etNextStation.getTag().toString());
		scanDataModel.setPackageCode(packageNo);
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));

		// 添加到插入队列中
		AddScanDataThread.getIntance(BaggingOutActivity.this).add(scanDataModel);
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(packageNoKey, packageNo);
		map.put(nextStatoinKey, stationName);
		map.put(vehicleIdKey, vehicleId);
		lvx.add(map);
		scanCount++;
		updateView();
		// lockScreen();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (!new math().daihao(etBag.getText().toString())) {
			// etBag.setText("");
			PromptUtils.getInstance().showToastHasFeel("袋号非法！ ", this, EVoiceType.fail, 0);
			return false;
		}
		if (!new math().CODE1(etBarcode.getText().toString())) {
			PromptUtils.getInstance().showToastHasFeel("单号非法！ ", this, EVoiceType.fail, 0);
			return false;
		}
		// 不能自己将自己装包
		if (etBag.getText().toString().trim().equals(etBarcode.getText().toString().trim())) {
			etBarcode.setText("");
			PromptUtils.getInstance().showToastHasFeel("扫描袋号与装袋袋号相同！ ", this, EVoiceType.fail, 0);
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:// 下一站
			stationValidate.restoreNo(etNextStation);
			break;
		case R.id.et_package_number_etPackageNo:

			stationValidate.showName(etNextStation);
			if (!stationValidate.vlidateInputData(etNextStation)) {
				etNextStation.requestFocus();
				return;
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				if (etBag.getText().toString().trim().length() <= 0) {
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "袋号非法！ ", null, EVoiceType.fail, 0);
					controlUtil.setEditVeiwFocus(etBag);
					etBag.setText("");
					return;
				}
				stationValidate.showName(etNextStation);
				if (!stationValidate.vlidateInputData(etNextStation)) {
					return;
				}
				if (!new math().daihao(etBag.getText().toString().trim())) {
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "袋号非法", null, EVoiceType.fail, 0);
					etBag.setText("");
				}

			}

		}

	}
}
