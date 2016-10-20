package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.OrderAbnormal;
import android.content.Context;

public class OrderAbnormalService extends BaseDao {

	public OrderAbnormalService(Context context) {
		super(context);
		tableName = "tb_dic_orderAbnormal";
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		String sql = "select code,reasonDesc,state,lasttime from tb_dic_orderAbnormal where state='A' ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		OrderAbnormal model = (OrderAbnormal) object;
		Object[] objs = { model.getCode(), model.getReasonDesc(),
				model.getState(), model.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "replace into tb_dic_orderAbnormal (code,reasonDesc,state,lasttime) values(?,?,?,?)";
		return sql;
	}

	public OrderAbnormal getOrderAbnormal(String code) {
		if (!strUtil.isNullOrEmpty(code)) {
			String[] words = { " and code=?" };
			String[] selectArgs = { code };
			OrderAbnormal orderAbnormalModel = new OrderAbnormal();
			orderAbnormalModel = (OrderAbnormal) getSingleObj(words,
					selectArgs, orderAbnormalModel);
			return orderAbnormalModel;
		}
		return null;
	}

}
