package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.ModuleService;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.datacenter.AutoUploadThread;
import com.cneop.stoExpress.datacenter.DownloadThread;
import com.cneop.stoExpress.datacenter.SystimeSyncThread;
import com.cneop.stoExpress.datacenter.UploadThread;
import com.cneop.stoExpress.model.ModuleModle;
import com.cneop.update.UpdateManager;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends CommonTitleActivity {
	private static int is_Nok = 1;
	private final String TAG = "MainActivity";
	private GridView gvMenu;
	private boolean isExitTip;
	private String moduleName = "moduleName";
	private String picId = "picId";
	private String packageName = "packageName";
	public static String initValue = "initValue";
	private String id = "id";
	private int roleId;
	private SimpleAdapter simpleAdapter;
	private int pageIndex = 1;
	private List<Map<String, Object>> listSource;
	private List<Object> moduleList;
	private Button btnExit;
	private TextView tvLoginUser;
	private final int pageSize = 12;
	private OrderService orderService;
	private int totalPage = 1;
	private NetworkInfo netInfo;
	private ConnectivityManager mConnectivityManager;
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);// 关键代码
		setContentView(R.layout.activity_main);
		setTitle("主界面");
		super.onCreate(savedInstanceState);
		Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24, "24");//  24小时制
		acquireWakeLock();
		// 检查更新
		new UpdateManager(this, GlobalParas.getGlobalParas().getUpgradeUrl(),
				GlobalParas.getGlobalParas().getCompanyCode()).checkUpdate();
		// -------------------------------时间同步----------------------------------------------
		if (isNetworkAvailable(this)) {
			uptime();// 时间同步
		}
		Runtime.getRuntime().gc();// 通知jvm
	}

	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public void uptime() {
		new SystimeSyncThread(this).start();
	};

	private void startFirst() {
		DownloadThread.getIntance(this).StartDownload();

		UploadThread.getIntance(this).startUpload();
		AddScanDataThread.getIntance(this).startAddScanData();
		if (GlobalParas.getGlobalParas().getAutoUploadTimeSpilt() > 0) {
			System.out.println("=========time\t" + GlobalParas.getGlobalParas().getAutoUploadTimeSpilt());
			AutoUploadThread.getInsance(getApplicationContext()).startWork();
		}
	}

	@Override
	protected void initializeComponent() {
		gvMenu = (GridView) this.findViewById(R.id.main_gvMain);
		gvMenu.setOnItemClickListener(gvClickItem);
		btnExit = bindButton(R.id.btn_main_btnBack);
		tvLoginUser = (TextView) this.findViewById(R.id.tv_main_loginName);
	}

	private OnItemClickListener gvClickItem = new OnItemClickListener() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
			openWindow(position);
		}
	};

	/**
	 * 点击事件
	 * 
	 * @param position
	 */
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings({ "unchecked", "unused" })
	private void openWindow(int position) {
		// ---------------------------------------------------------------------------------------------
		if (position + 1 > listSource.size()) {
			return;
		}
		Map<String, Object> map = (Map<String, Object>) simpleAdapter.getItem(position);
		String destPackageName = map.get(packageName).toString();
		String destInitValue = map.get(initValue).toString();
		int destId = Integer.parseInt(map.get(id).toString());
		String destModuleName = map.get(moduleName).toString();
		if (destPackageName.equals(STR_PACK_NEXT)) {
			next();
			return;
		} else if (destPackageName.equals(STR_PACK_PRE)) {
			pre();
			return;
		}
		destModuleName = destModuleName.substring(destModuleName.indexOf(".") + 1);
		int cacheUserRole = 0;
		if (destModuleName.equals("24小时件") || destModuleName.equals("车辆操作") || destModuleName.equals("包操作")) {
			cacheUserRole = GlobalParas.getGlobalParas().getUserRole().value();
		}
		try {
			/**
			 * com.cneop.stoExpress.activity.scan.RecipientScanActivity：收件
			 * com.cneop.stoExpress.activity.scan.OutgoingStationActivity：发件
			 * com.cneop.stoExpress.activity.scan.TopiecesActivity：到件
			 * com.cneop.stoExpress.activity.scan.SendingpiecesActivity 派件
			 * com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity：留仓
			 * com.cneop.stoExpress.activity.scan.ExceptionActivity:问题件
			 * com.cneop.stoExpress.activity.scan.BaggingOutActivity:装袋发件
			 * com.cneop.stoExpress.activity.MainActivity:车辆操作
			 * com.cneop.stoExpress.activity.MainActivity:24小时件
			 * com.cneop.stoExpress.activity.DataUploadActivity:数据上传
			 * com.cneop.stoExpress.activity.BarcodeTrackActivity:快件查询 上一页 下一页
			 * com.cneop.stoExpress.activity.AreaQueryActivity:区域查询
			 * com.cneop.stoExpress.activity.DataQueryActivity:数据查询
			 * com.cneop.stoExpress.activity.SystemConfigActivity:系统设置
			 * com.cneop.stoExpress.activity.PhoneMessageActivity：电话短信
			 * com.cneop.stoExpress.activity.AfterServerActivity:售后服务
			 * com.cneop.stoExpress.activity.BarcodeTrackActivity
			 * com.cneop.stoExpress.activity.scan.PickupChooseActivity
			 * com.cneop.stoExpress.activity.scan.PickupChooseActivity //收件
			 * com.cneop.stoExpress.activity.scan.SignChooseActivity 签收
			 * com.cneop.stoExpress.activity.BarcodeTrackActivity 快件查询
			 * com.cneop.stoExpress.activity.scan.MsgNoticeActivity:服务点短信
			 * com.cneop.stoExpress.activity.scan.FcOrDcActivity
			 * com.cneop.stoExpress.activity.BarcodeTrackActivity
			 * com.cneop.stoExpress.activity.BarcodeTrackActivity
			 * com.cneop.stoExpress.activity.scan.OutgoingRouteActivity
			 * com.cneop.stoExpress.activity.scan.LeaveWarehouseActivity
			 * com.cneop.stoExpress.activity.scan.FbOrDbActivity
			 * com.cneop.stoExpress.activity.scan.DbScanActivity
			 * com.cneop.stoExpress.activity.scan.PickupChooseActivity
			 * com.cneop.stoExpress.activity.scan.PackOutActivity
			 * com.cneop.stoExpress.activity.scan.ZCFJActivity
			 * com.cneop.stoExpress.activity.scan.PickupChooseActivity
			 */
			Intent intent = new Intent(MainActivity.this, Class.forName(destPackageName));
			System.out.println("======》》》》》 \t" + destPackageName);
			intent.putExtra(initValue, destInitValue);
			intent.putExtra("exitTip", false);
			intent.putExtra("parentId", destId);
			intent.putExtra("roleId", roleId);
			intent.putExtra("title", destModuleName);
			intent.putExtra("cacheUserRole", cacheUserRole);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Runtime.getRuntime().gc();
		}
	}

	@Override
	protected void uiOnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_btnBack:
			finish();
			break;
		}
	}

	private void page() {

		pre();
	}

	/**
	 * 下一页
	 */
	private void next() {

		pageIndex++;
		if (pageIndex > totalPage) {
			pageIndex = totalPage;
			return;
		}
		fillGvDataSource(moduleList, listSource, pageIndex);
		simpleAdapter.notifyDataSetChanged();
	}

	/**
	 * 上一页
	 */
	private void pre() {

		pageIndex--;
		if (pageIndex < 1) {
			pageIndex = 1;
			return;
		}
		fillGvDataSource(moduleList, listSource, pageIndex);
		simpleAdapter.notifyDataSetChanged();
	}

	@Override
	protected void initializeValues() {

		Intent intent = this.getIntent();
		isExitTip = intent.getBooleanExtra("exitTip", true);
		int parentId = intent.getIntExtra("parentId", 6);
		// 缓存用户角色
		int cacheUserRole = intent.getIntExtra("cacheUserRole", 0);
		if (cacheUserRole != 0) {
			GlobalParas.getGlobalParas().setUserRole(EUserRole.valueof(cacheUserRole));
		} else {
			GlobalParas.getGlobalParas().setUserRole(EUserRole.valueof(parentId));
		}
		roleId = intent.getIntExtra("roleId", 0);
		int parentId2 = intent.getIntExtra("parentId2", parentId);
		String titleName = intent.getStringExtra("title");
		if (!(new StrUtil()).isNullOrEmpty(titleName)) {
			setTitle(titleName);
		}
		listSource = new ArrayList<Map<String, Object>>();
		moduleList = getListDataSource(parentId, parentId2, roleId);// 返回查询数据
		simpleAdapter = new SimpleAdapter(this, listSource, R.layout.gv_item, new String[] { moduleName, picId },
				new int[] { R.id.gv_ItemText, R.id.gv_ItemImage });
		gvMenu.setAdapter(simpleAdapter);

		tvLoginUser.setText(GlobalParas.getGlobalParas().getUserName());
		int pageLevel = intent.getIntExtra("pageLevel", 0);
		if (pageLevel == 0) {
			appendPreNextPage();
			startFirst();
		}
		totalPage = moduleList.size() / pageSize;
		if (moduleList.size() % pageSize != 0) {
			totalPage++;
		}
		fillGvDataSource(moduleList, listSource, pageIndex);
		simpleAdapter.notifyDataSetChanged();
	}

	/*
	 * 获得数据源
	 */
	private List<Object> getListDataSource(int parentId, int parentId2, int roleId) {

		ModuleService moduleService = new ModuleService(MainActivity.this);
		List<Object> moduleList = moduleService.getModuleList(String.valueOf(parentId), String.valueOf(parentId2),
				String.valueOf(roleId));
		return moduleList;
	}

	private String STR_PACK_NEXT = "nextpage";
	private String STR_PACK_PRE = "prepage";

	private boolean isExists(String pack) {

		boolean isExists = false;
		for (Object objModel : moduleList) {
			ModuleModle model = (ModuleModle) objModel;
			if (model.getPackageName().equalsIgnoreCase(pack)) {
				isExists = true;
				break;
			}
		}
		return isExists;
	}

	private void appendPreNextPage() {

		if (moduleList.size() <= pageSize) {
			return;
		}
		if (isExists(STR_PACK_NEXT)) {
			return;
		}
		int nextPageCount = 0;
		int pageCount = moduleList.size() / pageSize;
		if (moduleList.size() % pageSize != 0) {
			pageCount++;
		}
		pageCount = pageCount * 2 - 2;// 也要考虑上一页,下一页各占用的个数

		nextPageCount = (moduleList.size() + pageCount) / pageSize;
		if (moduleList.size() % pageSize != 0) {
			nextPageCount++;
		}

		if (nextPageCount == 0) {
			return;
		}
		for (int i = 0; i < nextPageCount; i++) {
			int index = (1 + i) * pageSize - 1;

			if (index > moduleList.size()) {
				// moduleList.set(index+1 , getPreNextModel(false));
				continue;
			}

			moduleList.add(index, getPreNextModel(true));

			// moduleList.add( (1+i)*9,getPreNextModel(false));

			moduleList.add(index + 1, getPreNextModel(false));

		}
	}

	private ModuleModle getPreNextModel(boolean isNext) {

		ModuleModle moduleModle = new ModuleModle();
		moduleModle.setId(moduleList.size() + 1);

		moduleModle.setInitValue("");
		moduleModle.setLevel(1);
		if (isNext) {
			moduleModle.setModuleName("下一页");
			moduleModle.setPackageName(STR_PACK_NEXT);
			moduleModle.setImgName(String.valueOf(R.drawable.next));
		} else {
			moduleModle.setModuleName("上一页");
			moduleModle.setPackageName(STR_PACK_PRE);
			moduleModle.setImgName(String.valueOf(R.drawable.pre));
		}
		// moduleModle.setParentId( 2);//因为2在module表中对应的是:网点扫描员
		// moduleModle.setRoleId(2);
		moduleModle.setState("A");
		return moduleModle;
	}

	/*
	 * f填充数据源
	 */
	private void fillGvDataSource(List<Object> moduleList, List<Map<String, Object>> listSource, int pageIndex) {

		listSource.clear();
		int startIndex = (pageIndex - 1) * pageSize;

		int moduleCount = pageIndex == totalPage ? moduleList.size() : (startIndex + pageSize); // 即为endSize
		if (moduleCount > moduleList.size()) {
			moduleCount = moduleList.size();
		}
		if (pageIndex == 1) {
			if (moduleList.size() > pageSize) {
				moduleCount = pageSize;
			}
		}
		int i = 1;
		if (moduleList == null || moduleList.size() == 0) {
			return;
		}
		for (; startIndex < moduleCount; startIndex++) {
			ModuleModle modle = (ModuleModle) moduleList.get(startIndex);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(moduleName, i + "." + modle.getModuleName());
			map.put(picId, getDrawableId(modle.getImgName()));
			map.put(packageName, modle.getPackageName());
			map.put(initValue, modle.getInitValue());
			map.put(id, modle.getId());
			listSource.add(map);
			i++;
		}
	}

	/*
	 * 获得图片资源ID
	 */
	private int getDrawableId(String typeName) {

		return getResourceId(typeName, "drawable");
	}

	private int getResourceId(String typeName, String className) {

		Resources res = this.getResources();
		int resId = res.getIdentifier(typeName, className, this.getPackageName());
		return resId;
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
		case KeyEvent.KEYCODE_F1:
			startActivity(new Intent(getApplicationContext(), DataUploadActivity_f1.class));
			break;
		}
		return super.onKeyDown(keyCode, event);
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

	@Override
	protected void buttonEvent8() {

		openWindow(7);
	}

	@Override
	protected void buttonEvent9() {

		openWindow(8);
	}

	WakeLock wakeLock;

	private void acquireWakeLock() {

		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	private void releaseWakeLock() {

		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}

}
