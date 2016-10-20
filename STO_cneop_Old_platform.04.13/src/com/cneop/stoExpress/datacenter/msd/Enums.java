package com.cneop.stoExpress.datacenter.msd;

public class Enums {

	/**
	 * ע����
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
	 * ���ؽ��
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EDownError {
		netError, paramError, unRegisterError, otherError, noError
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
		
		private String value="";
		
		private EDownTableName(String value){
			this.value = value;
		}
		
		public String valueOf(){
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
}
