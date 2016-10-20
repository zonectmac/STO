package com.cneop.stoExpress.activity.scan;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cneop.stoExpress.activity.MainActivity;
import com.cneop.stoExpress.activity.ObjectSerectorActivity;
import com.cneop.stoExpress.activity.common.CustomDialogActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EAddThreadOpt;
import com.cneop.stoExpress.common.Enums.EAddType;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.dataValidate.UserValidate;
import com.cneop.stoExpress.datacenter.UploadThread;
import com.cneop.stoExpress.util.BarcodeCheck;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.SoundUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.bluetooth.BluetoothUtil;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IListenDelSelRowSuc;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.BarcodeScan;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.ScantrigCon;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ShowToast")
public abstract class ScanBaseActivity extends CommonTitleActivity {

	private EditText etPackNo;
	protected Button btnUpload;
	protected Button btnDel;
	protected Button btnBack;
	protected Button btnAdd;
	protected EditText etBarcode;
	protected ListViewEx lvx;

	protected int scanCount; // ɨ������
	protected int msgCount;// ��������
	private UserValidate userValidate;
	protected EScanType scanType;// ɨ������
	protected EUploadType uploadType;// �ϴ�����
	protected ESiteProperties siteProperties;// վ������
	protected StrUtil strUtil;
	protected BarcodeCheck barcodeCheck;
	protected ControlUtil controlUtil;
	protected ScanDataService scanDataService;
	protected String barcodeKey = "barcode"; // ����
	protected String weightKey = "weight"; // ����
	protected String recipientKey = "recipienter"; // �ռ���
	protected String destinationKey = "destination"; // Ŀ�ĵ�
	protected String itemCategoryKey = "itemCategory"; // ��Ʒ���
	protected String nextStatoinKey = "nextStation"; // ��һվ
	protected String vehicleIdKey = "vehicleId"; // ����ID
	protected String routeNoKey = "routeNo"; // ·�ɺ�
	protected String abnormalKey = "abnormalNo"; // ���������
	protected String packageNoKey = "packageNo"; // ����
	protected String carLotNumberKey = "carLotNumber";// ��ǩ��
	protected String phoneFromKey = "phoneFrom";// �ķ��ֻ�
	protected String phoneToKey = "phoneTo";// �շ��ֻ�
	protected String sigerKey = "signer";// ǩ����
	protected String signTypeKey = "sign";// ǩ��
	protected String phonekey = "phone";// �ֻ�����
	protected String serverstationkey = "serverstation";// ��������
	private String mudetypekey = "mudetype";// ҵ������
	protected String amountkey = "amount";// ����
	protected String unsendkey = "unupload";// δ��
	protected String sendkey = "upload";// �ѷ�
	private String opertypekey = "opertype";// ��������
	protected String barcodeValidateErrorTip = "���Ų���Ϊ��";
	protected String packageNoValidateErrorTip = "�����쳣��";
	protected String signerValidateErrorTip = "ǩ�����쳣";
	protected String initValue = ""; // ��ʼ��ֵ
	protected BrocastUtil brocastUtil;
	protected final int delRequestCode = 0x02;
	protected int selectorRequestCode = 0x01;
	private ItemDelEvent itemDelEvent = new ItemDelEvent();
	private BluetoothUtil bluetoothUtil;
	protected ProgressDialog pd = null;
	private int enterCount;
	private ScantrigCon mScantrigCon;
	public static String ImagePath = "iamgepath";
	private boolean IsBthConnected = false;

	protected abstract void initListView();

	protected abstract void setHeadTitle();

