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
		setTitle("���ж����鿴");
		super.onCreate(savedInstanceState);
		// ע�ᶩ�����ع㲥
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
				PromptUtils.getInstance().showToastHasFeel("����ʧ�ܣ�����������쳣��", OrderActivity.this, EVoiceType.fail, 0);
				return;
			} else if (orderCount == -2) {
				PromptUtils.getInstance().showToastHasFeel("����ʧ�ܣ�δע���ע����Ϣ��ʧ��", OrderActivity.this, EVoiceType.fail, 0);
				return;
			} else if (orderCount == 0) {
				PromptUtils.getInstance().showToastHasFeel("��ǰû���µĶ�����", OrderActivity.this, EVoiceType.normal, 0);
				return;
			} else {
				PromptUtils.getInstance().showToastHasFeel("���سɹ�,����" + orderCount + "����������", OrderActivity.this, EVoiceType.normal, 0);
				// ˢ�½���
				refreshOrder();
			}
		}
	};

	/**
	 * ˢ�½���
	 */
	private void refreshOrder() {
		List<Map<String, Object>> list = getListSource();
		orderAdapter.updateSource(list);
		tvOrderCount.setText("δ��������" + list.size());
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
		tvHead.setText("���ж����鿴");
		btnView.setText("�鿴");
		btnOper.setText("����");
		btnBack.setText("����");
		btnDown.setText("����");
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
			sb.append("�ͻ���ַ:").append(order.getSender_Address()).append(splitStr);
			sb.append("�绰����:").append(order.getSender_Phone()).append(splitStr);
			sb.append("�ֻ�����:").append(order.getSender_Mobile());
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
		tvOrderCount.setText("δ��������" + sourceList.size());
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
			// ����
			downOrder();
			break;
		case R.id.bottom_3_btnUpload:
			// �鿴
			viewOrder();
			break;
		case R.id.bottom_3_btnDel:
			// ����
			orderOper();
			break;
		case R.id.bottom_3_btnBack:
			back();
			break;
		}
	}

	/**
	 * ��������
	 */
	private void orderOper() {

		if (!validateOrder("����")) {
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
			// ��������
			if (resultCode == OrderOperActivity.OPERRESULTCODE) {
				boolean flag = data.getBooleanExtra(UPDATEFLAG, false);
				if (flag) {
					refreshOrder();
				}
			}
		}
	}

	private void viewOrder() {
		if (!validateOrder("�鿴")) {
			return;
		}
		Order model = getSelOrder(selItemIndex);
		if (model == null) {
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "�鿴ʧ�ܣ�", null, EVoiceType.fail, 0);
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
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "û����Ҫ" + msg + "�Ķ�����", null, EVoiceType.fail, 0);
			return false;
		}
		if (selItemIndex == -1) {
			PromptUtils.getInstance().showAlertDialogHasFeel(OrderActivity.this, "��ѡ����Ҫ" + msg + "�Ķ�����", null, EVoiceType.fail, 0);
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
	 * ���ض���
	 */
	private void downOrder() {
		startService(new Intent(OrderActivity.this, OrderServer.class));
		PromptUtils.getInstance().showToast("�������أ����Ժ�", OrderActivity.this);
	}

}
