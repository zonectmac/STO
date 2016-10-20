package com.cneop.stoExpress.activity.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.MainActivity;
import com.cneop.util.activity.CommonTitleActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SecondMenuActivity extends CommonTitleActivity {

	ListView lvList;
	SimpleAdapter simpleAdapter;
	List<Map<String, Object>> dataList;
	private final String FUNCTIONNAME = "FUNCTIONNAME";
	private final String SJ = "sj";
	private final String QS = "qs";
	public final static String ISTelSJ = "ISTelSJ";
	public final static String ISPHOTOSIGN = "ISPHOTOSIGN";
	public final static String Scan24HFLAG = "Scan24HFLAG";
	private final String SCANTYPE = "SCANTYPE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_second_menu);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();
		lvList = (ListView) this.findViewById(R.id.lv_second_menu_lvList);
		lvList.setOnItemClickListener(lvItemClick);
	}

	private OnItemClickListener lvItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
		 
			openWindow(position);
		}
	};

	private void openWindow(int position) {
		Map<String, Object> map = dataList.get(position);
		String scanType = map.get(SCANTYPE).toString().trim();
		Intent intent = null;
		if (scanType.equalsIgnoreCase(SJ)) {
			intent = new Intent(SecondMenuActivity.this,
					PickupScanActivity.class);
			intent.putExtra(ISTelSJ,
					Boolean.parseBoolean(map.get(ISTelSJ).toString().trim()));
			intent.putExtra(Scan24HFLAG, map.get(Scan24HFLAG).toString().trim());
		} else if (scanType.equalsIgnoreCase(QS)) {
			intent = new Intent(SecondMenuActivity.this,
					SignatureActivity.class);
			intent.putExtra(ISPHOTOSIGN,
					Boolean.parseBoolean(map.get(ISPHOTOSIGN).toString().trim()));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	// @Override
	// protected void onOneKeyDown(boolean isGroupKeyDowned) {
	// openWindow(0);
	// super.onOneKeyDown( isGroupKeyDowned);
	// }
	// @Override
	// protected void onTwoKeyDown(boolean isGroupKeyDowned) {
	// openWindow(1);
	// super.onTwoKeyDown( isGroupKeyDowned);
	// }
	@Override
	protected void initializeValues() {
	 
		super.initializeValues();
		dataList = new ArrayList<Map<String, Object>>();
		Intent intent = this.getIntent();
		String initValue = intent.getStringExtra(MainActivity.initValue);
		if (initValue.equalsIgnoreCase(SJ) || initValue.equals("1")) {
			setTitle("收件选择");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCTIONNAME, "1. 不指定电话收件");
			map.put(SCANTYPE, SJ);
			map.put(ISTelSJ, false);
			if (initValue.equals("1")) {
				map.put(Scan24HFLAG, "1");
			} else {
				map.put(Scan24HFLAG, "");
			}
			dataList.add(map);
			map = new HashMap<String, Object>();
			map.put(FUNCTIONNAME, "2. 指定电话收件");
			map.put(SCANTYPE, SJ);
			map.put(ISTelSJ, true);
			if (initValue.equals("1")) {
				map.put(Scan24HFLAG, "1");
			} else {
				map.put(Scan24HFLAG, "");
			}
			dataList.add(map);
		} else if (initValue.equalsIgnoreCase(QS)) {
			setTitle("签收选择");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FUNCTIONNAME, "1. 签收");
			map.put(SCANTYPE, QS);
			map.put(ISPHOTOSIGN, false);
			dataList.add(map);
			map = new HashMap<String, Object>();
			map.put(FUNCTIONNAME, "2. 拍照签收");
			map.put(SCANTYPE, QS);
			map.put(ISPHOTOSIGN, true);
			dataList.add(map);
		}
		simpleAdapter = new SimpleAdapter(SecondMenuActivity.this, dataList,
				R.layout.lv_item_column_1, new String[] { FUNCTIONNAME },
				new int[] { R.id.tv_lv_item_colum1_tvtip });
		lvList.setAdapter(simpleAdapter);
	}

 

}
