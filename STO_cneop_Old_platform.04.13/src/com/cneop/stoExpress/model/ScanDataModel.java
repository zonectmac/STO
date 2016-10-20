package com.cneop.stoExpress.model;

public class ScanDataModel {

	private int id;
	private String barcode = ""; // ����
	private String scanUser = ""; // ɨ���û�
	private String scanStation = ""; // ɨ��վ��
	private String sampleType = ""; // ��������
	private String shiftCode = ""; // ���
	private String destinationSiteCode = ""; // Ŀ�ĵ�վ�����
	private String senderPhone = ""; // �ķ��ֻ�
	private String recipientPhone = ""; // �շ��ֻ�
	private String routeCode = ""; // ·�ɴ���
	private String preStationCode = ""; // ��һվ�����
	private String nextStationCode = ""; // ��һվ�����
	private String secondStationCode = ""; // ����վ�����
	private String vehicleNumber = ""; // �������
	private String weight = ""; // ����
	private String courier = ""; // ҵ����Ա����
	private String packageCode = ""; // ����/����
	private String abnormalCode = ""; // �쳣����
	private String abnormalDesc = ""; // �쳣����
	private String carLotNumber = ""; // ��ǩ��
	private String flightCode = ""; // �����
	private String signTypeCode = ""; // ǩ�����ʹ���
	private String signer = ""; // ǩ����
	private String siteProperties = ""; // վ������
	private String expressType = ""; // �������
	private String scanCode = ""; // ɨ����� ��Ӧ EScanType�е�ֵ(wm)
	private String scanTime = ""; // ɨ��ʱ��
	private String issynchronization = ""; // ͬ��״̬
	private String lasttime = ""; // �ϴ�ʱ��

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
