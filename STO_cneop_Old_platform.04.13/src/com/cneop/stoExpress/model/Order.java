package com.cneop.stoExpress.model;

import java.io.Serializable;

public class Order  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * logisticid
	 */
	private String logisticid;

	public String getLogisticid() {
		return logisticid;
	}

	public void setLogisticid(String logisticid) {
		this.logisticid = logisticid;
	}

	/**
	 * userNo
	 */
	private String userNo;

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	/**
	 * acceptDate
	 */
	private String acceptDate;

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	/**
	 * customerCode
	 */
	private String customerCode;

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	/**
	 * customerName
	 */
	private String customerName;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * sender_Name
	 */
	private String sender_Name;

	public String getSender_Name() {
		return sender_Name;
	}

	public void setSender_Name(String sender_Name) {
		this.sender_Name = sender_Name;
	}

	/**
	 * sender_Address
	 */
	private String sender_Address;

	public String getSender_Address() {
		return sender_Address;
	}

	public void setSender_Address(String sender_Address) {
		this.sender_Address = sender_Address;
	}

	/**
	 * sender_Phone
	 */
	private String sender_Phone;

	public String getSender_Phone() {
		return sender_Phone;
	}

	public void setSender_Phone(String sender_Phone) {
		this.sender_Phone = sender_Phone;
	}

	/**
	 * sender_Mobile
	 */
	private String sender_Mobile;

	public String getSender_Mobile() {
		return sender_Mobile;
	}

	public void setSender_Mobile(String sender_Mobile) {
		this.sender_Mobile = sender_Mobile;
	}

	/**
	 * destcode
	 */
	private String destcode;

	public String getDestcode() {
		return destcode;
	}

	public void setDestcode(String destcode) {
		this.destcode = destcode;
	}

	/**
	 * issynchronization
	 */
	private String issynchronization;

	public String getIssynchronization() {
		return issynchronization;
	}

	public void setIssynchronization(String issynchronization) {
		this.issynchronization = issynchronization;
	}
	
	private String cusnote;

	public String getCusnote() {
		return cusnote;
	}

	public void setCusnote(String cusnote) {
		this.cusnote = cusnote;
	}
	
	private int isUrge;

	public int getIsUrge() {
		return isUrge;
	}

	public void setIsUrge(int isUrge) {
		this.isUrge = isUrge;
	}
	
}
