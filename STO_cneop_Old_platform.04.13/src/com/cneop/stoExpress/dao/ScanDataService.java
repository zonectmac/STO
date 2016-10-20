package com.cneop.stoExpress.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.model.QueryView;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.util.DataBaseManager;
import com.cneop.util.DateUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.file.FileUtil;

import android.content.Context;

public class ScanDataService {
	DataBaseManager dataBaseManager;
	StrUtil strUtil;
	DateUtil dateUtil;
	StatictisSqlByUserRole contactSql;

	public ScanDataService(Context context) {
		this.dataBaseManager = DataBaseManager.getInstance(context);
		strUtil = new StrUtil();
		dateUtil = new DateUtil();
		contactSql = new StatictisSqlByUserRole();
	}

	/**
	 * 获得扫描数据
	 * 
	 * @param scanCode
	 *            扫描代码
	 * @param siteProperties
	 *            站点属性 1：网点中心 2：航空
	 * @param syncStatus
	 *            上传状态
	 * @param limitCount
	 *            数量
	 */
	@SuppressWarnings("static-access")
	public List<ScanDataModel> GetScanData(String scanCode, String siteProperties, String syncStatus,
			String limitCount) {
		List<ScanDataModel> list = new ArrayList<ScanDataModel>();
		StringBuilder sql = new StringBuilder("select * from tb_scanData where 1=1");
		sql.append(getSqlwhere(scanCode, siteProperties, syncStatus));
		if (!strUtil.isNullOrEmpty(limitCount)) {
			sql.append(" limit " + limitCount);
		}
		list = getList(sql.toString(), null);
		return list;
	}

	/*
	 * 获得扫描数据 分页
	 */
	public List<ScanDataModel> GetScanData(String condition, int pageSize, int pageIndex) {
		List<ScanDataModel> list = new ArrayList<ScanDataModel>();

		StringBuilder sql = new StringBuilder("select * from tb_scanData where 1=1 ");
		sql.append(condition);
		sql.append(" limit ").append(pageSize).append(" offset ").append((pageIndex - 1) * pageSize);
		list = getList(sql.toString(), null);
		System.out.println("=========limit \t" + sql.toString());
		return list;
	}

	private String getSqlwhere(String scanCode, String siteProperties, String syncStatus) {
		StringBuilder sb = new StringBuilder();

		if (!strUtil.isNullOrEmpty(scanCode)) {
			sb.append(" and scanCode='" + scanCode + "'");
		}
		if (!strUtil.isNullOrEmpty(siteProperties)) {
			sb.append(" and siteProperties='" + siteProperties + "'");
		}
		if (!strUtil.isNullOrEmpty(syncStatus)) {
			sb.append(" and issynchronization='" + syncStatus + "'");
		}
		return sb.toString();
	}

	private String getSqlwhere(String scanUser, String siteProperties, String startTime, String endTime,
			String barcode) {
		StringBuilder sb = new StringBuilder();
		sb.append(getSqlwhere("", siteProperties, ""));
		if (!strUtil.isNullOrEmpty(scanUser)) {
			sb.append(" and scanuser='" + scanUser + "'");
		}
		if (!strUtil.isNullOrEmpty(startTime) && !strUtil.isNullOrEmpty(endTime)) {
			sb.append(" and (scantime>='" + startTime + "'  and scantime<='" + endTime + "')");
		} else if (!strUtil.isNullOrEmpty(startTime)) {
			sb.append(" and scantime>='" + startTime + "'");
		} else if (!strUtil.isNullOrEmpty(endTime)) {
			sb.append(" and scantime<='" + endTime + "'");
		}
		if (!strUtil.isNullOrEmpty(barcode)) {
			sb.append(" and barcode='" + barcode + "'");
		}
		return sb.toString();
	}

