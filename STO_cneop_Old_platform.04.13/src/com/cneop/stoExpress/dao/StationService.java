package com.cneop.stoExpress.dao;

import android.content.Context;
import com.cneop.stoExpress.model.Station;

public class StationService extends BaseDao {

	public StationService(Context context) {
		super(context);
		tableName = "tb_dic_station";
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		Station station = (Station) object;
		Object[] objs = { station.getStationId(), station.getStationName(), station.getAttribute(), station.getState(), station.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_station (stationId,stationName,attribute,state,lasttime) values(?,?,?,?,?)";
		return sql;
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select stationId,stationName,attribute,state,lasttime from tb_dic_station where state='A'";
		sql += strUtil.arrayToString(words);
		sql += " limit 100";
		return sql;
	}

	// ªÒµ√’æµ„
	public Station getStationModel(String stationId) {
		if (!strUtil.isNullOrEmpty(stationId)) {
			Station stationModel = new Station();
			String[] words = { " and stationId=?" };
			String[] selectArgs = { stationId };
			stationModel = (Station) getSingleObj(words, selectArgs, stationModel);
			return stationModel;
		}
		return null;
	}

}
