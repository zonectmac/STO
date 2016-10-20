package com.cneop.stoExpress.common;

public class Enums {

	public enum EAddThreadOpt {
		RepeatWhenAdd(1), // ����ʱ�����ظ���
		SJMD(2), // �ռ�ʱ�����Ų����ռ��浥���ͼ�¼֮��
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
	 * ͼƬ����
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
	 * ע����
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
	 * ���ؽ��
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
	 * ���ر���
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EDownTableName {
		tab_siteinfo("StationXml"), // վ��
		tab_problem_type_new("AbnormalXml"), // �����
		tab_employee("UserXml"), // Ա��
		tab_routing("RouteXml"), // ·��
		tab_nextstation("NextStationXml"), // ��һվ
		tab_returnreason("OrderAbnormalXml"), // ���ԭ��
		tab_province("DestinationXml"); // ʡ��(Ŀ�ĵ�)

		private String value = "";

		private EDownTableName(String value) {

			this.value = value;
		}

		public String valueOf() {

			return this.value;
		}
	}

	/*
	 * �����ɫ
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
	 * ��������
	 * 
	 * @author Administrator
	 * 
	 */
	public enum RequestOp {
		track, // �����ѯ
		getCallCenterOrderByID, // ���ض���
		getOrderStatus, // ���شߴٺ�ȡ��
		acceptCallCenterOrder, // ��ȡ����
		notAcceptCallCenterOrder // ��ض���
	}

	/*
	 * �û���ɫ
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
	 * �ϴ����
	 */
	public enum EUploadResult {
		ok, // �ɹ�
		fail, // ʧ��
		neterror, // �����쳣
		none
	}

	/*
	 * �ϴ�����
	 */
	public enum EUploadType {
		scanData, // ɨ������
		pic, // ͼƬ
		msg, // ����
		order, // ����
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
	 * ��������
	 */
	public enum EAddType {
		scandata(1), // ɨ������
		msg(2), // ��������

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
		Station, // վ��
		NextStation, // ��һվ
		Route, // ·��
		User, // �û�
		Abnormal, // ������쳣
		Destination, // Ŀ�ĵ�
		ServerStatoin, // ���ŷ���վ��
		OrderAbnormal, // �����쳣

		Province, // ѡ��������õ�
		City, Area,
	}

	/*
	 * ��������
	 */
	public enum EDownType {
		Station, // վ��
		NextStation, // ��һվ
		Route, // ·��
		User, // �û�
		Abnormal, // ������쳣
		Destination, // Ŀ�ĵ�
		ServerStatoin, // ���ŷ���վ��
		OrderAbnormal, // �����쳣
		DownSJMDRecords, // �����ռ��浥���ͼ�¼

		AreaQuery, // ����
		Time, // ͬ��ʱ��
	}

	/**
	 * ����״̬
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EOrderStatus {
		accept(0), // ��ȡ
		noAccept(1); // ���

		private int value;

		private EOrderStatus(int value) {

			this.value = value;
		}

		public int value() {

			return this.value;
		}
	}

	/*
	 * �ļ�վ������
	 */
	public enum ESiteProperties {
		C_N(1), // ����.����
		C_H(2), // ����
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
	 * ��������
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
		SJ("01"), // �ռ�
		FJ("02"), // ����
		DJ("03"), // ����
		PJ("04"), // �ɼ�
		LC("08"), // ����
		YN("07"), // �����
		QS("99"), // ǩ��
		ZD("05"), // װdai
		ZB("05"), // װ��
		FB("33"), // ����
		DB("34"), // ����
		ZC("35"), // װ��
		FC("36"), // ����
		DC("37"), // ����
		ALL(""), OTHER("00");

		// FWDX //����
		// Order //����
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
	 * ϵͳ����
	 */
	public enum ESysConfig {
		stationId, // վ����
		scanMode, // ɨ��ģʽ
		// uploadMode, // �ϴ�ģʽ
		serviceIp, // �ϴ���������ַ
		companyCode, // ��˾����
		upgradeIp, // ���µ�ַ
		programRole, // �����ɫ
		allowSelectOper, // ����ѡ�����Ա
		smsInfo, // �������Ϣ����
		smsSend, // ���ŷ���
		imageUpload, // ͼƬ��
		autoUploadTimeSpilt, // �Զ��ϴ���ʱ����
		upgradeCompanyCode, // ����ʱ����ҵCode
		MdDownloadUrl, // �ռ��浥���ص�ַ
		MdControlIsOpen, // �ռ��浥�ܿ��Ƿ���
		LastDownMDTime, // ���һ�������浥��ʱ��
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
	 * �˵�����
	 */
	public enum EBarcodeType {
		barcode, // ����
		packageNo, // ����
		carLotNumber, // ��ǩ��
		vehicleId, // ����ID
		phoneNum, // �ֻ���
		tel// �绰����
	}

	/*
	 * ��������
	 */
	public enum EGoodsType {
		����("1"), �ǻ���("2"), other("0");

		private String value = "0";

		private EGoodsType(String value) {

			this.value = value;
		}

		public String value() {

			return this.value;
		}
	}

}
