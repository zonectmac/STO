package com.cneop.stoExpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

public class AreaQueryActivity extends CommonTitleActivity {

	private EditText etprovince, etcity, etarea;
	private Button btnProvinceCho, btnCityCho, btnAreaCho;
	private Button btnSel, btnBack;

	private String province, city, area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_area_query);
		setTitle("区域查询");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		btnProvinceCho = bindButton(R.id.btn_province_btnCho);
		etprovince = bindEditText(R.id.et_province, null);
		btnCityCho = bindButton(R.id.btn_city_btnCho);
		etcity = bindEditText(R.id.et_city, null);

		btnAreaCho = bindButton(R.id.btn_area_btnCho);
		etarea = bindEditText(R.id.et_area, null);

		btnSel = bindButton(R.id.bottom_2_btnOk);
		btnSel.setText("查询");
		btnBack = bindButton(R.id.bottom_2_btnBack);
		etprovince.clearFocus();
		etcity.clearFocus();
		etarea.clearFocus();
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_province_btnCho:
			etcity.setText("");
			etarea.setText("");
			city = "";
			area = "";
			openSlecotr(EDownType.AreaQuery, "province", "where 1=1");
			break;
		case R.id.btn_city_btnCho:
			if (province == null || province.equals("")) {
				PromptUtils.getInstance().showToast("请选择省份", AreaQueryActivity.this);
				return;
			}
			etarea.setText("");
			// openSlecotr(ESelectPageItem.City);
			openSlecotr(EDownType.AreaQuery, "city", "where province = " + "'" + province + "'");
			break;
		case R.id.btn_area_btnCho:
			if (city == null || city.equals("")) {
				PromptUtils.getInstance().showToast("请选择城市", AreaQueryActivity.this);
				return;
			}
			// openSlecotr(ESelectPageItem.Area);
			openSlecotr(EDownType.AreaQuery, "area", "where province = " + "'" + province + "'" + "and city = " + "'" + city + "'");
			break;
		case R.id.bottom_2_btnOk:
			doSearchClick();
			break;
		case R.id.bottom_2_btnBack:
			finish();
			// doKeyCode_Back();
			break;
		}
	}

	private void doSearchClick() {
		if (StrUtil.isNullOrEmpty(area)) {
			PromptUtils.getInstance().showToast("请选择地区", AreaQueryActivity.this);
			return;
		}
		openAreaNoDelivery();
	}

	protected void openAreaNoDelivery() {
		Intent intent = new Intent(AreaQueryActivity.this, AreaNoDelvieryAcitvity.class);
		intent.putExtra("province", province);
		intent.putExtra("city", city);
		intent.putExtra("area", area);
		startActivity(intent);
	}

	protected void openSlecotr(EDownType dowmType, String colunmName, String condition) {
		Intent intent = new Intent(this, ObjectSerectorActivity.class);
		intent.putExtra(ObjectSerectorActivity.selectTypeKey, dowmType);
		intent.putExtra(ObjectSerectorActivity.selectStrKey, colunmName);
		intent.putExtra(ObjectSerectorActivity.selectWheKey, condition);
		startActivityForResult(intent, ObjectSerectorActivity.resultCode);
	}

	@Override
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
		case AreaQuery:
			if (res_3.equals("province")) {
				etprovince.setText(res_2);
				province = res_2;
				// ControlUtil.setEditVeiwFocus(etcity);
			}
			if (res_3.equals("city")) {
				etcity.setText(res_2);
				city = res_2;

				// ControlUtil.setEditVeiwFocus(etarea);
			}
			if (res_3.equals("area")) {
				etarea.setText(res_2);
				area = res_2;
				doSearchClick();
			}
		default:
			break;
		}
	};
}
