package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.Route;

import android.content.Context;

public class RouteXmlService extends
		BaseDao {

	public RouteXmlService(Context context) {
		super(context);
		tableName = "tb_dic_route";
	}

	@Override
	protected String getSearchSql(
			String[] words) {

		String sql = " select routeId,routeDesc,nextStationName,stationid as nextStationId,secondStationName,secondStatoinId,r.state,r.lasttime from tb_dic_route as r left join tb_dic_station as s on s.stationName=r.nextStationName where r.state='A' and s.state='A' ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(
			Object object) {

		Route route = (Route) object;
		Object[] objs = {
				route.getRouteId(),
				route.getRouteDesc(),
				route.getNextStationName(),
				route.getNextStationId(),
				route.getSecondStationName(),
				route.getSecondStatoinId(),
				route.getState(),
				route.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_route(routeId,routeDesc,nextStationName,nextStationId,secondStationName,secondStatoinId,state,lasttime) values(?,?,?,?,?,?,?,?)";
		return sql;
	}

	public Route getRoute(String routeNo) {
		if (!strUtil.isNullOrEmpty(routeNo)) {
			String[] words = { "  and routeId=?" };
			String[] selectArgs = { routeNo };
			Route routeModel = new Route();
			routeModel = (Route) getSingleObj(
					words, selectArgs, routeModel);
			return routeModel;
		}
		return null;
	}

}
