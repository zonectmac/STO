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
	boolean flag = false;// FALSE ��ʾ:��ȡ����� | TURE ��ʾ������� ��δ���
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
			btnLeft.setText("�����");
			btnRight.setText("δ���");
		} else {
			btnLeft.setText("��ȡ");
			btnRight.setText("���");
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
			// δ���
			unFinishOrder();
		} else {
			// ���
			noAcceptOrder();
		}
	}

	private void leftEvent() {
	 
		if (flag) {
			// �����
			finishOrder();
		} else {
			// ��ȡ
			acceptOrder();
		}
	}

	/**
	 * ��ȡ����
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
	 * ��ض���
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
	 * ����ɶ���
	 */
	private void finishOrder() {
		setResult(finishFlagCode);
		this.finish();
	}

	/**
	 * δ��ɶ���
	 */
	private void unFinishOrder() {
		setResult(unFinishFlagCode);
		this.finish();
	}

}
