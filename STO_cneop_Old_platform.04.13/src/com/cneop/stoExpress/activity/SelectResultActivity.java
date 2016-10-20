package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.DataQueryService;
import com.cneop.stoExpress.dao.DestinationService;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.OrderAbnormalService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.dao.ServerStationService;
import com.cneop.stoExpress.dao.StationService;
import com.cneop.stoExpress.dao.StationXmlService;
import com.cneop.stoExpress.dao.UserService;
import com.cneop.stoExpress.dao.UserXmlService;
import com.cneop.stoExpress.datacenter.UploadThread;
import com.cneop.stoExpress.model.DestinationStation;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.model.OrderAbnormal;
import com.cneop.stoExpress.model.OrderOperating;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.model.ServerStation;
import com.cneop.stoExpress.model.ServerStationModel;
import com.cneop.stoExpress.model.Station;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.stoExpress.model.User;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.stoExpress.common.Enums.EDbSelectOrUpdate;
import com.cneop.stoExpress.common.Enums.EPicType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.util.DateUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IItemSelected;
import com.cneop.util.controls.ListViewEx.IListenDelSelRowSuc;

public class SelectResultActivity extends CommonTitleActivity {

	private ListViewEx lvx;
	private EditText etResult;
	private String title;
	private BrocastUtil brocastUtil;

	protected Button btnUpload;
	protected Button btnDel;
	protected Button btnBack;
	private Button btn_data_query_result_btnPre;
	private Button btn_data_query_result_btnNext;

	private StationService stationService;
	private UserService userService;
	private MsgSendService msgSendService;
	private ServerStationService serverStationService;
	private DestinationService destinationService;
	private OrderOperService orderOperService;
	private OrderAbnormalService orderAbnormalService;
	private ScanDataService scanDataService;

	protected EScanType scanType;
	ESiteProperties siteProperties = ESiteProperties.all;
	int pageSize = 200;// ÿҳ��С
	int pageNum;// ҳ��
	int pageIndex = 1;// ��ǰҳ
	int totalCount;// �ܼ�¼
	String sqlWhere = "";

