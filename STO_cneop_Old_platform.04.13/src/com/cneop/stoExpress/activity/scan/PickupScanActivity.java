package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EGoodsType;
import com.cneop.stoExpress.common.Enums.EOrderStatus;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dataValidate.DestinationValidate;
import com.cneop.stoExpress.dataValidate.MobileValidateUtil;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.OrderOperating;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class PickupScanActivity extends ScanBaseActivity {

	RelativeLayout relPhone;
	EditText etSendPhone;
	EditText etReceivePhone;
	CheckBox chkMulti;
	RadioButton rbGoods;
	EditText etDestination;
	Button btnSel;
	boolean isTelSJ = false;
	DestinationValidate destinationValidate;
	String expressType = "";
	MobileValidateUtil mobileValidateUtil;
	String orderNum = "";
	OrderOperService orderOperService;
	private final int orderOperRequstCode = 0x52;
	public static final int operResultCode = 0x53;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pickup_scan);
		setTitle("收件");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {
		PromptUtils.getInstance().closeAlertDialog();
		etBarcode.setText(barcode);
		if (etBarcode != null) {
			if (etDestination.getText().toString().trim().length() <= 0) {
				Toast.makeText(getApplicationContext(), "目的地不能为空", 1).show();
				etDestination.requestFocus();
			} else {
				boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
				if (flag) {
					addRecord();
				} else {
					Toast.makeText(getApplicationContext(), "非法条码", Toast.LENGTH_SHORT).show();
					etBarcode.setText("");
					etBarcode.requestFocus();
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		Intent intent = getIntent();
		isTelSJ = intent.getBooleanExtra(SecondMenuActivity.ISTelSJ, false);
		expressType = intent.getStringExtra(SecondMenuActivity.Scan24HFLAG);
		orderNum = intent.getStringExtra(OrderOperActivity.ORDERNUM);
		if (!strUtil.isNullOrEmpty(orderNum)) {
			orderOperService = new OrderOperService(PickupScanActivity.this);
		}
		relPhone = (RelativeLayout) this.findViewById(R.id.rll_pickup_rllTop);
		if (isTelSJ) {
			etSendPhone = bindEditText(R.id.et_pickup_etSendPhone, null);
			etReceivePhone = bindEditText(R.id.et_pickup_etReceivePhone, null);
			chkMulti = (CheckBox) this.findViewById(R.id.chk_pickup_chkRepeat);
		} else {
			relPhone.setVisibility(View.GONE);
		}
		rbGoods = (RadioButton) this.findViewById(R.id.rdo_goods_category_rbGoods);
		etDestination = bindEditText(R.id.et_destination_etDestination, null);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null);
		btnSel = bindButton(R.id.btn_destination_btnSelDestination);
		btnSel.setText("选择");
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
	}

	@Override
	protected void HandleScanData(String barcode) {
		super.HandleScanData(barcode);
		PromptUtils.getInstance().closeAlertDialog();
		if (barcode.length() < 4) {
			controlUtil.setEditVeiwFocus(etDestination);
			etDestination.setText(barcode);
			controlUtil.setEditVeiwFocus(etBarcode);
			return;
		}
		etBarcode.setText(barcode);
		addRecord();
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		destinationValidate = new DestinationValidate(PickupScanActivity.this);
		mobileValidateUtil = new MobileValidateUtil(PickupScanActivity.this);
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_destination_btnSelDestination:
			openSlecotr(EDownType.Destination);
			break;
		case R.id.btn_barcode_btnAdd:
			if (etBarcode != null) {
				if (etDestination.getText().toString().trim().length() <= 0) {
					Toast.makeText(getApplicationContext(), "目的地不能为空", 1).show();
					etDestination.requestFocus();
				} else {
					boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
					if (flag) {
						addRecord();
					} else {
						Toast.makeText(getApplicationContext(), "非法条码", Toast.LENGTH_SHORT).show();
						etBarcode.setText("");
						etBarcode.requestFocus();
					}
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.Destination) {
			etDestination.setText(res_2);
			etDestination.setTag(res_1);

			ControlUtil.setEditVeiwFocus(etBarcode);

		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_pickup_lvList);
		lvx.inital(R.layout.list_item_three_a, new String[] { barcodeKey, destinationKey, itemCategoryKey }, new int[] { R.id.tv_list_item_three_a_tvhead1, R.id.tv_list_item_three_a_tvhead2, R.id.tv_list_item_three_a_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("单号");
		tvHead2.setText("目的地");
		tvHead3.setText("物品类别");

		controlUtil.setControlLayoutWidth(tvHead1, 130, PickupScanActivity.this);
		controlUtil.setControlLayoutWidth(tvHead2, 130, PickupScanActivity.this);
		controlUtil.setControlLayoutWidth(tvHead3, 106.5f, PickupScanActivity.this);
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.SJ;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		if (!validateInput()) {
			return;
		}
		String sendPhone = "";
		String receivePhone = "";
		if (etSendPhone != null) {
			sendPhone = etSendPhone.getText().toString().trim();
		}
		if (etReceivePhone != null) {
			receivePhone = etReceivePhone.getText().toString().trim();
		}
		EGoodsType goodsType = EGoodsType.非货样;
		if (rbGoods != null && rbGoods.isChecked()) {
			goodsType = EGoodsType.货样;
		}
		String destinationNo = "";
		String destinationName = etDestination.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(destinationName)) {
			destinationNo = etDestination.getTag().toString().trim();
		}
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setSampleType(goodsType.value());
		scanDataModel.setDestinationSiteCode(destinationNo);
		scanDataModel.setSenderPhone(sendPhone);
		scanDataModel.setRecipientPhone(receivePhone);
		scanDataModel.setExpressType(expressType);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));

		boolean isUpdateView = true;
		if (strUtil.isNullOrEmpty(orderNum)) {
			// 添加到插入队列中
			AddScanDataThread.getIntance(PickupScanActivity.this).add(scanDataModel);
		} else {
			OrderOperating orderOperModel = new OrderOperating();
			orderOperModel.setBarcode(barcode);
			orderOperModel.setEmployeeName(GlobalParas.getGlobalParas().getUserName());
			orderOperModel.setEmployeeSite(GlobalParas.getGlobalParas().getStationName());
			orderOperModel.setEmployeeSiteCode(GlobalParas.getGlobalParas().getStationId());
			orderOperModel.setFlag(String.valueOf(EOrderStatus.accept.value()));
			orderOperModel.setLogisticid(orderNum);
			orderOperModel.setReasonCode("");
			orderOperModel.setUserNo(GlobalParas.getGlobalParas().getUserNo());
			if (orderOperService.addRecord(scanDataModel, orderOperModel) > 0) {
				brocastUtil.sendUnUploadCountChange(1, EUploadType.scanData);
				brocastUtil.sendUnUploadCountChange(1, EUploadType.order);
			} else {
				isUpdateView = false;
				PromptUtils.getInstance().showAlertDialogHasFeel(PickupScanActivity.this, "扫描失败！", null, EVoiceType.fail, 0);
			}
		}
		if (isUpdateView) {
			// 更新界面
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(barcodeKey, barcode);
			map.put(destinationKey, destinationName);
			map.put(itemCategoryKey, goodsType.toString().trim());
			lvx.add(map, barcode, barcodeKey);
			scanCount++;

			updateView();
		}
	}

	@Override
	protected void updateView() {

		// 清空单号
		etBarcode.setText("");
		if (chkMulti != null) {
			if (chkMulti.isChecked()) {
				controlUtil.setEditVeiwFocus(etBarcode);
			} else {
				etSendPhone.setText("");
				etReceivePhone.setText("");
				controlUtil.setEditVeiwFocus(etSendPhone);
			}
		}

		if (scanCount > 0) {
			btnAdd.setText(scanCount + "票");
		} else {
			btnAdd.setText(R.string.add);
		}
	}

	@Override
	protected boolean validateInput() {

		if (!mobileValidateUtil.validateMobilePhone(etSendPhone, true)) {
			return false;
		}
		if (!mobileValidateUtil.validateMobilePhone(etReceivePhone, true)) {
			return false;
		}
		String destination = etDestination.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(destination)) {
			if (!destinationValidate.vlidateInputData(etDestination)) {
				return false;
			}
		}
		if (!validateScanBarcode(etBarcode)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				destinationValidate.showName(etDestination);
				String destination = etDestination.getText().toString().trim();
				if (!strUtil.isNullOrEmpty(destination)) {
					if (!destinationValidate.vlidateInputData(etDestination)) {
						return;
					}
				}
			}
			break;
		case R.id.et_destination_etDestination:
			if (hasFocus) {
				destinationValidate.restoreNo(etDestination);
			}
			break;
		}
	}

	// @Override
	// protected boolean doKeyCode_Back() {
	// if (strUtil.isNullOrEmpty(orderNum)) {
	// return super.doKeyCode_Back();
	// } else {
	// Intent intent = new Intent(PickupScanActivity.this,
	// OrderOperActivity.class);
	// intent.putExtra(OrderOperActivity.FLAGKEY, true);
	// startActivityForResult(intent, orderOperRequstCode);
	// return false;
	// }
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == orderOperRequstCode) {
			if (resultCode == OrderOperActivity.finishFlagCode) {
				// 已完成
				Intent intent = new Intent();
				intent.putExtra(OrderActivity.UPDATEFLAG, true);
				setResult(operResultCode, intent);
				this.finish();
			} else if (resultCode == OrderOperActivity.unFinishFlagCode) {
				// 未完成
				if (orderOperService.unFinishOrder(orderNum) > 0) {
					brocastUtil.sendUnUploadCountChange(-scanCount, EUploadType.scanData);
					brocastUtil.sendUnUploadCountChange(-scanCount, EUploadType.order);
				}
				Intent intent = new Intent();
				intent.putExtra(OrderActivity.UPDATEFLAG, false);
				setResult(operResultCode, intent);
				this.finish();
			}
		}
	}

	@Override
	protected void deleteDataInDatabase(String barcode) {
		if (strUtil.isNullOrEmpty(orderNum)) {
			super.deleteDataInDatabase(barcode);
		} else {
			if (orderOperService.deleteOrderOper(barcode) > 0) {
				brocastUtil.sendUnUploadCountChange(-1, EUploadType.scanData);
				brocastUtil.sendUnUploadCountChange(-1, EUploadType.order);
				// 删除成功
				lvx.delete(barcode, barcodeKey);
				scanCount--;
				updateView();
			} else {
				PromptUtils.getInstance().showToastHasFeel("删除失败!", PickupScanActivity.this, EVoiceType.fail, 0);
			}
		}
	}

}
