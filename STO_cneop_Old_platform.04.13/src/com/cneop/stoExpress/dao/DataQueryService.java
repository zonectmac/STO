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
		// δ��
		sbSql.append("  (select count(id) from tb_scanData where 1=1   and issynchronization=0  ");
		sbSql.append("    and   scanCode='").append(scanType.value()).append("' ");
		sbSql.append("    and   siteProperties='").append(siteProperties.value()).append("'  ");
		sbSql.append(str);
		sbSql.append("  ) as unupload, ");
		// �ѷ�
		sbSql.append("  (select count(id) from tb_scanData where 1=1   and issynchronization=1  ");
		sbSql.append("    and   scanCode='").append(scanType.value()).append("' ");
		sbSql.append("    and   siteProperties='").append(siteProperties.value()).append("'  ");
		sbSql.append(str);
		sbSql.append("  ) as upload ");
		return sbSql.toString();
	}

	/**
	 * ҵ��Ա��sql
	 * 
	 * @return
	 */
	private String getBusinesserSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("ҵ��Ա�ռ�", EScanType.SJ, ESiteProperties.C_N, str + " and courier=''"));// ҵ��Ա�ռ�ʱɨ��ԱΪ��
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("ǩ��", EScanType.QS, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("ҵ��Ա�����", EScanType.YN, ESiteProperties.C_N, str));
		return sbSql.toString();

		// String sql =
		// "select 'ҵ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '01' and siteProperties = '1' and courier='' "
		// + " union all"
		// +
		// " select 'ҵ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '0' and courier='' "
		// + " union all"
		// +
		// " select 'ҵ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '1' and courier='' "
		// + " union all"
		// +
		// " select 'ǩ��' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '99' and siteProperties = '1'"
		// + " union all"
		// +
		// " select 'ǩ��' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '99' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select 'ǩ��' as moduleName,'QS' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '99' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select 'ҵ��Ա�����' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select 'ҵ��Ա�����' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select 'ҵ��Ա�����' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'";
		// //+ " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str
		// // + " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str +" and issynchronization = '0'"
		// // + " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_order where 1=1"+
		// // str +" and issynchronization = '1'"
		// // + " union all"
		// // +
		// " select '����' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " union all"
		// // +
		// " select '����' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " and issynchronization = '0'"
		// // + " union all"
		// // +
		// " select '����' as moduleName,'FWDX' as scanType,count(*) as amount from tb_msgSend where 1=1"
		// // + " and issynchronization = '1'";
		// return sql;
	}

	/**
	 * ��ȡɨ��Աsql
	 * 
	 * @param str
	 * @return
	 */
	private String getScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("ɨ��Ա�ռ�", EScanType.SJ, ESiteProperties.C_N, str + " and courier!=''"));// ɨ��Ա�ռ�ʱɨ��Ա��Ϊ��
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���㷢��", EScanType.FJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���㵽��", EScanType.DJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("�ɼ�", EScanType.PJ, ESiteProperties.C_N, str));
		// sbSql.append( " union all ");
		// sbSql.append(appendQuerySql("�ɼ�",EScanType.PJ,ESiteProperties.C_N,str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("��������", EScanType.LC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("װ������", EScanType.ZD, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���������", EScanType.YN, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("����װ������", EScanType.ZC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���㷢��ɨ��", EScanType.FC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���㵽��ɨ��", EScanType.DC, ESiteProperties.C_N, str));

		return sbSql.toString();

		// ɨ��Ա�ռ���Ϊ��,�����ռ�ʱ,ɨ���Ϊ�յ�
		// String sql =
		// "select 'ɨ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1   "
		// + str
		// + " and scanCode = '01' and siteProperties = '1'  and courier!=''  "
		// + " union all"
		// +
		// " select 'ɨ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '0'  and courier!='' "
		// + " union all"
		// +
		// " select 'ɨ��Ա�ռ�' as moduleName,'SJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '01' and siteProperties = '1' and issynchronization = '1'  and courier!='' "
		// + " union all"
		// +
		// " select '���㷢��' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���㷢��' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���㷢��' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���㵽��' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���㵽��' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '03' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���㵽��' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '03' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '�ɼ�' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '04' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '�ɼ�' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '04' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '�ɼ�' as moduleName,'PJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '04' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '1'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���㷢��ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���㷢��ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���㷢��ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���㵽��ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���㵽��ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���㵽��ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '1'"
		// // + " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str +" and scanCode = '01' and siteProperties = '1'"
		// // + " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str
		// //
		// +" and scanCode = '01' and siteProperties = '1' and issynchronization = '0'"
		// // + " union all"
		// // +
		// //
		// " select '����' as moduleName,count(*) as amount from tb_scanData where 1=1"+
		// // str
		// //
		// +" and scanCode = '01' and siteProperties = '1' and issynchronization = '1'"
		// ;
		// return sql;
	}

	/**
	 * ��ȡ����ɨ��Աsql
	 * 
	 * @param str
	 * @return
	 */
	private String getCenterScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("���ĵ���", EScanType.DJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���ķ���", EScanType.FJ, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���������", EScanType.YN, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("װ������", EScanType.ZD, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("��������", EScanType.LC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("����װ������", EScanType.ZC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���ķ���ɨ��", EScanType.FC, ESiteProperties.C_N, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���ĵ���ɨ��", EScanType.DC, ESiteProperties.C_N, str));

		return sbSql.toString();
		// String sql =
		// "select '���ĵ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���ĵ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���ĵ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���ķ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���ķ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���ķ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '1'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZD' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���ķ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���ķ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���ķ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '1' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���ĵ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '1'"
		// + " union all"
		// +
		// " select '���ĵ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���ĵ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '1' and issynchronization = '1'";
		// return sql;
	}

	/*
	 * ��ȡ����ɨ��Աsql
	 */
	private String getAirScannerSql(String str) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(appendQuerySql("���յ���", EScanType.DJ, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���շ���", EScanType.FJ, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���������", EScanType.YN, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("��������", EScanType.LC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("װ������", EScanType.ZB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("����", EScanType.FB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("����", EScanType.DB, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("����װ������", EScanType.ZC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���շ���ɨ��", EScanType.FC, ESiteProperties.C_H, str));
		sbSql.append(" union all ");
		sbSql.append(appendQuerySql("���յ���ɨ��", EScanType.DC, ESiteProperties.C_H, str));
		return sbSql.toString();

		// String sql =
		// "select '���յ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '03' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '���յ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���յ���' as moduleName,'DJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '03' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���շ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '02' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '���շ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���շ���' as moduleName,'FJ' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '02' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '07' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���������' as moduleName,'YN' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '07' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '08' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '��������' as moduleName,'LC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '08' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '05' and siteProperties = '2'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select 'װ������' as moduleName,'ZB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '05' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '����' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '33' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '����' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '33' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����' as moduleName,'FB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '33' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '����' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '34' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '����' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '35' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����װ������' as moduleName,'ZC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '35' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���շ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '36' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '���շ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���շ���ɨ��' as moduleName,'FC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '36' and siteProperties = '2' and issynchronization = '1'"
		// + " union all"
		// +
		// " select '���յ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + " and scanCode = '37' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '���յ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '���յ���ɨ��' as moduleName,'DC' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// " and scanCode = '37' and siteProperties = '2' and issynchronization = '1'";
		// return sql;
	}

	private String getAirGetGoodserSql(String str) {
		return appendQuerySql("����ɨ��", EScanType.DB, ESiteProperties.C_H, str);
		//
		// String sql =
		// "select '����ɨ��' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// + "and scanCode = '34' and siteProperties = '2'"
		// + " union all"
		// +
		// " select '����ɨ��' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
		// + str
		// +
		// "and scanCode = '34' and siteProperties = '2' and issynchronization = '0'"
		// + " union all"
		// +
		// " select '����ɨ��' as moduleName,'DB' as scanType,count(*) as amount from tb_scanData where 1=1"
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

		if (role == EUserRole.other) {// ϵͳ���ã�����Ա
			switch (GlobalParas.getGlobalParas().getSitePropertyForManagerSearch()) {
			case air: {
				// ����ɨ��Ա
				//sbSql.append(" union all ").append(getAirScannerSql(strWhere));
				sbSql.append(getAirScannerSql(strWhere));
				// ������ȡԱ
				sbSql.append(" union all ").append(getAirGetGoodserSql(strWhere));
			}
				break;
			case center: {
				// ����ɨ��Ա
				//sbSql.append(" union all ").append(getCenterScannerSql(strWhere));
				sbSql.append(getCenterScannerSql(strWhere));
			}
				break;
			case station: {
				// ����ҵ��Ա
				sbSql.append(getBusinesserSql(strWhere));
				// ����ɨ��Ա
				sbSql.append(" union all ").append(getScannerSql(strWhere));
			}
				break;
			}

		} else {
			switch (role.value()) {
			case 1:// ����ҵ��Ա
				sbSql.append(getBusinesserSql(strWhere));
				if (!isUser) {// ��Ϊ��ѯ���豸������
					sbSql.append(" union all ").append(getScannerSql(strWhere));
				}
				break;
			case 2:// ����ɨ��Ա
				sbSql.append(getScannerSql(strWhere));
				if (!isUser) {// ��Ϊ��ѯ���豸������
					sbSql.append(" union all ").append(getBusinesserSql(strWhere));
				}
				break;
			case 3:// ����ɨ��Ա
				sbSql.append(getCenterScannerSql(strWhere));
				break;
			case 4:// ����ɨ��Ա
				sbSql.append(getAirScannerSql(strWhere));
				if (!isUser) {// ��Ϊ��ѯ���豸������
					sbSql.append(" union all ").append(getAirGetGoodserSql(strWhere));
				}
				break;
			case 5:// ������ȡԱ
				sbSql.append(getAirGetGoodserSql(strWhere));
				if (!isUser) {// ��Ϊ��ѯ���豸������
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
