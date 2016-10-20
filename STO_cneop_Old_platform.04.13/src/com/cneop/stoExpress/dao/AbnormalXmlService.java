package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.Abnormal;
import android.content.Context;

public class AbnormalXmlService extends BaseDao {

	public AbnormalXmlService(Context context) {

		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_abnormal";
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = " select code,reasonDesc,typeId,typeName,attribute,state,lasttime from tb_dic_abnormal where state='A' ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		Abnormal abnormal = (Abnormal) object;
		Object[] objs = { abnormal.getCode(), abnormal.getReasonDesc(), abnormal.getTypeId(), abnormal.getTypeName(), abnormal.getAttribute(), abnormal.getState(), abnormal.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_abnormal (code,reasonDesc,typeId,typeName,attribute,state,lasttime) values(?,?,?,?,?,?,?)";
		return sql;
	}

	public Abnormal getAbnormal(String abnormalNo) {

		if (!strUtil.isNullOrEmpty(abnormalNo)) {
			String[] words = { " and code=?" };
			String[] selectArgs = { abnormalNo };
			Abnormal abnormalModel = new Abnormal();
			abnormalModel = (Abnormal) getSingleObj(words, selectArgs, abnormalModel);
			return abnormalModel;
		}
		return null;
	}

}
