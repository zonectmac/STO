package com.cneop.stoExpress.activity.scan;

import java.util.List;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.model.Order;
import com.cneop.util.activity.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderAlertActivity extends BaseActivity {

	TextView tvOrderAlert;
	TextView tvOrderStatus;
	Button btnPre;
	Button btnNext;
	Button btnConfirm;
	Button btnCancel;
	LinearLayout llyPage;
	List<Order> urgeOrderList;
	OrderService orderService;
	int pageIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_alert);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		tvOrderAlert = (TextView) this.findViewById(R.id.tv_orderalert_tvOrderAlert);
		tvOrderStatus = (TextView) this.findViewById(R.id.tv_orderalert_tvOrderStatus);
		llyPage = (LinearLayout) this.findViewById(R.id.lly_orderalert_llyPage);
		btnCancel = bindButton(R.id.bottom_2_btnBack);
		btnConfirm = bindButton(R.id.bottom_2_btnOk);
		btnPre = bindButton(R.id.btn_orderalert_btnPrePage);
		btnNext = bindButton(R.id.btn_orderalert_btnNextPage);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		orderService = new OrderService(OrderAlertActivity.this);
		int count = orderService.getOrderCount(GlobalParas.getGlobalParas().getUserNo());
		tvOrderAlert.setText("当前有" + count + "条订单未处理");
		urgeOrderList = GlobalParas.getGlobalParas().getOrderList();
		if (urgeOrderList != null && urgeOrderList.size() == 0) {
			tvOrderStatus.setText("当前没有催促、取消信息");
			btnPre.setVisibility(View.INVISIBLE);
			btnNext.setVisibility(View.INVISIBLE);
		} else {
			showOrderStaus(0);
		}
	}

	private void showOrderStaus(int index) {
		if (index > urgeOrderList.size() - 1) {
			return;
		}
		Order order = urgeOrderList.get(index);
		StringBuilder sb = new StringBuilder();
		if (order.getIsUrge() == 1) {
			sb.append("订单催促：").append(order.getLogisticid());
		} else if (order.getIsUrge() == 2) {
			String splitStr = "\r\n";
			sb.append("订单取消：").append(order.getLogisticid()).append(splitStr);
			sb.append("客户地址：").append(order.getSender_Address()).append(splitStr);
			sb.append("客户电话：").append(order.getSender_Phone());
		}
		tvOrderStatus.setText(sb.toString().trim());
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_orderalert_btnNextPage:
			nextPage();
			break;
		case R.id.btn_orderalert_btnPrePage:
			prePage();
			break;
		case R.id.bottom_2_btnOk:
			dealwithOrder();
			break;
		case R.id.bottom_2_btnBack:
			backT();
			break;
		}
	}

	private void backT() {

		boolean flag = true;
		if (hasTwoOrderStatus(urgeOrderList)) {
			if (!isInCancel(urgeOrderList, pageIndex)) {
				flag = false;
				pageIndex = getFirstCancelPositoin(urgeOrderList);
				showOrderStaus(pageIndex);
			}
		}
		if (flag) {
			this.finish();
		}
	}

	private void dealwithOrder() {

		boolean flag = true;
		if (hasTwoOrderStatus(urgeOrderList)) {
			if (!isInCancel(urgeOrderList, pageIndex)) {
				flag = false;
				pageIndex = getFirstCancelPositoin(urgeOrderList);
				showOrderStaus(pageIndex);
			}
		}
		if (flag) {
			Intent intent = new Intent(OrderAlertActivity.this, OrderActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	/**
	 * 第一个取消的索引
	 * 
	 * @param list
	 * @return
	 */
	private int getFirstCancelPositoin(List<Order> list) {
		int index = 0;
		for (Order order : list) {
			if (order.getIsUrge() == 2) {
				break;
			}
			index++;
		}
		return index;
	}

	/**
	 * 当前是否在取消位置
	 * 
	 * @param pageIndex
	 * @return
	 */
	private boolean isInCancel(List<Order> list, int pageIndex) {
		Order order = list.get(pageIndex);
		if (order.getIsUrge() == 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否有两种状态的订单
	 * 
	 * @return
	 */
	private boolean hasTwoOrderStatus(List<Order> list) {
		boolean flag = false;
		boolean hasUrge = false;
		boolean hasCancel = false;
		for (Order order : list) {
			if (order.getIsUrge() == 1) {
				hasUrge = true;
			} else if (order.getIsUrge() == 2) {
				hasCancel = true;
				break;
			}
		}
		if (hasCancel && hasUrge) {
			flag = true;
		}
		return flag;
	}

	private void prePage() {

		pageIndex--;
		if (pageIndex < 0) {
			pageIndex = 0;
		}
		showOrderStaus(pageIndex);
	}

	private void nextPage() {

		pageIndex++;
		if (pageIndex > urgeOrderList.size() - 1) {
			pageIndex = urgeOrderList.size() - 1;
		}
		showOrderStaus(pageIndex);
	}

//	@Override
//	protected void doLeftButton() {
//
//		super.doLeftButton();
//		dealwithOrder();
//	}

	// @Override
	// protected boolean doKeyCode_Back() {
	//
	// backT();
	// return false;
	// }

}
