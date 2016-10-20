package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.scan.ScanManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SignChooseActivity extends CommonTitleActivity {

	ListView listview;
	List<Map<String, Object>> list;

	String[] str = { "1单票签收", "2多票签收", "3拍照签收" };
	int[] ima = { R.drawable.phones, R.drawable.message, R.drawable.connects };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_signchoose);
		setTitle("签收");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		listview = (ListView) findViewById(R.id.listsign);
		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < str.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", ima[i]);
			map.put("sign", str[i]);
			list.add(map);
		}
		SimpleAdapter adater = new SimpleAdapter(getApplicationContext(), list, R.layout.list_item_one_a, new String[] { "image", "sign" }, new int[] { R.id.listimage, R.id.tv_list_item_one_a });
		listview.setAdapter(adater);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				onChoose(pos);

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

	private void onChoose(int position) {
		Intent intent = null;
		Bundle bundle = new Bundle();
		Map<String, Object> map = (Map<String, Object>) list.get(position);
		if (map.get("sign").equals(str[0])) {
			intent = new Intent(SignChooseActivity.this, SignatureActivity.class);
			bundle.putSerializable("type", "signsiger");
		} else if (map.get("sign").equals(str[1])) {
			intent = new Intent(SignChooseActivity.this, SignatureActivity.class);
			bundle.putSerializable("type", "signmore");
		} else if (map.get("sign").equals(str[2])) {
			intent = new Intent(SignChooseActivity.this, SignaturePhotoActivity.class);
			bundle.putSerializable("type", "signphoto");
		}
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
