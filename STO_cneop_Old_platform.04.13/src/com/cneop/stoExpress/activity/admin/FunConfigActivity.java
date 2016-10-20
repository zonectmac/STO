package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.dao.ModuleService;
import com.cneop.stoExpress.model.ModuleModle;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FunConfigActivity extends CommonTitleActivity {

	ListView lv;
	Button btnOk;
	Button btnBack;
	EProgramRole programRole;
	CustomAdapter customAdapter;
	List<Map<String, Object>> list; // 显示数据源
	List<Map<String, Object>> cacheList; // 缓存LIST
	ModuleService moduleService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_fun_config);
		setTitle("功能设置");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();
		btnOk = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
		lv = (ListView) findViewById(R.id.fun_config_lvFuncList);
		lv.setOnItemClickListener(itemClick);
	}

	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		 
			Map<String, Object> map = list.get(arg2);
			ModuleModle modle = (ModuleModle) map.get(CustomAdapter.ITEM_NAME);
			if (modle.getChildrenCount() > 0) { // 有子项
				boolean expanded = (Boolean) map
						.get(CustomAdapter.KEY_EXPANDED);
				if (expanded) { // 已展开，则收缩
					customAdapter.removeChildListData(arg2);
				} else { // 展开
					int id = modle.getId();
					int level = modle.getLevel();
					customAdapter.addChildListData(arg2, level, id);
					map.put(CustomAdapter.KEY_EXPANDED, true);
					customAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void initializeValues() {
	 
		super.initializeValues();
		// 加载程序角色
		programRole = GlobalParas.getGlobalParas().getProgramRole();
		moduleService = new ModuleService(this);
		list = new ArrayList<Map<String, Object>>();
		cacheList = new ArrayList<Map<String, Object>>();
		list.addAll(0, getList(programRole, moduleService, 0));
		LoadCacheList(programRole, moduleService, 0);
		customAdapter = new CustomAdapter(this, list, cacheList,
				R.layout.func_menu_item, new String[] {
						CustomAdapter.KEY_EXPANDED, CustomAdapter.ITEM_NAME },
				new int[] { R.id.func_menu_ivPic, R.id.func_menu_ckSel });
		lv.setAdapter(customAdapter);
	}

	/*
	 * 加载缓存列表
	 */
	private void LoadCacheList(EProgramRole programRole,
			ModuleService moduleService, int parentId) {
		List<Map<String, Object>> tempList = getList(programRole,
				moduleService, parentId);
		for (int i = 0; i < tempList.size(); i++) {
			Map<String, Object> map = tempList.get(i);
			cacheList.add(map);
			ModuleModle modle = (ModuleModle) map.get(CustomAdapter.ITEM_NAME);
			if (modle.getChildrenCount() > 0) {
				LoadCacheList(programRole, moduleService, modle.getId());
			}
		}
	}

	/*
	 * 根据父ID查找角色
	 */
	private List<Map<String, Object>> getList(EProgramRole programRole,
			ModuleService moduleService, int parentId) {
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		List<Object> objList = moduleService.getModuleList(parentId,
				programRole);
		if (objList != null) {
			for (Object object : objList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(CustomAdapter.KEY_EXPANDED, false);
				map.put(CustomAdapter.ITEM_NAME, object);
				tempList.add(map);
			}
		}
		return tempList;
	}

	@Override
	protected void uiOnClick(View v) {
	 
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			saveModuleConfig();
			break;
		case R.id.bottom_2_btnBack:
			back();
			break;
		}
	}

	// 保存存配置
	private void saveModuleConfig() {
	 
		List<ModuleModle> modleList = new ArrayList<ModuleModle>();
		for (int i = 0; i < cacheList.size(); i++) {
			ModuleModle modle = (ModuleModle) cacheList.get(i).get(
					CustomAdapter.ITEM_NAME);
			modleList.add(modle);
		}
		ModuleService moduleService = new ModuleService(FunConfigActivity.this);
		if (moduleService.addRecord(modleList) > 0) {
			PromptUtils.getInstance().showAlertDialog(FunConfigActivity.this,
					"保存成功！", okevent);
		} else {
			PromptUtils.getInstance().showAlertDialog(FunConfigActivity.this,
					"保存失败！", null);
		}
	}

	private OnClickListener okevent = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		 
			back();
		}
	};

}
