package com.cneop.stoExpress.dao;

import com.cneop.stoExpress.common.Enums.*;

/**
 * 数据查询SQL拼接
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

	// 管理员
	private String GetAdminStatisticsSql(String sqlWhere, String ariSqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("业务员收件", EScanType.SJ.value(), " and courier =''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("签收", EScanType.QS.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetScanerStatisticsSql(sqlWhere));
		sb.append(contactStr);
		sb.append(GetAirScanerStatisticsSql(ariSqlWhere));
		sb.append(contactStr);
		sb.append(GetAriDeliveryStatisticsSql(ariSqlWhere));
		return sb.toString();
	}

	// 业务员
	private String GetBusinessStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("业务员收件", EScanType.SJ.value(), " and courier =''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("签收", EScanType.QS.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("问题件", EScanType.YN.value(), sqlWhere));
		sb.append(sqlWhere);
		return sb.toString();
	}

	// 网点扫描员
	private String GetScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("扫描员收件", EScanType.SJ.value(), " and courier <>''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("派件", EScanType.PJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetCenterScanerStatisticsSql(sqlWhere));
		return sb.toString();
	}

	// 中心扫描员
	private String GetCenterScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("发件", EScanType.FJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("到件", EScanType.DJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("问题件", EScanType.YN.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("留仓", EScanType.LC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("装袋发件", EScanType.ZD.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("发车", EScanType.FC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("到车", EScanType.DC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("装车发件", EScanType.ZC.value(), sqlWhere));
		return sb.toString();
	}

	// 航空扫描员
	private String GetAirScanerStatisticsSql(String sqlWhere) {
		StringBuilder sb = new StringBuilder();
		sb.append(GetStaticsSql("航空发件", EScanType.FJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空到件", EScanType.DJ.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空问题件", EScanType.YN.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空留仓", EScanType.LC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("发包", EScanType.FB.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("到包", EScanType.DB.value(), " and routeCode<>''" + sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("装包发件", EScanType.ZD.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空发车", EScanType.FC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空到车", EScanType.DC.value(), sqlWhere));
		sb.append(contactStr);
		sb.append(GetStaticsSql("航空装车发件", EScanType.ZC.value(), sqlWhere));
		return sb.toString();
	}

	// 航空提货员
	private String GetAriDeliveryStatisticsSql(String sqlWhere) {
		return GetStaticsSql("到包扫描", EScanType.DB.value(), " and routeCode=''" + sqlWhere);
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
