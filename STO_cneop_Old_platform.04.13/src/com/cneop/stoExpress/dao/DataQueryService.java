package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.common.Enums.EDbSelectOrUpdate;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.model.ListModel;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.model.ScanDataModel;

import android.content.Context;

public class DataQueryService extends BaseDao {

	public DataQueryService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		tableName = "tb_scanData";
	}

	@Override
	protected String getSearchSql(String[] words) {

		return null;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		return null;
	}

	@Override
	protected String getInsertSql() {

		return null;
	}

	public List<Map<String, Object>> getResult(EDbSelectOrUpdate str, String sqlwhere, String starttime, String endtime, EScanType scanType) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String sql = "";
		if (str.equals(EDbSelectOrUpdate.select)) {
			// if (str.equals("select")) {
			sql += "select * from tb_scanData where 1=1 ";
		}
		if (str.equals(EDbSelectOrUpdate.update)) {
			// if (str.equals("update")) {
			sql += "update tb_scanData set issynchronization='0' where 1=1 ";
		}
		if (sqlwhere != null && !("".equals(sqlwhere))) {
			sql += sqlwhere;
		}
		if (scanType != null && !("".equals(scanType))) {
			String scanCode = scanType.value();
			sql += " and scanCode = '" + scanCode + "'";
		}
		sql += " and scanTime between '" + starttime + "' and '" + endtime + "'";
		try {
			if (str.equals(EDbSelectOrUpdate.update)) {
				// if (str.equals("update")) {
				dataBaseManager.updateDataBySql(sql, null);
			}
			if (str.equals(EDbSelectOrUpdate.select)) {
				// if (str.equals("select")) {
				dataList = dataBaseManager.queryData2Map(sql, null, new ScanDataModel());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return dataList;
	}

	public List<Map<String, Object>> getDXResult(String str, String sqlwhere, String starttime, String endtime) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String sql = "";
		if (str.equals("select")) {
			sql += "select * from tb_msgSend where 1=1 ";
		}
		if (str.equals("update")) {
			sql += "update tb_msgSend set issynchronization='0' where 1=1 ";
		}
		if (sqlwhere != null && !("".equals(sqlwhere))) {
			sql += sqlwhere;
		}
		sql += " and scanTime between '" + starttime + "' and '" + endtime + "'";

		try {
			if (str.equals("update")) {
				dataBaseManager.updateDataBySql(sql, null);
			}
			if (str.equals("select")) {
				dataList = dataBaseManager.queryData2Map(sql, null, new MsgSend());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	private String appendQuerySql(String moduleName, EScanType scanType, ESiteProperties siteProperties, String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("select '").append(moduleName).append("' as moduleName,'").append(scanType.toString()).append("' as scanType,");
		sbSql.append("  (select count(id) from tb_scanData where 1=1  ");
		sbSql.append("    and   scanCode='").append(scanType.value()).append("' ");
		sbSql.append("    and   siteProperties='").append(siteProperties.value()).append("'  ");
		sbSql.append(str);
		sbSql.append("  ) as amount, ");
		// 未发
		sbSql.append("  (select count(id) from tb_scanData where 1=1   and issynchronization=0  ");
		sbSql.append("    and   scanCode='").append(scanType.value()).append("' ");
		sbSql.append("    and   siteProperties='").append(siteProperties.value()).append("'  ");
		sbSql.append(str);
		sbSql.append("  ) as unupload, ");
		// 已发
		sbSql.append("  (select count(id) from tb_scanData where 1=1   and issynchronization=1  ");
		sbSql.append("    and   scanCode='").append(scanType.value()).append("' ");
		sbSql.append("    and   siteProperties='").append(siteProperties.value()).append("'  ");
		sbSql.append(str);
		sbSql.append("  ) as upload ");
		return sbSql.toString();
	}

	/**
	 * 业务员的sql
	 * 
	 * @return
	 */
	private String getBusinesserSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("业务员收件", EScanType.SJ, ESiteProperties.C_N, str + " and courier=''"));// 业务员收件时扫描员为空
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("签收", EScanType.QS, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("业务员问题件", EScanType.YN, ESiteProperties.C_N, str));
		return sbSql.toString();

		// String sql =
		// "select '业务员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '01' and siteProperties = '1' and courier='' "
		// + " union all"
		// +
		// " select '业务员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '0' and courier='' "
		// + " union all"
		// +
		// " select '业务员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '1' and courier='' "
		// + " union all"
		// +
		// " select '签收' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '99' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '签收' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '99' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '签收' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '99' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '业务员问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '业务员问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '业务员问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'";
		// //+ " union all"
		// // +
		// //
		// " select '订单' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str
		// // + " union all"
		// // +
		// //
		// " select '订单' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str +" and issynchronization = '0'"
		// // + " union all"
		// // +
		// //
		// " select '订单' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str +" and issynchronization = '1'"
		// // + " union all"
		// // +
		// " select '短信' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " union all"
		// // +
		// " select '短信' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " and issynchronization = '0'"
		// // + " union all"
		// // +
		// " select '短信' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " and issynchronization = '1'";
		// return sql;
	}

	/**
	 * 获取扫描员sql
	 * 
	 * @param str
	 * @return
	 */
	private String getScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("扫描员收件", EScanType.SJ, ESiteProperties.C_N, str + " and courier!=''"));// 扫描员收件时扫描员不为空
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点发件", EScanType.FJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点到件", EScanType.DJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("派件", EScanType.PJ, ESiteProperties.C_N, str));
		// sbSql.append( " union all ");
		// sbSql.append(appendQuerySql("派件",EScanType.PJ,ESiteProperties.C_N,str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点留仓", EScanType.LC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("装袋发件", EScanType.ZD, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点问题件", EScanType.YN, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点装车发件", EScanType.ZC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点发车扫描", EScanType.FC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("网点到车扫描", EScanType.DC, ESiteProperties.C_N, str));

		return sbSql.toString();

		// 扫描员收件不为空,其他收件时,扫描会为空的
		// String sql =
		// "select '扫描员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1   "
		// + str
		// + " and scanCode = '01' and siteProperties = '1'  and courier!=''  "
		// + " union all"
		// +
		// " select '扫描员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '0'  and courier!='' "
		// + " union all"
		// +
		// " select '扫描员收件' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '1'  and courier!='' "
		// + " union all"
		// +
		// " select '网点发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '03' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '03' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '派件' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '04' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '派件' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '04' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '派件' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '04' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '网点到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '网点到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '网点到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '1'"
		// // + " union all"
		// // +
		// //
		// " select '短信' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str +" and scanCode = '01' and siteProperties = '1'"
		// // + " union all"
		// // +
		// //
		// " select '短信' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str
		// //
		// +" and scanCode = '01' and siteProperties = '1' and issynchronization = '0'"
		// // + " union all"
		// // +
		// //
		// " select '短信' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str
		// //
		// +" and scanCode = '01' and siteProperties = '1' and issynchronization = '1'"
		// ;
		// return sql;
	}

	/**
	 * 获取中心扫描员sql
	 * 
	 * @param str
	 * @return
	 */
	private String getCenterScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("中心到件", EScanType.DJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心发件", EScanType.FJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心问题件", EScanType.YN, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("装袋发件", EScanType.ZD, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心留仓", EScanType.LC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心装车发件", EScanType.ZC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心发车扫描", EScanType.FC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("中心到车扫描", EScanType.DC, ESiteProperties.C_N, str));

		return sbSql.toString();
		// String sql =
		// "select '中心到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '装袋发件' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '中心到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '中心到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '中心到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '1'";
		// return sql;
	}

	/*
	 * 获取航空扫描员sql
	 */
	private String getAirScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("航空到件", EScanType.DJ, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空发件", EScanType.FJ, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空问题件", EScanType.YN, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空留仓", EScanType.LC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("装包发件", EScanType.ZB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("发包", EScanType.FB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("到包", EScanType.DB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空装车发件", EScanType.ZC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空发车扫描", EScanType.FC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("航空到车扫描", EScanType.DC, ESiteProperties.C_H, str));
		return sbSql.toString();

		// String sql =
		// "select '航空到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空到件' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空发件' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空问题件' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空留仓' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '装包发件' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '装包发件' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '装包发件' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '发包' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '33' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '发包' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '33' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '发包' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '33' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '到包' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '34' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '到包' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '到包' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空装车发件' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空发车扫描' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '航空到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '航空到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '航空到车扫描' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '2' and issynchronization = '1'";
		// return sql;
	}

	private String getAirGetGoodserSql(String str) {
		return appendQuerySql("到包扫描", EScanType.DB, ESiteProperties.C_H, str);
		//
		// String sql =
		// "select '到包扫描' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + "and scanCode = '34' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '到包扫描' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '到包扫描' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '34' and siteProperties = '2' and issynchronization = '1'";
		// return sql;
	}

	@SuppressWarnings("incomplete-switch")
	public List<Map<String, Object>> getSearch(String starttime, String endtime, EUserRole role, boolean isUser, String barcode) {
		List<Map<String, Object>> dataList = null;
		// String sql = "", str = "";
		StringBuilder sbWhere = new StringBuilder();
		StringBuilder sbSql = new StringBuilder();

		if (isUser && role != null) {
			sbWhere.append(" and scanUser = '").append(GlobalParas.getGlobalParas().getUserNo()).append("'");
		}
		if (barcode != null && !(barcode.equals(""))) {
			sbWhere.append(" and barcode = '").append(barcode).append("'");
		}
		// str+="  and siteProperties="+GlobalParas.getGlobalParas().getSiteProperties().value();
		sbWhere.append(" and scanTime between '").append(starttime);
		sbWhere.append("' and '").append(endtime).append("'");
		String strWhere = sbWhere.toString();

		if (role == EUserRole.other) {// 系统设置，管理员
			switch (GlobalParas.getGlobalParas().getSitePropertyForManagerSearch()) {
			case air: {
				// 航空扫描员
				//sbSql.append(" union all ").append(getAirScannerSql(strWhere));
				sbSql.append(getAirScannerSql(strWhere));
				// 航空提取员
				sbSql.append(" union all ").append(getAirGetGoodserSql(strWhere));
			}
				break;
			case center: {
				// 中心扫描员
				//sbSql.append(" union all ").append(getCenterScannerSql(strWhere));
				sbSql.append(getCenterScannerSql(strWhere));
			}
				break;
			case station: {
				// 网点业务员
				sbSql.append(getBusinesserSql(strWhere));
				// 网点扫描员
				sbSql.append(" union all ").append(getScannerSql(strWhere));
			}
				break;
			}

		} else {
			switch (role.value()) {
			case 1:// 网点业务员
				sbSql.append(getBusinesserSql(strWhere));
				if (!isUser) {// 则为查询本设备的数据
					sbSql.append(" union all ").append(getScannerSql(strWhere));
				}
				break;
			case 2:// 网点扫描员
				sbSql.append(getScannerSql(strWhere));
				if (!isUser) {// 则为查询本设备的数据
					sbSql.append(" union all ").append(getBusinesserSql(strWhere));
				}
				break;
			case 3:// 中心扫描员
				sbSql.append(getCenterScannerSql(strWhere));
				break;
			case 4:// 航空扫描员
				sbSql.append(getAirScannerSql(strWhere));
				if (!isUser) {// 则为查询本设备的数据
					sbSql.append(" union all ").append(getAirGetGoodserSql(strWhere));
				}
				break;
			case 5:// 航空提取员
				sbSql.append(getAirGetGoodserSql(strWhere));
				if (!isUser) {// 则为查询本设备的数据
					sbSql.append(" union all ").append(getAirScannerSql(strWhere));
				}
				break;
			}
		}
		try {
			ListModel lstModel = new ListModel();
			lstModel.setUploadType(EUploadType.scanData.toString());

			dataList = dataBaseManager.queryData2Map(sbSql.toString(), null, lstModel);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return dataList;
	}
}
