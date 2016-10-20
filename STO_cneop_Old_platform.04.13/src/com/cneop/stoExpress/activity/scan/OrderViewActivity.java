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
		setTitle("订单查看");
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
		sb.append("订 单 号:").append(order.getLogisticid()).append(splitStr);
		sb.append("下单时间:").append(order.getAcceptDate()).append(splitStr);
		sb.append("客户编号:").append(order.getCustomerCode()).append(splitStr);
		sb.append("客户名称:").append(order.getCustomerName()).append(splitStr);
		sb.append("发 件 人:").append(order.getSender_Name()).append(splitStr);
		sb.append("发件地址:").append(order.getSender_Address()).append(splitStr);
		sb.append("发件人电话:").append(order.getSender_Phone()).append(splitStr);
		sb.append("发件人手机:").append(order.getSender_Mobile()).append(splitStr);
		sb.append("目 的 地:").append(order.getSender_Address()).append(splitStr);
		sb.append("客户备注:").append(order.getCusnote());
		return sb.toString().trim();
	}

}
