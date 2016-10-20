package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cneop.stoExpress.cneops.R;
import com.cneop.util.activity.CommonTitleActivity;

public class PhoneMessageActivity extends CommonTitleActivity {

	ListView listview;

	List<Map<String, Object>> list;
	String[] str = { "1电话服务", "2短信服务", "3联系人" };
	int[] ima = { R.drawable.phones, R.drawable.message, R.drawable.connects };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_phone_message);
		setTitle("电话短信");
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		listview = (ListView) findViewById(R.id.listpmc);
		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < str.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", ima[i]);
			map.put("phone", str[i]);
			list.add(map);
		}
		SimpleAdapter adater = new SimpleAdapter(getApplicationContext(), list, R.layout.list_item_one_a, new String[] { "image", "phone" }, new int[] { R.id.listimage, R.id.tv_list_item_one_a });
		listview.setAdapter(adater);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onChoose(arg2);

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case 8:
			// 1
			onChoose(0);
			break;
		case 9:
			// 2
			onChoose(1);
			break;
		case 10:
			onChoose(2);
			// 3
			break;
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		}
		return false;
	}

	// @Override
	// protected void onOneKeyDown(boolean isGroupKeyDowned) {
	// onChoose(0);
	// super.onOneKeyDown(isGroupKeyDowned);
	// }
	//
	// @Override
	// protected void onTwoKeyDown(boolean isGroupKeyDowned) {
	// onChoose(1);
	// super.onTwoKeyDown(isGroupKeyDowned);
	// }
	//
	// @Override
	// protected void onThreeKeyDown(boolean isGroupKeyDowned) {
	// onChoose(2);
	// super.onThreeKeyDown(isGroupKeyDowned);
	// }

	private void onChoose(int position) {
		Intent intent;
		// Map<String, Object> map = (Map<String,
		// Object>)arg0.getItemAtPosition(arg2);
		Map<String, Object> map = (Map<String, Object>) list.get(position);
		if (map.get("phone").equals(str[0])) {
			intent = new Intent(Intent.ACTION_DIAL);
			intent.setClassName("com.android.contacts", "com.android.contacts.DialtactsActivity");
			startActivity(intent);
		} else if (map.get("phone").equals(str[1])) {
			intent = new Intent(Intent.ACTION_MAIN);
			intent.setType("vnd.android-dir/mms-sms");
			// intent.setData(Uri.parse("content://mms-sms/conversations/"));//此为号码
			startActivity(intent);
		} else if (map.get("phone").equals(str[2])) {
			intent = new Intent();
			intent.setClassName("com.android.contacts", "com.android.contacts.activities.PeopleActivity");
			startActivity(intent);
		}
	}
}
