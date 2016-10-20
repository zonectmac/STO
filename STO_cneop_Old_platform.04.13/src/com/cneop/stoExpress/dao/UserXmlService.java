package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.User;

import android.content.ContentValues;
import android.content.Context;

public class UserXmlService extends BaseDao {

	public UserXmlService(Context context) {
		super(context);
		tableName = "tb_dic_user";
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		User userModel = (User) object;
		Object[] objs = { userModel.getUserNo(), userModel.getPassword(), userModel.getUserName(), userModel.getStationId(), userModel.getUserType(), userModel.getState(), userModel.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_user(userNo,password,userName,stationId,userType,state,lasttime) values(?,?,?,?,?,?,?)";
		return sql;
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select userNo,password,userName,stationId,userType,state,lasttime from tb_dic_user where state='A'  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	public User getUserByNo(String userNo) {// 900000,0010
		String[] words = { " and userNo=?" };
		String[] selectArgs = { userNo };
		User userModel = new User();
		userModel = (User) getSingleObj(words, selectArgs, userModel);
		return userModel;
	}

	public int updatePwd(String newpwd, String userid) {
		int result = 0;
		ContentValues values = new ContentValues();
		values.put("Password", newpwd);
		String[] whereArgs = { userid };
		result = dataBaseManager.updataData(tableName, values, "userNo = ?", whereArgs);
		return result;
	}

	public void closeDB() {
		dataBaseManager.closeDB();
	}

}