	public String getSqlwhere(String scanCode, String siteProperties, String syncStatus, String scanUser,
			String startTime, String endTime, String barcode, String expressType, String courier, String nextStation,
			String route) {
		StringBuilder sb = new StringBuilder();
		sb.append(getSqlwhere(scanCode, siteProperties, syncStatus));
		sb.append(getSqlwhere(scanUser, "", startTime, endTime, barcode));
		if (expressType != null) {
			sb.append(" and expressType='" + expressType + "'");
		}
		if (!strUtil.isNullOrEmpty(courier)) {
			sb.append(" and courier='" + courier + "'");
		}
		if (!strUtil.isNullOrEmpty(nextStation)) {
			sb.append(" and nextStationCode='" + nextStation + "'");
		}
		if (!strUtil.isNullOrEmpty(route)) {
			sb.append(" and routeCode='" + route + "'");
		}
		return sb.toString();
	}

	/*
	 * 扫描数据数量
	 * 
	 * @param scanCode 扫描代码
	 * 
	 * @param siteProperties 站点属性 *
	 * 
	 * @param syncStatus 同步状态
	 */
	public int getCount(String scanCode, String siteProperties, String syncStatus) {
		int count = 0;
		count = getCount(getSqlwhere(scanCode, siteProperties, syncStatus));
		return count;
	}

