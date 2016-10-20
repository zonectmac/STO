package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.model.MsgSend;
import com.cneop.stoExpress.model.OrderOperating;
import com.cneop.stoExpress.model.QueryView;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.util.StrUtil;

import android.content.Context;
import android.util.Log;

public class OrderOperService extends BaseDao {

	public OrderOperService(Context context) {
		super(context);
		tableName = "tb_orderOperating";
	}

	@Override
	protected String getSearchSql(String[] words) {

		String sql = "select * from tb_orderOperating where 1=1  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		OrderOperating orderOperModel = (OrderOperating) object;
		Object[] objs = { orderOperModel.getUserNo(), orderOperModel.getEmployeeName(), orderOperModel.getEmployeeSite(), orderOperModel.getEmployeeSiteCode(), orderOperModel.getLogisticid(), orderOperModel.getBarcode(), orderOperModel.getReasonCode(), orderOperModel.getFlag() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "insert into tb_orderOperating(userNo,employeeName,employeeSite,employeeSiteCode,logisticid,barcode,reasonCode,flag) values(?,?,?,?,?,?,?,?)";
		return sql;
	}

	/**
	 * 根据订单状态获取未上传订单
	 * 
	 * @param orderStatus
	 * @return
	 */
	public List<OrderOperating> getOrderByFlag(int orderStatus) {
		String[] words = { "  and (issynchronization='0' or issynchronization is null) and flag=? limit 30" };
		String[] selectArgs = { String.valueOf(orderStatus) };
		List<Object> objList = getListObj(words, selectArgs, new OrderOperating());
		List<OrderOperating> orderOperList = new ArrayList<OrderOperating>();
		if (objList != null && objList.size() > 0) {
			for (Object obj : objList) {
				orderOperList.add((OrderOperating) obj);
			}
		}
		return orderOperList;
	}

	/**
	 * 增加订单记录
	 * 
	 * @param scanDataModel
	 * @param orderOper
	 * @return
	 */
	public int addRecord(ScanDataModel scanDataModel, OrderOperating orderOper) {
		List<String> sqlList = new ArrayList<String>();
		List<Object[]> paramsList = new ArrayList<Object[]>();
		if (scanDataModel != null) {
			sqlList.add("insert into tb_scanData(barcode,scanUser,scanStation,sampleType,ShiftCode,destinationSiteCode,senderPhone,recipientPhone,routeCode,preStationCode,nextStationCode,secondStationCode,vehicleNumber,weight,courier,packageCode,abnormalCode,abnormalDesc,carLotNumber,flightCode,signTypeCode,signer,siteProperties,expressType,scanCode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			Object[] scanDataParams = { scanDataModel.getBarcode(), scanDataModel.getScanUser(), scanDataModel.getScanStation(), scanDataModel.getSampleType(), scanDataModel.getShiftCode(), scanDataModel.getDestinationSiteCode(), scanDataModel.getSenderPhone(), scanDataModel.getRecipientPhone(), scanDataModel.getRouteCode(), scanDataModel.getPreStationCode(), scanDataModel.getNextStationCode(), scanDataModel.getSecondStationCode(), scanDataModel.getVehicleNumber(), scanDataModel.getWeight(), scanDataModel.getCourier(), scanDataModel.getPackageCode(), scanDataModel.getAbnormalCode(), scanDataModel.getAbnormalDesc(), scanDataModel.getCarLotNumber(), scanDataModel.getFlightCode(), scanDataModel.getSignTypeCode(), scanDataModel.getSigner(), scanDataModel.getSiteProperties(), scanDataModel.getExpressType(), scanDataModel.getScanCode() };
			paramsList.add(scanDataParams);
		}
		sqlList.add("insert into tb_orderOperating (userno,employeename,employeesite,employeesitecode,logisticid,barcode,reasoncode,flag) values(?,?,?,?,?,?,?,?)");
		Object[] orderScanParams = { orderOper.getUserNo(), orderOper.getEmployeeName(), orderOper.getEmployeeSite(), orderOper.getEmployeeSiteCode(), orderOper.getLogisticid(), orderOper.getBarcode(), orderOper.getReasonCode(), orderOper.getFlag() };
		paramsList.add(orderScanParams);
		sqlList.add("update tb_order set issynchronization=1 where logisticid=?");
		Object[] orderStatusParams = { orderOper.getLogisticid() };
		paramsList.add(orderStatusParams);
		int t = dataBaseManager.executeSqlByTran(sqlList, paramsList);
		return t;
	}

	/**
	 * 未完成操作
	 * 
	 * @param logisticid
	 * @return
	 */
	public int unFinishOrder(String logisticid) {
		List<String> sqlList = new ArrayList<String>();
		List<Object[]> paramsList = new ArrayList<Object[]>();
		sqlList.add("delete from tb_scanData where scanCode='01' and  barcode in (select barcode from tb_orderoperating where logisticid=?)");
		Object[] params = { logisticid };
		paramsList.add(params);
		sqlList.add("delete from tb_orderoperating where logisticid=?");
		paramsList.add(params);
		sqlList.add("update tb_order set issynchronization=0 where logisticid=?");
		paramsList.add(params);
		int t = dataBaseManager.executeSqlByTran(sqlList, paramsList);
		return t;
	}

	/**
	 * 删除
	 * 
	 * @param barcode
	 * @return
	 */
	public int deleteOrderOper(String barcode) {
		List<String> sqlList = new ArrayList<String>();
		List<Object[]> paramsList = new ArrayList<Object[]>();
		sqlList.add("delete from tb_scanData where scanCode='01' and  barcode =?");
		Object[] params = { barcode };
		paramsList.add(params);
		sqlList.add("delete from tb_orderoperating where barcode=?");
		paramsList.add(params);
		int t = dataBaseManager.executeSqlByTran(sqlList, paramsList);
		return t;
	}

	public List<UploadView> getUnUploadView() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		sb.append("select '订单' as scanTypeStr,");
		sb.append("count(id) as totalCount,");
		sb.append("'order' as uploadType,");
		sb.append("'OTHER' as scanType ");
		sb.append("from tb_orderoperating where (issynchronization=0 or issynchronization is null) )");
		sb.append("as c where c.totalCount>0");
		List<UploadView> uploadList = new ArrayList<UploadView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sb.toString(), null, new UploadView());
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

	/**
	 * 查询视图
	 * 
	 * @return
	 */
	public List<QueryView> getQueryView(String barcode, String userNo, String startTime, String endTime) {
		String conditionSql = getSqlWhere(barcode, userNo, startTime, endTime, "");
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select moduleName,sum(totalcount) as totalDataCount,");
		sqlSb.append("sum(unUpload) as unUpload,scanCode,siteProperties,'OTHER' as scanType ,'order' as uploadType from(");
		sqlSb.append("select '订单' as moduleName,count(id) as totalcount,0 as unUpload,");
		sqlSb.append("'00' as scanCode,'0' as siteProperties from tb_orderoperating where 1=1");
		sqlSb.append(conditionSql);
		sqlSb.append(" union all ");
		sqlSb.append("select '订单' as moduleName,0 as totalcount,count(id) as unUpload,");
		sqlSb.append("'00' as scanCode,'0' as siteProperties from tb_orderoperating where issynchronization=0" + conditionSql + ") as c ");
		sqlSb.append("where (totalcount>0 or unupload>0) group by moduleName having totalDataCount>0;");
		List<QueryView> queryList = new ArrayList<QueryView>();
		try {
			List<Object> list = dataBaseManager.queryData2Object(sqlSb.toString(), null, new QueryView());
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

	@SuppressWarnings("static-access")
	public String getSqlWhere(String barcode, String userNo, String startTime, String endTime, String uploadStatus) {
		StringBuilder whereSb = new StringBuilder();
		if (!strUtil.isNullOrEmpty(barcode)) {
			whereSb.append(" and barcode='" + barcode + "'");
		}
		if (!strUtil.isNullOrEmpty(userNo)) {
			whereSb.append(" and userno='" + userNo + "'");
		}
		if (!strUtil.isNullOrEmpty(startTime) && !strUtil.isNullOrEmpty(endTime)) {
			whereSb.append(" and (scantime>='" + startTime + "' and scantime<='" + endTime + "')");
		} else if (!strUtil.isNullOrEmpty(startTime)) {
			whereSb.append(" and scantime>='" + startTime + "'");
		} else if (!strUtil.isNullOrEmpty(endTime)) {
			whereSb.append(" and scantime<='" + endTime + "'");
		}
		if (!strUtil.isNullOrEmpty(uploadStatus)) {
			whereSb.append(" and issynchronization='" + uploadStatus + "'");
		}
		return whereSb.toString();
	}

	/**
	 * 获取数量
	 * 
	 * @return
	 */
	public int getCountByStatus(String status) {
		int count = 0;
		String condition = "";
		if (!strUtil.isNullOrEmpty(status)) {
			condition += " and issynchronization=" + status;
		}
		count = getCount(condition);
		return count;
	}

	public int getCount(String condition) {
		int count = 0;
		String sql = "select count(Id) from tb_orderoperating where 1=1   ";
		if (!strUtil.isNullOrEmpty(condition)) {
			sql += condition;
		}
		try {
			String str = dataBaseManager.querySingleData(sql, null);
			if (!strUtil.isNullOrEmpty(str)) {
				count = Integer.parseInt(str);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 订单数量
	 * 
	 * @param flag
	 * @return
	 */
	public int getCountByOper(String flag) {
		int count = 0;
		String condition = "";
		if (!strUtil.isNullOrEmpty(flag)) {
			condition += " and ( issynchronization='0' or issynchronization is null ) and flag=" + flag;
		}
		count = getCount(condition);
		return count;
	}

	public List<OrderOperating> getOrderOperData(String condition, int pageSize, int pageIndex) {
		List<OrderOperating> list = new ArrayList<OrderOperating>();
		StringBuilder sql = new StringBuilder("select * from tb_orderoperating where 1=1 ");
		sql.append(condition);
		sql.append(" limit " + pageSize + " offset " + (pageIndex - 1) * pageSize);
		list = getList(sql.toString(), null);
		return list;
	}

	public List<Map<String, Object>> getList(String sqlWhere, String starttime, String endtime) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("select * from tb_orderoperating where 1=1 ");
			sql.append(sqlWhere);

			sql.append(" and scantime between '").append(starttime).append("' ");
			sql.append(" and     '").append(endtime).append("' ");

			dataList = dataBaseManager.queryData2Map(sql.toString(), null, new OrderOperating());
		} catch (Exception ex) {
			Log.e("DB", ex.getMessage());
			ex.printStackTrace();
		}
		return dataList;
	}

	private List<OrderOperating> getList(String sql, String[] params) {
		List<OrderOperating> list = new ArrayList<OrderOperating>();
		try {
			OrderOperating model = new OrderOperating();
			List<Object> listT = dataBaseManager.queryData2Object(sql, params, model);
			if (listT != null && listT.size() > 0) {
				for (Object object : listT) {
					list.add((OrderOperating) object);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据ID删除
	 * 
	 * @param id
	 * @return
	 */
	public int delRecord(int id) {
		String[] whereArgs = { String.valueOf(id) };
		int t = dataBaseManager.deleteData("tb_orderoperating", "id=?", whereArgs);
		return t;
	}

	/**
	 * 批量删除数据
	 */
	public int delData(String condition) {
		int t = -1;
		String sql = "delete from tb_orderoperating where 1=1 " + condition;
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
	 * 删除7天前的数据
	 * 
	 * @param day
	 * @return
	 */
	public int delData() {
		String condition = " and datetime('now','localtime','-7 day')>datetime(scantime)";
		return delData(condition);
	}

	/**
	 * 上传后，更新状态
	 * 
	 * @param list
	 * @return
	 */
	public int updateStatus(List<OrderOperating> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("update tb_orderoperating set issynchronization='1',");
		sql.append("lasttime=datetime('now','localtime') ");
		sql.append("where id=?  ");
		List<Object[]> objectList = new ArrayList<Object[]>();
		for (OrderOperating model : list) {
			Object[] objs = { model.getId() };
			objectList.add(objs);
		}
		int result = dataBaseManager.executeSqlByTran(sql.toString(), objectList);
		return result;
	}

}
