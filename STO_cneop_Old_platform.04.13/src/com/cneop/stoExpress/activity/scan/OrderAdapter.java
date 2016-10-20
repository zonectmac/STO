package com.cneop.stoExpress.activity.scan;

import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.model.Order;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;
	private int layoutID;
	private int itemIDs;
	public static final String ITEM_NAME = "ITEM_NAME";

	public OrderAdapter(Context context, List<Map<String, Object>> list, int layoutID, int itemIDs) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutID;
		this.itemIDs = itemIDs;
	}

	@Override
	public int getCount() {

		int count = 0;
		if (list != null) {
			count = list.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {

		if (list != null && list.size() > 0 && position < list.size() && position > -1) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = mInflater.inflate(layoutID, null);
		Map<String, Object> item = list.get(position); // Ïî
		Order orderModel = (Order) item.get(ITEM_NAME);
		TextView tv = (TextView) convertView.findViewById(itemIDs);
		String urgeStr = "";
		if (orderModel.getIsUrge() == 1) {
			// ´ß´Ù
			tv.setTextColor(Color.RED);
			urgeStr = "´ß|";
		}
		tv.setText("¶©µ¥±àºÅ:" + urgeStr + orderModel.getLogisticid());
		return convertView;
	}

	public void updateSource(List<Map<String, Object>> addList) {
		list.clear();
		list.addAll(addList);
		notifyDataSetChanged();
	}

}
