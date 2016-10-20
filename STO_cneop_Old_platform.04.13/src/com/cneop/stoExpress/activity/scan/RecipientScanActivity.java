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
	private String expressType = ""; // 是否是24小时扫描为1
	private ViewStub vs_tvWeight;
	private DeviceUtil deviceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_recipient_scan);
		setTitle("收件扫描");
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
	 * 扫描数据
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
		super.initializeComponent();// 底部三按钮
		btnSelUser = bindButton(R.id.btn_recipient_scan_btnSelUser);// 收件人选择
		btnSelDestination = bindButton(R.id.btn_recipient_scan_btnSelDestination);// 目的地选择
		etUser = bindEditText(R.id.et_recipient_scan_etRecipienter, null, null);// 人工编号
		etDestination = bindEditText(R.id.et_recipient_scan_etDestination, null, null);// 目的地
		etBarcode = bindEditText(R.id.et_recipient_scan_etBarcode, null, null);// 单号
		btnAdd = bindButton(R.id.btn_recipient_scan_btnAdd);// 添加
		rdoGoods = (RadioButton) this.findViewById(R.id.rdo_recipient_scan_rbGoods);// 货样
		// 打开蓝牙电子称才显示重量
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

	// 设置头部标题
	@Override
	protected void setHeadTitle() {
		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead3);
		TextView tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead4);
		TextView tvHead5 = (TextView) this.findViewById(R.id.tv_list_head_five_tvhead5);
		tvHead1.setText("单号");
		tvHead2.setText("重量");
		tvHead3.setText("收件员");
		tvHead4.setText("目的地 ");
		tvHead5.setText("物品类型");
	}

	public boolean isNumeric(String str) {
		// true 数字
		// false 非数字
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
		case R.id.et_recipient_scan_etRecipienter:// 人工编号(点击收件界面的时候会走这个函数（一进来就获得焦点）)
			if (hasFocus) {
				// 用户工号还原
				userValidate.restoreNo(etUser);
				// todo:查找目的地
				// destinationValidate.showName(etDestination);//(好像并没有什么用)
			}
			break;
		case R.id.et_recipient_scan_etDestination:// 目的地
			if (hasFocus) {
				// 设置用户名
				userValidate.showName(etUser);
				// 目地地代码还原
				destinationValidate.restoreNo(etDestination);
				if (!userValidate.vlidateInputData(etUser)) {
					return;
				}
			}
			break;
		case R.id.et_recipient_scan_etBarcode:// 单号
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

	// 增加
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
			PromptUtils.getInstance().showToastHasFeel("目的地不能为空", this, EVoiceType.fail, 0);
			return;
		}

		String barcode = etBarcode.getText().toString().trim();// 单号
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String recipientName = etUser.getText().toString().trim();// 员工编号
		String user = etUser.getTag().toString().trim();// 得到员工编号
		String destinationNo = "";// 目的地
		if (etDestination.getTag() != null) {
			destinationNo = etDestination.getTag().toString().trim();
		}
		String destinationName = etDestination.getText().toString().trim();

		String weightStr = "";
		if (tvWeight != null) {
			weightStr = tvWeight.getText().toString().trim();
		}
		EGoodsType goodsType = EGoodsType.非货样;
		if (rdoGoods.isChecked()) {
			goodsType = EGoodsType.货样;
			etDestination.setFocusableInTouchMode(true);
			etDestination.requestFocus();
		}
		String weight = "";
		if (AppContext.getAppContext().getBluetoothIsOpen()) {
			if (rdoGoods.isChecked()) {
				// 货样
				if (strUtil.isNullOrEmpty(weightStr) || weightStr.equals("0kg") || weightStr.startsWith("-")) {
					PromptUtils.getInstance().showAlertDialogHasFeel(RecipientScanActivity.this,
							"电子秤显示重量异常，为0或负数或无数据，请检查电子秤！", null, EVoiceType.fail, 0);
					return;
				}
			} else {
				// 非货样
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
		// 保存数据库
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
		// 添加到插入队列中
		AddScanDataThread.getIntance(this).add(scanDataModel);

		// 更新界面
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
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {
		if (!userValidate.vlidateInputData(etUser)) {
			PromptUtils.getInstance().showToastHasFeel("收件人不能为空", this, EVoiceType.fail, 0);
			return false;
		}
		boolean flag = new math().CODE1(etBarcode.getText().toString().trim());// 手动添加数据
		if (!flag) {
			PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
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
			controlUtil.setEditVeiwFocus(etDestination);// 光标指向目的地
			break;
		case Destination:
			etDestination.setText(res_2);
			etDestination.setTag(res_1);
			ControlUtil.setEditVeiwFocus(etBarcode);
			break;
		}
	}
}
