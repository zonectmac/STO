package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.model.Order;
import com.cneop.stoExpress.server.OrderServer;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

public class OrderActivity extends CommonTitleActivity {
	private ListView lvOrder;
	private TextView tvOrderCount;
	private EditText etOrderDetail;
	private Button btnDown;
	private Button btnView;
	private Button btnOper;
	private Button btnBack;
	private OrderAdapter orderAdapter;
	private OrderService orderService;
	private int selItemIndex = -1;
	private int requestCode = 0x28;
	public static String UPDATEFLAG = "updateflag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order);
		setTitle("呼叫订单查看");
		super.onCreate(savedInstanceState);
		// 注册订单下载广播
		IntentFilter intentFileter = new IntentFilter(BrocastUtil.ORDER_DOWN_BROCAST);
		registerReceiver(orderReceiver, intentFileter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(orderReceiver);
	}

	private BroadcastReceiver orderReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int orderCount = intent.getIntExtra(BrocastUtil.ORDER_RESULT, 0);
			if (orderCount == -1) {
				PromptUtils.getInstance().showToastHasFeel("下载失败，网络或服务端异常！", OrderActivity.this, EVoiceType.fail, 0);
				return;
			} else if (orderCount == -2) {
				PromptUtils.getInstance().showToastHasFeel("下载失败，未注册或注册信息丢失！", OrderActivity.this, EVoiceType.fail, 0);
				return;
			} else if (orderCount == 0) {
				PromptUtils.getInstance().showToastHasFeel("当前没有新的订单！", OrderActivity.this, EVoiceType.normal, 0);
				return;
			} else {
				PromptUtils.getInstance().showToastHasFeel("下载成功,共【" + orderCount + "】条订单！", OrderActivity.this, EVoiceType.normal, 0);
				// 刷新界面
				refreshOrder();
			}
		}
	};

	/**
	 * 刷新界面
	 */
	private void refreshOrder() {
		List<Map<String, Object>> list = getListSource();
		orderAdapter.updateSource(list);
		tvOrderCount.setText("未处理订单：" + list.size());
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		tvOrderCount = (TextView) this.findViewById(R.id.tv_order_tvCount);
		etOrderDetail = bindEditText(R.id.et_order_etOrderDetail, null);
		btnDown = bindButton(R.id.btn_order_btnDown);
		btnView = bindButton(R.id.bottom_3_btnUpload);
		btnOper = bindButton(R.id.bottom_3_btnDel);
		btnBack = bindButton(R.id.bottom_3_btnBack);
		lvOrder = (ListView) this.findViewById(R.id.lv_order_lvOrder);
		TextView tvHead = (TextView) this.findViewById(R.id.tv_list_head_one_tvhead1);
		tvHead.setText("呼叫订单查看");
		btnView.setText("查看");
		btnOper.setText("操作");
		btnBack.setText("返回");
		btnDown.setText("下载");
		lvOrder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selItemIndex = arg2;
				showOrderDetail(selItemIndex);
			}
		});
		lvOrder.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selItemIndex = arg2;
				showOrderDetail(selItemIndex);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	private void showOrderDetail(int position) {
		String splitStr = "\r\n";
		Order order = getSelOrder(position);
		if (order != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("客户地址:").append(order.getSender_Address()).append(splitStr);
			sb.append("电话号码:").append(order.getSender_Phone()).append(splitStr);
			sb.append("手机号码:").append(order.getSender_Mobile());
			etOrderDetail.setText(sb.toString().trim());
		}
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		orderService = new OrderService(OrderActivity.this);
		List<Map<String, Object>> sourceList = getListSource();
		orderAdapter = new OrderAdapter(OrderActivity.this, sourceList, R.layout.list_item_one, R.id.tv_list_item_one_tvhead1);
		lvOrder.setAdapter(orderAdapter);
		tvOrderCount.setText("未处理订单：" + sourceList.size());
	}

	private List<Map<String, Object>> getListSource() {
		List<Order> orderList = orderService.getOrderList(GlobalParas.getGlobalParas().getUserNo());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (orderList != null && orderList.size() > 0) {
			for (Order order : orderList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(OrderAdapter.ITEM_NAME, order);
				list.add(map);
			}
		}
		return list;
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_order_btnDown:
			// 下载
			downOrder();
			break;
		case R.id.bottom_3_btnUpload:
			// 查看
			viewOrder();
			break;
		case R.id.bottom_3_btnDel:
			// 操作
			orderOper();
			break;
		case R.id.bottom_3_btnBack:
			back();
			break;
		}
	}

	/**
	 * 订单操作
	 */
	private void orderOper() {

		if (!validateOrder("操作")) {
			return;
		}
		Order model = getSelOrder(selItemIndex);
		Intent intent = new Intent(OrderActivity.this, OrderOperActivity.class);
		intent.putExtra(OrderOperActivity.FLAGKEY, false);
		intent.putExtra(OrderOperActivity.ORDERNUM, model.getLogisticid());
		startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (this.requestCode == requestCode) {
			// 订单操作
			if (resultCode == OrderOperActivity.OPERRESULTCODE) {
				boolean flag = data.getBooleanExtra(UPDATEFLAG, false);
				if (flag) {
					refreshOrder();
				}
			}
		}
	}

	private void viewOrder() {
		if (!validateOrder("查看")) {
			return;
		}
		Order model = getSelOrder(selItemIndex);
		if (model == null) {
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "查看失败！", null, EVoiceType.fail, 0);
			return;
		}
		Intent intent = new Intent(OrderActivity.this, OrderViewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(OrderViewActivity.ORDERKEY, model);
		intent.putExtra(OrderViewActivity.ORDEREXTRA, bundle);
		startActivity(intent);
	}

	private boolean validateOrder(String msg) {
		if (orderAdapter.getCount() == 0) {
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "没有需要" + msg + "的订单！", null, EVoiceType.fail, 0);
			return false;
		}
		if (selItemIndex == -1) {
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "请选择需要" + msg + "的订单！", null, EVoiceType.fail, 0);
			return false;
		}
		return true;
	}

	private Order getSelOrder(int sleIndex) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) orderAdapter.getItem(selItemIndex);
		if (map != null) {
			Order model = (Order) map.get(OrderAdapter.ITEM_NAME);
			return model;
		}
		return null;
	}

	/**
	 * 下载订单
	 */
	private void downOrder() {
		startService(new Intent(OrderActivity.this, OrderServer.class));
		PromptUtils.getInstance().showToast("正在下载，请稍候", OrderActivity.this);
	}

}
