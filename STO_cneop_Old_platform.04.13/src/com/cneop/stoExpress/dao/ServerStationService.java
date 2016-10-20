package com.cneop.stoExpress.dao;

import java.util.List;

import com.cneop.stoExpress.model.ServerStation;
import com.cneop.stoExpress.model.ServerStationModel;

import android.content.Context;

public class ServerStationService extends BaseDao {

	public ServerStationService(Context context) {
		super(context);
		tableName = "tb_dic_serverStation";
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select serverNo, stationName, serverName, serverAddress ,serverPhone from tb_dic_serverStation where 1=1 ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		ServerStationModel serverStationModel = (ServerStationModel) object;
		Object[] objs = { serverStationModel.getSitecode(), serverStationModel.getSite(), serverStationModel.getServersite(), serverStationModel.getAddress(), serverStationModel.getPhone() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_serverStation (serverNo,stationName,serverName,serverAddress,serverPhone) values(?,?,?,?,?)";
		return sql;
	}

	/**
	 * 查找服务代码
	 * 
	 * @param serverCode
	 * @return
	 */
	public ServerStation getServerStation(String serverCode) {
		String[] words = { "  and serverNo=?" };
		String[] selectArgs = { serverCode };
		List<Object> objList = getListObj(words, selectArgs, new ServerStation());
		if (objList != null && objList.size() > 0) {
			return (ServerStation) objList.get(0);
		}
		return null;
	}

}
