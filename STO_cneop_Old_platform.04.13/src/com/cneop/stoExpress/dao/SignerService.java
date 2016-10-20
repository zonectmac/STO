package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;

import com.cneop.stoExpress.model.Signer;

import android.content.Context;

/**
 * 签收名
 * 
 * @author Administrator
 * 
 */
public class SignerService extends BaseDao {

	public SignerService(Context context) {
		super(context);
		tableName = "tb_dic_signer";
	}

	@Override
	protected String getSearchSql(String[] words) {
	 
		String sql = "select id,signerName from tb_dic_signer where 1=1 ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {
	 
		Signer singerModel = (Signer) object;
		Object[] objs = { singerModel.getSignerName(), singerModel.getUserNo() };
		return objs;
	}

	@Override
	protected String getInsertSql() {
	 
		String sql = "insert into tb_dic_signer(signerName,userNo) values(?,?)";
		return sql;
	}

	/*
	 * 插入一条
	 */
	public int addRecord(Signer model) {
		int flag = 0;
		// 判断是否重复
		String[] words = { " and signerName=?" };
		String[] selectArgs = { model.getSignerName() };
		Object obj = getSingleObj(words, selectArgs, new Signer());
		if (obj != null) {
			// 重复
			flag = -1;
		} else {
			List<Signer> list = new ArrayList<Signer>();
			list.add(model);
			flag = addRecord(list);
		}
		return flag;
	}

	public int isExist(String signerName) {
		int flag = 0;
		String sql = "select count(id) from tb_dic_signer where signerName = ?";
		String[] selectionArgs = {signerName};
		String result = dataBaseManager.querySingleData(sql, selectionArgs);
		if(!strUtil.isNullOrEmpty(result)){
			flag = Integer.parseInt(result);
		}
		return flag;
	}

	public void insertSigner(List<Signer> listModel){
		String sql = "insert into tb_dic_signer (signerName,userNo) values(?,?)";
		List<Object[]> objList = new ArrayList<Object[]>();
		for(Signer signerModel : listModel){
			Object[] objs = {signerModel.getSignerName(),signerModel.getUserNo()};
			objList.add(objs);
		}
		dataBaseManager.executeSqlByTran(sql, objList);
	}
	public int deleteSigner(String signerName) {
		String[] whereargs = {signerName};
		return dataBaseManager.deleteData("tb_dic_signer", "signerName = ?" , whereargs);
	}

}