	/*
	 * 数量
	 */
	public int getCount(String condition) {
		int count = 0;
		String sql = "select count(Id) from tb_scanData where 1=1  ";
		sql += condition;
		try {
			String str = dataBaseManager.querySingleData(sql, null);
			System.out.println("======str \t" + str);
			if (!strUtil.isNullOrEmpty(str)) {
				count = Integer.parseInt(str);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return count;
	}

	/*
	 * 更新上传状态
	 */
	public int updateStatus(List<ScanDataModel> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("update tb_scanData set issynchronization='1',");
		sql.append("lasttime=datetime('now','localtime') ");
		sql.append("where Id=? and issynchronization=0");
		List<Object[]> objectList = new ArrayList<Object[]>();
		for (ScanDataModel model : list) {
			Object[] objs = { model.getId() };
			objectList.add(objs);
		}
		int result = dataBaseManager.executeSqlByTran(sql.toString(), objectList);
		return result;
	}

	/*
	 * 更新上传状态
	 */
	public int updateStatus(String condition, String uploadStatus) {
		int flag = -1;
		StringBuilder sql = new StringBuilder();
		sql.append("update tb_scanData set issynchronization='" + uploadStatus + "',");
		sql.append("lasttime=datetime('now','localtime') ");
		sql.append("where 1=1" + condition);
		try {
			dataBaseManager.updateDataBySql(sql.toString(), null);
			flag = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	/*
	 * 根据包号统计未上传的记录
	 */
	public List<ScanDataModel> getDataGroupByPackage(String scanCode, String siteProperties, String minId,
			String maxId) {
		List<ScanDataModel> list = new ArrayList<ScanDataModel>();
		String sql = "select * from tb_scanData where issynchronization='0' and scanCode=? and siteProperties=? and id>=? and id<=? group by packageCode order by id";
		String[] params = { scanCode, siteProperties, minId, maxId };
		list = getList(sql, params);
		return list;
	}

	private List<ScanDataModel> getList(String sql, String[] params) {
		List<ScanDataModel> list = new ArrayList<ScanDataModel>();
		try {
			ScanDataModel model = new ScanDataModel();
			List<Object> listT = dataBaseManager.queryData2Object(sql, params, model);
			if (listT != null && listT.size() > 0) {
				for (Object object : listT) {
					list.add((ScanDataModel) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 添加单条记录
	 * 
	 * @param scanModel
	 * @return
	 */
	public int addRecord(ScanDataModel scanModel) {
		List<ScanDataModel> list = new ArrayList<ScanDataModel>();
		list.add(scanModel);
		return addRecord(list);
	}

	/*
	 * 添加多条记录
	 */
	public int addRecord(List<ScanDataModel> scanDataModelList) {
		int result = 0;
		if (scanDataModelList != null && scanDataModelList.size() > 0) {

			String sql = "insert into tb_scanData(barcode,scanUser,scanStation,sampleType,ShiftCode,destinationSiteCode,senderPhone,recipientPhone,routeCode,preStationCode,nextStationCode,secondStationCode,vehicleNumber,weight,courier,packageCode,abnormalCode,abnormalDesc,carLotNumber,flightCode,signTypeCode,signer,siteProperties,expressType,scanCode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			List<Object[]> objList = new ArrayList<Object[]>();
			for (ScanDataModel scanDataModel : scanDataModelList) {
				Object[] objs = { scanDataModel.getBarcode(), scanDataModel.getScanUser(),
						scanDataModel.getScanStation(), scanDataModel.getSampleType(), scanDataModel.getShiftCode(),
						scanDataModel.getDestinationSiteCode(), scanDataModel.getSenderPhone(),
						scanDataModel.getRecipientPhone(), scanDataModel.getRouteCode(),
						scanDataModel.getPreStationCode(), scanDataModel.getNextStationCode(),
						scanDataModel.getSecondStationCode(), scanDataModel.getVehicleNumber(),
						scanDataModel.getWeight(), scanDataModel.getCourier(), scanDataModel.getPackageCode(),
						scanDataModel.getAbnormalCode(), scanDataModel.getAbnormalDesc(),
						scanDataModel.getCarLotNumber(), scanDataModel.getFlightCode(), scanDataModel.getSignTypeCode(),
						scanDataModel.getSigner(), scanDataModel.getSiteProperties(), scanDataModel.getExpressType(),
						scanDataModel.getScanCode() };
				objList.add(objs);
			}
			result = dataBaseManager.executeSqlByTran(sql, objList);
		}
		return result;
	}

	/*
	 * 当天内单号是否重复
	 */
	public int isBarcodeRepeat(String barcode, String scanCode, String condition) {
		int flag = 0;
		String sql = "select count(Id) from tb_scanData where scanCode=? and barcode=? and (scanTime >? and scanTime<?)";
		if (!strUtil.isNullOrEmpty(condition)) {
			sql += condition;
		}
		String dateStr = dateUtil.getDateTimeByPattern("yyyy-MM-dd");
		String startTime = dateStr + " 00:00:00";
		String endTime = dateStr + " 23:59:59";
		String[] queryArgs = { scanCode, barcode, startTime, endTime };
		String result = dataBaseManager.querySingleData(sql, queryArgs);
		if (!strUtil.isNullOrEmpty(result)) {
			flag = Integer.parseInt(result);
		}
		return flag;
	}

	/*
	 * 删除单条数据
	 */
	public int delSingleData(String barcode, String scanCode, boolean isManager) {
		String[] whereArgs = { scanCode, barcode };
		StringBuilder sb = new StringBuilder();
		if (isManager) {
			sb.append(" scanCode=? and barcode=? ");
		} else {
			sb.append(" scanCode=? and barcode=? and issynchronization='0'");
		}
		int t = dataBaseManager.deleteData("tb_scanData", sb.toString(), whereArgs);
		return t;
	}

	/*
	 * 删除单条数据
	 */
	public int delSingleData(int id) {
		String[] whereArgs = { String.valueOf(id) };
		int t = dataBaseManager.deleteData("tb_scanData", "id=?", whereArgs);
		return t;
	}

	/*
	 * 批量删除数据
	 */
	public int delData(String condition) {
		int t = -1;
		String sql = "delete from tb_scanData where 1=1 " + condition;
		try {
			dataBaseManager.deleteDataBySql(sql, null);
			t = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 批量删除数据
	 * 
	 * @param day
	 *            天数
	 * @return
	 */
	public int delData(int day) {
		String condition = " and datetime('now','localtime','-" + day + " day')>datetime(scanTime)";
		return delData(condition);
	}

	/**
	 * 获得未上传数据的统计
	 * 
	 * @return
	 */
	public List<UploadView> getUnUpload() {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select * from (");
		sqlSb.append(
				"select '收件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'SJ' as scanType from tb_scandata where issynchronization=0 and scancode='01'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '发件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'FJ' as scanType from tb_scandata where issynchronization=0 and scancode='02'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '到件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'DJ' as scanType from tb_scandata where issynchronization=0 and scancode='03'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '派件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'PJ' as scanType from tb_scandata where issynchronization=0 and scancode='04'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '留仓' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'LC' as scanType from tb_scandata where issynchronization=0 and scancode='08'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '装袋发件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'ZD' as scanType    from tb_scandata where issynchronization=0 and siteproperties=1 and scancode='05'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '问题件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'YN' as scanType    from tb_scandata where issynchronization=0 and scancode='07'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '签收' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'QS' as scanType    from tb_scandata where issynchronization=0 and scancode='99'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '发包' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'FB' as scanType    from tb_scandata where issynchronization=0 and scancode='33'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '到包' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'DB' as scanType    from tb_scandata where issynchronization=0 and scancode='34'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '装包发件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'ZB' as scanType    from tb_scandata where issynchronization=0 and siteproperties=2 and scancode='05'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '装车发件' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'ZC' as scanType    from tb_scandata where issynchronization=0 and scancode='35'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '发车扫描' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'FC' as scanType    from tb_scandata where issynchronization=0 and scancode='36'");
		sqlSb.append(" union all ");
		sqlSb.append(
				"select '到车扫描' as scanTypeStr,count(id) as totalCount,'scanData' as uploadType,'DC' as scanType    from tb_scandata where issynchronization=0 and scancode='37')");
		sqlSb.append("as t where t.totalcount>0;");

		List<UploadView> uploadList = new ArrayList<UploadView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sqlSb.toString(), null, new UploadView());
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					uploadList.add((UploadView) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadList;
	}

	public List<Map<String, Object>> getList(String sqlwhere) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String sql = "  select * from tb_scanData where 1=1  " + sqlwhere;
		try {
			dataList = dataBaseManager.queryData2Map(sql, null, new ScanDataModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	/**
	 * 查询扫描数据统计
	 * 
	 * @return
	 */
	public List<QueryView> getScanDataStatistics(String userCode, String startTime, String endTime,
			ESiteProperties siteProperties, String barcode, EUserRole userRole) {
		String sitePropertiesStr = "";
		if (siteProperties != null) {
			sitePropertiesStr = String.valueOf(siteProperties.value());
		}
		String sqlWhereStr = getSqlwhere(userCode, sitePropertiesStr, startTime, endTime, barcode);
		String airSqlwhere = "";
		if (userRole == EUserRole.other) {
			airSqlwhere = getSqlwhere("", String.valueOf(ESiteProperties.C_H.value()), startTime, endTime, barcode);
		}
		String sql = contactSql.GetSqlByUserRole(userRole, sqlWhereStr, airSqlwhere);
		List<QueryView> queryList = new ArrayList<QueryView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sql, null, new QueryView());
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					queryList.add((QueryView) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public int delSevenData() {
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, -7);
		Date date = cd.getTime();
		deleteImg(date);
		return deleteData(date);
	}

	/**
	 * 登录时删除数据
	 * 
	 * @return
	 */
	public void delData2Login() {
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, -15);
		Date date = cd.getTime();
		// 登录时删除15天前的数据,删除7天前的图片
		deleteData(date);

		cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, -7);
		deleteImg(cd.getTime());

	}

	private int deleteData(Date date) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sd.format(date);
		String[] whereArgs = { time };
		int t = dataBaseManager.deleteData("tb_scanData", "lasttime<?", whereArgs);

		return t;
	}

	private void deleteImg(Date date) {
		// 删除图片
		FileUtil.deleteFileByDate(new java.io.File(GlobalParas.getGlobalParas().getSignUploadPath()), date);
		FileUtil.deleteFileByDate(new java.io.File(GlobalParas.getGlobalParas().getProblemUploadPath()), date);

	}

	public int delMsgSingleData(String barcode, boolean isManager) {
		String[] whereArgs = { barcode };
		StringBuilder sb = new StringBuilder();
		if (isManager) {
			sb.append("barcode=? ");
		} else {
			sb.append("barcode=? and issynchronization='0'");
		}
		int t = dataBaseManager.deleteData("tb_msgSend", sb.toString(), whereArgs);
		return t;
	}

}
