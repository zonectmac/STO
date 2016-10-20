package com.cneop.stoExpress.activity.scan;

import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dataValidate.UserValidate;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.scan.ScanManager;
import com.math.math;

public class SendingpiecesActivity extends ScanBaseActivity {

	EditText etUser;
	Button btnSelUser;
	UserValidate userValidate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_sendingpieces);
		setTitle("派件");
		super.onCreate(savedInstanceState);
		super.scannerPower = true;
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
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
				controlUtil.setEditVeiwFocus(etUser);
				etUser.setText(barcode.substring(6));
				controlUtil.setEditVeiwFocus(etBarcode);
				return;
			} else if (barcode.length() == 4) {
				controlUtil.setEditVeiwFocus(etUser);
				etUser.setText(barcode);
				controlUtil.setEditVeiwFocus(etBarcode);
				return;
			}
			etBarcode.setText(barcode);
			boolean flag = new math().CODE2(barcode);
			if (flag) {
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
				return;
			}
			addRecord();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		etUser = bindEditText(R.id.et_send_pieces_etSendPieces, null, null);
		btnSelUser = bindButton(R.id.btn_send_pieces_btnSelUser);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		userValidate = new UserValidate(SendingpiecesActivity.this, GlobalParas.getGlobalParas().getStationId());
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_send_pieces_btnSelUser:
			openSlecotr(EDownType.User);
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			// --------------------------------------------------------
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etBarcode != null) {// 单号
				// 说明Code2函数处理的是code1这种码。特此说明
				boolean flag = new math().CODE2(etBarcode.getText().toString().trim());
				if (flag) {
					addRecord();
				} else {
					PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
					etBarcode.setText("");
				}
			}
			// ----------------------------------------------------------
			break;

		default:
			break;
		}
	}

	@Override
	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3, String res_4) {

		if (selType == EDownType.User) {
			etUser.setText(res_2);
			etUser.setTag(res_1);

			ControlUtil.setEditVeiwFocus(etBarcode);

		}
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) this.findViewById(R.id.lv_sendingpieces_lvBarcode);
		lvx.inital(R.layout.list_item_two, new String[] { barcodeKey, recipientKey }, new int[] { R.id.tv_list_item_two_tvhead1, R.id.tv_list_item_two_tvhead2 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
		tvHead1.setText("条码");
		tvHead2.setText("派件人员");
		// 设置布局宽度
		controlUtil.setControlLayoutWidth(tvHead1, 150, SendingpiecesActivity.this);
		controlUtil.setControlLayoutWidth(tvHead2, 139, SendingpiecesActivity.this);
	}

	@Override
	protected void HandleScanData(String barcode) {

		super.HandleScanData(barcode);
		if (barcode.length() == GlobalParas.getGlobalParas().getUserNoLen()) {
			controlUtil.setEditVeiwFocus(etUser);
			etUser.setText(barcode);
			controlUtil.setEditVeiwFocus(etBarcode);
		} else if (barcode.substring(0, 6).equals(GlobalParas.getGlobalParas().getStationId())) {
			controlUtil.setEditVeiwFocus(etUser);
			etUser.setText(barcode.substring(6));
			controlUtil.setEditVeiwFocus(etBarcode);
		} else {
			etBarcode.setText(barcode);
			addRecord();
		}
	};

	@Override
	protected void initScanCode() {

		scanType = EScanType.PJ;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		if (!validateInput()) {
			return;
		}
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}
		String userNo = "";
		if (etUser.getTag() != null) {
			userNo = etUser.getTag().toString().trim();
		}
		String userName = etUser.getText().toString().trim();
		// 保存数据库
		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setCourier(userNo);
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 添加到插入队列中
		AddScanDataThread.getIntance(SendingpiecesActivity.this).add(scanDataModel);
		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(recipientKey, userName);
		lvx.add(map);

		scanCount++;
		updateView();
		if (AppContext.getAppContext().getIsLockScreen()) {
			lockScreen();// 锁屏
		}
	}

	@Override
	protected boolean validateInput() {

		if (!userValidate.vlidateInputData(etUser)) {
			PromptUtils.getInstance().showToastHasFeel("派件员异常！", this, EVoiceType.fail, 0);
			return false;
		}
		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		return true;
	}

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_send_pieces_etSendPieces:
			if (hasFocus) {
				userValidate.restoreNo(etUser);
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				userValidate.showName(etUser);
				if (!userValidate.vlidateInputData(etUser)) {
					etUser.requestFocus();
					return;
				}
			}
		default:
			break;
		}
	}

}
