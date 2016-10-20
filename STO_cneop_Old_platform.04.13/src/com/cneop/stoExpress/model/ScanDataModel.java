package com.cneop.stoExpress.model;

public class ScanDataModel {

	private int id;
	private String barcode = ""; // 单号
	private String scanUser = ""; // 扫描用户
	private String scanStation = ""; // 扫描站点
	private String sampleType = ""; // 货样类型
	private String shiftCode = ""; // 班次
	private String destinationSiteCode = ""; // 目的地站点代码
	private String senderPhone = ""; // 寄方手机
	private String recipientPhone = ""; // 收方手机
	private String routeCode = ""; // 路由代码
	private String preStationCode = ""; // 上一站点代码
	private String nextStationCode = ""; // 下一站点代码
	private String secondStationCode = ""; // 二级站点代码
	private String vehicleNumber = ""; // 车辆编号
	private String weight = ""; // 重量
	private String courier = ""; // 业务人员代码
	private String packageCode = ""; // 包号/袋号
	private String abnormalCode = ""; // 异常代码
	private String abnormalDesc = ""; // 异常描述
	private String carLotNumber = ""; // 车签号
	private String flightCode = ""; // 航班号
	private String signTypeCode = ""; // 签收类型代码
	private String signer = ""; // 签收人
	private String siteProperties = ""; // 站点属性
	private String expressType = ""; // 快件类型
	private String scanCode = ""; // 扫描代码 对应 EScanType中的值(wm)
	private String scanTime = ""; // 扫描时间
	private String issynchronization = ""; // 同步状态
	private String lasttime = ""; // 上传时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	//
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getScanUser() {
		return scanUser;
	}

	public void setScanUser(String scanUser) {
		this.scanUser = scanUser;
	}

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

	public String getScanStation() {
		return scanStation;
	}

	public void setScanStation(String scanStation) {
		this.scanStation = scanStation;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getDestinationSiteCode() {
		return destinationSiteCode;
	}

	public void setDestinationSiteCode(String destinationSiteCode) {
		this.destinationSiteCode = destinationSiteCode;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public void setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public String getPreStationCode() {
		return preStationCode;
	}

	public void setPreStationCode(String preStationCode) {
		this.preStationCode = preStationCode;
	}

	public String getNextStationCode() {
		return nextStationCode;
	}

	public void setNextStationCode(String nextStationCode) {
		this.nextStationCode = nextStationCode;
	}

	public String getSecondStationCode() {
		return secondStationCode;
	}

	public void setSecondStationCode(String secondStationCode) {
		this.secondStationCode = secondStationCode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getCourier() {
		return courier;
	}

	public void setCourier(String courier) {
		this.courier = courier;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getAbnormalCode() {
		return abnormalCode;
	}

	public void setAbnormalCode(String abnormalCode) {
		this.abnormalCode = abnormalCode;
	}

	public String getAbnormalDesc() {
		return abnormalDesc;
	}

	public void setAbnormalDesc(String abnormalDesc) {
		this.abnormalDesc = abnormalDesc;
	}

	public String getCarLotNumber() {
		return carLotNumber;
	}

	public void setCarLotNumber(String carLotNumber) {
		this.carLotNumber = carLotNumber;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public String getSignTypeCode() {
		return signTypeCode;
	}

	public void setSignTypeCode(String signTypeCode) {
		this.signTypeCode = signTypeCode;
	}

	public String getSigner() {
		return signer;
	}

	public void setSigner(String signer) {
		this.signer = signer;
	}

	public String getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(String siteProperties) {
		this.siteProperties = siteProperties;
	}

	public String getExpressType() {
		return expressType;
	}

	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}

	public String getScanCode() {
		return scanCode;
	}

	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	public String getScanTime() {
		return scanTime;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	public String getIssynchronization() {
		return issynchronization;
	}

	public void setIssynchronization(String issynchronization) {
		this.issynchronization = issynchronization;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
}
