package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.model.User;
import android.content.Context;

public class UserService extends BaseDao {

	public UserService(Context context) {
		super(context);
		tableName = "tb_dic_user";
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		User userModel = (User) object;
		Object[] objs = { userModel.getUserNo(), userModel.getPassword(),
				userModel.getUserName(), userModel.getStationId(),
				userModel.getUserType(), userModel.getState(),
				userModel.getLasttime() };
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "replace into tb_dic_user(userNo,password,userName,stationId,userType,state,lasttime) values(?,?,?,?,?,?,?)";
		return sql;
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		String sql = "select userNo,password,userName,stationId,userType,state,lasttime from tb_dic_user where state='A' ";
		sql += strUtil.arrayToString(words);
		sql += " limit 100";
		return sql;
	}

	public User getUserByNo(String userNo) {
		String[] words = { " and userNo=?" };
		String[] selectArgs = { userNo };
		User userModel = new User();
		userModel = (User) getSingleObj(words, selectArgs, userModel);
		return userModel;
	}

	public int updatePwd(String userNo, String pwd) {
		int t = 0;
		String sql = "update tb_dic_user set password=? where userNo=?";
		String[] bindArgs = { pwd, userNo };
		try {
			dataBaseManager.updateDataBySql(sql, bindArgs);
			t = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

}
