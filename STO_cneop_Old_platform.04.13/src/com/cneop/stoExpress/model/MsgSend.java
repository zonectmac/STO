package com.cneop.stoExpress.model;


public class MsgSend {

	private String lasttime;
	private String issynchronization;
	private String scanTime;
	private String serverNo;

	private String stationId;
	private String scanUser;
	private String phone;
	private int id;
	private String barcode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * barcode
	 */

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * phone
	 */

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * stationId
	 */

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	/**
	 * serverNo
	 */

	public String getServerNo() {
		return serverNo;
	}

	public void setServerNo(String serverNo) {
		this.serverNo = serverNo;
	}

	public String getScanUser() {
		return scanUser;
	}

	public void setScanUser(String scanUser) {
		this.scanUser = scanUser;
	}

	/**
	 * scantime
	 */

	public String getScanTime() {
		return scanTime;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	/**
	 * issynchronization
	 */

	public String getIssynchronization() {
		return issynchronization;
	}

	public void setIssynchronization(String issynchronization) {
		this.issynchronization = issynchronization;
	}

	/**
	 * lasttime
	 */

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

}
