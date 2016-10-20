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
		setTitle("װ������");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		ZD_FJ = "ZD_FJ";
	}

	@Override
	protected void onPause() {

		// ��Ҫ�ϴ������ļ�ZD,FJ,����ο��ϴ���ʽ
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
	 * ɨ������
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
					PromptUtils.getInstance().showToastHasFeel("����Ϊ��", this, EVoiceType.fail, 0);
					return;

				} else {
					etBarcode.setText(barcode);
					etBarcode.requestFocus();
				}

			} else {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
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
				// Toast.makeText(getApplicationContext(), "��һվ�Ƿ�",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("��һվ�Ƿ�", this, EVoiceType.fail, 0);
			} else if (!new math().daihao(etBag.getText().toString().trim())) {
				// Toast.makeText(getApplicationContext(), "���ŷǷ�",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("���ŷǷ�", this, EVoiceType.fail, 0);
			} else if (!new math().CODE1(etBarcode.getText().toString().trim())) {
				// Toast.makeText(getApplicationContext(), "���ŷǷ�",
				// Toast.LENGTH_SHORT).show();
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
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
		tvHead1.setText("����");
		tvHead2.setText("����");
		tvHead3.setText("��һվ");
		tvHead4.setText("����ID");
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
		String barcode = etBarcode.getText().toString().trim();// ����
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
			return;
		}
		String stationNo = etNextStation.getText().toString().trim();// 000053,��һվ
		xiayizhan = etNextStation.getTag().toString();
		String vehicleId = etVehicleId.getText().toString().trim();// ��¼����
		String packageNo = etBag.getText().toString().trim();// ����
		daihao = packageNo;
		String stationName = etNextStation.getText().toString().trim();// �㶫ʯ��վ
		// �������ݿ�
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setNextStationCode(etNextStation.getTag().toString());
		scanDataModel.setPackageCode(packageNo);
		scanDataModel.setVehicleNumber(vehicleId);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));

		// ��ӵ����������
		AddScanDataThread.getIntance(BaggingOutActivity.this).add(scanDataModel);
		// ���½���
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
			lockScreen();// ����
		}
	}

	@Override
	protected boolean validateInput() {

		if (!new math().daihao(etBag.getText().toString())) {
			// etBag.setText("");
			PromptUtils.getInstance().showToastHasFeel("���ŷǷ��� ", this, EVoiceType.fail, 0);
			return false;
		}
		if (!new math().CODE1(etBarcode.getText().toString())) {
			PromptUtils.getInstance().showToastHasFeel("���ŷǷ��� ", this, EVoiceType.fail, 0);
			return false;
		}
		// �����Լ����Լ�װ��
		if (etBag.getText().toString().trim().equals(etBarcode.getText().toString().trim())) {
			etBarcode.setText("");
			PromptUtils.getInstance().showToastHasFeel("ɨ�������װ��������ͬ�� ", this, EVoiceType.fail, 0);
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:// ��һվ
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
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "���ŷǷ��� ", null, EVoiceType.fail, 0);
					controlUtil.setEditVeiwFocus(etBag);
					etBag.setText("");
					return;
				}
				stationValidate.showName(etNextStation);
				if (!stationValidate.vlidateInputData(etNextStation)) {
					return;
				}
				if (!new math().daihao(etBag.getText().toString().trim())) {
					PromptUtils.getInstance().showAlertDialogHasFeel(this, "���ŷǷ�", null, EVoiceType.fail, 0);
					etBag.setText("");
				}

			}

		}

	}
}
