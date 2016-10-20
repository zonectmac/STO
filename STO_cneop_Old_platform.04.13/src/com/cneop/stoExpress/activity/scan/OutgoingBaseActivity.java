package com.cneop.stoExpress.activity.scan;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class OutgoingBaseActivity extends ScanBaseActivity {

	Button btnSelNextStation;
	Button btnSelRoute;
	EditText etVehicleId;
	EditText etNextStation;
	EditText etRoute;
	TextView tvWeight;
	TextView tvNextStation;
	TextView tvSecondStation;
	RadioButton rdoGoods;
	ViewStub vsFirst;
	ViewStub vsGoods;
	ViewStub vsListHead;
	ViewStub vsWeight;
	protected String expressType = ""; // 是否是24小时扫描为1

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_outgoing);
		setTitle("发件");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		findViewById(R.id.tv_vehicleId_tvVehicleId).setVisibility(View.GONE);
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		if (etRoute!=null) {
			if (barcode.length() > 6 && barcode.length() <= 10) {
				controlUtil.setEditVeiwFocus(etRoute);
				etRoute.setText(barcode);
				if (etRoute.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etBarcode);
				} else {
					controlUtil.setEditVeiwFocus(etBarcode);
				}
				return;
			} else {
				if (!new math().CODE1(barcode)) {
					PromptUtils.getInstance().showToastHasFeel("单号非法", this, EVoiceType.fail, 0);
					etBarcode.requestFocus();
					return;
				} else {
					etBarcode.setText(barcode);
				}
			}
			addRecord();
		} else if (etNextStation!=null) {
			if (barcode.length() == 6) {
				controlUtil.setEditVeiwFocus(etNextStation);
				etNextStation.setText(barcode);
				if (etNextStation.getText().toString().length() > 0) {
					controlUtil.setEditVeiwFocus(etBarcode);
				} else {
					controlUtil.setEditVeiwFocus(etBarcode);
				}
				return;
			} else {
				if (!new math().CODE1(barcode)) {
					PromptUtils.getInstance().showToastHasFeel("单号非法", this, EVoiceType.fail, 0);
					etBarcode.requestFocus();
					return;
				} else {
					etBarcode.setText(barcode);

				}
			}
			addRecord();
		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etVehicleId = bindEditText(R.id.et_vehicleId_etVehicleId, null, null);
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			vsWeight = (ViewStub) findViewById(R.id.vs_outgoing_tvWeight);
			if (vsWeight != null) {
				vsWeight.inflate();
				tvWeight = (TextView) this.findViewById(R.id.tv_weight_tvShowWeight);
			}

		}
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		expressType = initValue;

		openBluetooth();
	}

	@Override
	protected void showWeight(String text) {

		tvWeight.setText(text);
		super.showWeight(text);
	}

	@Override
	protected void initListView() {

	}

	@Override
	protected void setHeadTitle() {

	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.FJ;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

	}

	// 子类重写
	@Override
	protected boolean validateInput() {

		return DEBUG;

	}

}
