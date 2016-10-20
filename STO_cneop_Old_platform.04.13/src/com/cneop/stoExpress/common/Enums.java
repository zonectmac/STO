package com.cneop.stoExpress.common;

public class Enums {

	public enum EAddThreadOpt {
		RepeatWhenAdd(1), // 增加时数据重复了
		SJMD(2), // 收件时，单号不在收件面单发送记录之内
		Other(0);

		private int value;

		EAddThreadOpt(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}

		public static EAddThreadOpt valueOf(int v) {

			switch (v) {
			case 1:
				return RepeatWhenAdd;
			case 2:
				return SJMD;
			default:
				return Other;
			}
		}

	}

	/**
	 * 图片类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EPicType {
		sign(1), problem(2), other(3);

		private int value;

		private EPicType(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}

		public static EPicType valueOf(int value) {

			switch (value) {
			case 1:
				return sign;
			case 2:
				return problem;
			default:
				return other;
			}
		}

		public static EPicType getEnum(String value) {

			if (value.equalsIgnoreCase("sign")) {
				return sign;
			} else if (value.equalsIgnoreCase("problem")) {
				return problem;
			}
			return other;
		}
	}

	/**
	 * 注册结果
	 * 
	 * @author Administrator
	 * 
	 */
	public enum ERegResponse {
		netError(0), paramError(1), success(2), registered(3), other(4), fail(5);

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

	public enum EDbSelectOrUpdate {
		select, update, none
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

		private String value = "";

		private EDownTableName(String value) {

			this.value = value;
		}

		public String valueOf() {

			return this.value;
		}
	}

	/*
	 * 程序角色
	 */
	public enum EProgramRole {
		station(1), center(2), air(3), other(0);

		private int value;

		private EProgramRole(int value) {

			this.value = value;
		}