	private boolean lockLongPressKey;// �Ƿ񳤰� // ��ɨ��ҳ��ʱ,������������,����ٵĳ�����

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (Build.MODEL.equals("CN-S3")) {
			mBarcodeScan.close();
		} else if (Build.MODEL.equals("NLS-MT60")) {
			sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));
		}
	}

	/**
	 * ��Ҫ�����´�½��s3�豸���޸�ʱ����� Author������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**
		 * ɨ���ֵ�� �м�� 139 ���� 140 �Ҳ�� 141
		 */
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (AppContext.getAppContext().getIsLockScreen()) {
				lockScreen();// ����
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			if (Build.MODEL.equals("CN-S3")) {
				mBarcodeScan.close();
			} else if (Build.MODEL.equals("NLS-MT60")) {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));
			}
			finish();
			break;
		case 139:
			// Toast.makeText(getApplicationContext(), "�м�� 139", 1).show();
			guard_time();
			break;
		case 140:
			// Toast.makeText(getApplicationContext(), "�Ҳ�� 141", 1).show();
			guard_time();
			break;
		case 141:
			// Toast.makeText(getApplicationContext(), "���� 140", 1).show();
			guard_time();
			break;   
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_MUTE:
		case KeyEvent.KEYCODE_CTRL_LEFT:
			if (Build.MODEL.equals("CN-S3")) {
				if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("")) {
					mBarcodeScan.open();
					mBarcodeScan.scanning();
				} else if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("��ɨ")) {
					mBarcodeScan.setScannerContinuousMode();// ��ɨ
				}   
			}
			break;
		}
		Runtime.getRuntime().gc();
		return false;

	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unused")
	public void guard_time() {
		try {
			/**
			 * 2016-3-3 ���ʱ�䱣������
			 */
			Date today = new Date();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = f.parse(f.format(today));// ��ǰϵͳʱ��
			Date dt2 = f.parse(getSharedPreferences("timer", MODE_PRIVATE).getString("time", ""));// ͬ��ʱ��

			if (dt1.getTime() < dt2.getTime()) {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));// �ر�ɨ��ͷ����ֹɨ��
				Toast.makeText(getApplicationContext(), "��ǹʱ�����������ͬ��ʱ����ٽ��в���!", 1).show();
			} else {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 1));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), e.toString() + "\t" + e.getMessage(), 1).show();
			e.printStackTrace();
		}
	}

	private BarcodeScan mBarcodeScan;

	private final String ON_MUTE_KEYDOWN = "com.android.action.KEYCODE_MUTE_KEYDOWN";
	public static String SCAN_SERVICE_BROADCAST = "AUTO_SCAN_TOSERVICE_BROADCAST";// �㲥��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		scannerPower = true;// �Ƿ���ɨ��
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);// �ؼ�����
		super.onCreate(savedInstanceState);
		// ע��ɨ���쳣�㲥������
		IntentFilter filter = new IntentFilter();
		filter.addAction(BrocastUtil.ADD_SCANDATA_ERROR_BROCAST);
		registerReceiver(scanErrorReceiver, filter);
		brocastUtil = new BrocastUtil(this);
		if (Build.MODEL.equals("CN-S3")) {
			mScantrigCon = ScantrigCon.getInstance();
			mBarcodeScan = new BarcodeScan(this);
			mBarcodeScan.open();
		}

	}

	// ɨ���쳣�㲥������
	private BroadcastReceiver scanErrorReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean isRepeat = intent.getBooleanExtra(BrocastUtil.ADD_ISREPEAT, false);
			if (isRepeat) {
				EAddType addtype = EAddType.valueof(intent.getIntExtra(BrocastUtil.ADD_TYPE, 0));
				EAddThreadOpt threadOpt = EAddThreadOpt.valueOf(intent.getIntExtra(BrocastUtil.ADD_THREAD_OPT_TYPE, 0));
				String barcode = intent.getStringExtra(BrocastUtil.ADD_ERRORMSG);
				if (threadOpt.equals(EAddThreadOpt.SJMD)) {
				} else {
					SoundUtil.getIntance().PlayVoice(EVoiceType.fail);
					doDelListViewItem(addtype, barcode, true);
				}

			} else {
				SoundUtil.getIntance().PlayVoice(EVoiceType.fail);
				PromptUtils.getInstance().showToastHasFeel("�Ƿ�����", getApplicationContext(), EVoiceType.fail, 0);
			}
		}
	};

	@Override
	protected void onDestroy() {

		unregisterReceiver(scanErrorReceiver);
		closeBluetooth();
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		pd = null;

		super.onDestroy();
	}

	private void doDelListViewItem(EAddType addtype, String barcode, boolean isNotice) {

		lvx.delete(barcode, barcodeKey);
		if (addtype == EAddType.scandata) {
			scanCount--;
		} else {
			msgCount--;
		}
		updateView();
		if (isNotice) {
			PromptUtils.getInstance().showToastHasFeel("���ţ�" + barcode + "��ɨ�裡", this, EVoiceType.fail, 0);
		}
	}

	@Override
	protected void initializeComponent() {
		strUtil = new StrUtil();
		initValue = this.getIntent().getStringExtra(MainActivity.initValue);
		controlUtil = new ControlUtil();
		btnUpload = bindButton(R.id.bottom_3_btnUpload);
		btnDel = bindButton(R.id.bottom_3_btnDel);
		btnBack = bindButton(R.id.bottom_3_btnBack);
		setHeadTitle();
		initListView();
		lvx.setDelSelectedRowListener(deldataListener);
		isSetLock = AppContext.getAppContext().getIsLockScreen();
	}

	@Override
	protected void initializeValues() {

		scanCount = 0;
		siteProperties = GlobalParas.getGlobalParas().getSiteProperties();
		scanDataService = new ScanDataService(this);
		barcodeCheck = new BarcodeCheck();
		initScanCode();
		if (etBarcode != null) {
			etBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);// ���˵����������
		}
	}

	// ���ݿ���ɾ��
	protected IListenDelSelRowSuc deldataListener = new IListenDelSelRowSuc() {
		@Override
		public void delSuc(String columnName, String barcode) {
			int t = scanDataService.delSingleData(barcode, scanType.value(), false);
			if (t > 0) {
				// ɾ���ɹ�
				scanCount--;
				// GlobalParas.getGlobalParas().setUnUploadCount(-1);
				if (brocastUtil != null) {
					brocastUtil.sendUnUploadCountChange(-t, EUploadType.scanData);
					setUnuploadCount();
					updateView();
				} else {
					Toast.makeText(getApplicationContext(), "NullPointerException", 1).show();
				}
			}
		}
	};

	/**
	 * ����
	 */
	protected boolean isSetLock = false;

	protected void lockScreen() {
		startActivity(new Intent(this, LockScreenActivity.class));
	}

	protected void HandleScanData(String barcode) {
		SoundUtil.getIntance().PlayVoice(EVoiceType.ok);
	}

	// ��ʼ��ɨ�����ͺ��ϴ�����
	protected abstract void initScanCode();

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_3_btnUpload:
			uploadData(uploadType, scanType);// ��ͬ����ͨ��scanType�����ϴ�����
			break;
		case R.id.bottom_3_btnDel:
			delData();
			break;
		case R.id.bottom_3_btnBack:
			finish();
			break;
		}
	}

	/**
	 * ɾ��
	 */
	protected void delData() {

		String barcodeSel = "";
		Map<String, Object> map = lvx.GetSelValue();
		if (map != null) {
			barcodeSel = map.get(barcodeKey).toString();
		}
		Intent intent = new Intent(this, CustomDialogActivity.class);
		intent.putExtra(CustomDialogActivity.BARCODEKEY, barcodeSel);
		startActivityForResult(intent, delRequestCode);
	}

	/**
	 * ɾ��ʱ����(ɾ������ǩ�պ������ǩ�յ�ͼƬ)
	 * 
	 * @author Administrator
	 * 
	 */
	public class ItemDelEvent implements IListenDelSelRowSuc {

		@Override
		public void delSuc(String columnName, String keyValue) {

			if (StrUtil.isNullOrEmpty(keyValue)) {
				return;
			}
			String newPath = "";
			if (scanType == EScanType.QS) {
				newPath = GlobalParas.getGlobalParas().getSignUnUploadPath() + "/" + keyValue + ".jpg";
			} else if (scanType == EScanType.YN) {
				newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + "/" + keyValue + ".jpg";
			}
			if (StrUtil.isNullOrEmpty(newPath)) {
				return;
			}
			File file = new File(newPath);
			if (file.exists()) {
				file.delete();
				refreshImgCtrl(keyValue);
			}

		}

	};

	/**
	 * ɾ��ͼƬ��ˢ��ͼƬ�ؼ�
	 */
	protected void refreshImgCtrl(String barcode) {

	}

	/*
	 * ����/����/��ǩ����֤
	 */
	protected boolean validateBarcode(EditText et, EBarcodeType barcodeType, String tipMsg) {

		boolean flag = true;
		String code = et.getText().toString().trim().trim();
		if (code.toString().length() <= 0) {
			flag = false;
		}
		return flag;
	}

	/*
	 * �������ֻ���
	 */
	protected boolean validatePhone(EditText et) {

		boolean flag = false;
		String code = et.getText().toString().trim().trim();
		if (!strUtil.isNullOrEmpty(code)) {
			if (barcodeCheck.isValidateBarcode(code, EBarcodeType.phoneNum)) {
				flag = true;
			}
		}
		if (!flag) {
			controlUtil.setEditVeiwFocus(et);
			et.setText("");
			PromptUtils.getInstance().showToastHasFeel("��Ч���ֻ����룬���������룡", this, EVoiceType.fail, 0);
		}
		return flag;
	}

	/*
	 * �����뵥�Ż����
	 */
	protected boolean validateScanBarcode(EditText et) {

		boolean flag = false;
		String code = et.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(code)) {
			if (barcodeCheck.isValidateBarcode(code, EBarcodeType.barcode)
					|| barcodeCheck.isValidateBarcode(code, EBarcodeType.packageNo)) {
				flag = true;
			}
		}
		if (!flag) {
			controlUtil.setEditVeiwFocus(et);
			etBarcode.setText(code);

		}
		return flag;
	}

	/*
	 * ����ID
	 */
	@SuppressWarnings("static-access")
	protected boolean validateVehicleId(EditText et) {

		boolean flag = true;
		String code = et.getText().toString().trim().trim();
		if (!strUtil.isNullOrEmpty(code)) {
			flag = barcodeCheck.isValidateBarcode(code, EBarcodeType.vehicleId);
		}
		if (!flag) {
			controlUtil.setEditVeiwFocus(et);
			et.setText("");
		}
		return flag;
	}

	/*
	 * ��ǩ����֤
	 */
	protected boolean validateCarLotNumber(EditText et) {

		// ����ǩ�ż�ͷ��STO
		if (et.getText().length() <= 0) {
			PromptUtils.getInstance().showToastHasFeel("��ǩ�ŷǷ�", this, EVoiceType.fail, 0);
			controlUtil.setEditVeiwFocus(et);
			return false;
		}
		String carLotNumber = et.getText().toString().trim();
		String carLotNumberHead = "STO";
		if (!strUtil.isNullOrEmpty(carLotNumber) && carLotNumber.length() == 9) {
			et.setText(carLotNumberHead + carLotNumber);
		}
		boolean flag = new math().cqh(et.getText().toString());
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("��ǩ�ŷǷ�", this, EVoiceType.fail, 0);
			controlUtil.setEditVeiwFocus(et);
			et.setText("");
			return false;
		} else
			return true;
	}

	/*
	 * �ϴ�
	 */
	protected void uploadData(EUploadType uploadType, EScanType scanType2) {
		if (uploadType == EUploadType.scanData) {
			if (scanCount == 0) {
				PromptUtils.getInstance().showToast("û����Ҫ�ϴ������ݣ�", this);
				return;
			}
		}
		if (uploadType == EUploadType.msg) {
			if (msgCount == 0) {
				PromptUtils.getInstance().showToast("û����Ҫ�ϴ������ݣ�", this);
				return;
			}
		}
		Message uploadMsg = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putSerializable(UploadThread.uploadTypeKey, uploadType);//
		// scanData
		bundle.putSerializable(UploadThread.scanTypeKey, scanType2);// ZD
		bundle.putSerializable(UploadThread.sitePropertiesKey, siteProperties);//
		// C_N
		uploadMsg.setData(bundle);
		UploadThread.getIntance(this).uploadHandler.sendMessage(uploadMsg);
		PromptUtils.getInstance().showToast("���ں�̨�ϴ�,���Ժ�", this);
		// ����б�
		lvx.clear();
		msgCount = 0;
		updateView();
	}

	/**
	 * ɨ���������ʾ
	 */
	protected void updateView() {
		if (etBarcode == null) {
			return;
		}
		// ��յ���
		etBarcode.setText("");
		// controlUtil.setEditVeiwFocus(etBarcode);//��õ��Ž���
		if (scanCount > 0) {
			btnAdd.setText(scanCount + "Ʊ");
		} else {
			btnAdd.setText(R.string.add);
		}

	}

	/*
	 * ��ѡ����
	 */
	protected void openSlecotr(EDownType selectType) {
		Intent intent = new Intent(this, ObjectSerectorActivity.class);
		intent.putExtra(ObjectSerectorActivity.selectTypeKey, selectType);
		startActivityForResult(intent, selectorRequestCode);
	}

	/*
	 * ���
	 */
	protected abstract void addRecord();

	protected abstract boolean validateInput();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		scannerPower = true;
		ScanManager.getInstance().getScanner().setPower(true);
		if (requestCode == selectorRequestCode) {
			if (resultCode == ObjectSerectorActivity.resultCode) {
				EDownType selType = (EDownType) data.getSerializableExtra(ObjectSerectorActivity.res_key);
				String res_1 = data.getStringExtra(ObjectSerectorActivity.res_key_1);
				String res_2 = data.getStringExtra(ObjectSerectorActivity.res_key_2);
				String res_3 = data.getStringExtra(ObjectSerectorActivity.res_key_3);
				String res_4 = data.getStringExtra(ObjectSerectorActivity.res_key_4);
				setSelResult(selType, res_1, res_2, res_3, res_4);

			}
		} else if (requestCode == delRequestCode) {
			if (resultCode == CustomDialogActivity.resultCode) {
				String barcode = data.getStringExtra(CustomDialogActivity.BARCODEKEY);
				deleteDataInDatabase(barcode);
			}
		}
	}

	protected void onOtherResult(int requestCode, int resultCode, Intent data) {

	}

	protected void deleteDataInDatabase(String barcode) {

		int t = scanDataService.delSingleData(barcode, scanType.value(), false);
		if (t > 0) {
			// ɾ���ɹ�
			lvx.delete(barcode, barcodeKey);
			if (delImage(barcode)) {
				brocastUtil.sendUnUploadCountChange(-t, EUploadType.pic);
			}
			if (brocastUtil != null) {
				brocastUtil.sendUnUploadCountChange(-t, EUploadType.scanData);
			}
			scanCount--;
			updateView();
		} else {
			PromptUtils.getInstance().showToastHasFeel("ɾ��ʧ��!", ScanBaseActivity.this, EVoiceType.fail, 0);
		}
	}

	/**
	 * ��Ҫɾ��ͼƬ����������д
	 * 
	 * @param barcode
	 * @return
	 */
	protected boolean delImage(String barcode) {

		return false;
	}

	// ������д
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

	}

	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4,
			String res_5) {

	}

	/**
	 * ����User�ؼ���Tag��Text
	 * 
	 * @param isShowTag
	 * @param ctrl
	 */
	protected void setUserCtrl(boolean isShowTag, EditText ctrl, String str) {

		if (userValidate == null) {
			userValidate = new UserValidate(ScanBaseActivity.this, GlobalParas.getGlobalParas().getStationId());
		}

		if (isShowTag) {// Ҫ��ʾtagֵ(ͨ����ʧȥ����ʱ����)
			userValidate.restoreNo(ctrl);
		}

	}

	// /���������
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			String weight = intent.getStringExtra(BrocastUtil.WEIGHT);
			showWeight(weight + "kg");
		}
	};

	protected void showWeight(String text) {

	}

	private Handler handler = new Handler();

	protected void openBluetooth() {

		if (IsBthConnected) {
			return;
		}
		if (!AppContext.getAppContext().getBluetoothIsOpen()) {
			// PromptUtils.getInstance().showAlertDialogHasFeel(
			// ScanBaseActivity.this, "��������δ��,����<ϵͳ����> -<��������>��������", null,
			// EVoiceType.fail,
			// 0);
			return;
		}

		String blueAddr = AppContext.getAppContext().getBluetoothAddress();
		String code = AppContext.getAppContext().getBluetoothCode();
		String splitStr = AppContext.getAppContext().getBluetoothDataSplit();
		boolean isReverse = AppContext.getAppContext().getBluetoothIsReverse();
		bluetoothUtil = new BluetoothUtil(ScanBaseActivity.this, blueAddr, code, splitStr, isReverse);
		pd = ProgressDialog.show(ScanBaseActivity.this, "��ʾ", "�������������豸�����Ժ�...");
		new Thread() {

			@Override
			public void run() {
				super.run();
				int i = 0;
				boolean flagT = false;
				while (i < 3) {
					flagT = bluetoothUtil.pair();
					Log.i("bluetooth", "pare result:" + String.valueOf(flagT));
					if (flagT) {
						break;
					}
					i++;
				}
				final boolean flag = flagT;
				handler.post(new Runnable() {

					@Override
					public void run() {

						if (!flag) {
							PromptUtils.getInstance().showAlertDialogHasFeel(ScanBaseActivity.this, "����ʧ�ܣ����������ã�", null,
									EVoiceType.fail, 0);
						}
						pd.dismiss();
					}
				});
			}
		}.start();

		IntentFilter weightFilter = new IntentFilter(BrocastUtil.BLUETOOTH_SCALE_WEIGHT_BROCAST);
		registerReceiver(mReceiver, weightFilter);

		IsBthConnected = true;
	}

	private void closeBluetooth() {

		if (IsBthConnected) {
			unregisterReceiver(mReceiver);
			if (bluetoothUtil != null) {
				bluetoothUtil.cancel();
			}

		}
	}

}
