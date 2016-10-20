package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.NextStation;

import android.content.Context;

/**
 * 下一站 基础数据
 * 
 * @author Administrator
 * 
 */
public class NextStationService extends BaseDao {

	public NextStationService(Context context) {
		super(context);
		tableName = "tb_dic_nextStation";
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select stationId,stationName,state,lasttime from tb_dic_nextStation where state='A' ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		NextStation model = (NextStation) object;
		Object[] objs = { model.getStationId(), model.getStationName(), model.getState(), model.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_nextStation (stationId,stationName,state,lasttime) values(?,?,?,?)";
		return sql;
	}

	public NextStation getNextStation(String stationId) {
		if (!strUtil.isNullOrEmpty(stationId)) {
			String[] words = { " and stationId=?" };
			String[] selectArgs = { stationId };
			NextStation nextStationModel = new NextStation();
			nextStationModel = (NextStation) getSingleObj(words, selectArgs, nextStationModel);
			return nextStationModel;
		}
		return null;
	}

	public void updateStaionName() {
		String sql = "update tb_dic_nextStation set stationName =( select  tb_dic_station.[stationName] from tb_dic_station where tb_dic_station. stationId= tb_dic_nextStation.stationId limit 1 ) ";

		dataBaseManager.executeSql(sql);
	}

}
