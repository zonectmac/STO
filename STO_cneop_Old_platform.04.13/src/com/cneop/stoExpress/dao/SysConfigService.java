package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;

import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.model.SysConfig;

import android.content.Context;

/*
 *系统配置类 
 */
public class SysConfigService extends BaseDao {

	public SysConfigService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_sysConfig";
	}

	@Override
	protected String getSearchSql(String[] words) {
		String sql = "select config_name,config_value,config_desc,lasttime from tb_sysConfig where 1=1  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		SysConfig sysConfig = (SysConfig) object;
		Object[] objs = { sysConfig.getConfig_name(), sysConfig.getConfig_value(), sysConfig.getConfig_desc() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_sysConfig (config_name,config_value,config_desc) values(?,?,?)";
		return sql;
	}

	// /*
	// * 更新记录
	// */
	// public int updateRecord(List<SysConfig> modelList){
	// String
	// sql="update tb_sysConfig set config_value=?,lasttime=datetime('now','localtime') where config_name=?";
	// List<Object[]> objList=new ArrayList<Object[]>();
	// for (SysConfig sysConfig : modelList) {
	// Object[] objArray={
	// sysConfig.getConfig_value(),
	// sysConfig.getConfig_name()
	// };
	// objList.add(objArray);
	// }
	// int t=dataBaseManager.executeSqlByTran(sql, objList);
	// return t;
	// }

	public int updateRecord(SysConfig sysConfig) {
		List<SysConfig> list = new ArrayList<SysConfig>();
		list.add(sysConfig);
		return addRecord(list);

	}

	public void UpdateLastMDTime(String endtime) {
		SysConfig model = new SysConfig();
		model.setConfig_desc("最后一次下载收件面单发放记录的时间");
		model.setConfig_name(ESysConfig.LastDownMDTime.toString());
		model.setConfig_value(endtime);
		model.setLasttime(new java.util.Date());
		updateRecord(model);

		GlobalParas.getGlobalParas().setLastDownMDTime(endtime);
	}

}
