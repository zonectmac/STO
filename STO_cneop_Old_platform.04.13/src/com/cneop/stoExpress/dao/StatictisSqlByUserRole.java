package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.common.Enums.*;

/**
 * ���ݲ�ѯSQLƴ��
 * 
 * @author Administrator
 * 
 */
class StatictisSqlByUserRole {

	String contactStr = " union all ";

	public String GetSqlByUserRole(EUserRole userRole, String sqlWhereStr, String airSqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append("select scanTypeStr,sum(totalcount) as totalDataCount,sum(unUpload)as unUpload,scanCode,siteProperties,'scanData' as uploadType from( ");
		switch (userRole) {
		case business:
			sb.append(GetBusinessStatisticsSql(sqlWhereStr));
			break;
		case scaner:
			sb.append(GetScanerStatisticsSql(sqlWhereStr));
			break;
		case centerScaner:
			sb.append(GetCenterScanerStatisticsSql(sqlWhereStr));
			break;
		case airScaner:
			sb.append(GetAirScanerStatisticsSql(sqlWhereStr));
			break;
		case ariDelivery:
			sb.append(GetAriDeliveryStatisticsSql(sqlWhereStr));
			break;
		default:
			sb.append(GetAdminStatisticsSql(sqlWhereStr, airSqlWhere));
			break;
		}
		sb.append(") as c where (totalcount>0 or unupload>0) group by scanTypeStr having totalDataCount>0 order by rowid");
		return sb.toString();
	}

	// ����Ա
	private String GetAdminStatisticsSql(String sqlWhere, String ariSqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("ҵ��Ա�ռ�", EScanType.SJ.value(), " and courier =''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("ǩ��", EScanType.QS.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetScanerStatisticsSql(sqlWhere));
		sb.append(contactStr);
		sb.append(GetAirScanerStatisticsSql(ariSqlWhere));
		sb.append(contactStr);
		sb.append(GetAriDeliveryStatisticsSql(ariSqlWhere));
		return sb.toString();
	}

	// ҵ��Ա
	private String GetBusinessStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("ҵ��Ա�ռ�", EScanType.SJ.value(), " and courier =''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("ǩ��", EScanType.QS.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("�����", EScanType.YN.value(), sqlWhere));
		sb.append(sqlWhere);
		return sb.toString();
	}

	// ����ɨ��Ա
	private String GetScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("ɨ��Ա�ռ�", EScanType.SJ.value(), " and courier <>''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("�ɼ�", EScanType.PJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetCenterScanerStatisticsSql(sqlWhere));
		return sb.toString();
	}

	// ����ɨ��Ա
	private String GetCenterScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("����", EScanType.FJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.DJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("�����", EScanType.YN.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.LC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("װ������", EScanType.ZD.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.FC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.DC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("װ������", EScanType.ZC.value(), sqlWhere));
		return sb.toString();
	}

	// ����ɨ��Ա
	private String GetAirScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("���շ���", EScanType.FJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("���յ���", EScanType.DJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("���������", EScanType.YN.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("��������", EScanType.LC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.FB.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����", EScanType.DB.value(), " and routeCode<>''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("װ������", EScanType.ZD.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("���շ���", EScanType.FC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("���յ���", EScanType.DC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("����װ������", EScanType.ZC.value(), sqlWhere));
		return sb.toString();
	}

	// �������Ա
	private String GetAriDeliveryStatisticsSql(String sqlWhere) {
		return GetStaticsSql("����ɨ��", EScanType.DB.value(), " and routeCode=''" + sqlWhere);
	}

	private String GetStaticsSql(String scanTypeStr, String scanCode, String condition) {
		StringBuilder sb = new StringBuilder();
		sb.append("select '" + scanTypeStr + "' as scanTypeStr,count(id) as totalcount,0 as unUpload,scanCode,siteProperties from tb_scandata where scancode='" + scanCode + "'");
		sb.append(condition);
		sb.append(contactStr);
		sb.append("select '" + scanTypeStr + "' as scanTypeStr,0 as totalcount,count(id) as unUpload,scanCode,siteProperties from tb_scandata where scancode='" + scanCode + "' and issynchronization=0");
		sb.append(condition);
		return sb.toString();
	}
}
