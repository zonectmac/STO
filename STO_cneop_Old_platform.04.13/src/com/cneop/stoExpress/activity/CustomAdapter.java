package com.cneop.stoExpress.activity;

import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private int resource;
	public List<Map<String, Object>> list;
	
	

	//	private Map<Integer, Object> selected;



	public CustomAdapter(Context context,List<Map<String, Object>> list,int resource,String flag[], int itemid[]){

		this.list = list;
		this.resource = resource;
		this.inflater = LayoutInflater.from(context);
		//		selected = new HashMap<Integer, Object>();
	}

	//	private class ViewHolder{
	//		TextView textview1;
	//		TextView textview2;
	//		CheckBox checkbox1;
	//	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
	 
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
	 
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//		ViewHolder holder=null;
		//		View view=null;
		//		if(convertView==null){
		convertView = inflater.inflate(resource, null);
		//			holder = new ViewHolder();
		//			holder.checkbox1 = (CheckBox) convertView.findViewById(R.id.checkbox);
		//			holder.textview1 = (TextView) convertView.findViewById(R.id.textview1);
		//			holder.textview2 = (TextView) convertView.findViewById(R.id.textview2);

		//			holder.checkbox1.setTag(position);
		//			convertView.setTag(holder);
		CheckBox checkbox1 = (CheckBox) convertView.findViewById(R.id.checkbox);
		TextView textview1 = (TextView) convertView.findViewById(R.id.textview1);
		TextView textview2 = (TextView) convertView.findViewById(R.id.textview2);

		textview1.setText(list.get(position).get("operation").toString().trim());
		textview2.setText(list.get(position).get("num").toString().trim());

		checkbox1.setChecked(list.get(position).get("state").equals("true")==true?true:false);
		addListener(convertView, position);
		return convertView;
		//		}else{
		//			view = convertView;
		//			holder = (ViewHolder) view.getTag();
		//		}

		//		holder.textview1.setText(list.get(position).get("operation").toString().trim());
		//		holder.textview2.setText(list.get(position).get("num").toString().trim());

		//		holder.checkbox1.setChecked(list.get(position).get("state").equals("true")==true?true:false);
		//		holder.checkbox1.setTag(position);
		//		if(selected.containsKey(position)){
		//			holder.checkbox1.setChecked(true);
		//		}else{
		//			holder.checkbox1.setChecked(false);
		//		}
		//		addListener(holder,position);
		//		return convertView;
	}

	private void addListener(View convertView,final int position){
		((CheckBox)convertView.findViewById(R.id.checkbox)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			 
				String flag="false";
				if(isChecked){
					flag="true";
				}
				list.get(position).put("state", flag);
			}
		});
	}
}
