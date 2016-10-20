package com.cneop.stoExpress.activity.scan;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.ObjectSerectorActivity;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EOrderStatus;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.dao.OrderAbnormalService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dataValidate.OrderAbnormalValidate;
import com.cneop.stoExpress.model.OrderAbnormal;
import com.cneop.stoExpress.model.OrderOperating;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OrderAbnormalActivity extends CommonTitleActivity {

	EditText etOrderNum;
	EditText etOrderAbnormalCode;
	EditText etOrderAbnormalDesc;
	Button btnSelAbnormal;
	Button btnBack;
	OrderAbnormalValidate orderAbnormalValidate;
	OrderAbnormalService orderAbnormalService;
	private final int orderAbnormalRequest = 0x34;
	private final int orderOperRequest = 0x35;
	public static final int operResultCode=0x36;
	OrderOperService orderOperService;
	BrocastUtil brocastUtil;
	private String orderNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_abnormal);
		setTitle("订单打回");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();
		etOrderNum = bindEditText(R.id.et_orderabnormal_etOrderNum, null);
		etOrderAbnormalCode = bindEditText(
				R.id.et_orderabnormal_etAbnormalCode, null);
		etOrderAbnormalDesc = bindEditText(R.id.et_orderabnormal_etDesc, null);
		btnSelAbnormal = bindButton(R.id.btn_orderabnormal_btnAbnormalCode);
		btnBack = bindButton(R.id.bottom_1_btnBack);
		etOrderAbnormalCode.addTextChangedListener(textChangeEvent);
	}

	private TextWatcher textChangeEvent = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		 

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		 

		}

		@Override
		public void afterTextChanged(Editable s) {
		 
			String code = s.toString().trim();
			OrderAbnormal model = orderAbnormalService.getOrderAbnormal(code);
			if (model != null) {
				etOrderAbnormalDesc.setText(model.getReasonDesc());
				etOrderAbnormalCode.setTag(code);
			} else {
				etOrderAbnormalDesc.setText("");
				etOrderAbnormalCode.setTag(null);
			}
		}
	};

	@Override
	protected void initializeValues() {
	 
		super.initializeValues();
		orderAbnormalValidate = new OrderAbnormalValidate(
				OrderAbnormalActivity.this, etOrderAbnormalDesc);
		orderAbnormalService = new OrderAbnormalService(
				OrderAbnormalActivity.this);
		orderOperService = new OrderOperService(OrderAbnormalActivity.this);
		brocastUtil = new BrocastUtil(OrderAbnormalActivity.this);
		Intent intent = getIntent();
		orderNum = intent.getStringExtra(OrderOperActivity.ORDERNUM);
		etOrderNum.setText(orderNum);
	}

	@Override
	protected void uiOnClick(View v) {
	 
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_orderabnormal_btnAbnormalCode:
			openOrderAbnormal();
			break;
		case R.id.bottom_1_btnBack:
			backT();
			break;
		}
	}

	private void backT() {
	 
		
		Intent intent = new Intent(OrderAbnormalActivity.this,
				OrderOperActivity.class);
		intent.putExtra(OrderOperActivity.FLAGKEY, true);		
		startActivityForResult(intent, orderOperRequest);
	}

	private void openOrderAbnormal() {
	 
		Intent intent = new Intent(OrderAbnormalActivity.this,
				ObjectSerectorActivity.class);
		intent.putExtra(ObjectSerectorActivity.selectTypeKey,
				EDownType.OrderAbnormal);
		startActivityForResult(intent, orderAbnormalRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == orderAbnormalRequest) {
			if (resultCode == ObjectSerectorActivity.resultCode) {
				EDownType downType = (EDownType) data
						.getSerializableExtra(ObjectSerectorActivity.res_key);
				if (downType == EDownType.OrderAbnormal) {
					String code = data
							.getStringExtra(ObjectSerectorActivity.res_key_1);
					// String desc = data
					// .getStringExtra(ObjectSerectorActivity.res_key_2);
					etOrderAbnormalCode.setText(code);
					// etOrderAbnormalDesc.setText(desc);
				}
			}
		} else if (requestCode == orderOperRequest) {
			Intent intent = new Intent();
			if (resultCode == OrderOperActivity.finishFlagCode) {
				// 完成
				if (!orderAbnormalValidate.vlidateInputData(etOrderAbnormalCode)) {
					return;
				}
				OrderOperating orderOperModel = new OrderOperating();
				orderOperModel.setBarcode("");
				orderOperModel.setEmployeeName(GlobalParas.getGlobalParas()
						.getUserName());
				orderOperModel.setEmployeeSite(GlobalParas.getGlobalParas()
						.getStationName());
				orderOperModel.setEmployeeSiteCode(GlobalParas.getGlobalParas()
						.getStationId());
				orderOperModel.setFlag(String.valueOf(EOrderStatus.noAccept
						.value()));
				orderOperModel.setLogisticid(orderNum);
				orderOperModel.setReasonCode(etOrderAbnormalCode.getText()
						.toString().trim());
				orderOperModel.setUserNo(GlobalParas.getGlobalParas()
						.getUserNo());
				if (orderOperService.addRecord(null, orderOperModel) > 0) {
					brocastUtil.sendUnUploadCountChange(1, EUploadType.order);
					intent.putExtra(OrderActivity.UPDATEFLAG, true);
				} else {
					PromptUtils.getInstance().showToastHasFeel("订单操作失败！",
							OrderAbnormalActivity.this, EVoiceType.fail, 0);
					intent.putExtra(OrderActivity.UPDATEFLAG, false);
				}
			} else if (resultCode == OrderOperActivity.unFinishFlagCode) {
				// 未完成
				intent.putExtra(OrderActivity.UPDATEFLAG, false);
			}
			setResult(operResultCode, intent);
			this.finish();
		}
	}
	//
	// @Override
	// protected boolean doKeyCode_Back() {
	//
	// backT();
	// return false;
	// }

}
