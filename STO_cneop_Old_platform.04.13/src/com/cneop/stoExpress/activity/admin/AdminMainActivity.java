package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.HashMap;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AdminMainActivity extends CommonTitleActivity {
	private BrocastUtil brocast;

	String packageName = "com.cneop.stoExpress.activity.admin.";// APP包名
	private GridView gvMenu;
	private SimpleAdapter gvAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			setContentView(R.layout.activity_admin_main);
			setTitle("管理员");
			super.onCreate(savedInstanceState);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeComponent() {
		gvMenu = (GridView) this.findViewById(R.id.adminMain_gvMain);
		gvMenu.setOnItemClickListener(oicl);
		brocast = new BrocastUtil(this);
	}

	// 功能
	private String[] moduleNameArray = { "1.服务地址", "2.网点设置", "3.功能设置", "4.角色设置", "5.数据清空", "6.数据查询", "7.系统设置" };
	// 对应图片ID
	private int[] imgId = { R.drawable.admin_serverurl, R.drawable.admin_siteconfig, R.drawable.admin_funconfig,
			R.drawable.admin_roleconfig, R.drawable.admin_dataclear, R.drawable.dataquery, R.drawable.systemconfig };
	// 对应启动activity完全包名
	private String[] destPackageNameArray = { packageName + "ServiceUrlActivity", packageName + "SiteConfigActivity",
			packageName + "FunConfigActivity", packageName + "RoleConfigActivity", packageName + "DataClearActivity",
			"com.cneop.stoExpress.activity.DataQueryActivity", "com.cneop.stoExpress.activity.SystemConfigActivity" };

	@Override
	protected void initializeValues() {
		ArrayList<HashMap<String, Object>> gvListSource = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < moduleNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImg", imgId[i]);
			map.put("ItemName", moduleNameArray[i]);
			map.put("packageName", destPackageNameArray[i]);
			gvListSource.add(map);
		}
		gvAdapter = new SimpleAdapter(this, gvListSource, R.layout.gv_item, new String[] { "ItemImg", "ItemName" },
				new int[] { R.id.gv_ItemImage, R.id.gv_ItemText });
		gvMenu.setAdapter(gvAdapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case 7:
			// 0
			openWindow(9);
			break;
		case 8:
			// 1
			openWindow(0);
			break;
		case 9:
			// 2
			openWindow(1);
			break;
		case 10:
			// 3
			openWindow(2);
			break;
		case 11:
			// 4
			openWindow(3);
			break;
		case 12:
			// 5
			openWindow(4);
			break;
		case 13:
			// 6
			openWindow(5);
			break;
		case 14:
			// 7
			openWindow(6);
			break;
		case 15:
			// 8
			openWindow(7);
			break;
		case 16:
			// 9
			openWindow(8);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnItemClickListener oicl = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			openWindow(arg2);
		};
	};

	private void openWindow(int position) {
		if (position + 1 > moduleNameArray.length) {
			// 下标越界了!
			return;
		}
		HashMap<String, Object> item = (HashMap<String, Object>) gvAdapter.getItem(position);
		String destPackageName = item.get("packageName").toString();
		if (destPackageName.equals(destPackageNameArray[4])) {
			clearData();
		} else {
			try {
				startActivity(new Intent(this, Class.forName(destPackageName)));
			} catch (ClassNotFoundException e) {
				Toast.makeText(getApplicationContext(), "管理员界面异常了!!!", 1).show();
				e.printStackTrace();
			}
		}

	}

	private void clearData() {
		AlertDialog.Builder builder = new Builder(AdminMainActivity.this);
		builder.setTitle("数据删除");
		final EditText editText = new EditText(getApplicationContext());
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(editText);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if (editText.getText().toString().trim().equals("516615")) {
					ScanDataService service = new ScanDataService(AdminMainActivity.this);
					int t = service.delSevenData();
					if (t > 0) {
						PromptUtils.getInstance().showToast("删除成功", AdminMainActivity.this);
					} else {
						PromptUtils.getInstance().showToast("无可删除数据,删除失败", AdminMainActivity.this);
						return;
					}
					int count = service.getCount(null, null, "0");// 0未上传
					brocast.sendUnUploadCountChange(count - t);
				} else {
					PromptUtils.getInstance().showToast("密码不正确", AdminMainActivity.this);
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();

	}

	@Override
	protected void buttonEvent1() {

		openWindow(0);
	}

	@Override
	protected void buttonEvent2() {

		openWindow(1);
	}

	@Override
	protected void buttonEvent3() {

		openWindow(2);
	}

	@Override
	protected void buttonEvent4() {

		openWindow(3);
	}

	@Override
	protected void buttonEvent5() {

		openWindow(4);
	}

	@Override
	protected void buttonEvent6() {

		openWindow(5);
	}

	@Override
	protected void buttonEvent7() {
		openWindow(6);
	}

	private OnClickListener okEvent = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			// Intent intent=new Intent();
			setResult(1);
			back();
		}
	};

}
