package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.DestinationStation;

import android.content.Context;

public class DestinationService extends BaseDao {

	public DestinationService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_destinationStation";
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		String sql = "select provinceNo,province,alphabet,state,lasttime from tb_dic_destinationStation where state='A' ";
		sql += strUtil.arrayToString(words);
		sql += "  order by alphabet";
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		DestinationStation model = (DestinationStation) object;
		Object[] objs = { model.getProvinceNo(), model.getProvince(),
				model.getAlphabet(), model.getState(), model.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "replace into tb_dic_destinationStation (provinceNo,province,alphabet,state,lasttime) values(?,?,?,?,?)";
		return sql;
	}

	public DestinationStation getDestination(String input) {
		if (!strUtil.isNullOrEmpty(input)) {
			String[] words = { " and (provinceNo like '" + input
					+ "%' or alphabet like '" + input + "%')" };
			DestinationStation model = new DestinationStation();
			model = (DestinationStation) getSingleObj(words, null, model);
			return model;
		}
		return null;
	}

}
