package com.cneop.stoExpress.model;

import android.R.string;
import android.text.method.DateTimeKeyListener;

public class QueryView {
	private String moduleName;
	private String scanType;
	private String scanTime="";
	private int totalDataCount;
	private int unUpload;
	private String scanCode = "00";
	private String siteProperties = "0";
	private String uploadType;
	
	

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public String getScanType() {
		return scanType;
	}
	public String getScanTime() {
		return scanTime;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}
	public String getScanCode() {
		return scanCode;
	}

	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	public String getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(String siteProperties) {
		this.siteProperties = siteProperties;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getTotalDataCount() {
		return totalDataCount;
	}

	public void setTotalDataCount(int totalDataCount) {
		this.totalDataCount = totalDataCount;
	}

	public int getUnUpload() {
		return unUpload;
	}

	public void setUnUpload(int unUpload) {
		this.unUpload = unUpload;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

}
