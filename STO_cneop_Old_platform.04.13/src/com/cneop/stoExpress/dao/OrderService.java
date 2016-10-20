package com.cneop.stoExpress.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cneop.stoExpress.model.Order;

public class OrderService extends BaseDao {
	public OrderService(Context context) {
		super(context);
		tableName = "tb_order";
	}

	@Override
	protected String getSearchSql(String[] words) {
		String sql = "select logisticid,userNo,acceptDate,customerCode,customerName,sender_Name,sender_Address,sender_Phone,sender_Mobile,destcode,cusnote,isUrge,issynchronization from tb_order where 1=1  ";
		sql += strUtil.arrayToString(words);
		return sql;
	}

	@Override
	protected Object[] getInsertParams(Object object) {

		Order order = (Order) object;
		Object[] objs = { order.getLogisticid(), order.getUserNo(), order.getAcceptDate(), order.getCustomerCode(), order.getCustomerName(), order.getSender_Name(), order.getSender_Address(), order.getSender_Phone(), order.getSender_Mobile(), order.getDestcode(), order.getCusnote(), order.getIsUrge(), order.getIssynchronization() };
		return objs;
	}

	@Override
	protected String getInsertSql() {

		String sql = "replace into tb_order(logisticid,userNo,acceptDate,customerCode,customerName,sender_Name,sender_Address,sender_Phone,sender_Mobile,destcode,cusnote,isUrge,issynchronization) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	/**
	 * 根据条件查找订单
	 * 
	 * @param logisticid
	 * @param userNo
	 * @param issynchronization
	 * @return
	 */
	public Order getOrder(String logisticid, String userNo, String issynchronization) {
		String[] words = null;
		String[] selectArgs = null;
		int len = 0;
		String logisticidConditin = "";
		String userCondition = "";
		String synCondition = "";
		if (!strUtil.isNullOrEmpty(logisticid)) {
			logisticidConditin = " and logisticid=?";
			len++;
		}
		if (!strUtil.isNullOrEmpty(userNo)) {
			userCondition = " and userno=?";
			len++;
		}
		if (!strUtil.isNullOrEmpty(issynchronization)) {
			synCondition = " and issynchronization=?";
			len++;
		}
		words = new String[len];
		selectArgs = new String[len];
		for (int i = 0; i < len; i++) {
			if (!strUtil.isNullOrEmpty(logisticidConditin)) {
				words[i] = logisticidConditin;
				selectArgs[i] = logisticid;
				logisticidConditin = "";
			} else if (!strUtil.isNullOrEmpty(userCondition)) {
				words[i] = userCondition;
				selectArgs[i] = userNo;
				userCondition = "";
			} else if (!strUtil.isNullOrEmpty(synCondition)) {
				words[i] = synCondition;
				selectArgs[i] = issynchronization;
				synCondition = "";
			}
		}
		Order orderModel = new Order();
		orderModel = (Order) getSingleObj(words, selectArgs, orderModel);
		return orderModel;
	}

	/**
	 * 更新订单状态
	 * 
	 * @param orderList
	 * @return
	 */
	public int updateOrderStatus(List<Order> orderList) {
		int count = 0;
		if (orderList != null && orderList.size() > 0) {
			List<String> sqlList = new ArrayList<String>();
			for (Order order : orderList) {
				if (order.getIsUrge() == 1) {
					sqlList.add("update tb_order set isUrge=1 where  ( issynchronization=0 or issynchronization is null)  and userno='" + order.getUserNo() + "' and logisticid='" + order.getLogisticid() + "'");
				} else if (order.getIsUrge() == 2) {
					sqlList.add("delete from tb_order where  ( issynchronization=0 or issynchronization is null)  and userno='" + order.getUserNo() + "' and logisticid='" + order.getLogisticid() + "'");
				}
			}
			if (sqlList.size() > 0) {
				count = dataBaseManager.executeSqlByTran(sqlList);
			}
		}
		return count;
	}

	/**
	 * 查找未处理的订单
	 * 
	 * @param userNo
	 * @return
	 */
	public List<Order> getOrderList(String userNo) {
		String[] words = { " and userno=? and (issynchronization=0 or issynchronization is null) order by isUrge desc,logisticid desc " };
		String[] args = { userNo };
		List<Object> objList = getListObj(words, args, new Order());
		List<Order> orderList = null;
		if (objList != null && objList.size() > 0) {
			orderList = new ArrayList<Order>();
			for (Object obj : objList) {
				orderList.add((Order) obj);
			}
		}
		return orderList;
	}

	@Override
	public String getLastTime() {
		String lasttime = getLastTime("");
		return lasttime;
	}

	@Override
	public String getLastTime(String condition) {
		String sql = "select Max(acceptDate) from " + tableName;
		if (!strUtil.isNullOrEmpty(condition)) {
			if (condition.startsWith("where")) {
				sql += " " + condition;
			} else {
				sql += " where " + condition;
			}
		}
		String lasttime = dataBaseManager.querySingleData(sql, null);
		if (strUtil.isNullOrEmpty(lasttime)) {
			lasttime = "2000-01-01 00:00:00";
		}
		return lasttime;
	}

	public String getLastTime(String userNo, String synStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append(" where userNo='");
		sb.append(userNo);
		sb.append("'");
		if (!strUtil.isNullOrEmpty(synStatus)) {
			sb.append(" and issynchronization='");
			sb.append(synStatus);
			sb.append("'");
		}
		return getLastTime(sb.toString());
	}

	public String getMinAcceptTime(String userNo) {
		String sql = "select Min(acceptDate) from " + tableName;
		if (!strUtil.isNullOrEmpty(userNo)) {
			sql += "  where userNo='" + userNo + "' and issynchronization='0'";
		}
		String lasttime = dataBaseManager.querySingleData(sql, null);
		if (strUtil.isNullOrEmpty(lasttime)) {
			lasttime = "2000-01-01 00:00:00";
		}
		return lasttime;
	}

	/**
	 * 未处理订单数量
	 * 
	 * @param userNo
	 * @return
	 */
	public int getOrderCount(String userNo) {
		int count = 0;
		String sql = "select count(logisticid) from tb_order where  ( issynchronization=0 or issynchronization is null)  and userno=?";
		String[] selectionArgs = { userNo };
		String countStr = dataBaseManager.querySingleData(sql, selectionArgs);
		if (!strUtil.isNullOrEmpty(countStr)) {
			count = Integer.parseInt(countStr);
		}
		return count;
	}

	/**
	 * 清除过期订单
	 * 
	 * @return
	 */
	public int clearlOutDataOrder(String userNo) {
		int count = 0;
		String sql = "delete from tb_order where datetime('now','localtime','-7 day')>datetime(acceptdate) and userno=? and acceptdate not in(select Max(acceptdate) from tb_order where userno=?)";
		String[] bindArgs = { userNo, userNo };
		try {
			dataBaseManager.deleteDataBySql(sql, bindArgs);
			count = 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

}
