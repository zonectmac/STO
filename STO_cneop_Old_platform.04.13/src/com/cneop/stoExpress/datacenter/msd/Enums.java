package com.cneop.stoExpress.datacenter.msd;

public class Enums {

	/**
	 * 注册结果
	 * 
	 * @author Administrator
	 * 
	 */
	public enum ERegResponse {
		netError(0), paramError(1), success(2), registered(3), other(4);

		private int value;

		private ERegResponse(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	/**
	 * 下载结果
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EDownError {
		netError, paramError, unRegisterError, otherError, noError
	}

	/**
	 * 下载表名
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EDownTableName {
		tab_siteinfo("StationXml"), // 站点
		tab_problem_type_new("AbnormalXml"), // 问题件
		tab_employee("UserXml"), // 员工
		tab_routing("RouteXml"), // 路由
		tab_nextstation("NextStationXml"), // 下一站
		tab_returnreason("OrderAbnormalXml"), // 打回原因
		tab_province("DestinationXml"); // 省份(目的地)
		
		private String value="";
		
		private EDownTableName(String value){
			this.value = value;
		}
		
		public String valueOf(){
			return this.value;
		}
	}

	/**
	 * 请求类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum RequestOp {
		track, // 快件查询
		getCallCenterOrderByID, // 下载订单
		getOrderStatus, // 下载催促和取消
		acceptCallCenterOrder, // 提取订单
		notAcceptCallCenterOrder // 打回订单
	}
}
