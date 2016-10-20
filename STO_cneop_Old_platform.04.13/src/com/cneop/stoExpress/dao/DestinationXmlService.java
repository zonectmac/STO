package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.DestinationStation;
import com.cneop.stoExpress.model.Station;

import android.content.Context;

public class DestinationXmlService extends BaseDao{

	public DestinationXmlService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_destinationStation";
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		String sql = "select provinceNo,province,state,lasttime from tb_dic_destinationStation where state='A' ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		DestinationStation destination = (DestinationStation) object;
		Object[] objs = {destination.getProvinceNo(),destination.getProvince(),
				destination.getState(),destination.getLasttime()};
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "replace into tb_dic_destinationStation (provinceNo,province,state,lasttime) values (?,?,?,?)";
		return sql;
	}

	// ªÒµ√’æµ„
	public DestinationStation getStationModel(String provinceNo) {
		if (!strUtil.isNullOrEmpty(provinceNo)) {
			DestinationStation destinationModel = new DestinationStation();
			String[] words = { " and provinceNo=?" };
			String[] selectArgs = { provinceNo };
			destinationModel = (DestinationStation) getSingleObj(words, selectArgs,
					destinationModel);
			return destinationModel;
		}
		return null;
	}

}
