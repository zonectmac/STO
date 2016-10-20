package com.cneop.stoExpress.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.model.UploadView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DataUploadAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;
	private int layoutID;
	private int[] itemIDs;
	public static final String ITEM_UPLOADVIEW = "UPLOADVIEW";
	private HashMap<String, Boolean> selMap;

	public DataUploadAdapter(Context context, List<Map<String, Object>> list, int layoutID, int[] itemIDs, HashMap<String, Boolean> selMap) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutID;
		this.itemIDs = itemIDs;
		this.selMap = selMap;
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
		Map<String, Object> item = list.get(position); // 项
		UploadView uploadView = (UploadView) item.get(ITEM_UPLOADVIEW);
		for (int i = 0; i < itemIDs.length; i++) {
			if (convertView.findViewById(itemIDs[i]) instanceof CheckBox) {
				CheckBox chk = (CheckBox) convertView.findViewById(itemIDs[i]);
				chk.setText(uploadView.getScanTypeStr());
				chk.setChecked(uploadView.getSelected());
			} else if (convertView.findViewById(itemIDs[i]) instanceof TextView) {
				TextView tv = (TextView) convertView.findViewById(itemIDs[i]);
				tv.setText(String.valueOf(uploadView.getTotalCount()));
			}
		}
		addListener(convertView, position);
		return convertView;
	}

	private void addListener(View convertView, final int position) {

		((CheckBox) convertView.findViewById(R.id.chk_data_upload_item_ckSel)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// CHECK点击事件
				Map<String, Object> map = list.get(position);
				UploadView uploadView = (UploadView) map.get(DataUploadAdapter.ITEM_UPLOADVIEW);
				uploadView.setSelected(isChecked);
				if (selMap.containsKey(uploadView.getScanTypeStr())) {
					selMap.remove(uploadView.getScanTypeStr());
				}
				selMap.put(uploadView.getScanTypeStr(), isChecked);
			}
		});
	}

	/**
	 * 更新数据源
	 * 
	 * @param list
	 */
	public void UpdateDataSource(List<Map<String, Object>> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
