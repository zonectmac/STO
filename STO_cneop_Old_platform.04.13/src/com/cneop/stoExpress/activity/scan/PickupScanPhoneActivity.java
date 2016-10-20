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
import com.cneop.stoExpress.dataValidate.DestinationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PickupScanPhoneActivity extends ScanBaseActivity {

	TextView tvFrom, tvTo, tvStation;
	EditText etFrom, etTo, etDestination;
	Button btnSelStation;
	RadioButton rdoGoods;
	DestinationValidate destinationValidate;
	String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pickupphone_scan);
		setTitle("�ռ�ɨ��");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		type = bundle.getString("type");
	}

	public void showmsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, 1).show();
	}

	/**
	 * ɨ������
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		etBarcode.setText(barcode);
		boolean flag = new math().CODE1(barcode);
		if (flag) {
			addRecord();
			etBarcode.requestFocus();
		} else {
			PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
			etBarcode.setText("");

		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		tvFrom = (TextView) findViewById(R.id.tv_carlotnumber_tvCarLotNumber);
		tvFrom.setText("�ķ��ֻ�");
		etFrom = bindEditText(R.id.et_carlotnumber_etCarLotNumber, null, null);
		etFrom.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
		etFrom.setInputType(InputType.TYPE_CLASS_NUMBER);
		tvTo = (TextView) findViewById(R.id.tv_pack_number_tvPackNo);
		tvTo.setText("�շ��ֻ�");
		etTo = bindEditText(R.id.et_pack_number_etPackNo, null, null);
		etTo.setInputType(InputType.TYPE_CLASS_NUMBER);
		rdoGoods = (RadioButton) findViewById(R.id.rdo_goods_category_rbGoods);

		tvStation = (TextView) findViewById(R.id.tv_next_station_tvNextStation);
		tvStation.setText("Ŀ�ĵ�");
		etDestination = bindEditText(R.id.et_next_station_etNextStation, null, null);
		btnSelStation = bindButton(R.id.btn_next_station_btnSelStation);

		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		destinationValidate = new DestinationValidate(PickupScanPhoneActivity.this);
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_pickupp_lvBarcodeList);
		lvx.inital(R.layout.list_item_five_a,
				new String[] { barcodeKey, destinationKey, itemCategoryKey, phoneFromKey, phoneToKey },
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
		tvHead1.setText("����");
		tvHead2.setText("Ŀ�ĵ�");
		tvHead3.setText("��Ʒ���");
		tvHead4.setText("�ķ��ֻ�");
		tvHead5.setText("�շ��ֻ�");
		controlUtil.setControlLayoutWidth(tvHead2, 130, PickupScanPhoneActivity.this);
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
			etBarcode.setText(etBarcode.getText().toString().trim());
			boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
			if (flag) {
				addRecord();
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
				etBarcode.setText("");

			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void addRecord() {

		// controlUtil.setEditVeiwFocus(etBarcode);
		if (etDestination.getText().toString().length() > 0) {
			destinationValidate.showName(etDestination);
			if (etDestination.getText().toString().length() <= 0)
				return;
		} else {
			PromptUtils.getInstance().showToastHasFeel("Ŀ�ĵز���Ϊ��", this, EVoiceType.fail, 0);
			return;
		}
		if (!validateInput()) {
			return;
		}
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
			return;
		}
		String destinationNo = "";
		if (etDestination.getTag() != null) {
			destinationNo = etDestination.getTag().toString().trim();
		}
		String destinationName = etDestination.getText().toString().trim();
		EGoodsType goodsType = EGoodsType.�ǻ���;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.����;
		}

		String senderPhone = etFrom.getText().toString().trim();
		String recipientPhone = etTo.getText().toString().trim();
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setDestinationSiteCode(destinationNo);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		scanDataModel.setSenderPhone(senderPhone);
		scanDataModel.setRecipientPhone(recipientPhone);
		scanDataModel.setExpressType(this.initValue);
		// ��ӵ����������
		AddScanDataThread.getIntance(PickupScanPhoneActivity.this).add(scanDataModel);
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// ����
		}
		// ���½���
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(destinationKey, destinationName);
		map.put(itemCategoryKey, goodsType.toString().trim());
		map.put(phoneFromKey, senderPhone);
		map.put(phoneToKey, recipientPhone);
		lvx.add(map);

		scanCount++;
		updateView();

		if (type.equals("phonesiger")) {
			etFrom.setText("");
			etTo.setText("");
		}
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
		if (etFrom.getText().toString().length() > 0) {
			if (!validatePhone(etFrom)) {
				return false;
			}
		}
		if (etTo.getText().toString().length() > 0) {
			if (!validatePhone(etTo)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_barcode_etBarcode:
		case R.id.et_carlotnumber_etCarLotNumber:
		case R.id.et_pack_number_etPackNo:
			if (hasFocus) {
				destinationValidate.showName(etDestination);
			}
			break;
		case R.id.et_next_station_etNextStation:
			if (hasFocus) {
				destinationValidate.restoreNo(etDestination);
			}
			break;
		}
	}

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
