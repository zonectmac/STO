package com.cneop.util.model;

public class Enums {
	public enum ESessionIDType{
		JSESSIONID, //jsp ��session ��ID ����
		PHPSESSID   //php
		
	}
	public enum ENetworkType{
		Wifi,
		GPRS,
		None
	}
	
	/*
	 * �˵�����
	 */
	public enum EBarcodeType {
		barcode("CODE0001"), // ����
		packageNo("CODE0002"), // ����
		carLotNumber("CODE0003"), // ��ǩ��
		QFNumber("CODE0004"), // Ǧ������� (���ǵ�ϵͳ����,ֻ���˳�ǩ��,û��Ǧ���)
		netSiteNum("CODE0005"), // ������
		empNum("CODE0006"), // ְԱ����
		routeNum("CODE0007"), // ·�ɺ�����
		areaNum("CODE0008"), // ������������
		vehicleId("0000"); // ����ID

		public String value;

		EBarcodeType(String value) {
			this.value = value;
		}
	}
	
	
	
	
	
	
	// ==============================�������ϴ�״̬=======================================
	/**
	 * �������ϴ�״̬
	 * 
	 * @author Martin
	 * 
	 */
	public enum EToolbarUploadStatus {
		Downing(-1), Normal(0), Uploading(1), DownAndUploading(2); // ���ù��캯��������ö����

		private int value = 0;

		private EToolbarUploadStatus(int value) { // ������private�ģ�����������
			this.value = value;
		}

		public static EToolbarUploadStatus valueOf(int value) { // ��д�Ĵ�int��enum��ת������
			switch (value) {
			case -1:
				return Downing;
			case 0:
				return Normal;
			case 1:
				return Uploading;
			case 2:
				return DownAndUploading;
			default:
				return null;
			}
		}

		public int value() {
			return this.value;
		}
	}
	/**
	 * ɨ��ģʽ
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EScanModel {
		single, continueModel
	}
}
