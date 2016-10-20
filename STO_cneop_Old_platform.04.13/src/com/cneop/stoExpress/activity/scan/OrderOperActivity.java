package com.cneop.stoExpress.activity.scan;

import com.cneop.stoExpress.cneops.R;
import com.cneop.util.activity.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OrderOperActivity extends BaseActivity {

	Button btnLeft;
	Button btnRight;
	boolean flag = false;// FALSE 表示:提取、打回 | TURE 表示：已完成 、未完成
	public static final String FLAGKEY = "flagkey";
	public static final String ORDERNUM = "orderNum";
	public static final String ORDEROPER = "orderOper";
	private String orderNum;
	private final int acceptRequestCode = 0x44;
	private final int noAcceptRequestCode = 0x45;
	public static final int finishFlagCode = 0x46;
	public static final int unFinishFlagCode = 0x47;
	public static final int OPERRESULTCODE = 0x48;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_oper);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();
		btnRight = bindButton(R.id.btn_orderoper_btnRight);
		btnLeft = bindButton(R.id.btn_orderoper_btnLeft);
	}

	@Override
	protected void initializeValues() {
	 
		super.initializeValues();
		Intent intent = getIntent();
		flag = intent.getBooleanExtra(FLAGKEY, false);
		orderNum = intent.getStringExtra(ORDERNUM);
		if (flag) {
			btnLeft.setText("已完成");
			btnRight.setText("未完成");
		} else {
			btnLeft.setText("提取");
			btnRight.setText("打回");
		}
	}

	@Override
	protected void uiOnClick(View v) {
	 
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_orderoper_btnRight:
			rightEvent();
			break;
		case R.id.btn_orderoper_btnLeft:			
			leftEvent();
			break;
		}
	}

	// @Override
	// protected void doLeftButton() {
	//
	// super.doLeftButton();
	// leftEvent();
	// }
	//
	// @Override
	// protected boolean doKeyCode_Back() {
	//
	// rightEvent();
	// return false;
	// }

	private void rightEvent() {
	 
		if (flag) {
			// 未完成
			unFinishOrder();
		} else {
			// 打回
			noAcceptOrder();
		}
	}

	private void leftEvent() {
	 
		if (flag) {
			// 已完成
			finishOrder();
		} else {
			// 提取
			acceptOrder();
		}
	}

	/**
	 * 提取订单
	 */
	private void acceptOrder() {
		Intent intent = new Intent(OrderOperActivity.this,
				PickupScanActivity.class);
		intent.putExtra(SecondMenuActivity.ISTelSJ, false);
		intent.putExtra(SecondMenuActivity.Scan24HFLAG, "");
		intent.putExtra(ORDERNUM, orderNum);
		startActivityForResult(intent, acceptRequestCode);
	}

	/**
	 * 打回订单
	 */
	private void noAcceptOrder() {
		Intent inent = new Intent(OrderOperActivity.this,
				OrderAbnormalActivity.class);
		inent.putExtra(ORDERNUM, orderNum);
		startActivityForResult(inent, noAcceptRequestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == acceptRequestCode) {
			if (resultCode == PickupScanActivity.operResultCode) {
				setResult(OPERRESULTCODE, data);
				this.finish();
			}
		} else if (requestCode == noAcceptRequestCode) {
			if (resultCode == OrderAbnormalActivity.operResultCode) {
				setResult(OPERRESULTCODE, data);
				this.finish();
			}
		}
	}

	/**
	 * 已完成订单
	 */
	private void finishOrder() {
		setResult(finishFlagCode);
		this.finish();
	}

	/**
	 * 未完成订单
	 */
	private void unFinishOrder() {
		setResult(unFinishFlagCode);
		this.finish();
	}

}
