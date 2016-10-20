package com.cneop.util.model;

public class Enums {
	public enum ESessionIDType{
		JSESSIONID, //jsp 的session 的ID 名称
		PHPSESSID   //php
		
	}
	public enum ENetworkType{
		Wifi,
		GPRS,
		None
	}
	
	/*
	 * 运单类型
	 */
	public enum EBarcodeType {
		barcode("CODE0001"), // 单号
		packageNo("CODE0002"), // 包号
		carLotNumber("CODE0003"), // 车签号
		QFNumber("CODE0004"), // 铅封号条码 (我们的系统里面,只做了车签号,没做铅封号)
		netSiteNum("CODE0005"), // 网点编号
		empNum("CODE0006"), // 职员条码
		routeNum("CODE0007"), // 路由号条码
		areaNum("CODE0008"), // 行政区域条码
		vehicleId("0000"); // 车辆ID

		public String value;

		EBarcodeType(String value) {
			this.value = value;
		}
	}
	
	
	
	
	
	
	// ==============================工具栏上传状态=======================================
	/**
	 * 工具栏上传状态
	 * 
	 * @author Martin
	 * 
	 */
	public enum EToolbarUploadStatus {
		Downing(-1), Normal(0), Uploading(1), DownAndUploading(2); // 调用构造函数来构造枚举项

		private int value = 0;

		private EToolbarUploadStatus(int value) { // 必须是private的，否则编译错误
			this.value = value;
		}

		public static EToolbarUploadStatus valueOf(int value) { // 手写的从int到enum的转换函数
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
	 * 扫描模式
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EScanModel {
		single, continueModel
	}
}
