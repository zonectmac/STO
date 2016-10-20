package com.cneop.stoExpress.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.dao.AreaQueryService;
import com.cneop.util.activity.CommonTitleActivity;

public class AreaNoDelvieryAcitvity extends CommonTitleActivity{

	TextView tvarea;
	EditText etarea;
	Button btnBack;

	String province, city, area,scopeOfNoDelivery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 
		setContentView(R.layout.activity_area_nodelivery);
		setTitle("不派送范围");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {
	 
		super.initializeComponent();

		Intent intent = this.getIntent();
		province = (String) intent.getSerializableExtra("province");
		city = (String) intent.getSerializableExtra("city");
		area = (String) intent.getSerializableExtra("area");

		tvarea = (TextView) findViewById(R.id.tv_nodelivery);
		tvarea.setText(province+">"+city+">"+area);

		btnBack = bindButton(R.id.bottom_nodeliver_btnBack);

		etarea =bindEditText(R.id.et_nodelivery_area,null);
		etarea.setText(getAreas());
	}

	@Override
	protected void uiOnClick(View v) {
	 
		super.uiOnClick(v);
		switch(v.getId()){
		case R.id.bottom_nodeliver_btnBack:
			back();
			break;
		}
	}

	private String getAreas(){
		AreaQueryService areaDao = new AreaQueryService(this);
		String condition = "where province = "+"'"+province+"'"+"and city = "+"'"+city+"'"+"and area = "+"'"+area+"'";
		List<Map<String, Object>> listsouce = areaDao.getProvince("scopeOfNoDelivery", condition);
		if(listsouce.size()==0){
			return "该地区全境派送";
		}
		
		if(listsouce.get(0).get("scopeOfNoDelivery")!=null){
			String areas = listsouce.get(0).get("scopeOfNoDelivery").toString().trim();
			return areas;
		}
		return "该地区全境派送";
	}
}
