package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EPicType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dao.PhotoService;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.datacenter.UploadThread;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;

public class DataUploadActivity extends CommonTitleActivity {

	private Button btnUpload;
	private Button btnBack;
	private List<Map<String, Object>> sourceList;
	private DataUploadAdapter dataUploadAdapter;
	private ListView lvList;
	private ScanDataService scanDataService;
	private MsgSendService msgSendService;
	private PhotoService photoService;
	private OrderOperService orderOperService;
	private CheckBox chkAll;
	boolean isFirstLoad = true;
	private ProgressDialog pd;
	private ESiteProperties siteProperties;// 站点属性
	private HashMap<String, Boolean> selMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_data_upload);
		setTitle("数据上传");
		super.onCreate(savedInstanceState);
		// 注册扫描异常广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(BrocastUtil.SCREEN_UPDATE);
		registerReceiver(updateScreen, filter);

	}

	private BroadcastReceiver updateScreen = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean isupdate = intent.getBooleanExtra(BrocastUtil.SCREEN_UPDATE, false);
			if (isupdate) {
				updateListView();
			}
		}
	};

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		btnUpload = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
		btnUpload.setText(R.string.upload);
		chkAll = (CheckBox) this.findViewById(R.id.chk_data_upload_chkDataType);
		chkAll.setOnCheckedChangeListener(checkChangeEvent);
		lvList = (ListView) this.findViewById(R.id.lv_data_upload_lvList);
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		orderOperService = new OrderOperService(DataUploadActivity.this);
		siteProperties = GlobalParas.getGlobalParas().getSiteProperties();
		sourceList = new ArrayList<Map<String, Object>>();
		scanDataService = new ScanDataService(DataUploadActivity.this);
		msgSendService = new MsgSendService(DataUploadActivity.this);
		photoService = new PhotoService(GlobalParas.getGlobalParas().getSignUnUploadPath(), GlobalParas.getGlobalParas().getProblemUnUploadPath(), GlobalParas.getGlobalParas().getImageSuffix());
		selMap = new HashMap<String, Boolean>();
		// 初始化适配器
		dataUploadAdapter = new DataUploadAdapter(DataUploadActivity.this, sourceList, R.layout.lv_data_upload_item, new int[] { R.id.chk_data_upload_item_ckSel, R.id.tv_data_upload_item_tvCount }, selMap);
		lvList.setAdapter(dataUploadAdapter);
		updateListView();
	}

	private OnCheckedChangeListener checkChangeEvent = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (sourceList.size() > 0) {
				for (int i = 0; i < sourceList.size(); i++) {
					Map<String, Object> map = sourceList.get(i);
					UploadView updateView = (UploadView) map.get(DataUploadAdapter.ITEM_UPLOADVIEW);
					updateView.setSelected(isChecked);
				}
				dataUploadAdapter.UpdateDataSource(sourceList);
			}
		}
	};

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			// 上传
			uploadData();
			break;
		case R.id.bottom_2_btnBack:
			back();
			break;
		}
	}

	// 上传
	private void uploadData() {
		if (sourceList.size() == 0) {
			PromptUtils.getInstance().showToast("没有需要上传的数据！", DataUploadActivity.this);
			return;
		}
		boolean flag = true;
		for (Map<String, Object> map : sourceList) {
			UploadView uploadView = (UploadView) map.get(DataUploadAdapter.ITEM_UPLOADVIEW);
			if (uploadView.getSelected()) {
				Message uploadMsg = UploadThread.getIntance(DataUploadActivity.this).uploadHandler.obtainMessage();
				Bundle bundle = new Bundle();
				EUploadType uploadType = EUploadType.getEnum(uploadView.getUploadType());// 上传类型
				bundle.putSerializable(UploadThread.uploadTypeKey, uploadType);
				switch (uploadType) {
				case scanData:
					bundle.putSerializable(UploadThread.scanTypeKey, EScanType.getEnum(uploadView.getScanType(), null));
					bundle.putSerializable(UploadThread.sitePropertiesKey, siteProperties);
					break;
				case order: {
					bundle.putSerializable(UploadThread.scanTypeKey, EScanType.getEnum(uploadView.getScanType(), null));
					bundle.putSerializable(UploadThread.sitePropertiesKey, siteProperties);
				}
					break;
				case pic:
					bundle.putSerializable(UploadThread.imageTypeKey, EPicType.getEnum(uploadView.getScanType()));
					break;
				default:
					break;
				}
				uploadMsg.setData(bundle);
				UploadThread.getIntance(DataUploadActivity.this).uploadHandler.sendMessage(uploadMsg);
				flag = false;
			}
		}
		if (flag) {
			PromptUtils.getInstance().showToast("请选择要上传的项！", DataUploadActivity.this);
		} else {
			PromptUtils.getInstance().showToast("正在后台上传,请稍候！", DataUploadActivity.this);
		}
	}

	private void updateListView() {
		try {
			sourceList.clear();
			List<UploadView> list = scanDataService.getUnUpload();
			List<UploadView> msgList = msgSendService.getUnUploadView();
			if (msgList != null && msgList.size() > 0) {
				list.addAll(msgList);
			}
			List<UploadView> orderList = orderOperService.getUnUploadView();
			if (orderList != null && orderList.size() > 0) {
				list.addAll(orderList);
			}
			List<UploadView> picList = photoService.getUnUploadView();
			if (picList != null && picList.size() > 0) {
				list.addAll(picList);
			}
			if (list != null && list.size() > 0) {
				for (UploadView uploadView : list) {
					// 初始化缓存值
					if (selMap.containsKey(uploadView.getScanType())) {
						uploadView.setSelected(selMap.get(uploadView.getScanType()));
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DataUploadAdapter.ITEM_UPLOADVIEW, uploadView);
					sourceList.add(map);
				}
			}
			dataUploadAdapter.UpdateDataSource(sourceList);
			if (isFirstLoad) {
				if (sourceList.size() > 0) {
					chkAll.setChecked(true);
				}
				isFirstLoad = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(updateScreen);
	}
}
