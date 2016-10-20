package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.ServerStationService;
import com.cneop.stoExpress.dataValidate.MobileValidateUtil;
import com.cneop.stoExpress.dataValidate.ServerStationValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IListenDelSelRowSuc;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class MsgNoticeActivity extends ScanBaseActivity {

	EditText etSerStaNo, etSerStaAdd, etSerStaPho, etphonenum;
	MobileValidateUtil mobileValidateUtil;
	ServerStationService serverStationService;
	private final String phoneKey = "phone";
	private final String serverCodeKey = "serverCode";
	MsgSendService msgSendService;
	BrocastUtil brocastUtil;
	// Button btnAdd;
	Button btnDel;

	ServerStationValidate serverStationValidate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_serverstation_message);
		setTitle("��������");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
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
		if (etSerStaNo.getText().toString().trim().length() <= 0) {
			showmsg("������벻��Ϊ��");
			etSerStaNo.requestFocus();
		} else if (etSerStaAdd.getText().toString().trim().length() <= 0) {
			showmsg("�����ַ����Ϊ��");
			etSerStaAdd.requestFocus();
		} else if (etSerStaPho.getText().toString().trim().length() <= 0) {
			showmsg("����绰����Ϊ��");
			etSerStaPho.requestFocus();
		} else if (etphonenum.getText().toString().trim().length() <= 0) {
			showmsg("�ֻ����벻��Ϊ��");
			etphonenum.requestFocus();
		} else {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(barcode);
			if (flag) {
				addRecord();
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
				etBarcode.requestFocus();
				etBarcode.setText("");
			}
		}

	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		etSerStaNo = bindEditText(R.id.et_serverstation_code, null, null);
		etSerStaAdd = bindEditText(R.id.et_serverstation_address, null, null);
		etSerStaPho = bindEditText(R.id.et_serverstation_phone, null, null);
		etSerStaPho.setInputType(InputType.TYPE_CLASS_NUMBER);
		etphonenum = bindEditText(R.id.et_serverstation_phonenum, null, null);
		etphonenum.setInputType(InputType.TYPE_CLASS_NUMBER);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);

		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		btnDel = bindButton(R.id.bottom_3_btnDel);

		lvx.setDelSelectedRowListener(deldataListener);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		mobileValidateUtil = new MobileValidateUtil(MsgNoticeActivity.this);

		serverStationService = new ServerStationService(MsgNoticeActivity.this);
		msgSendService = new MsgSendService(MsgNoticeActivity.this);
		brocastUtil = new BrocastUtil(MsgNoticeActivity.this);

		serverStationValidate = new ServerStationValidate(MsgNoticeActivity.this, etSerStaAdd, etSerStaPho);

	}

	@Override
	protected void HandleScanData(String barcode) {
		super.HandleScanData(barcode);
		etBarcode.setText(barcode);
		addRecord();
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {

		case R.id.et_serverstation_phonenum:
			if (hasFocus) {
				validateServerCode();
			}
			break;
		case R.id.et_serverstation_code:
			if (!validateServerCode()) {
				return;
			}
			// mobileValidateUtil.validateMobilePhone(etphonenum, false);
			break;
		}

		switch (v.getId()) {
		case R.id.et_serverstation_address:
		case R.id.et_serverstation_phone:
		case R.id.et_serverstation_phonenum:
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				serverStationValidate.showName(etSerStaNo);
			}
			break;
		case R.id.et_serverstation_code:
			if (hasFocus) {
				serverStationValidate.restoreNo(etSerStaNo);
			}
			break;
		}
	}

	private boolean validateServerCode() {
		String serverCode = etSerStaNo.getText().toString().trim();
		if (strUtil.isNullOrEmpty(serverCode)) {

			return false;
		} else {
			String address = etSerStaAdd.getText().toString().trim();
			if (strUtil.isNullOrEmpty(address)) {
				return false;
			}
		}
		return true;
	}

	// ���ݿ���ɾ��
	protected IListenDelSelRowSuc deldataListener = new IListenDelSelRowSuc() {

		@Override
		public void delSuc(String columnName, String barcode) {
			MsgSendService service = new MsgSendService(MsgNoticeActivity.this);
			int t = service.delSingleData(barcode, "");
			if (t > 0) {
				// ɾ���ɹ�
				msgCount--;
				// GlobalParas.getGlobalParas().setMsgUnUploadCount(-1);
				brocastUtil.sendUnUploadCountChange(-1, EUploadType.msg);
				setUnuploadCount();
				updateView();
			}

		}
	};

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_serversm_lvBarcodeList);
		lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, phonekey, serverstationkey }, new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2, R.id.tv_list_item_three_a_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("����");
		tvHead2.setText("�ֻ�����");
		tvHead3.setText("��������");
		controlUtil.setControlLayoutWidth(tvHead1, 130, MsgNoticeActivity.this);
		controlUtil.setControlLayoutWidth(tvHead2, 130, MsgNoticeActivity.this);
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etSerStaNo.getText().toString().trim().length() <= 0) {
				showmsg("������벻��Ϊ��");
				etSerStaNo.requestFocus();
			} else if (etSerStaAdd.getText().toString().trim().length() <= 0) {
				showmsg("�����ַ����Ϊ��");
				etSerStaAdd.requestFocus();
			} else if (etSerStaPho.getText().toString().trim().length() <= 0) {
				showmsg("����绰����Ϊ��");
				etSerStaPho.requestFocus();
			} else if (etphonenum.getText().toString().trim().length() <= 0) {
				showmsg("�ֻ����벻��Ϊ��");
				etphonenum.requestFocus();
			} else {
				etBarcode.setText(etBarcode.getText().toString().trim());
				boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
				if (flag) {
					addRecord();
					etBarcode.requestFocus();
				} else {
					PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", this, EVoiceType.fail, 0);
					etBarcode.requestFocus();
					etBarcode.setText("");
				}
			}
			break;
		}
	}

	@Override
	protected void initScanCode() {
		uploadType = EUploadType.msg;
		scanType = EScanType.ALL;
	}

	@Override
	protected void addRecord() {
		controlUtil.setEditVeiwFocus(etBarcode);
		if (!validateInput()) {
			return;
		}

		String barcode = etBarcode.getText().toString().trim();
	
		String phone = etphonenum.getText().toString().trim();
		String serverNo = GlobalParas.getGlobalParas().getStationId() + etSerStaNo.getText().toString().trim();

		etphonenum.setText("");
		MsgSend msgsendModel = new MsgSend();

		msgsendModel.setBarcode(barcode);
		msgsendModel.setPhone(phone);
		msgsendModel.setServerNo(serverNo);
		msgsendModel.setStationId(GlobalParas.getGlobalParas().getStationId());
		msgsendModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		// msgsendModel.set

		// ��ӵ����������
		AddScanDataThread.getIntance(this).add(msgsendModel);

		// ���½���
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(phonekey, phone);
		map.put(serverstationkey, serverNo);
		lvx.add(map);

		msgCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// ����
		}

	}

	// ɨ���������ʾ
	protected void updateView() {
		// ��յ���
		etBarcode.setText("");
		controlUtil.setEditVeiwFocus(etBarcode);
		if (msgCount > 0) {
			btnAdd.setText(msgCount + "��");
		} else {
			btnAdd.setText(R.string.add);
		}
	}

	@Override
	protected void delData() {

		final Map<String, Object> map = lvx.GetSelValue();
		if (map == null) {
			PromptUtils.getInstance().showToastHasFeel("��ѡ��Ҫɾ����һ�", MsgNoticeActivity.this, EVoiceType.fail, 0);
			return;
		}
		PromptUtils.getInstance().showAlertDialog(MsgNoticeActivity.this, "ȷ��Ҫɾ����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String barcode = map.get(barcodeKey).toString();
				String serverCode = map.get(serverstationkey).toString();
				String phone = map.get(phoneKey).toString();
				if (msgSendService.delRecord(barcode, serverCode, phone) > 0) {
					lvx.delete(map);
					brocastUtil.sendUnUploadCountChange(-1, EUploadType.msg);
					msgCount--;
					updateView();
				} else {
					PromptUtils.getInstance().showToastHasFeel("ɾ��ʧ�ܣ�", MsgNoticeActivity.this, EVoiceType.fail, 0);
				}
			}
		}, null);
	}

	@Override
	protected boolean validateInput() {

		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		if (!validatePhone(etphonenum)) {
			return false;
		}
		if (!validateServerCode()) {
			return false;
		}
		return true;
	}

}
