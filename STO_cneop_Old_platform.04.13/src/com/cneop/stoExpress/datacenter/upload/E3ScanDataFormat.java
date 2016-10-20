package com.cneop.stoExpress.datacenter.upload;

import com.cneop.util.DateUtil;
import com.cneop.util.StrUtil;

class E3ScanDataFormat {
	StrUtil strUtil;

	public E3ScanDataFormat() {
		strUtil = new StrUtil();
	}

	private String scanCode = ""; // 扫描代码
	private String code = ""; // 编号
	private String scanTime = ""; // 扫描日期
	private String sampleType = ""; // 物品类型
	private String expressType = ""; // 快件类型
	private String barcode = ""; // 单号
	private String scanUser = ""; // 扫描人编号
	private String operDate = ""; // 操作日期
	private String shift = ""; // 班次
	private String abnormalCode = ""; // 问题件/路由号/目的地编号
	private String cashOfCollection = ""; // 代收款
	private String vehicleNum = ""; // 车辆ID
	private String signer = ""; // 签收人
	private String weight = ""; // 重量
	private String phone = ""; // 手机号
	private String deviceId = ""; // 巴枪ID

	public String getScanCode() {
		return strUtil.padLeft(scanCode, 2);
	}

	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	public String getCode() {
		return strUtil.padLeft(code, 14);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getScanTime() {
		String scanTimeStr = DateUtil.formatTimeStr(scanTime,"yyyyMMddHHmmss");
		return strUtil.padLeft(scanTimeStr, 14);
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	public String getSampleType() {
		return strUtil.padLeft(sampleType, 1);
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getExpressType() {
		return strUtil.padLeft(expressType, 1);
	}

	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}

	public String getBarcode() {
		return strUtil.padLeft(barcode, 16);
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getScanUser() {
		return strUtil.padLeft(scanUser, 15);
	}

	public void setScanUser(String scanUser) {
		this.scanUser = scanUser;
	}

	public String getOperDate() {
		return strUtil.padLeft(operDate, 8);
	}

	public void setOperDate(String operDate) {
		this.operDate = operDate;
	}

	public String getShift() {
		return strUtil.padLeft(shift, 1);
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getAbnormalCode() {
		return strUtil.padLeft(abnormalCode, 10);
	}

	public void setAbnormalCode(String abnormalCode) {
		this.abnormalCode = abnormalCode;
	}

	public String getCashOfCollection() {
		return strUtil.padLeft(cashOfCollection, 8);
	}

	public void setCashOfCollection(String cashOfCollection) {
		this.cashOfCollection = cashOfCollection;
	}

	public String getVehicleNum() {
		return strUtil.padLeft(vehicleNum, 10);
	}

	public void setVehicleNum(String vehicleNum) {
		this.vehicleNum = vehicleNum;
	}

	public String getSigner() {
		int strLength = signer.length();
		int GBCount = strUtil.getGBCount(signer);
		String singerT = signer;
		for (int i = 0; i < 14 - GBCount - strLength; i++) {
			singerT += " ";
		}
		return singerT;
	}

	public void setSigner(String signer) {
		this.signer = signer;
	}

	public String getWeight() {
		return strUtil.padLeft(weight, 8);
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getPhone() {
		return strUtil.padLeft(phone, 15);
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDeviceId() {
		return strUtil.padLeft(deviceId, 15);
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getScanCode());
		sb.append(getCode());
		sb.append(getScanTime());
		sb.append(getSampleType());
		sb.append(getExpressType());
		sb.append(getBarcode());
		sb.append(getScanUser());
		sb.append(getOperDate());
		sb.append(getShift());
		sb.append(getAbnormalCode());
		sb.append(getCashOfCollection());
		sb.append(getVehicleNum());
		sb.append(getSigner());
		sb.append(getWeight());
		sb.append(getPhone());
		sb.append(getDeviceId());
		return sb.toString().trim();
	}
}
