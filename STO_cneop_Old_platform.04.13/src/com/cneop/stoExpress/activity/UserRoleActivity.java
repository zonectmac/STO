package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

public class UserRoleActivity extends CommonTitleActivity {

	private Context context = this;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_user_role);
		setTitle("角色选择");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		lv = (ListView) findViewById(R.id.lv_user_role_lvUserRole);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				// TODO Auto-generated method stub
				openWindow(position);
			}
		});

	}

	private String roleName = "roleName";
	private String roleId = "roleId";
	private Map<String, Object> map = new HashMap<String, Object>();
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

	@Override
	protected void initializeValues() {

		dataList.clear();
		map.clear();
		if ((EProgramRole) getIntent().getExtras().getSerializable("programRole") == EProgramRole.station) {
			map.put(roleName, "1: 网点业务员");
			map.put(roleId, EUserRole.business.value());// 1
			dataList.add(map);
			// -----------------------------------------------
			map = new HashMap<String, Object>();
			map.put(roleName, "2: 网点扫描员");
			map.put(roleId, EUserRole.scaner.value());// 2
			dataList.add(map);
		} else if ((EProgramRole) getIntent().getExtras().getSerializable("programRole") == EProgramRole.air) {
			map.put(roleName, "1: 航空扫描员");
			map.put(roleId, EUserRole.airScaner.value());
			dataList.add(map);
			// -------------------------------------------------------
			map = new HashMap<String, Object>();
			map.put(roleName, "2: 航空提货员");
			map.put(roleId, EUserRole.ariDelivery.value());
			dataList.add(map);
		}
		lv.setAdapter(new SimpleAdapter(this, dataList, R.layout.lv_item_column_1_a, new String[] { roleName }, new int[] { R.id.tv_lv_item_colum1_a_tvtip }));

	}

	private void openWindow(int position) {

		if (position + 1 > dataList.size()) {
			PromptUtils.getInstance().showAlertDialogHasFeel(this, "角色选择页面下面越界啦!!!!", null, EVoiceType.fail, 0);
			return;
		}
		Map<String, Object> map = dataList.get(position);
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("exitTip", false);
		intent.putExtra("parentId", Integer.parseInt(map.get(roleId).toString().trim()));
		intent.putExtra("roleId", ((EProgramRole) getIntent().getExtras().getSerializable("programRole")).value());
		intent.putExtra("parentId2", EUserRole.other.value());
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			// 1
			openWindow(0);
			break;
		case KeyEvent.KEYCODE_2:
			// 2
			openWindow(1);
			break;
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;

		}
		return true;
	}
}
