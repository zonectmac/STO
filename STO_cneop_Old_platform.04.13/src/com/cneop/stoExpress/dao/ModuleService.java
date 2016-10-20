package com.cneop.stoExpress.dao;

import java.util.List;

import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.model.ModuleModle;

import android.content.Context;

/*
 * π¶ƒ‹ƒ£øÈ≈‰÷√
 */
public class ModuleService extends BaseDao {

	public ModuleService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_dic_module";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cneop.stoExpress.dao.BaseDao#getSearchSql(java.lang.String[])
	 * and parentid=? and (roleid=? or roleid=0)
	 */
	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select id,moduleName,parentId,roleId,level,packageName,imgName,initValue,state,remark,(select count(Id) from tb_dic_module where parentid=r.Id) as childrenCount from tb_dic_module as r where 1=1 ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		ModuleModle modle = (ModuleModle) object;
		Object[] objs = { modle.getId(), modle.getModuleName(), modle.getParentId(), modle.getRoleId(), modle.getLevel(), modle.getPackageName(), modle.getImgName(), modle.getInitValue(), modle.getState(), modle.getRemark() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_dic_module (id,moduleName,parentId,roleId,level,packageName,imgName,initValue,state,remark) values(?,?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	public List<Object> getModuleList(int parentId, EProgramRole programRole) {
		ModuleModle moduleModle = new ModuleModle();
		String[] words = { "  and parentId=?", " and (roleId=? or roleId=0)" };
		String[] selectArgs = { String.valueOf(parentId), String.valueOf(programRole.value()) };
		return getListObj(words, selectArgs, moduleModle);
	}

	public List<Object> getModuleList(String parentId, String parentId2, String roleId) {
		ModuleModle moduleModle = new ModuleModle();
		String[] words = { "  and (parentId=? or parentId=?) ", " and (roleId=? or roleId=0) and state='A' order by roleid desc,Id" };
		String[] selectArgs = { parentId, parentId2, roleId };
		return getListObj(words, selectArgs, moduleModle);
	}

}
