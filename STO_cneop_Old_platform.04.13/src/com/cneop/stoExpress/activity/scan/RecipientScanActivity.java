package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.DestinationValidate;
import com.cneop.stoExpress.dataValidate.UserValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.device.DeviceUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class RecipientScanActivity extends ScanBaseActivity {
	private Button btnSelUser, btnSelDestination;
	private EditText etUser, etDestination;
	private TextView tvWeight;
	private RadioButton rdoGoods;
	private UserValidate userValidate;
	private DestinationValidate destinationValidate;
	private String expressType = ""; // �Ƿ���24Сʱɨ��Ϊ1
	private ViewStub vs_tvWeight;
	private DeviceUtil deviceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_recipient_scan);
		setTitle("�ռ�ɨ��");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deviceUtil.setCurrentInputMethod(0);
		deviceUtil.SetKeyboardChangeState(false);
	}

	/**
	 * ɨ������
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		try {
			PromptUtils.getInstance().closeAlertDialog();
			if (barcode.length() == 10 && barcode.substring(0, 6).equals(GlobalParas.getGlobalParas().getStationId())) {
				if (etUser != null) {
					controlUtil.setEditVeiwFocus(etUser);
					etUser.setText(barcode.substring(6));
				}
				controlUtil.setEditVeiwFocus(etBarcode);
				return;
			} else if (barcode.length() == 4) {
				if (etUser != null) {
					controlUtil.setEditVeiwFocus(etUser);
					etUser.setText(barcode);
				}
				controlUtil.setEditVeiwFocus(etBarcode);
				return;
			} else if (barcode.length() < 4) {
				if (etDestination != null) {
					controlUtil.setEditVeiwFocus(etDestination);
					etDestination.setText(barcode);
				}
				controlUtil.setEditVeiwFocus(etBarcode);
				return;
			}
			etBarcode.setText(barcode);
			addRecord();
		} catch (Exception e) {
			PromptUtils.getInstance().showAlertDialogHasFeel(getApplicationContext(), e.getMessage() + "", null,
					EVoiceType.fail, 0);
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();// �ײ�����ť
		btnSelUser = bindButton(R.id.btn_recipient_scan_btnSelUser);// �ռ���ѡ��
		btnSelDestination = bindButton(R.id.btn_recipient_scan_btnSelDestination);// Ŀ�ĵ�ѡ��
		etUser = bindEditText(R.id.et_recipient_scan_etRecipienter, null, null);// �˹����
		etDestination = bindEditText(R.id.et_recipient_scan_etDestination, null, null);// Ŀ�ĵ�
		etBarcode = bindEditText(R.id.et_recipient_scan_etBarcode, null, null);// ����
		btnAdd = bindButton(R.id.btn_recipient_scan_btnAdd);// ���
		rdoGoods = (RadioButton) this.findViewById(R.id.rdo_recipient_scan_rbGoods);// ����
		// ���������ӳƲ���ʾ����
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			vs_tvWeight = (ViewStub) findViewById(R.id.vs_reciptient_scan_tvWeight);
			vs_tvWeight.inflate();
			tvWeight = (TextView) this.findViewById(R.id.tv_weight_tvShowWeight);
		}
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		deviceUtil = new DeviceUtil(RecipientScanActivity.this);
		deviceUtil.SetKeyboardChangeState(true);

		userValidate = new UserValidate(this, GlobalParas.getGlobalParas().getStationId());
		destinationValidate = new DestinationValidate(this);
		expressType = initValue;
		openBluetooth();
	}

	@Override
	protected void initListView() {
		lvx = (ListViewEx) this.findViewById(R.id.lv_recipient_scan_lvBarcodeList);
		lvx.inital(R.layout.list_item_five,
				new String[] { barcodeKey, weightKey, recipientKey, destinationKey, itemCategoryKey },
				new int[] { R.id.tv_list_item_five_tvhead1, R.id.tv_list_item_five_tvhead2,
						R.id.tv_list_item_five_tvhead3, R.id.tv_list_item_five_tvhead4,
						R.id.tv_list_item_five_tvhead5 });
	}

	@Override
	protected void showWeight(String text) {
		tvWeight.setText(text);
		super.showWeight(text);
	}

	@Override
	protected void initScanCode() {
		scanType = EScanType.SJ;
		uploadType = EUploadType.scanData;
	}

	// ����ͷ������
	@Override
	protected void setHeadTitle() {
		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead3);
		TextView tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead4);
		TextView tvHead5 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead5);
		tvHead1.setText("����");
		tvHead2.setText("����");
		tvHead3.setText("�ռ�Ա");
		tvHead4.setText("Ŀ�ĵ� ");
		tvHead5.setText("��Ʒ����");
	}

	public boolean isNumeric(String str) {
		// true ����
		// false ������
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_recipient_scan_etRecipienter:// �˹����(����ռ������ʱ��������������һ�����ͻ�ý��㣩)
			if (hasFocus) {
				// �û����Ż�ԭ
				userValidate.restoreNo(etUser);
				// todo:����Ŀ�ĵ�
				// destinationValidate.showName(etDestination);//(����û��ʲô��)
			}
			break;
		case R.id.et_recipient_scan_etDestination:// Ŀ�ĵ�
			if (hasFocus) {
				// �����û���
				userValidate.showName(etUser);
				// Ŀ�صش��뻹ԭ
				destinationValidate.restoreNo(etDestination);
				if (!userValidate.vlidateInputData(etUser)) {
					return;
				}
			}
			break;
		case R.id.et_recipient_scan_etBarcode:// ����
			if (hasFocus) {
				userValidate.showName(etUser);
				if (!userValidate.vlidateInputData(etUser)) {
					return;
				}
				destinationValidate.showName(etDestination);
				if (!destinationValidate.vlidateInputData(etDestination)) {
					return;
				}
			}
			break;
		case R.id.btn_recipient_scan_btnAdd:
			break;
		}
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_recipient_scan_btnSelUser:
			openSlecotr(EDownType.User);
			break;
		case R.id.btn_recipient_scan_btnSelDestination:
			openSlecotr(EDownType.Destination);
			break;
		case R.id.btn_recipient_scan_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (!validateInput()) {
				return;
			}
			addRecord();
			break;
		}
	}

	// ����
	@Override
	protected void addRecord() {
		if (!validateInput()) {
			return;
		}
		userValidate.showName(etUser);
		if (!userValidate.vlidateInputData(etUser)) {
			return;
		}
		if (etDestination.getText().toString().length() > 0) {
			destinationValidate.showName(etDestination);
			if (!destinationValidate.vlidateInputData(etDestination)) {
				return;
			}
		} else {
			PromptUtils.getInstance().showToastHasFeel("Ŀ�ĵز���Ϊ��", this, EVoiceType.fail, 0);
			return;
		}

		String barcode = etBarcode.getText().toString().trim();// ����
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//��ȡlistview�б���λ��
		if (existsBarcode>=0) {//�жϵ����Ƿ����б��Ѿ�����
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
			return;
		}
		String recipientName = etUser.getText().toString().trim();// Ա�����
		String user = etUser.getTag().toString().trim();// �õ�Ա�����
		String destinationNo = "";// Ŀ�ĵ�
		if (etDestination.getTag() != null) {
			destinationNo = etDestination.getTag().toString().trim();
		}
		String destinationName = etDestination.getText().toString().trim();

		String weightStr = "";
		if (tvWeight != null) {
			weightStr = tvWeight.getText().toString().trim();
		}
		EGoodsType goodsType = EGoodsType.�ǻ���;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.����;
			etDestination.setFocusableInTouchMode(true);
			etDestination.requestFocus();
		}
		String weight = "";
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			if (rdoGoods.isChecked()) {
				// ����
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(RecipientScanActivity.this,
							"���ӳ���ʾ�����쳣��Ϊ0�����������ݣ�������ӳӣ�", null, EVoiceType.fail, 0);
					return;
				}
			} else {
				// �ǻ���
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.startsWith("-")) {
					weightStr = "0.2kg";
				} else {
					double t = Double.parseDouble(weightStr.substring(0, weightStr.length() - 2));
					if (t < 0.2) {
						weightStr = "0.2kg";
					}
				}
			}
			weight = weightStr.substring(0, weightStr.length() - 2);
		}
		// �������ݿ�
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setCourier(user);// yon
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setExpressType(expressType);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setDestinationSiteCode(destinationNo);
		scanDataModel.setWeight(weight);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// ��ӵ����������
		AddScanDataThread.getIntance(this).add(scanDataModel);

		// ���½���
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(weightKey, weightStr);
		map.put(recipientKey, recipientName);
		map.put(destinationKey, destinationName);
		map.put(itemCategoryKey, goodsType.toString().trim());
		lvx.add(map);
		scanCount++;

		updateView();

		if (AppContext.getAppContext().getIsLockScreen())

		{
			lockScreen();// ����
		}
	}

	@Override
	protected boolean validateInput() {
		if (!userValidate.vlidateInputData(etUser)) {
			PromptUtils.getInstance().showToastHasFeel("�ռ��˲���Ϊ��", this, EVoiceType.fail, 0);
			return false;
		}
		boolean flag = new math().CODE1(etBarcode.getText().toString().trim());// �ֶ��������
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
			etBarcode.setText("");
			return false;
		}

		return true;
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		super.setSelResult(selType, res_1, res_2, res_3, res_4);
		switch (selType) {
		case User:
			etUser.setText(res_2);
			etUser.setTag(res_1);
			controlUtil.setEditVeiwFocus(etDestination);// ���ָ��Ŀ�ĵ�
			break;
		case Destination:
			etDestination.setText(res_2);
			etDestination.setTag(res_1);
			ControlUtil.setEditVeiwFocus(etBarcode);
			break;
		}
	}
}
