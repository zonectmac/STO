package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.MainActivity;
import com.cneop.util.activity.CommonTitleActivity;

public class PickupChooseActivity extends CommonTitleActivity {

	private ListView listview;

	private List<Map<String, Object>> list;
	private String[] str = { "1不指定电话收件", "2指定电话单票收件", "3指定电话多票收件" };
	int[] ima = { R.drawable.phones, R.drawable.message, R.drawable.connects };
	private String initValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_phone_message);
		setTitle("收件");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeValues() {

		initValue = this.getIntent().getStringExtra(MainActivity.initValue);
		super.initializeValues();
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				onChoose(position);
			}
		});

	}

	private void onChoose(int position) {

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		Map<String, Object> map = (Map<String, Object>) list.get(position);
		if (map.get("phone").equals(str[0])) {
			intent = new Intent(PickupChooseActivity.this, PickupScanNophoneActivity.class);
			bundle.putSerializable("type", "nophone");
		} else if (map.get("phone").equals(str[1])) {
			intent = new Intent(PickupChooseActivity.this, PickupScanPhoneActivity.class);
			bundle.putSerializable("type", "phonesiger");
		} else if (map.get("phone").equals(str[2])) {
			intent = new Intent(PickupChooseActivity.this, PickupScanPhoneActivity.class);
			bundle.putSerializable("type", "phonemore");
		}
		intent.putExtra(MainActivity.initValue, PickupChooseActivity.this.initValue);
		intent.putExtras(bundle);
		startActivity(intent);
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
				// 3
				onChoose(2);
				break;
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
