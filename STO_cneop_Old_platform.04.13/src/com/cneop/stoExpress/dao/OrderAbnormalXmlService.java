package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.OrderAbnormal;

import android.content.Context;

public class OrderAbnormalXmlService extends BaseDao{

	public OrderAbnormalXmlService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_orderAbnormal";
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		return null;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		OrderAbnormal orderAbnormal = (OrderAbnormal) object;
		Object[] objs = {orderAbnormal.getCode(),orderAbnormal.getReasonDesc(),
				orderAbnormal.getState(),orderAbnormal.getLasttime()};
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "replace into tb_dic_orderAbnormal (code,reasonDesc,state,lasttime) values (?,?,?,?)";
		return sql;
	}

}
