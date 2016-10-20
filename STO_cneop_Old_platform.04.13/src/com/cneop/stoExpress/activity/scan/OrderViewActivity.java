package com.cneop.stoExpress.activity.scan;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.model.Order;
import com.cneop.util.activity.CommonTitleActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OrderViewActivity extends CommonTitleActivity {

	EditText etOrder;
	Button btnBack;
	public static final String ORDERKEY = "orderkey";
	public static final String ORDEREXTRA = "orderextra";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_view);
		setTitle("�����鿴");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();
		etOrder = bindEditText(R.id.et_orderview_etOrder, null);
		btnBack = bindButton(R.id.bottom_1_btnBack);
	}

	@Override
	protected void uiOnClick(View v) {
	 
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_1_btnBack:
			back();
			break;
		}
	}

	@Override
	protected void initializeValues() {
	 
		super.initializeValues();
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra(ORDEREXTRA);
		Order orderModel = (Order) bundle.get(ORDERKEY);
		String orderStr = getOrderStr(orderModel);
		etOrder.setText(orderStr);
	}

	private String getOrderStr(Order order) {
		String splitStr = "\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append("�� �� ��:").append(order.getLogisticid()).append(splitStr);
		sb.append("�µ�ʱ��:").append(order.getAcceptDate()).append(splitStr);
		sb.append("�ͻ����:").append(order.getCustomerCode()).append(splitStr);
		sb.append("�ͻ�����:").append(order.getCustomerName()).append(splitStr);
		sb.append("�� �� ��:").append(order.getSender_Name()).append(splitStr);
		sb.append("������ַ:").append(order.getSender_Address()).append(splitStr);
		sb.append("�����˵绰:").append(order.getSender_Phone()).append(splitStr);
		sb.append("�������ֻ�:").append(order.getSender_Mobile()).append(splitStr);
		sb.append("Ŀ �� ��:").append(order.getSender_Address()).append(splitStr);
		sb.append("�ͻ���ע:").append(order.getCusnote());
		return sb.toString().trim();
	}

}
