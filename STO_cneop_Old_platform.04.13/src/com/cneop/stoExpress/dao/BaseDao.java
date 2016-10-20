package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.cneop.stoExpress.model.SJMDSendedRecordsVO;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.util.DBHelper;
import com.cneop.util.DataBaseManager;
import com.cneop.util.StrUtil;

public abstract class BaseDao {

	String tableName;
	DataBaseManager dataBaseManager;
	StrUtil strUtil;

	public BaseDao(Context context) {

		dataBaseManager = DataBaseManager.getInstance(context);
		strUtil = new StrUtil();
	}

	/**
	 * �����ʱ��
	 */
	public String getLastTime() {

		String sql = "select Max(lasttime) from " + tableName;
		String lasttime = dataBaseManager.querySingleData(sql, null);
		if (StrUtil.isNullOrEmpty(lasttime)) {
			lasttime = "2000-01-01 00:00:00";
		}
		return lasttime;
	}

	/**
	 * ��������ȡ�����ʱ��
	 * 
	 * @param condition
	 * @return
	 */
	public String getLastTime(String condition) {

		String sql = "select Max(lasttime) from " + tableName;
		if (!strUtil.isNullOrEmpty(condition)) {
			if (condition.startsWith("where")) {
				sql += " " + condition;
			} else {
				sql += " where " + condition;
			}
		}
		String lasttime = dataBaseManager.querySingleData(sql, null);
		if (strUtil.isNullOrEmpty(lasttime)) {
			lasttime = "2000-01-01 00:00:00";
		}
		return lasttime;
	}

	/*
	 * ������������, ��������
	 */
	public int addRecord(List<?> modelList) {
		int t = 0;
		String sql = getInsertSql();
		List<Object[]> objList = new ArrayList<Object[]>();
		for (Object object : modelList) {
			Object[] objs = getInsertParams(object);
			objList.add(objs);
		}
		t = dataBaseManager.executeSqlByTran(sql, objList);

		return t;
	}

	public List<Object> getListObj(String[] words, String[] selectArgs, Object obj) {

		List<Object> objList = null;
		String sql = getSearchSql(words);
		try {
			objList = dataBaseManager.queryData2Object(sql, selectArgs, obj);// size:13
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objList;
	}

	/*
	 * ��������
	 */
	public Object getSingleObj(String[] words, String[] selectArgs, Object obj) {

		Object newObj = null;
		List<Object> objList = getListObj(words, selectArgs, obj);
		if (objList != null && objList.size() > 0) {
			newObj = objList.get(0);
		}
		return newObj;
	}

	/*
	 * ɾ������
	 */
	public int delRecord(String whereClause, String[] whereArgs) {

		int t = dataBaseManager.deleteData(tableName, whereClause, whereArgs);
		return t;
	}

	protected abstract String getSearchSql(String[] words);

	protected abstract Object[] getInsertParams(Object object);

	protected abstract String getInsertSql();

}
