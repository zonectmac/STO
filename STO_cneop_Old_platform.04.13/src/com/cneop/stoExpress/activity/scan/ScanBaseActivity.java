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

	protected int scanCount; // 扫描数量
	protected int msgCount;// 短信数量
	private UserValidate userValidate;
	protected EScanType scanType;// 扫描类型
	protected EUploadType uploadType;// 上传类型
	protected ESiteProperties siteProperties;// 站点属性
	protected StrUtil strUtil;
	protected BarcodeCheck barcodeCheck;
	protected ControlUtil controlUtil;
	protected ScanDataService scanDataService;
	protected String barcodeKey = "barcode"; // 单号
	protected String weightKey = "weight"; // 重量
	protected String recipientKey = "recipienter"; // 收件人
	protected String destinationKey = "destination"; // 目的地
	protected String itemCategoryKey = "itemCategory"; // 物品类别
	protected String nextStatoinKey = "nextStation"; // 下一站
	protected String vehicleIdKey = "vehicleId"; // 车辆ID
	protected String routeNoKey = "routeNo"; // 路由号
	protected String abnormalKey = "abnormalNo"; // 问题件类型
	protected String packageNoKey = "packageNo"; // 包号
	protected String carLotNumberKey = "carLotNumber";// 车签号
	protected String phoneFromKey = "phoneFrom";// 寄方手机
	protected String phoneToKey = "phoneTo";// 收方手机
	protected String sigerKey = "signer";// 签收人
	protected String signTypeKey = "sign";// 签收
	protected String phonekey = "phone";// 手机号码
	protected String serverstationkey = "serverstation";// 服务点代码
	private String mudetypekey = "mudetype";// 业务类型
	protected String amountkey = "amount";// 总数
	protected String unsendkey = "unupload";// 未发
	protected String sendkey = "upload";// 已发
	private String opertypekey = "opertype";// 操作类型
	protected String barcodeValidateErrorTip = "单号不能为空";
	protected String packageNoValidateErrorTip = "包号异常！";
	protected String signerValidateErrorTip = "签收人异常";
	protected String initValue = ""; // 初始化值
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

	private boolean lockLongPressKey;// 是否长按 // 在扫描页面时,屏蔽声音增大,或减少的长按键

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
	 * 需要兼容新大陆跟s3设备，修改时需谨慎 Author：尹勇
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**
		 * 扫描键值： 中间键 139 左侧键 140 右侧键 141
		 */
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (AppContext.getAppContext().getIsLockScreen()) {
				lockScreen();// 锁屏
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
			// Toast.makeText(getApplicationContext(), "中间键 139", 1).show();
			guard_time();
			break;
		case 140:
			// Toast.makeText(getApplicationContext(), "右侧键 141", 1).show();
			guard_time();
			break;
		case 141:
			// Toast.makeText(getApplicationContext(), "左侧键 140", 1).show();
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
				} else if (getSharedPreferences("s3", MODE_PRIVATE).getString("0", "").equals("连扫")) {
					mBarcodeScan.setScannerContinuousMode();// 连扫
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
			 * 2016-3-3 添加时间保护功能
			 */
			Date today = new Date();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt1 = f.parse(f.format(today));// 当前系统时间
			Date dt2 = f.parse(getSharedPreferences("timer", MODE_PRIVATE).getString("time", ""));// 同步时间

			if (dt1.getTime() < dt2.getTime()) {
				sendBroadcast(new Intent("ACTION_BAR_SCANCFG").putExtra("EXTRA_SCAN_POWER", 0));// 关闭扫描头，禁止扫描
				Toast.makeText(getApplicationContext(), "巴枪时间错误，请联网同步时间后再进行操作!", 1).show();
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
	public static String SCAN_SERVICE_BROADCAST = "AUTO_SCAN_TOSERVICE_BROADCAST";// 广播名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		scannerPower = true;// 是否开启扫描
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);// 关键代码
		super.onCreate(savedInstanceState);
		// 注册扫描异常广播接收器
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

	// 扫描异常广播接收器
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
				PromptUtils.getInstance().showToastHasFeel("非法条码", getApplicationContext(), EVoiceType.fail, 0);
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
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
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
			etBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);// 将运单号设成数字
		}
	}

	// 数据库中删除
	protected IListenDelSelRowSuc deldataListener = new IListenDelSelRowSuc() {
		@Override
		public void delSuc(String columnName, String barcode) {
			int t = scanDataService.delSingleData(barcode, scanType.value(), false);
			if (t > 0) {
				// 删除成功
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
	 * 锁屏
	 */
	protected boolean isSetLock = false;

	protected void lockScreen() {
		startActivity(new Intent(this, LockScreenActivity.class));
	}

	protected void HandleScanData(String barcode) {
		SoundUtil.getIntance().PlayVoice(EVoiceType.ok);
	}

	// 初始化扫描类型和上传类型
	protected abstract void initScanCode();

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_3_btnUpload:
			uploadData(uploadType, scanType);// 不同界面通过scanType区分上传数据
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
	 * 删除
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
	 * 删除时调用(删除拍照签收和问题件签收的图片)
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
	 * 删除图片后，刷新图片控件
	 */
	protected void refreshImgCtrl(String barcode) {

	}

	/*
	 * 单号/包号/车签号验证
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
	 * 可输入手机号
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
			PromptUtils.getInstance().showToastHasFeel("无效的手机号码，请重新输入！", this, EVoiceType.fail, 0);
		}
		return flag;
	}

	/*
	 * 可输入单号或包号
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
	 * 车辆ID
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
	 * 车签号验证
	 */
	protected boolean validateCarLotNumber(EditText et) {

		// 给车签号加头部STO
		if (et.getText().length() <= 0) {
			PromptUtils.getInstance().showToastHasFeel("车签号非法", this, EVoiceType.fail, 0);
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
			PromptUtils.getInstance().showToastHasFeel("车签号非法", this, EVoiceType.fail, 0);
			controlUtil.setEditVeiwFocus(et);
			et.setText("");
			return false;
		} else
			return true;
	}

	/*
	 * 上传
	 */
	protected void uploadData(EUploadType uploadType, EScanType scanType2) {
		if (uploadType == EUploadType.scanData) {
			if (scanCount == 0) {
				PromptUtils.getInstance().showToast("没有需要上传的数据！", this);
				return;
			}
		}
		if (uploadType == EUploadType.msg) {
			if (msgCount == 0) {
				PromptUtils.getInstance().showToast("没有需要上传的数据！", this);
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
		PromptUtils.getInstance().showToast("正在后台上传,请稍候！", this);
		// 清空列表
		lvx.clear();
		msgCount = 0;
		updateView();
	}

	/**
	 * 扫描完更新显示
	 */
	protected void updateView() {
		if (etBarcode == null) {
			return;
		}
		// 清空单号
		etBarcode.setText("");
		// controlUtil.setEditVeiwFocus(etBarcode);//获得单号焦点
		if (scanCount > 0) {
			btnAdd.setText(scanCount + "票");
		} else {
			btnAdd.setText(R.string.add);
		}

	}

	/*
	 * 打开选择器
	 */
	protected void openSlecotr(EDownType selectType) {
		Intent intent = new Intent(this, ObjectSerectorActivity.class);
		intent.putExtra(ObjectSerectorActivity.selectTypeKey, selectType);
		startActivityForResult(intent, selectorRequestCode);
	}

	/*
	 * 添加
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
			// 删除成功
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
			PromptUtils.getInstance().showToastHasFeel("删除失败!", ScanBaseActivity.this, EVoiceType.fail, 0);
		}
	}

	/**
	 * 需要删除图片的由子类重写
	 * 
	 * @param barcode
	 * @return
	 */
	protected boolean delImage(String barcode) {

		return false;
	}

	// 子类重写
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

	}

	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4,
			String res_5) {

	}

	/**
	 * 设置User控件的Tag和Text
	 * 
	 * @param isShowTag
	 * @param ctrl
	 */
	protected void setUserCtrl(boolean isShowTag, EditText ctrl, String str) {

		if (userValidate == null) {
			userValidate = new UserValidate(ScanBaseActivity.this, GlobalParas.getGlobalParas().getStationId());
		}

		if (isShowTag) {// 要显示tag值(通常是失去焦点时调用)
			userValidate.restoreNo(ctrl);
		}

	}

	// /蓝牙称相关
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
			// ScanBaseActivity.this, "蓝牙功能未打开,请在<系统设置> -<蓝牙设置>打开蓝牙！", null,
			// EVoiceType.fail,
			// 0);
			return;
		}

		String blueAddr = AppContext.getAppContext().getBluetoothAddress();
		String code = AppContext.getAppContext().getBluetoothCode();
		String splitStr = AppContext.getAppContext().getBluetoothDataSplit();
		boolean isReverse = AppContext.getAppContext().getBluetoothIsReverse();
		bluetoothUtil = new BluetoothUtil(ScanBaseActivity.this, blueAddr, code, splitStr, isReverse);
		pd = ProgressDialog.show(ScanBaseActivity.this, "提示", "正在连接蓝牙设备，请稍候...");
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
							PromptUtils.getInstance().showAlertDialogHasFeel(ScanBaseActivity.this, "连接失败，请重新设置！", null,
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
