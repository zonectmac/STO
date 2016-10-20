package com.cneop.stoExpress.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.AreaQueryService;
import com.cneop.stoExpress.dao.BaseDao;
import com.cneop.stoExpress.model.Abnormal;
import com.cneop.stoExpress.model.DestinationStation;
import com.cneop.stoExpress.model.NextStation;
import com.cneop.stoExpress.model.OrderAbnormal;
import com.cneop.stoExpress.model.Route;
import com.cneop.stoExpress.model.Station;
import com.cneop.stoExpress.model.User;
import com.cneop.stoExpress.util.ReflectorUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ObjectSerectorActivity extends CommonTitleActivity {

	Button btnOk;
	Button BtnBack;
	ListView lvList;
	SimpleAdapter simpleAdapter;
	BaseDao baseDao;
	List<Map<String, Object>> listSource;
	ProgressDialog pd;
	EditText etKeyword;
	Button btnSearch;
	public static String selectTypeKey = "selectorType";

	public static String selectStrKey = "selectorStr";
	public static String selectWheKey = "selectorWhr";

	public static int resultCode = 0x01;
	public static String res_key = "res_key";
	public static String res_key_1 = "res_key_1";
	public static String res_key_2 = "res_key_2";
	public static String res_key_3 = "res_key_3";
	public static String res_key_4 = "res_key_4";
	public static String res_key_5 = "res_key_5";

	private String column1 = "column1";
	private String column2 = "column2";
	private String column3 = "column3";
	private String column4 = "column4";

	private String colunmName = "";
	private String condition = "";

	EDownType selectType;
	boolean isFirst = true;// ���������ר�ã�true Ϊ��һҳ

	private int selItemIndex = -1; // ѡ��������

	TextView tvColumn1;
	TextView tvColumn2;
	TextView tvColumn3_1;
	TextView tvColumn3_2;
	TextView tvColumn3_3;

	private int lastClickId = -1; // ���һ�ε����ID
	private long lastClickTime = 0; // ���ʱ��
	private String typeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_object_serector);
		setTitle("ѡ����");
		super.onCreate(savedInstanceState);
		queryKeyword();
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		btnOk = bindButton(R.id.bottom_2_btnOk);
		BtnBack = bindButton(R.id.bottom_2_btnBack);
		etKeyword = bindEditText(R.id.et_selector_etKeyword, null, null);
		btnSearch = bindButton(R.id.btn_selector_btnQuery);
		lvList = (ListView) this.findViewById(R.id.lv_selector_lvList);
		lvList.setOnItemClickListener(listItemClick);
		lvList.setOnItemSelectedListener(selectEvent);
		listSource = new ArrayList<Map<String, Object>>();
		etKeyword.setOnKeyListener(editTextKeyDown);
	}

	@Override
	protected void initializeValues() {
		super.initializeValues();
		Intent intent = this.getIntent();
		selectType = (EDownType) intent.getSerializableExtra(selectTypeKey);// User
		if (selectType == EDownType.AreaQuery) {
			colunmName = intent.getStringExtra(selectStrKey);
			condition = intent.getStringExtra(selectWheKey);
		}
		// ��ʼ����ʾ
		initViewStub(selectType);
		baseDao = new ReflectorUtil(this).getDao(selectType);
		// ���ñ���
		setHeadTitle(selectType);
		lvList.setAdapter(simpleAdapter);
	}

	private OnKeyListener editTextKeyDown = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
				queryKeyword();
				isSearch = false;
				return true;
			}
			return false;
		}

	};

	/*
	 * ��ʼ����ʾ
	 */
	@SuppressWarnings("incomplete-switch")
	private void initViewStub(EDownType selectType) {

		switch (selectType) {
		case AreaQuery:
			ViewStub vsHead1 = (ViewStub) this.findViewById(R.id.vs_selector_vsHead1);
			vsHead1.inflate();
			tvColumn1 = (TextView) this.findViewById(R.id.tv_list_head_one_tvhead1);
			simpleAdapter = new SimpleAdapter(this, listSource, R.layout.lv_item_column_1, new String[] { column1 },
					new int[] { R.id.tv_lv_item_colum1_tvtip });
			break;
		case Abnormal:
		case Destination:
		case Station:
		case User:
		case NextStation:
		case OrderAbnormal:
			ViewStub vsHead2 = (ViewStub) this.findViewById(R.id.vs_selector_vsHead2);
			vsHead2.inflate();
			tvColumn1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
			tvColumn2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
			simpleAdapter = new SimpleAdapter(this, listSource, R.layout.lv_item_column_2,
					new String[] { column1, column2 },
					new int[] { R.id.tv_list_item_two_tvcolumn1, R.id.tv_list_item_two_tvcolumn2 });
			break;
		case Route:
			ViewStub vsHead3 = (ViewStub) this.findViewById(R.id.vs_selector_vsHead3);
			vsHead3.inflate();
			tvColumn3_1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
			tvColumn3_2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
			tvColumn3_3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
			simpleAdapter = new SimpleAdapter(this, listSource, R.layout.lv_item_column_3,
					new String[] { column1, column2, column3 }, new int[] { R.id.tv_list_item_three_tvhead1,
							R.id.tv_list_item_three_tvhead2, R.id.tv_list_item_three_tvhead3 });
			break;
		}
	}

	/*
	 * ���ñ���
	 */
	@SuppressWarnings("incomplete-switch")
	private void setHeadTitle(EDownType selType) {

		switch (selType) {
		case User:
			tvColumn1.setText("�û����");
			tvColumn2.setText("�û�����");
			break;
		case Station:
		case NextStation:
			tvColumn1.setText("վ����");
			tvColumn2.setText("վ������");
			break;
		case Abnormal:
			// �����
			tvColumn1.setText("������");
			tvColumn2.setText("���");
			break;
		case OrderAbnormal:
			tvColumn1.setText("���");
			tvColumn2.setText("ԭ������");
			break;
		case Destination:
			tvColumn1.setText("���");
			tvColumn2.setText("ʡ������");
			break;
		case Route:
			tvColumn3_1.setText("·�ɺ�");
			tvColumn3_2.setText("�¼�Ŀ�ĵ�");
			tvColumn3_3.setText("����Ŀ�ĵ�");
		case AreaQuery:
			if (colunmName.equals("province")) {
				tvColumn1.setText("ʡ��");
			}
			if (colunmName.equals("city")) {
				tvColumn1.setText("����");
			}
			if (colunmName.equals("area")) {
				tvColumn1.setText("����");
			}
		}
	}

	@Override
	protected void uiOnClick(View v) {

		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			selectValue();
			break;
		case R.id.bottom_2_btnBack:
			finish();
			break;
		case R.id.btn_selector_btnQuery:
			queryKeyword();
			isSearch = false;
			break;
		}
	}

	private String[] words;

	/*
	 * ��ѯ
	 */
	private void queryKeyword() {
		pd = ProgressDialog.show(ObjectSerectorActivity.this, "ѡ����", "���ڼ������ݣ����Ժ�");
		new Thread() {
			@Override
			public void run() {
				String keyword = etKeyword.getText().toString().trim();
				keyword = StrUtil.FilterSpecial(keyword);
				words = new String[1];
				StringBuilder sbSql = new StringBuilder();
				if (selectType == EDownType.User) {
					sbSql.append(" and ( userName like '%").append(keyword).append("%'");
					sbSql.append("  or  userNo like '%").append(keyword).append("%'");
					sbSql.append("  ) ");
					words[0] = sbSql.toString();
				} else if (selectType == EDownType.Station || selectType == EDownType.NextStation) {
					sbSql.append(" and ( stationName like '%").append(keyword).append("%'");
					sbSql.append("  or stationId like '%").append(keyword).append("%'");
					sbSql.append("  ) ");
					words[0] = sbSql.toString();
				} else if (selectType == EDownType.Route) {
					sbSql.append(" and ( routeId like '%").append(keyword).append("%'");
					sbSql.append("  or  routeDesc like '%").append(keyword).append("%'");
					sbSql.append("  or  nextStationName like '%").append(keyword).append("%'");
					sbSql.append("  or  nextStationId like '%").append(keyword).append("%'");
					sbSql.append("  ) ");
					words[0] = sbSql.toString();
				} else if (selectType == EDownType.Abnormal) {
					// �����
					sbSql.append(" and ( code like '%").append(keyword).append("%'");
					sbSql.append("  or  reasonDesc like '%").append(keyword).append("%'");
					sbSql.append("  ) ");
					words[0] = sbSql.toString();
					System.out.println("===================sbsql \t" + sbSql.toString());
				} else if (selectType == EDownType.Destination) {
					sbSql.append(" and ( provinceNo like '%").append(keyword).append("%'");
					sbSql.append("  or  province like '%").append(keyword).append("%'");
					sbSql.append("  ) ");
					words[0] = sbSql.toString();
				} else if (selectType == EDownType.AreaQuery) {
					if (colunmName.equals("province")) {
						sbSql.append(" and ( province like '%").append(keyword).append("%'");
						sbSql.append("  ) ");

					} else if (colunmName.equals("city")) {
						sbSql.append(" and ( province like '%").append(keyword).append("%'");
						sbSql.append("  or  city like '%").append(keyword).append("%'");
						sbSql.append("  ) ");
					} else {
						sbSql.append(" and ( province like '%").append(keyword).append("%'");
						sbSql.append("  or  city like '%").append(keyword).append("%'");
						sbSql.append("  or  area like '%").append(keyword).append("%'");
						sbSql.append("  ) ");
					}
					words[0] = sbSql.toString();
				} else {
					words = null;
				}

				updateListViewHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	Handler updateListViewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			updateListSource(words, null);
			updateListView();
		}
	};

	/**
	 * ˫��ѡ��
	 */
	private void selectValue() {

		if (selItemIndex != -1 && selItemIndex <= lvList.getCount() && lvList.getItemAtPosition(selItemIndex) != null

		) {
			Map<String, Object> map = (Map<String, Object>) lvList.getItemAtPosition(selItemIndex);
			if (selectType == EDownType.Abnormal && isFirst == true) {
				isFirst = false;
				tvColumn1.setText("���");
				tvColumn2.setText("���������");
				typeId = map.get(column1).toString().trim();
				queryKeyword();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(res_key, selectType);
			intent.putExtra(res_key_1, map.get(column1).toString().trim());
			intent.putExtra(res_key_2, map.get(column2).toString().trim());
			if (selectType == EDownType.AreaQuery) {
				intent.putExtra(res_key_3, colunmName);
			}
			if (selectType == EDownType.Route) {
				intent.putExtra(res_key_3, map.get(column3).toString().trim());
				intent.putExtra(res_key_4, map.get(column4).toString().trim());
				// intent.putExtra(res_key_5, map.get( ).toString().trim());

			}
			setResult(resultCode, intent);
			back();
		} else {
			PromptUtils.getInstance().showToast("��ѡ��һ����¼��", ObjectSerectorActivity.this);
		}
	}

	private OnItemSelectedListener selectEvent = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			selItemIndex = arg2;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private OnItemClickListener listItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			selItemIndex = position;

			// ˫���¼�
			if (lastClickId == position && (Math.abs(lastClickTime - System.currentTimeMillis())) < 1000) {
				selectValue();
			}
			lastClickId = position;
			lastClickTime = System.currentTimeMillis();
		}
	};

	/*
	 * ����Listview��ʾ
	 */
	private void updateListView() {

		if (listSource.size() > 0) {
			lvList.setSelection(0);
		}
		simpleAdapter.notifyDataSetChanged();
		pd.dismiss();
	}

	ProgressDialog progressDialog;
	/*
	 * ����listview����Դ
	 */
	List<Map<String, Object>> listMap;
	boolean isSearch = true;// ������������߰�ent���ͱ�Ϊfalse

	private void updateListSource(String[] words, String[] selectArgs) {
		if (baseDao != null) {
			List<Object> listObj = null;
			listMap = null;
			listSource.clear();
			switch (selectType) {
			case User:
				User userModel = new User();
				listObj = baseDao.getListObj(words, selectArgs, userModel);
				if (listObj != null) {
					for (Object object : listObj) {
						userModel = (User) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, userModel.getUserNo());// ���
						map.put(column2, userModel.getUserName());// ����
						listSource.add(map);
					}
				}

				break;
			case Station:
				Station statonModel = new Station();
				listObj = baseDao.getListObj(words, selectArgs, statonModel);
				if (listObj != null) {
					for (Object object : listObj) {
						statonModel = (Station) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, statonModel.getStationId());
						map.put(column2, statonModel.getStationName());
						listSource.add(map);
					}
				}
				break;
			case NextStation:
				NextStation nextStationModel = new NextStation();
				listObj = baseDao.getListObj(words, selectArgs, nextStationModel);
				if (listObj != null) {
					for (Object object : listObj) {
						nextStationModel = (NextStation) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, nextStationModel.getStationId());
						map.put(column2, nextStationModel.getStationName());
						listSource.add(map);
					}
				}
				break;
			case Abnormal:
				Abnormal abnormalModel = new Abnormal();
				if (isSearch) {
					if (isFirst) {
						words = new String[1];
						selectArgs = new String[1];
						words[0] = " and (attribute='ȫ��' or attribute=?) group by typeId order by typeId";
						selectArgs[0] = getArrtibute();
					} else {
						words = new String[1];
						selectArgs = new String[2];
						words[0] = " and (attribute='ȫ��' or attribute=?) and typeid=?";
						selectArgs[0] = getArrtibute();
						selectArgs[1] = typeId;
					}
				}
				listObj = baseDao.getListObj(words, selectArgs, abnormalModel);
				if (listObj != null) {
					for (Object object : listObj) {
						abnormalModel = (Abnormal) object;
						Map<String, Object> map = new HashMap<String, Object>();
						if (isSearch) {
							if (isFirst) {
								map.put(column1, abnormalModel.getTypeId());
								map.put(column2, abnormalModel.getTypeName());
							} else {
								map.put(column1, abnormalModel.getCode());
								map.put(column2, abnormalModel.getReasonDesc());
							}
						} else {
							map.put(column1, abnormalModel.getCode());
							map.put(column2, abnormalModel.getReasonDesc());
						}
						listSource.add(map);
					}
				}
				break;
			case Route:
				Route routeModel = new Route();
				listObj = baseDao.getListObj(words, selectArgs, routeModel);
				if (listObj != null) {
					for (Object object : listObj) {
						routeModel = (Route) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, routeModel.getRouteId());
						map.put(column2, routeModel.getNextStationName());
						map.put(column3, routeModel.getSecondStationName());
						map.put(column4, routeModel.getNextStationId());
						listSource.add(map);
					}
				}
				break;
			case Destination:
				DestinationStation destinationModel = new DestinationStation();
				listObj = baseDao.getListObj(words, selectArgs, destinationModel);
				if (listObj != null) {
					for (Object object : listObj) {
						destinationModel = (DestinationStation) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, destinationModel.getProvinceNo());
						map.put(column2, destinationModel.getProvince());
						listSource.add(map);
					}
				}
				break;
			case OrderAbnormal:
				OrderAbnormal orderAbnormalModel = new OrderAbnormal();
				listObj = baseDao.getListObj(words, selectArgs, orderAbnormalModel);
				if (listObj != null) {
					for (Object object : listObj) {
						orderAbnormalModel = (OrderAbnormal) object;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(column1, orderAbnormalModel.getCode());
						map.put(column2, orderAbnormalModel.getReasonDesc());
						listSource.add(map);
					}
				}
				break;
			case AreaQuery:
				progressDialog = ProgressDialog.show(this, "Loading...", "���ڼ�������", true, false);
				if (!((AreaQueryService) baseDao).isExist()) {
					try {
						InputStream in = getResources().getAssets().open("area.txt");
						((AreaQueryService) baseDao).addDataFromTxt(in);

						getdate();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				getdate();
			default:
				break;
			}

		}
		selItemIndex = -1;
	}

	public void getdate() {
		String sqlCondition = condition + words[0];
		listMap = ((AreaQueryService) baseDao).getProvince(colunmName, sqlCondition);
		for (Map<String, Object> map : listMap) {
			map.put(column1, map.get(colunmName).toString().trim());
			map.put(column2, map.get(colunmName).toString().trim());
			listSource.add(map);
		}
		progressDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		selItemIndex = -1;
		super.onDestroy();
	}

	private String getArrtibute() {

		String attribute = "";
		switch (GlobalParas.getGlobalParas().getUserRole()) {
		case business:
		case scaner:
			attribute = "����";
			break;
		default:
			attribute = "����";
			break;
		}
		return attribute;
	}

}
