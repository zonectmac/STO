package com.cneop.stoExpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewStub;
import android.widget.*;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.dataValidate.RouteValidate;
import com.cneop.stoExpress.dataValidate.StationValidate;
import com.cneop.stoExpress.dataValidate.UserValidate;
import com.cneop.util.activity.CommonTitleActivity;

public class SelectorControlActivity extends CommonTitleActivity {

	ViewStub vsstation, vsupstatus, vstypestatus, vscouriser, vsroutnum;

	Button btnOk, btnBack, btnStationChoose, btnScanerChoose, btnRoutChoose;
	EditText etStation, etScaner, etRout;
	TextView tvStation, tvScaner;
	RadioButton rbup, rbunup, rbnormal, rbhourstype;

	StationValidate stationValidate;
	UserValidate userValidate;
	RouteValidate routeValidate;

	StringBuilder sb;
	String type;
	String scanTypeCode;
	String starttime, endtime;
	EScanType scanType;
	String barcode, uploadType;
	boolean bCurUserChecked;

	private boolean isInflate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_selector_control);
		setTitle("数据维护");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		// 下一站
		vsstation = (ViewStub) this.findViewById(R.id.vs_selectcontrol_nextstation);

		// 上传状态
		vsupstatus = (ViewStub) this.findViewById(R.id.vs_selectcontrol_statustype);
		// 业务类型
		vstypestatus = (ViewStub) findViewById(R.id.vs_selectcontrol_type);
		// 收件员
		vscouriser = (ViewStub) this.findViewById(R.id.vs_selectcontrol_couriser);
		// 路由号
		vsroutnum = (ViewStub) this.findViewById(R.id.vs_selectcontrol_routnum);
		//
		btnOk = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_next_station_btnSelStation:
			openSeleteor(EDownType.Station);
			break;
		case R.id.btn_barcode_btnAdd:
			openSeleteor(EDownType.User);
			break;
		case R.id.btn_pre_station_btnSelStation:
			openSeleteor(EDownType.Route);
			break;
		case R.id.bottom_2_btnBack:
			back();
			break;
		case R.id.bottom_2_btnOk:
			startSelectResult();
			break;
		}
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// startSelectResult();
	// }

	protected void openSeleteor(EDownType selectType) {

		Intent intent = new Intent(SelectorControlActivity.this, ObjectSerectorActivity.class);
		intent.putExtra(ObjectSerectorActivity.selectTypeKey, selectType);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void initializeValues() {

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		type = bundle.getString(DataQueryActivity.BUSINESSTYPE_NAME);
		scanTypeCode = bundle.getString(DataQueryActivity.SCAN_TYPE);
		starttime = bundle.getString(DataQueryActivity.STARTTIME);
		endtime = bundle.getString(DataQueryActivity.ENDTIME);
		barcode = bundle.getString(DataQueryActivity.BAROCDE);
		uploadType = bundle.getString(DataQueryActivity.UploadType);
		bCurUserChecked = bundle.getBoolean(DataQueryActivity.CUR_USER_CHECKED);

		if (!scanTypeCode.equals("FWDX")) {
			scanType = EScanType.valueOf(scanTypeCode);
		}
		upstatusInflate();

		if (type.equals("中心发件") || type.equals("网点发件") || type.equals("装袋发件") || type.equals("装袋") || type.equals("航空发车扫描") || type.equals("中心发车扫描") || type.equals("网点发车扫描")) {
			stationInflate();
			if (type.equals("网点发件") || type.equals("装袋发件") || type.equals("装袋")) {
				typestatusInflate();
			}
		} else if (type.equals("航空到件")) {
			stationInflate();
			tvStation.setText("上一站");
			typestatusInflate();
		} else if (type.equals("航空发件") || type.equals("航空装车发件") || type.equals("中心装车发件") || type.equals("网点装车发件")) {
			stationInflate();
			routInflate();
			// if (type.equals("航空发件")) {
			// stationInflate();
			// }
		} else if (type.equals("扫描员收件")) {
			scanerInflate();
			tvScaner.setText("收件员");
			typestatusInflate();
		} else if (type.equals("派件")) {
			scanerInflate();
			tvScaner.setText("派件员");
		} else if (type.equals("装包发件") || type.equals("装包") || type.equals("到包") || type.equals("发包")) {
			routInflate();
			stationInflate();
			if (type.equals("装包发件") || type.equals("装包") || type.equals("发包")) {
				typestatusInflate();
			}
		} else if (type.equals("网点到件") || type.equals("业务员收件")) {
			typestatusInflate();
		}
	}

	private void routInflate() {
		routeValidate = new RouteValidate(SelectorControlActivity.this, null, null);
		vsroutnum.inflate();
		TextView tvrount = (TextView) findViewById(R.id.tv_pre_station_tvPreStation);
		tvrount.setText("路由号");
		etRout = bindEditText(R.id.et_pre_station_etPreStation, null, null);
		btnRoutChoose = bindButton(R.id.btn_pre_station_btnSelStation);

	}

	private void scanerInflate() {
		userValidate = new UserValidate(SelectorControlActivity.this, GlobalParas.getGlobalParas().getStationId());
		vscouriser.inflate();
		tvScaner = (TextView) findViewById(R.id.tv_barcode_tvBarcode);
		etScaner = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnScanerChoose = bindButton(R.id.btn_barcode_btnAdd);

	}

	private void upstatusInflate() {
		vsupstatus.inflate();
		rbup = (RadioButton) findViewById(R.id.rdo_status_up);
		rbunup = (RadioButton) findViewById(R.id.rdo_status_unup);
	}

	private void typestatusInflate() {
		vstypestatus.inflate();
		rbnormal = (RadioButton) findViewById(R.id.rdo_status_normal);
		rbhourstype = (RadioButton) findViewById(R.id.rdo_status_hourstype);

	}

	private void stationInflate() {
		stationValidate = new StationValidate(SelectorControlActivity.this);

		vsstation.inflate();

		btnScanerChoose = bindButton(R.id.btn_next_station_btnSelStation);
		tvStation = (TextView) findViewById(R.id.tv_next_station_tvNextStation);
		etStation = bindEditText(R.id.et_next_station_etNextStation, null, null);

	}

	private void startSelectResult() {
		sb = new StringBuilder();
		Intent intent = null;
		String expressType = "";
		String nextSite = "";
		String rount = "";
		String courier = "";
		EUploadType eUploadType = EUploadType.getEnum(uploadType);
		if (eUploadType == EUploadType.pic) {
			intent = new Intent(SelectorControlActivity.this, PicQueryActivity.class);
		} else {
			intent = new Intent(SelectorControlActivity.this, SelectResultActivity.class);
		}

		if (etStation != null) {
			if (etStation.getText().toString().trim() != null && !("".equals(etStation.getText().toString().trim()))) {
				if (stationValidate.vlidateInputData(etStation)) {
					sb.append(" and nextStationCode = '" + etStation.getTag().toString().trim() + "' ");
					nextSite = etStation.getTag().toString().trim();
				}
			}
		}
		if (rbup != null) {
			if (rbup.isChecked()) {
				sb.append(" and issynchronization='1' ");
			}
			if (rbunup.isChecked()) {
				sb.append(" and issynchronization = '0' ");
			}
		}
		if (rbnormal != null) {
			if (rbnormal.isChecked()) {
				sb.append(" and expressType = '' ");
				expressType = "";
			}
			if (rbhourstype.isChecked()) {
				sb.append(" and expressType = '1' ");
				expressType = "1";
			}
		}
		if (etScaner != null) {
			if (etScaner.getText().toString().trim() != null && !("".equals(etScaner.getText().toString().trim()))) {
				if (userValidate.vlidateInputData(etScaner)) {
					sb.append(" and courier = '" + etScaner.getTag().toString().trim() + "' ");
				}
			}
		}
		if (etRout != null) {
			if (etRout.getText().toString().trim() != null && !("".equals(etRout.getText().toString().trim()))) {
				if (routeValidate.vlidateInputData(etRout)) {
					sb.append(" and routeCode = '" + etRout.getTag().toString().trim() + "' ");
					rount = etRout.getTag().toString().trim();
				}
			}
		}

		intent.putExtra("sql", sb.toString().trim());
		intent.putExtra(DataQueryActivity.EXPRESSTYPE, expressType);
		intent.putExtra(DataQueryActivity.NEXTSTATION, nextSite);
		intent.putExtra(DataQueryActivity.ROUTE, rount);

		intent.putExtra(DataQueryActivity.BUSINESSTYPE_NAME, type);
		intent.putExtra(DataQueryActivity.SCAN_TYPE, scanTypeCode);
		intent.putExtra(DataQueryActivity.STARTTIME, starttime);
		intent.putExtra(DataQueryActivity.ENDTIME, endtime);
		intent.putExtra(DataQueryActivity.BAROCDE, barcode);
		intent.putExtra(DataQueryActivity.UploadType, uploadType);
		intent.putExtra(DataQueryActivity.CUR_USER_CHECKED, bCurUserChecked);
		if (rbup.isChecked()) {
			intent.putExtra(DataQueryActivity.SYNCSTATUS, "1");
		} else if (rbunup.isChecked()) {
			intent.putExtra(DataQueryActivity.SYNCSTATUS, "0");
		}

		startActivity(intent);
		this.finish();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == ObjectSerectorActivity.resultCode) {
			EDownType selType = (EDownType) data.getSerializableExtra(ObjectSerectorActivity.res_key);
			String res_1 = data.getStringExtra(ObjectSerectorActivity.res_key_1);
			String res_2 = data.getStringExtra(ObjectSerectorActivity.res_key_2);
			String res_3 = data.getStringExtra(ObjectSerectorActivity.res_key_3);
			setSelResult(selType, res_1, res_2, res_3);
		}
	}

	protected void setSelResult(EDownType selType, String res_1, String res_2, String res_3) {
		switch (selType) {
		case Station:
			etStation.setText(res_2);
			etStation.setTag(res_1);
			break;
		case User:
			etScaner.setText(res_2);
			etScaner.setTag(res_1);
			break;
		case Route:
			etRout.setText(res_2);
			etRout.setTag(res_1);
			break;
		default:
			break;
		}
	};

	@Override
	public void editFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.et_next_station_etNextStation:
			if (hasFocus) {
				if (stationValidate != null) {
					stationValidate.restoreNo(etStation);
				}
			} else {
				stationValidate.showName(etStation);
			}
			break;
		case R.id.et_barcode_etBarcode:
			if (hasFocus) {
				if (userValidate != null) {
					userValidate.restoreNo(etScaner);
				}
			} else {
				userValidate.showName(etScaner);
			}
			break;
		case R.id.et_pre_station_etPreStation:
			if (hasFocus) {
				if (routeValidate != null) {
					routeValidate.restoreNo(etRout);
				}
			} else {
				routeValidate.showName(etRout);
			}
			break;
		}
	}
}
