package com.cneop.stoExpress.model;

/**
 * �ϴ����洫�ݶ���
 * 
 * @author Administrator
 * 
 */
public class UploadView {

	private String scanType = "";
	private int totalCount = 0;
	private boolean selected = true;
	private String uploadType = "";//ɨ������
	private String scanTypeStr = "";//ɨ�����͵�����ɨ��

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public String getScanTypeStr() {
		return scanTypeStr;
	}

	public void setScanTypeStr(String scanTypeStr) {
		this.scanTypeStr = scanTypeStr;
	}



	public String getScanType() {
		return scanType;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