		public int value() {

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

	/*
	 * 用户角色
	 */
	public enum EUserRole {
		business(1), scaner(2), centerScaner(3), airScaner(4), ariDelivery(5), other(6);

		private int value;

		private EUserRole(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}

		public static EUserRole valueof(int value) {

			switch (value) {
			case 1:
				return business;
			case 2:
				return scaner;
			case 3:
				return centerScaner;
			case 4:
				return airScaner;
			case 5:
				return ariDelivery;
			default:
				return other;
			}
		}
	}

	/*
	 * 上传结果
	 */
	public enum EUploadResult {
		ok, // 成功
		fail, // 失败
		neterror, // 网络异常
		none
	}

	/*
	 * 上传类型
	 */
	public enum EUploadType {
		scanData, // 扫描数据
		pic, // 图片
		msg, // 短信
		order, // 订单
		other;

		public static EUploadType getEnum(String value) {

			if (value.equalsIgnoreCase("scanData")) {
				return scanData;
			} else if (value.equalsIgnoreCase("pic")) {
				return pic;
			} else if (value.equalsIgnoreCase("msg")) {
				return msg;
			} else if (value.equalsIgnoreCase("order")) {
				return order;
			}
			return other;
		}
	}

	/*
	 * 增加类型
	 */
	public enum EAddType {
		scandata(1), // 扫描数据
		msg(2), // 短信数据

		other(0);

		private int value = 0;

		public int value() {

			return this.value;
		}

		private EAddType(int value) {

			this.value = value;
		}

		public static EAddType valueof(int value) {

			switch (value) {
			case 1:
				return scandata;
			case 2:
				return msg;
			case 0:
				return other;

			default:
				return null;
			}

		}

	}

	public enum ESelectPageItem {
		Station, // 站点
		NextStation, // 下一站
		Route, // 路由
		User, // 用户
		Abnormal, // 问题件异常
		Destination, // 目的地
		ServerStatoin, // 短信服务站点
		OrderAbnormal, // 订单异常

		Province, // 选择界面上用的
		City, Area,
	}

	/*
	 * 下载类型
	 */
	public enum EDownType {
		Station, // 站点
		NextStation, // 下一站
		Route, // 路由
		User, // 用户
		Abnormal, // 问题件异常
		Destination, // 目的地
		ServerStatoin, // 短信服务站点
		OrderAbnormal, // 订单异常
		DownSJMDRecords, // 下载收件面单发送记录

		AreaQuery, // 区域
		Time, // 同步时间
	}

	/**
	 * 订单状态
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EOrderStatus {
		accept(0), // 提取
		noAccept(1); // 打回

		private int value;

		private EOrderStatus(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}
	}

	/*
	 * 文件站点属性
	 */
	public enum ESiteProperties {
		C_N(1), // 网点.中心
		C_H(2), // 航空
		all(0);

		private int value = 0;

		public int value() {

			return this.value;
		}

		private ESiteProperties(int value) {

			this.value = value;
		}

		public static ESiteProperties valueOf(int value) {

			switch (value) {
			case 1:
				return C_N;
			case 2:
				return C_H;
			default:
				return all;
			}
		}

	}

	/**
	 * 声音类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EVoiceType {
		ok(1), fail(2), normal(3), neworder(4), unfinishorder(5), other(0);

		private int value;

		private EVoiceType(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}
	}

	public enum EScanType {
		SJ("01"), // 收件
		FJ("02"), // 发件
		DJ("03"), // 到件
		PJ("04"), // 派件
		LC("08"), // 留仓
		YN("07"), // 问题件
		QS("99"), // 签收
		ZD("05"), // 装dai
		ZB("05"), // 装包
		FB("33"), // 发包
		DB("34"), // 到包
		ZC("35"), // 装车
		FC("36"), // 发车
		DC("37"), // 到车
		ALL(""), OTHER("00");

		// FWDX //短信
		// Order //订单
		private String value = "";

		private EScanType(String value) {

			this.value = value;
		}

		public String value() {

			return this.value;
		}

		public static EScanType getEnum(String value, ESiteProperties siteProperties) {

			if (value.equalsIgnoreCase("SJ") || value.equalsIgnoreCase("01")) {
				return SJ;
			} else if (value.equalsIgnoreCase("FJ") || value.equalsIgnoreCase("02")) {
				return FJ;
			} else if (value.equalsIgnoreCase("DJ") || value.equalsIgnoreCase("03")) {
				return DJ;
			} else if (value.equalsIgnoreCase("PJ") || value.equalsIgnoreCase("04")) {
				return PJ;
			} else if (value.equalsIgnoreCase("LC") || value.equalsIgnoreCase("08")) {
				return LC;
			} else if (value.equalsIgnoreCase("YN") || value.equalsIgnoreCase("07")) {
				return YN;
			} else if (value.equalsIgnoreCase("QS") || value.equalsIgnoreCase("99")) {
				return QS;
			} else if (value.equalsIgnoreCase("ZB")
					|| (value.equalsIgnoreCase("05") && siteProperties == ESiteProperties.C_H)) {
				return ZB;
			} else if (value.equalsIgnoreCase("FB") || value.equalsIgnoreCase("33")) {
				return FB;
			} else if (value.equalsIgnoreCase("DB") || value.equalsIgnoreCase("34")) {
				return DB;
			} else if (value.equalsIgnoreCase("ZC") || value.equalsIgnoreCase("35")) {
				return ZC;
			} else if (value.equalsIgnoreCase("FC") || value.equalsIgnoreCase("36")) {
				return FC;
			} else if (value.equalsIgnoreCase("DC") || value.equalsIgnoreCase("37")) {
				return DC;
			} else if (value.equalsIgnoreCase("ZD")
					|| (value.equalsIgnoreCase("05") && siteProperties == ESiteProperties.C_N)) {
				return ZD;
			} else if (value.equalsIgnoreCase("OTHER") || value.equalsIgnoreCase("00")) {
				return OTHER;
			}
			return ALL;
		}
	}

	/*
	 * 系统配置
	 */
	public enum ESysConfig {
		stationId, // 站点编号
		scanMode, // 扫描模式
		// uploadMode, // 上传模式
		serviceIp, // 上传服务器地址
		companyCode, // 公司代码
		upgradeIp, // 更新地址
		programRole, // 程序角色
		allowSelectOper, // 允许选择操作员
		smsInfo, // 服务点信息下载
		smsSend, // 短信发送
		imageUpload, // 图片上
		autoUploadTimeSpilt, // 自动上传的时间间隔
		upgradeCompanyCode, // 升级时的企业Code
		MdDownloadUrl, // 收件面单下载地址
		MdControlIsOpen, // 收件面单管控是否开启
		LastDownMDTime, // 最后一次下载面单的时间
		other;

		public static ESysConfig getEnum(String value) {

			if (value != null) {
				if (value.equalsIgnoreCase("stationId")) {
					return stationId;
				} else if (value.equalsIgnoreCase("scanMode")) {
					return scanMode;
				} else if (value.equalsIgnoreCase("serviceIp")) {
					return serviceIp;
				} else if (value.equalsIgnoreCase("companyCode")) {
					return companyCode;
				} else if (value.equalsIgnoreCase("upgradeIp")) {
					return upgradeIp;
				} else if (value.equalsIgnoreCase("programRole")) {
					return programRole;
				} else if (value.equalsIgnoreCase("allowSelectOper")) {
					return allowSelectOper;
				} else if (value.equalsIgnoreCase("smsInfo")) {
					return smsInfo;
				} else if (value.equalsIgnoreCase("smsSend")) {
					return smsSend;
				} else if (value.equalsIgnoreCase("imageUpload")) {
					return imageUpload;
				} else if (value.equalsIgnoreCase("autoUploadTimeSpilt")) {
					return autoUploadTimeSpilt;
				} else if (value.equalsIgnoreCase("upgradeCompanyCode")) {
					return upgradeCompanyCode;
				} else if (value.equalsIgnoreCase("MdDownloadUrl")) {
					return MdDownloadUrl;
				} else if (value.equalsIgnoreCase("LastDownMDTime")) {
					return LastDownMDTime;
				} else if (value.equalsIgnoreCase("MdControlIsOpen")) {
					return MdControlIsOpen;
				}
			}
			return other;
		}
	}

	/*
	 * 运单类型
	 */
	public enum EBarcodeType {
		barcode, // 单号
		packageNo, // 包号
		carLotNumber, // 车签号
		vehicleId, // 车辆ID
		phoneNum, // 手机号
		tel// 电话号码
	}

	/*
	 * 货样类型
	 */
	public enum EGoodsType {
		货样("1"), 非货样("2"), other("0");

		private String value = "0";

		private EGoodsType(String value) {

			this.value = value;
		}

		public String value() {

			return this.value;
		}
	}

}
