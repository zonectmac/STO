package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.NextStation;
import com.cneop.stoExpress.model.Station;

import android.content.Context;

public class NextStationXmlService extends BaseDao {

	public NextStationXmlService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_nextStation";
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select stationId,stationName,	state ,lasttime  from tb_dic_nextStation where state='A' ";
		sql += strUtil.arrayToString(words);

		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		NextStation nextStation = (NextStation) object;
		Object[] objs = { nextStation.getStationId(), nextStation.getStationName(), nextStation.getState(), nextStation.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "insert into tb_dic_nextStation (stationId,stationName,state,lasttime) values (?,?,?,?)";
		return sql;
	}

	public NextStation getStationModel(String stationNo) {
		if (!strUtil.isNullOrEmpty(stationNo)) {
			NextStation stationModel = new NextStation();
			String[] words = { "  and stationId=?" };
			String[] selectArgs = { stationNo };
			stationModel = (NextStation) getSingleObj(words, selectArgs, stationModel);
			return stationModel;
		}
		return null;
	}

}