	// String barcode, uploadType;
	EUploadType enumUploadType = EUploadType.other;
	// boolean bCurUserChecked;
	ControlUtil control = null;
	final String ITEM_BARCODE = "ITEM_BARCODE";
	final String ITEM_UPLOADSTATUS = "ITEM_UPLOADSTATUS";
	final String ITME_SCANDATA = "ITME_SCANDATA";
	String rowSplit = "\r\n";
	private TextView tv_data_query_curPage;
	private TextView tv_data_query_totalPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString(DataQueryActivity.BUSINESSTYPE_NAME);
		setSqlWhere();
		setContentView(R.layout.activity_selector_result);
		setTitle(title);
		super.onCreate(savedInstanceState);
	}

	private void setSqlWhere() {

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		boolean bCurUserChecked = bundle.getBoolean(DataQueryActivity.CUR_USER_CHECKED);
		String starttime = bundle.getString(DataQueryActivity.STARTTIME);
		String endtime = bundle.getString(DataQueryActivity.ENDTIME);
		String barcode = bundle.getString(DataQueryActivity.BAROCDE);
		enumUploadType = EUploadType.valueOf(bundle.getString(DataQueryActivity.UploadType));

		String scanUser = "";
		if (bCurUserChecked) {
			scanUser = GlobalParas.getGlobalParas().getUserNo();
		}
		String uploadStatus = bundle.getString(DataQueryActivity.SYNCSTATUS);

		if (enumUploadType == EUploadType.msg) {
			serverStationService = new ServerStationService(SelectResultActivity.this);
			msgSendService = new MsgSendService(SelectResultActivity.this);
			sqlWhere = msgSendService.getSqlWhere(barcode, scanUser, starttime, endtime, uploadStatus);
		} else if (enumUploadType == EUploadType.order) {
			orderAbnormalService = new OrderAbnormalService(SelectResultActivity.this);
			orderOperService = new OrderOperService(SelectResultActivity.this);
			sqlWhere = orderOperService.getSqlWhere(barcode, scanUser, starttime, endtime, uploadStatus);
		} else if (enumUploadType == EUploadType.scanData) {
			scanDataService = new ScanDataService(SelectResultActivity.this);
			stationService = new StationService(SelectResultActivity.this);
			userService = new UserService(SelectResultActivity.this);
			destinationService = new DestinationService(SelectResultActivity.this);
			String scanCodeStr = intent.getStringExtra(DataQueryActivity.SCAN_TYPE);
			int siteProPerties = GlobalParas.getGlobalParas().getSiteProperties().value();
			String expressType = intent.getStringExtra(DataQueryActivity.EXPRESSTYPE);

			scanType = EScanType.getEnum(scanCodeStr, siteProperties);

			// String courier =
			// intent.getStringExtra(DataQueryActivity.COURIER);
			String courier = "";
			String nextStation = intent.getStringExtra(DataQueryActivity.NEXTSTATION);
			String route = intent.getStringExtra(DataQueryActivity.ROUTE);
			sqlWhere = scanDataService.getSqlwhere(scanType.value(), String.valueOf(siteProPerties), uploadStatus, scanUser, starttime, endtime, barcode, expressType, courier, nextStation, route);
			title = bundle.getString(DataQueryActivity.BUSINESSTYPE_NAME);

			if (title.equals("ҵ��Ա�ռ�")) {
				sqlWhere += " and courier =''";
			} else if (title.equals("ɨ��Ա�ռ�")) {
				sqlWhere += " and courier <>''";
			}
			siteProperties = ESiteProperties.valueOf(siteProPerties);

		}

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		control = new ControlUtil();

		btnUpload = bindButton(R.id.bottom_3_btnUpload);
		btnUpload.setText("�ط�");
		if (enumUploadType == EUploadType.msg) {
			scanType = EScanType.OTHER;// .valueOf(scanTypeCode);
		} else {
			// btnUpload.setEnabled(false);
		}
		btnDel = bindButton(R.id.bottom_3_btnDel);
		btnBack = bindButton(R.id.bottom_3_btnBack);
		btn_data_query_result_btnPre = bindButton(R.id.btn_data_query_result_btnPre);
		btn_data_query_result_btnNext = bindButton(R.id.btn_data_query_result_btnNext);
		tv_data_query_curPage = (TextView) findViewById(R.id.tv_data_query_curPage);
		tv_data_query_totalPage = (TextView) findViewById(R.id.tv_data_query_totalPage);

		etResult = bindEditText(R.id.et_select_result, null);

		setHeadTitle();

	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		scanDataService = new ScanDataService(SelectResultActivity.this);
		brocastUtil = new BrocastUtil(SelectResultActivity.this);
		msgSendService = new MsgSendService(SelectResultActivity.this);
		this.orderOperService = new OrderOperService(SelectResultActivity.this);
		initListView();
	}

	private void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);

		tvHead1.setText("�ϴ�״̬");
		tvHead2.setText("�˵���");

		control.setControlLayoutWidth(tvHead1, 130, SelectResultActivity.this);

	}

	private void initListView() {
		lvx = (ListViewEx) findViewById(R.id.lv_selres_lvBarcodeList);
		lvx.inital(R.layout.list_item_two_a, new String[] { ITEM_UPLOADSTATUS, ITEM_BARCODE }, new int[] { R.id.tv_list_item_two_a_tvhead1, R.id.tv_list_item_two_a_tvhead2 });

		loadListview();
		// lvx.setOnItemSelected(listener);
		// lvx.setOnItemClickListener(lstItemClick)
		lvx.setDelSelectedRowListener(delSelectRowRvent);
		lvx.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// TODO Auto-generated method stub
				getResult(position);
			}
		});
	}

	private IItemSelected listener = new IItemSelected() {

		@Override
		public void onItemSelected(int position) {

			getResult(position);
		}
	};

	private void getResult(int position) {

		Map<String, Object> map = lvx.GetValue(position);

		String scanCode = "";
		String flag = "";
		if (map.containsKey("scanCode")) {
			scanCode = map.get("scanCode").toString().trim();
		}
		StationXmlService stationservice = new StationXmlService(SelectResultActivity.this);
		UserXmlService userservice = new UserXmlService(SelectResultActivity.this);
		ServerStationService serverStationService = new ServerStationService(SelectResultActivity.this);
		DestinationService destinationService = new DestinationService(SelectResultActivity.this);
		StringBuilder sb = new StringBuilder();

		if (enumUploadType == EUploadType.scanData) {
			ScanDataModel model = (ScanDataModel) map.get(ITME_SCANDATA);
			sb.append("ɨ  ��  ��:" + model.getScanUser());
			sb.append(rowSplit);
			if (scanType == EScanType.FC || scanType == EScanType.DC) {
				sb.append("��  ǩ  ��:" + model.getBarcode());
			} else {
				String expressType = "";
				if (model.getExpressType().equals("1")) {
					expressType = "(24H)";
				}
				sb.append("��  ��  ��:" + model.getBarcode() + expressType);
			}
			sb.append(rowSplit);
			sb.append("ɨ��ʱ��:" + model.getScanTime());
			sb.append(rowSplit);
			String uploadTime = "";
			if (model.getIssynchronization().equals("1")) {
				uploadTime = model.getLasttime();
			}
			sb.append("�ϴ�ʱ��:" + uploadTime);
			sb.append(rowSplit);
			if (!StrUtil.isNullOrEmpty(model.getSigner())) {
				sb.append("ǩ  ��  ��:" + model.getSigner());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getCourier())) {
				User userModel = userservice.getUserByNo(model.getCourier());
				if (userModel != null) {
					String titleName = "��  ��  ��:";
					if (scanType == EScanType.PJ) {
						titleName = "��  ��  ��:";
					}
					sb.append(titleName + userModel.getUserName());
					sb.append(rowSplit);
				}
			}
			if (!StrUtil.isNullOrEmpty(model.getWeight())) {
				sb.append(" ��      ��:" + model.getWeight());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getSenderPhone())) {
				sb.append("�ķ��绰:" + model.getSenderPhone());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getRecipientPhone())) {
				sb.append("�շ��绰:" + model.getRecipientPhone());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getDestinationSiteCode())) {
				DestinationStation desModel = destinationService.getDestination(model.getDestinationSiteCode());
				if (desModel != null) {
					sb.append("Ŀ  ��  ��:" + desModel.getProvince());
					sb.append(rowSplit);
				}
			}
			if (!StrUtil.isNullOrEmpty(model.getAbnormalCode())) {
				sb.append("�������:" + model.getAbnormalCode());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getAbnormalDesc())) {
				sb.append("����ԭ��:" + model.getAbnormalDesc());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getCarLotNumber())) {
				sb.append("��  ǩ  ��:" + model.getCarLotNumber());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getVehicleNumber())) {
				sb.append("��  ��  ID:" + model.getVehicleNumber());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getNextStationCode())) {
				Station stationModel = stationservice.getStationModel(model.getNextStationCode());
				if (stationModel != null) {
					sb.append("��  һ  վ:" + stationModel.getStationName());
					sb.append(rowSplit);
				}
			}
			if (!StrUtil.isNullOrEmpty(model.getRouteCode())) {
				sb.append("·  ��  ��:" + model.getRouteCode());
				sb.append(rowSplit);
			}
			if (!StrUtil.isNullOrEmpty(model.getPreStationCode())) {
				Station stationModel = stationservice.getStationModel(model.getPreStationCode());
				if (stationModel != null) {
					sb.append("��  һ  վ:" + stationModel.getStationName());
					sb.append(rowSplit);
				}
			}
			if (!StrUtil.isNullOrEmpty(model.getPackageCode())) {
				String titleName = " ��       ��:";
				if (scanType == EScanType.ZB) {
					titleName = " ��     ��:";
				}
				sb.append(titleName + model.getPackageCode());
				sb.append(rowSplit);
			}
		} else if (enumUploadType == EUploadType.msg) {
			// ---------------------------------------------------------------------------------------------------�����޸�
			MsgSend msgModel = (MsgSend) map.get(ITME_SCANDATA);
			sb.append("ɨ  ��  ��:" + msgModel.getScanUser());
			sb.append(rowSplit);
			sb.append("��  ��  ��:" + msgModel.getBarcode());
			sb.append(rowSplit);
			sb.append("ɨ��ʱ��:" + msgModel.getScanTime());
			sb.append(rowSplit);
			String uploadTime = "";
			if (msgModel.getIssynchronization().equals("1")) {
				uploadTime = msgModel.getLasttime();
			}
			sb.append("�ϴ�ʱ��:" + uploadTime);
			sb.append(rowSplit);
			sb.append("��  ��  ��:" + msgModel.getPhone());
			sb.append(rowSplit);
			ServerStation serverStationModel = serverStationService.getServerStation(msgModel.getServerNo());
			if (serverStationModel != null) {
				sb.append("��  ��  ��:" + serverStationModel.getServerNo());
			}
		} else if (enumUploadType == EUploadType.order) {
			OrderOperating orderOperModel = (OrderOperating) map.get(ITME_SCANDATA);
			sb.append("ɨ  ��  ��:" + orderOperModel.getUserNo());
			sb.append(rowSplit);
			String barcode = orderOperModel.getBarcode();
			if (!StrUtil.isNullOrEmpty(barcode)) {
				sb.append("��  ��  ��:" + barcode);
				sb.append(rowSplit);
			}
			sb.append("ɨ��ʱ��:").append(DateUtil.getStrDate(orderOperModel.getScantime(), "yyyy-MM-dd HH:mm:ss"));
			sb.append(rowSplit);
			String uploadTime = "";
			if (orderOperModel.getIssynchronization().equals("1")) {
				uploadTime = DateUtil.getStrDate(orderOperModel.getLasttime(), "yyyy-MM-dd HH:mm:ss");
			}
			sb.append("�ϴ�ʱ��:" + uploadTime);
			sb.append(rowSplit);
			sb.append("��  ��  ��:" + orderOperModel.getLogisticid());
			sb.append(rowSplit);
			sb.append("����״̬:" + (orderOperModel.getFlag().equalsIgnoreCase("0") ? "��ȡ" : "���"));
			sb.append(rowSplit);
			if (orderOperModel.getFlag().equals("1")) {
				OrderAbnormal orderAbnormalModel = orderAbnormalService.getOrderAbnormal(orderOperModel.getReasonCode());
				if (orderAbnormalModel != null) {
					sb.append("���ԭ��:" + orderAbnormalModel.getReasonDesc());
				}
			}
		}

		etResult.setText(sb.toString());
	}

	private void loadListview() {

		// if (dataQueryService == null) {
		// dataQueryService = new DataQueryService(SelectResultActivity.this);
		// }
		if (scanDataService == null) {
			scanDataService = new ScanDataService(SelectResultActivity.this);
		}
		if (msgSendService == null) {
			msgSendService = new MsgSendService(SelectResultActivity.this);
		}
		if (orderOperService == null) {
			orderOperService = new OrderOperService(SelectResultActivity.this);
		}

		if (enumUploadType == EUploadType.scanData) {

			totalCount = scanDataService.getCount(sqlWhere);
		} else if (enumUploadType == EUploadType.msg) {
			totalCount = msgSendService.getCount(sqlWhere);
		} else if (enumUploadType == EUploadType.order) {
			if (sqlWhere.contains("scanUser")) {
				sqlWhere = sqlWhere.replace("scanUser", "userNo");
			}
			totalCount = orderOperService.getCount(sqlWhere);
		}
		pageNum = totalCount / pageSize;
		if (totalCount % pageSize != 0) {
			pageNum++;
		}
		if (pageNum == 0) {
			pageIndex = 0;
		}
		final List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
		if (enumUploadType == EUploadType.scanData) {
			List<ScanDataModel> list = scanDataService.GetScanData(sqlWhere, pageSize, pageIndex);
			if (list != null && list.size() > 0) {
				for (ScanDataModel model : list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ITEM_BARCODE, model.getBarcode());
					map.put(ITEM_UPLOADSTATUS, model.getIssynchronization().equals("1") ? "���ϴ�" : "δ�ϴ�");
					map.put(ITME_SCANDATA, model);
					sourceList.add(map);
				}
			}
		} else if (enumUploadType == EUploadType.msg) {
			// ------------------------------------------------------------------------------------------------------------------------------------------
			// ����
			List<MsgSend> msgList = msgSendService.getMsgData(sqlWhere, pageSize, pageIndex);
			if (msgList != null && msgList.size() > 0) {
				for (MsgSend model : msgList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ITEM_BARCODE, model.getBarcode());
					map.put(ITEM_UPLOADSTATUS, model.getIssynchronization().equals("1") ? "���ϴ�" : "δ�ϴ�");
					map.put(ITME_SCANDATA, model);
					sourceList.add(map);
				}
			}
		} else if (enumUploadType == EUploadType.order) {
			// ����
			List<OrderOperating> orderOperList = orderOperService.getOrderOperData(sqlWhere, pageSize, pageIndex);
			if (orderOperList != null && orderOperList.size() > 0) {
				for (OrderOperating orderOperating : orderOperList) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (StrUtil.isNullOrEmpty(orderOperating.getBarcode())) {
						map.put(ITEM_BARCODE, orderOperating.getLogisticid());
					} else {
						map.put(ITEM_BARCODE, orderOperating.getBarcode());
					}
					map.put(ITEM_UPLOADSTATUS, orderOperating.getIssynchronization().equals("1") ? "���ϴ�" : "δ�ϴ�");
					map.put(ITME_SCANDATA, orderOperating);
					sourceList.add(map);
				}
			}
		}

		lvx.add(sourceList);
		if (lvx.getSize() > 0) {
			lvx.setSelection(0);

		}

		setBtnPageStatus();
		tv_data_query_curPage.setText("��" + String.valueOf(pageIndex) + "ҳ");
		tv_data_query_totalPage.setText("��" + String.valueOf(pageNum) + "ҳ");
	}

	private int delId = 0;

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_3_btnBack:
			back();
			break;
		case R.id.bottom_3_btnDel:
			delData();
			break;
		case R.id.bottom_3_btnUpload:
			startUploadData();
			break;
		case R.id.btn_data_query_result_btnPre:
			doShowPreData();
			break;
		case R.id.btn_data_query_result_btnNext:
			doShowNextData();
			break;
		}
	}

	private void delData() {

		if (this.lvx.getSize() == 0) {
			PromptUtils.getInstance().showToast("û����Ҫɾ�������ݣ�", SelectResultActivity.this);
			return;
		}
		if (GlobalParas.getGlobalParas().getUserRole() == null || GlobalParas.getGlobalParas().getUserRole() == EUserRole.other) {// ����Ա����ɾ�����ϴ�����

			// btnUpload.setEnabled(false);
			deleteListData();
		} else {
			// ����ɾ��δ�ϴ�����
			Map<String, Object> map = lvx.GetSelValue();
			if (map == null) {
				PromptUtils.getInstance().showAlertDialog(SelectResultActivity.this, "��ѡ��һ��Ҫɾ�������ݣ�", null);
				return;
			}
			if (map.get(ITEM_UPLOADSTATUS).toString().equalsIgnoreCase("���ϴ�")) {
				PromptUtils.getInstance().showAlertDialog(SelectResultActivity.this, "���ϴ������ݲ���ɾ����", null);
				return;
			}
			if (enumUploadType == EUploadType.scanData) {
				delId = ((ScanDataModel) map.get(ITME_SCANDATA)).getId();
			} else if (enumUploadType == EUploadType.msg) {
				delId = ((MsgSend) map.get(ITME_SCANDATA)).getId();
			} else if (enumUploadType == EUploadType.order) {
				delId = ((OrderOperating) map.get(ITME_SCANDATA)).getId();
			}
			lvx.deleteSelectedRow(ITEM_BARCODE);
		}

	}

	private IListenDelSelRowSuc delSelectRowRvent = new IListenDelSelRowSuc() {

		@Override
		public void delSuc(String columnName, String value) {

			int t = 0;
			if (enumUploadType == EUploadType.scanData) {
				t = scanDataService.delSingleData(delId);
			} else if (enumUploadType == EUploadType.msg) {
				t = msgSendService.delRecord(delId);
			} else if (enumUploadType == EUploadType.order) {
				t = orderOperService.delRecord(delId);
			}
			if (t > 0) {
				PromptUtils.getInstance().showToast("ɾ���ɹ���", SelectResultActivity.this);

				etResult.setText("");
				brocastUtil.sendUnUploadCountChange(-1, enumUploadType);

				// query();
				loadListview();

			}
		}
	};

	private void setBtnPageStatus() {

		if (this.pageNum <= 1) {
			this.btn_data_query_result_btnNext.setEnabled(false);
			this.btn_data_query_result_btnPre.setEnabled(false);
		} else if (pageIndex >= pageNum - 1) {
			this.btn_data_query_result_btnNext.setEnabled(false);
			this.btn_data_query_result_btnPre.setEnabled(true);

		} else if (pageIndex <= 0) {
			this.btn_data_query_result_btnNext.setEnabled(true);
			this.btn_data_query_result_btnPre.setEnabled(false);
		} else {
			this.btn_data_query_result_btnNext.setEnabled(true);
			this.btn_data_query_result_btnPre.setEnabled(true);
		}

	}

	private void doShowPreData() {
		pageIndex--;
		if (pageIndex <= 0) {
			pageIndex = 0;
		}

		this.loadListview();
	}

	private void doShowNextData() {

		pageIndex++;
		if (pageIndex >= pageNum - 1) {
			pageIndex = pageNum - 1;
		}

		this.loadListview();
	}

	int delUnupload = 0;
	int delMsgUnupload = 0;

	private void deleteListData() {

		PromptUtils.getInstance().showAlertDialog(SelectResultActivity.this, "�Ƿ�ɾ���б�����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				delUnupload = 0;
				for (int i = 0; i < lvx.getSize(); i++) {
					Map<String, Object> map = lvx.GetValue(i);
					String barcode = map.get(ITEM_BARCODE).toString();
					int iTemp = deleteData(true, barcode);
					String issynchronization = map.get(ITEM_UPLOADSTATUS).toString();
					if (issynchronization.equals("δ�ϴ�")) {
						delUnupload += iTemp;
					}
				}
				lvx.clear();
				etResult.setText("");
			}
		}, null);
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// startUploadData();
	// }

	private void startUploadData() {

		this.scanDataService.updateStatus(sqlWhere, "0");

		new ReUploadThread().start();
		PromptUtils.getInstance().showToast("���ں�̨�ϴ�,���Ժ�", SelectResultActivity.this);

	}

	private int deleteData(boolean isManager, String barcodeParameter) {

		int t = 0;
		try {

			if (enumUploadType == EUploadType.msg) {
				// if (scanTypeCode.equals("FWDX")) {// ����ɾ��
				t = scanDataService.delMsgSingleData(barcodeParameter, isManager);
				if (t > 0) {
					// ɾ���ɹ�
					// GlobalParas.getGlobalParas().setMsgUnUploadCount(-1);
					brocastUtil.sendUnUploadCountChange(-t, EUploadType.msg);
					setUnuploadCount();
				}
			} else {
				t = scanDataService.delSingleData(barcodeParameter, scanType.value(), isManager);
				if (t > 0) {
					// ɾ���ɹ�
					// GlobalParas.getGlobalParas().setUnUploadCount(-1);
					brocastUtil.sendUnUploadCountChange(-t, EUploadType.scanData);
					// setUnuploadCount();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	private EUploadType reuploadType = EUploadType.scanData;

	// ���±�����δ�ϴ�������
	private void updateTitleUnUploadCount() {

		int count = 0;
		if (reuploadType == EUploadType.scanData) {

			count = scanDataService.getCount("", "", "0") - GlobalParas.getGlobalParas().getUnUploadCount();

		} else if (reuploadType == EUploadType.msg) {
			count = msgSendService.getCountByStatus("0") - GlobalParas.getGlobalParas().getMsgUnUploadCount();
		} else if (reuploadType == EUploadType.order) {
			count = orderOperService.getCountByStatus("0") - GlobalParas.getGlobalParas().getOrderUnUploadCount();
		}

		brocastUtil.sendUnUploadCountChange(count, reuploadType);
	}

	public class ReUploadThread extends Thread {

		@Override
		public void run() {

			super.run();

			List<Map<String, Object>> lstData = scanDataService.getList(sqlWhere);
			ESiteProperties siteProperties = GlobalParas.getGlobalParas().getSiteProperties();
			int t = lstData.size();// scanDataService.updateStatus(sqlWhere,
			// "0");
			if (t > 0) {
				updateTitleUnUploadCount();
			}
			for (Map<String, Object> map : lstData) {// �ش��Ļ���ֻ�ش�ɨ�������
				Message uploadMsg = UploadThread.getIntance(SelectResultActivity.this).uploadHandler.obtainMessage();
				Bundle bundle = new Bundle();
				EScanType scanType = EScanType.getEnum(map.get("scanCode").toString().trim(), siteProperties);
				bundle.putSerializable(UploadThread.uploadTypeKey, EUploadType.scanData);
				bundle.putSerializable(UploadThread.scanTypeKey, scanType);
				bundle.putSerializable(UploadThread.sitePropertiesKey, siteProperties);

				uploadMsg.setData(bundle);
				UploadThread.getIntance(SelectResultActivity.this).uploadHandler.sendMessage(uploadMsg);

			}

		}
	};

}
