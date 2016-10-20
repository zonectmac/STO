package com.cneop.stoExpress.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.ScanBaseActivity;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.DataQueryService;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dao.PhotoService;
import com.cneop.stoExpress.model.QueryView;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.DateUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IDoubleClickItem;
import com.cneop.util.scan.NewScanManager;
import com.cneop.util.scan.ScanManager;
import com.math.math;

@SuppressLint("SimpleDateFormat")
public class DataQueryActivity extends ScanBaseActivity {

	private com.cneop.stoExpress.activity.common.DatePicker datestart, dateend;
	private EditText etstart, etend;
	private Button btnstart, btnend;
	private EditText etBarcode;
	private RadioButton rbuserno, rbdevice;
	boolean isUser = false;
	private Button btnToday, btnYesterday, btnWeek, btnLastweek;

	private Map<String, Object> map;
	private List<Map<String, Object>> list = null;
	private String starttime, endtime;
	private int lastClickId = -1;
	private long lastClickTime = 0;
	private MsgSendService msgSendService;
	private PhotoService photoService;
	private OrderOperService orderOperService;
	public static String SCAN_TYPE = "scanType";// 模块
	public static String BUSINESSTYPE_NAME = "moduleName";// 模块中文描述
	public static String BAROCDE = "barcode";// 条码
	public static String STARTTIME = "starttime";// 开始时间
	public static String ENDTIME = "endtime"; // 结束时间
	public static String SYNCSTATUS = "uploadStatus";// 上传状态
	public static String UploadType = "uploadType";
	public static String CUR_USER_CHECKED = "CUR_USER_CHECKED";// 本工号
	public static final String EXPRESSTYPE = "EXPRESSTYPE";
	public static final String NEXTSTATION = "NEXTSTATION";
	public static final String ROUTE = "ROUTE";
	DateUtil dateUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_data_query);
		setTitle("数据维护");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	@Override
	protected void onResume() {
		// 扫描头初始化
		// 上电放到Resume中，避免出现待机唤醒后不上电的情况
		scannerPower = true;
		super.setScannerPower();
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
		super.onResume();
	}

	NewScanManager manager = new NewScanManager() {

	};

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		try {
			datestart = (com.cneop.stoExpress.activity.common.DatePicker) findViewById(R.id.date_picker_start);
			datestart.setuType(1);
			etstart = (EditText) datestart.findViewById(R.id.datepicker_etDate);

			dateend = (com.cneop.stoExpress.activity.common.DatePicker) findViewById(R.id.date_picker_end);
			dateend.setuType(2);
			etend = (EditText) dateend.findViewById(R.id.datepicker_etDate);

			etBarcode = bindEditText(R.id.et_dataquery_barcode, null);
			String digits = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			etBarcode.setKeyListener(DigitsKeyListener.getInstance(digits));

			rbuserno = (RadioButton) findViewById(R.id.rdo_query_type_userno);
			rbdevice = (RadioButton) findViewById(R.id.rdo_query_type_device);

			btnToday = bindButton(R.id.btn_today);
			btnYesterday = bindButton(R.id.btn_yesterday);
			btnWeek = bindButton(R.id.btn_week);
			btnLastweek = bindButton(R.id.btn_lastweek);

			btnUpload.setText("确定");
			btnDel.setText("详情");

			if (GlobalParas.getGlobalParas().getUserRole() == EUserRole.other) {
				rbdevice.setChecked(true);
				rbuserno.setEnabled(false);
				rbdevice.setEnabled(false);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void initScanCode() {

	}

	@Override
	protected void addRecord() {

	}

	@Override
	protected boolean validateInput() {

		return false;
	}

	/**
	 * 扫描数据
	 */
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		if (etBarcode != null) {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(barcode);
			if (flag) {
				addRecord();
				etBarcode.requestFocus();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
			}
		}
	}

	@Override
	protected void onRestart() {

		doSearchBtnClick();// 暂不刷新,查询太耗时,以后看用户需求是否要加上,原CE的未加上
		super.onRestart();
	}

	@Override
	protected void uiOnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_today:
			getToday();
			break;
		case R.id.btn_yesterday:
			getYesterday();
			break;
		case R.id.btn_week:
			getWeek();
			break;
		case R.id.btn_lastweek:
			getLastweek();
			break;
		case R.id.bottom_3_btnBack:
			finish();
			break;
		case R.id.bottom_3_btnUpload:
			doSearchBtnClick();
			break;
		case R.id.bottom_3_btnDel:
			detail_click();
			break;
		}
	}

	ProgressDialog pd;
	Handler handler = new Handler();

	@Override
	protected void onDestroy() {

		if (sourceList != null) {
			sourceList.clear();
		}
		super.onDestroy();
	}

	private List<String> lists = new ArrayList<String>();

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void showmsg(Context context, String msg, byte[] date) {

		if (context == null) {
			if (DEBUG) {
				getActionBar().hide();// 隐藏通知栏
				for (int i = 0; i < date.length; i++) {
					if (date.length < 15) {
						lists.add(msg);
						Debug.getGlobalExternalAllocSize();
					} else {
						list.clear();
					}
				}
				try {
					getApplicationContext().clearWallpaper();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.getMessage());

				}
			}
			context = getApplicationContext();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
		Runtime.getRuntime().gc();// 回收内存

	}

	private void doSearchBtnClick() {
		pd = ProgressDialog.show(DataQueryActivity.this, "查询", "正在查询请稍候...");
		if (sourceList != null) {
			sourceList.clear();
			lvx.clear();
		}
		String startTimeStr = etstart.getText().toString() + ":00";
		String endTimeStr = etend.getText().toString() + ":00";
		Date startTime = dateUtil.getDateFormStr(startTimeStr, "yyyy-MM-dd HH:mm:ss");
		Date endTime = dateUtil.getDateFormStr(endTimeStr, "yyyy-MM-dd HH:mm:ss");
		if (startTime.getTime() > endTime.getTime()) {
			PromptUtils.getInstance().showToast("时间设置错误,请重新设置!", DataQueryActivity.this);
			// PromptUtils.getInstance().showAlertDialogHasVoice(
			// DataQueryActivity.this, "时间设置错误,请重新设置!", null,
			// EVoiceType.fail);
			return;
		}

		new SearchThread().start();

	}

	private class SearchThread extends Thread {
		@Override
		public void run() {
			try {
				loadListView();
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				pd.dismiss();
			}
		}

	}

	private void detail_click() {

		if (lvx.GetSelValue() == null) {
			PromptUtils.getInstance().showToast("请先在列表中选取一行记录!", DataQueryActivity.this);
			return;
		}
		PromptUtils.getInstance().showAlertDialog(DataQueryActivity.this, "是否多条件筛选数据？", dialogOnClick, dialogOnClick);
	}

	private void getToday() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		etstart.setText(date + " 00:00:00");
		etend.setText(date + " 23:59:59");
	}

	private void getYesterday() {

		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, -1);
		Date date = cd.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		etstart.setText(sdf.format(date) + " 00:00:00");
		etend.setText(sdf.format(date) + " 23:59:59");
	}

	private void getWeek() {

		Calendar cd = Calendar.getInstance();
		int i = 0;
		int week = cd.get(Calendar.DAY_OF_WEEK);
		switch (week) {
		case 1:
			i = 0;
			break;
		case 2:
			i = 1;
			break;
		case 3:
			i = 2;
			break;
		case 4:
			i = 3;
			break;
		case 5:
			i = 4;
			break;
		case 6:
			i = 5;
			break;
		case 7:
			i = 6;
			break;
		}
		cd.add(Calendar.DAY_OF_MONTH, -i + 1);
		Date date = cd.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		etstart.setText(sdf.format(date) + " 00:00:00");
		cd.add(Calendar.DAY_OF_MONTH, 6);
		date = cd.getTime();
		etend.setText(sdf.format(date) + " 23:59:59");
	}

	private void getLastweek() {

		Calendar cd = Calendar.getInstance();
		int i = 0;
		int week = cd.get(Calendar.DAY_OF_WEEK);
		switch (week) {
		case 1:
			i = 0;
			break;
		case 2:
			i = 1;
			break;
		case 3:
			i = 2;
			break;
		case 4:
			i = 3;
			break;
		case 5:
			i = 4;
			break;
		case 6:
			i = 5;
			break;
		case 7:
			i = 6;
			break;
		}
		cd.add(Calendar.DAY_OF_MONTH, -i - 6);
		Date date = cd.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		etstart.setText(sdf.format(date) + " 00:00:00");
		cd.add(Calendar.DAY_OF_MONTH, 6);
		date = cd.getTime();
		etend.setText(sdf.format(date) + " 23:59:59");
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		dateUtil = new DateUtil();
		getToday();
		if (GlobalParas.getGlobalParas().getUserRole() == null || GlobalParas.getGlobalParas().getUserRole() == EUserRole.other) {// 管理员
			rbuserno.setEnabled(false);
			rbdevice.setEnabled(false);
			rbdevice.setChecked(true);
		}

		orderOperService = new OrderOperService(DataQueryActivity.this);
		msgSendService = new MsgSendService(DataQueryActivity.this);
		photoService = new PhotoService(GlobalParas.getGlobalParas().getSignUnUploadPath(), GlobalParas.getGlobalParas().getSignUploadPath(), GlobalParas.getGlobalParas().getProblemUnUploadPath(), GlobalParas.getGlobalParas().getProblemUploadPath(), GlobalParas.getGlobalParas().getImageSuffix());
	}

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_dataquery_lvBarcodeList);
		lvx.inital(R.layout.list_item_four_b, new String[] { BUSINESSTYPE_NAME, amountkey, unsendkey, sendkey }, new int[] { R.id.tv_list_item_four_b_tvhead1, R.id.tv_list_item_four_b_tvhead2, R.id.tv_list_item_four_b_tvhead3, R.id.tv_list_item_four_b_tvhead4 });
		lvx.SetDoubleClickItem(new DoubleClickItem());
	}

	class DoubleClickItem implements IDoubleClickItem {

		@Override
		public void onItemDoubleClick(int position) {

			detail_click();
		}

	};

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead3);
		TextView tvHead4 = (TextView) this.findViewById(R.id.tv_list_head_four_tvhead4);
		tvHead1.setText("业务类型");
		tvHead2.setText("总数 ");
		tvHead3.setText("未发");
		tvHead4.setText("已发");
		tvHead1.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(DataQueryActivity.this, 130), LayoutParams.WRAP_CONTENT));
		tvHead3.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(DataQueryActivity.this, 70), LayoutParams.WRAP_CONTENT));
	}

	private final List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
	private DataQueryService service;

	// ----------------------------------------------------

	private void loadListView() {
		if (service == null) {
			service = new DataQueryService(this);
		}
		isUser = rbuserno.isChecked();
		starttime = etstart.getText().toString().trim();
		endtime = etend.getText().toString().trim();
		String barcode = etBarcode.getText().toString().trim();

		List<Map<String, Object>> DataList = service.getSearch(starttime, endtime, GlobalParas.getGlobalParas().getUserRole(), isUser, barcode);

		if (DataList == null) {
			return;
		}

		// 扫描数据
		for (int i = 0; i < DataList.size(); i++) {
			if (GlobalParas.getGlobalParas().getUserRole() == EUserRole.other && // 管理员界面不显示<=0的数据
					Integer.parseInt(DataList.get(i).get(amountkey).toString()) <= 0) {
				// i++;
				continue;
			}
			map = new HashMap<String, Object>();
			map.put(BUSINESSTYPE_NAME, DataList.get(i).get(BUSINESSTYPE_NAME).toString().trim());
			map.put(SCAN_TYPE, DataList.get(i).get(SCAN_TYPE).toString().trim());
			map.put(amountkey, DataList.get(i).get(amountkey).toString().trim());
			map.put(unsendkey, DataList.get(i).get(unsendkey).toString().trim());// 未发
			map.put(sendkey, DataList.get(i).get(sendkey).toString().trim());
			map.put(UploadType, EUploadType.scanData.toString().trim());// 已发
			sourceList.add(map);
		}

		addOtherData();

		handler.post(new Runnable() {
			@Override
			public void run() {
				lvx.add(sourceList);
				pd.dismiss();
			}
		});
	}

	/**
	 * 订单数据 ,短信数据 拍照数据
	 * 
	 */
	private void addOtherData() {

		List<QueryView> lstOther = new ArrayList<QueryView>();
		String startTime = etstart.getText().toString().trim();
		String endTime = this.etend.getText().toString().trim();
		String barcode = etBarcode.getText().toString().trim();
		String userCode = "";
		EUserRole userRole = GlobalParas.getGlobalParas().getUserRole();
		if (rbuserno.isChecked()) {
			userCode = GlobalParas.getGlobalParas().getUserNo();
		} else {
			userRole = EUserRole.other;
		}

		if (userRole == EUserRole.business || userRole == EUserRole.other) {
			List<QueryView> orderList = orderOperService.getQueryView(barcode, userCode, startTime, endTime);
			if (orderList != null && orderList.size() > 0) {
				lstOther.addAll(orderList);
			}
		}
		// 短信数据
		if (userRole == EUserRole.business || userRole == EUserRole.scaner || userRole == EUserRole.other) {
			List<QueryView> msgList = msgSendService.getQueryView(barcode, userCode, startTime, endTime);
			if (msgList != null && msgList.size() > 0) {
				// list.addAll(msgList);
				lstOther.addAll(msgList);
			}
		}
		// 拍照数据
		if (userRole == EUserRole.business || userRole == EUserRole.other) {
			QueryView signQueryView = photoService.getSignQueryView(barcode, startTime, endTime);
			if (signQueryView != null) {
				lstOther.add(signQueryView);
			}
		}
		if (userRole != EUserRole.ariDelivery) {
			QueryView problemQueryView = photoService.getProblemQueryView(barcode, startTime, endTime);
			if (problemQueryView != null) {
				lstOther.add(problemQueryView);
			}
		}

		for (QueryView view : lstOther) {

			int totalCount = view.getTotalDataCount();
			int unUploadCount = view.getUnUpload();
			int uploadedCount = totalCount - unUploadCount;

			map = new HashMap<String, Object>();
			map.put(BUSINESSTYPE_NAME, view.getModuleName());
			// map.put(SCAN_TYPE,
			// DataList.get(i).get(SCAN_TYPE).toString().trim());
			map.put(amountkey, totalCount);// 总数
			map.put(unsendkey, unUploadCount);// 未发
			map.put(sendkey, uploadedCount);// 已发
			map.put(SCAN_TYPE, view.getScanType());
			map.put(UploadType, view.getUploadType());

			sourceList.add(map);

			// lvx.add(map);

		}
	}

	@Override
	protected void onOtherResult(int requestCode, int resultCode, Intent data) {

		loadListView();// 重新加载页面
	}

	private DialogInterface.OnClickListener dialogOnClick = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			@SuppressWarnings("unchecked")
			Map<String, Object> map = lvx.GetSelValue();

			EUploadType uploadType = EUploadType.scanData;
			if (map.get(UploadType) != null) {
				uploadType = EUploadType.getEnum(map.get(UploadType).toString().trim());
			}

			Intent intent = null;
			if (which == DialogInterface.BUTTON_POSITIVE) {
				// 确定
				intent = new Intent(DataQueryActivity.this, SelectorControlActivity.class);
			} else {
				// 取消
				if (uploadType == EUploadType.pic) {
					intent = new Intent(DataQueryActivity.this, PicQueryActivity.class);
				} else {
					intent = new Intent(DataQueryActivity.this, SelectResultActivity.class);
				}
			}
			intent.putExtra(SCAN_TYPE, map.get(SCAN_TYPE).toString().trim());
			intent.putExtra(BUSINESSTYPE_NAME, map.get(BUSINESSTYPE_NAME).toString().trim());
			intent.putExtra(BAROCDE, etBarcode.getText().toString().trim());
			intent.putExtra(STARTTIME, starttime);
			intent.putExtra(ENDTIME, endtime);
			intent.putExtra(UploadType, uploadType.toString().trim());
			intent.putExtra(CUR_USER_CHECKED, rbuserno.isChecked());
			startActivity(intent);
		}
	};

}
