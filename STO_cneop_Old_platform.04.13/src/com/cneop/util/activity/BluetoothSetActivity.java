package com.cneop.util.activity;

import java.util.*;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.ScanBaseActivity;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.bluetooth.BluetoothUtil;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IItemSelected;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class BluetoothSetActivity extends ScanBaseActivity {

	private CheckBox ckx_scalage_switch;
	private EditText et_bluetooth_address;
	private EditText et_bluetooth_device_name;
	private EditText et_bluetooth_verification_code;
	private Spinner spin_bluetooth_scale_model;
	private EditText et_bluetooth_begin;
	private EditText et_bluetooth_end;
	private CheckBox ckx_bluetooth_reverse;
	Button btn_bluetooth_search;
	Button btn_bluetooth_save;
	Button btn_bluetooth_back;
	private ListViewEx lv_bluetooth_mac;
	private TextView tv_bluetooth_weight;
	Button btn_bluetooth_connect;
	private final String ITEM_BLUENAME = "blueName";
	private final String ITEM_BLUEADDRESS = "buleAddress";
	BluetoothUtil bluetoothUtil;
	String[] scalesType = { "=00.000=", "+   0.00 kg" };
	ArrayAdapter<String> arrayAdapter;
	ProgressDialog pd;
	StrUtil strUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_bluetooth_setting);
		setTitle("蓝牙称设置");
		super.onCreate(savedInstanceState);
		// 扫描头初始化
		// 注册广播BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter startFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		IntentFilter finishFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		IntentFilter bondStateChangeFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		IntentFilter pairRequestFilter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
		IntentFilter weightFilter = new IntentFilter(BrocastUtil.BLUETOOTH_SCALE_WEIGHT_BROCAST);
		registerReceiver(mReceiver, filter);
		registerReceiver(mReceiver, finishFilter);
		registerReceiver(mReceiver, startFilter);
		registerReceiver(mReceiver, bondStateChangeFilter);
		registerReceiver(mReceiver, pairRequestFilter);
		registerReceiver(mReceiver, weightFilter);

		openBuleForRequire();
	}

	/**
	 * 打开蓝牙
	 */
	private void openBuleForRequire() {
		if (!bluetoothUtil.isOpen()) {
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bluetoothUtil.cancel();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void HandleScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		super.HandleScanData(barcode);
		if (et_bluetooth_address != null) {
			et_bluetooth_address.setText(barcode);
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();

		private boolean isExist(String deviceName) {
			boolean flag = false;
			for (Map<String, Object> map : sourceList) {
				if (deviceName.equals(map.get(ITEM_BLUENAME))) {
					flag = true;
					break;
				}
			}
			return flag;
		}

		@Override
		public void onReceive(Context arg0, Intent intent) {

			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				// pd = ProgressDialog.show(BluetoothSetActivity.this, "提示",
				// "正在搜索，请稍候...");
				PromptUtils.getInstance().showToast("正在搜索，请稍候...", BluetoothSetActivity.this);
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getName() != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ITEM_BLUENAME, device.getName());
					map.put(ITEM_BLUEADDRESS, device.getAddress());
					if (!isExist(device.getName())) {
						sourceList.add(map);
					}
					lv_bluetooth_mac.add(sourceList);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

				sourceList.clear();
				if (pd != null) {
					pd.dismiss();
				}
				PromptUtils.getInstance().showToast("搜索完成", BluetoothSetActivity.this);
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int bondState = device.getBondState();
				switch (bondState) {
				case BluetoothDevice.BOND_BONDED:
					bluetoothUtil.connect();
					break;
				}
			} else if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(action)) {
				bluetoothUtil.setPin();
			} else if (BrocastUtil.BLUETOOTH_SCALE_WEIGHT_BROCAST.equals(action)) {
				String weight = intent.getStringExtra(BrocastUtil.WEIGHT);
				tv_bluetooth_weight.setText(weight + "kg");
			}
		}

	};

	protected void uploadData(com.cneop.stoExpress.common.Enums.EUploadType uploadType, com.cneop.stoExpress.common.Enums.EScanType scanType2) {

	};

	@Override
	protected void initializeComponent() {
		tv_bluetooth_weight = (TextView) findViewById(R.id.tv_bluetooth_weight);
		ckx_scalage_switch = (CheckBox) findViewById(R.id.ckx_scalage_switch);
		et_bluetooth_address = bindEditText(R.id.et_bluetooth_address, null, null);
		et_bluetooth_device_name = bindEditText(R.id.et_bluetooth_device_name, null, null);
		et_bluetooth_verification_code = bindEditText(R.id.et_bluetooth_verification_code, null, null);
		spin_bluetooth_scale_model = (Spinner) findViewById(R.id.spin_bluetooth_scale_model);
		et_bluetooth_begin = bindEditText(R.id.et_bluetooth_begin, null, null);
		et_bluetooth_end = bindEditText(R.id.et_bluetooth_end, null, null);
		ckx_bluetooth_reverse = (CheckBox) findViewById(R.id.ckx_bluetooth_reverse);
		btn_bluetooth_search = bindButton(R.id.btn_bluetooth_search);
		btn_bluetooth_connect = bindButton(R.id.btn_bluetooth_connect);
		btn_bluetooth_save = bindButton(R.id.btn_bluetooth_save);
		btn_bluetooth_back = bindButton(R.id.btn_bluetooth_back);
		lv_bluetooth_mac = (ListViewEx) findViewById(R.id.lv_bluetooth_mac);
		lv_bluetooth_mac.setOnItemSelected(lv_bluetooth_mac_itemclick);

		et_bluetooth_address.setInputType(InputType.TYPE_NULL); // 关闭软键盘

	}

	private void handleItemClick(int position) {
		Map<String, Object> map = lv_bluetooth_mac.GetValue(position);
		if (map != null) {
			String blueName = map.get(ITEM_BLUENAME).toString().trim();
			String blueAddress = map.get(ITEM_BLUEADDRESS).toString().trim();
			et_bluetooth_address.setText(blueAddress);
			et_bluetooth_device_name.setText(blueName);
			et_bluetooth_verification_code.setText("1234");
		}
	}

	private IItemSelected lv_bluetooth_mac_itemclick = new IItemSelected() {

		@Override
		public void onItemSelected(int position) {
			handleItemClick(position);

		}
	};

	@Override
	protected void initializeValues() {
		lv_bluetooth_mac.inital(R.layout.list_item_bluetooth, new String[] { ITEM_BLUENAME, ITEM_BLUEADDRESS }, new int[] { R.id.tv_list_item_bluetooth_tvhead1, R.id.tv_list_item_bluetooth_tvhead2 });
		strUtil = new StrUtil();
		bluetoothUtil = new BluetoothUtil(BluetoothSetActivity.this);
		boolean isOpenBlue = AppContext.getAppContext().getBluetoothIsOpen();
		String deviceName = AppContext.getAppContext().getBluetoothName();
		String deviceAddress = AppContext.getAppContext().getBluetoothAddress();
		String code = AppContext.getAppContext().getBluetoothCode();
		boolean isReverse = AppContext.getAppContext().getBluetoothIsReverse();
		int scaleTypeId = AppContext.getAppContext().getBluetoothScaleTypeId();
		ckx_scalage_switch.setChecked(isOpenBlue);
		ckx_bluetooth_reverse.setChecked(isReverse);
		et_bluetooth_address.setText(deviceAddress);
		et_bluetooth_device_name.setText(deviceName);
		et_bluetooth_verification_code.setText(code);
		// 初始化下拉列表
		arrayAdapter = new ArrayAdapter<String>(BluetoothSetActivity.this, android.R.layout.simple_spinner_item, scalesType);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_bluetooth_scale_model.setAdapter(arrayAdapter);
		spin_bluetooth_scale_model.setSelection(scaleTypeId);
		spin_bluetooth_scale_model.setOnItemSelectedListener(selectedEvent);
	}

	private OnItemSelectedListener selectedEvent = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

			if (position == 0) {
				et_bluetooth_begin.setText("=");
				et_bluetooth_end.setText("=");
			} else if (position == 1) {
				et_bluetooth_begin.setText("+");
				et_bluetooth_end.setText("+");
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	@Override
	protected void uiOnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bluetooth_search:
			search();
			break;
		case R.id.btn_bluetooth_save:
			saveConfig();
			break;
		case R.id.btn_bluetooth_connect:
			connect();
			break;
		case R.id.btn_bluetooth_back:
			exit();
			break;
		}
	}

	private void exit() {
		back();
	}

	private void connect() {

		if (!validateSave()) {
			return;
		}
		if (bluetoothUtil.getConnectStatus()) {
			return;
		}
		String address = et_bluetooth_address.getText().toString().trim();
		address = bluetoothUtil.getMacAddrFormat(address);
		String code = et_bluetooth_verification_code.getText().toString().trim();
		String splitStr = et_bluetooth_begin.getText().toString().trim();
		boolean isReverse = ckx_bluetooth_reverse.isChecked();
		bluetoothUtil.setParam(address, code, splitStr, isReverse);
		boolean flag = bluetoothUtil.pair();
		if (flag) {
			PromptUtils.getInstance().showToastHasFeel("连接成功！", BluetoothSetActivity.this, EVoiceType.normal, 0);
		} else {
			PromptUtils.getInstance().showAlertDialog(BluetoothSetActivity.this, "连接失败！", null);
		}
	}

	private void saveConfig() {

		String deviceName = et_bluetooth_device_name.getText().toString().trim();
		String deviceAddress = et_bluetooth_address.getText().toString().trim();
		String code = et_bluetooth_verification_code.getText().toString().trim();
		if (!validateSave()) {
			return;
		}
		boolean isOpenBlue = ckx_scalage_switch.isChecked();
		boolean isReverse = ckx_bluetooth_reverse.isChecked();
		int scaleTypeId = spin_bluetooth_scale_model.getSelectedItemPosition();
		String splitStr = et_bluetooth_begin.getText().toString().trim();
		AppContext.getAppContext().setBluetoothAddress(bluetoothUtil.getMacAddrFormat(deviceAddress));
		AppContext.getAppContext().setBluetoothCode(code);
		AppContext.getAppContext().setBluetoothIsReverse(isReverse);
		AppContext.getAppContext().setBluetoothName(deviceName);
		AppContext.getAppContext().setBluetoothOpen(isOpenBlue);
		AppContext.getAppContext().setBluetoothScaleTypeId(scaleTypeId);
		AppContext.getAppContext().setBluetoothDataSplit(splitStr);
		exit();
	}

	private boolean validateSave() {
		// String deviceName =
		// et_bluetooth_device_name.getText().toString().trim();
		String deviceAddress = et_bluetooth_address.getText().toString().trim();
		String code = et_bluetooth_verification_code.getText().toString().trim();
		if (strUtil.isNullOrEmpty(deviceAddress)) {
			PromptUtils.getInstance().showAlertDialog(BluetoothSetActivity.this, "蓝牙地址不能为空！", null);
			return false;
		}
		// if (strUtil.isNullOrEmpty(deviceName)) {
		// PromptUtils.getInstance().showAlertDialogHasFeel(
		// BluetoothSetActivity.this, "蓝牙名称不能为空！", null,
		// EVoiceType.fail, 0);
		// return false;
		// }

		// 验证蓝牙地址是否正确
		if (!bluetoothUtil.checkAddr(deviceAddress)) {
			PromptUtils.getInstance().showAlertDialog(BluetoothSetActivity.this, "蓝牙地址格式不正确！", null);
			return false;
		}
		if (strUtil.isNullOrEmpty(code)) {
			PromptUtils.getInstance().showAlertDialog(BluetoothSetActivity.this, "验证码不能为空！", null);
			return false;
		}
		return true;
	}

	private void search() {

		if (lv_bluetooth_mac.getSize() > 0) {
			lv_bluetooth_mac.clear();
		}
		bluetoothUtil.search();
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// saveConfig();
	// }

	@Override
	protected void initListView() {

	}

	@Override
	protected void setHeadTitle() {

	}

	@Override
	protected void initScanCode() {

	}

	@Override
	protected void addRecord() {

	}

	@Override
	protected boolean validateInput() {

		return false;
	}

}
