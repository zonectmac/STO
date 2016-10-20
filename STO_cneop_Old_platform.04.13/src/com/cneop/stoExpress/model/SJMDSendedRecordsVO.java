package com.cneop.stoExpress.model;

/**
 * 收件面单发放记录(业务员,扫描员) 
 * * @author Administrator
 * 
 */
public class SJMDSendedRecordsVO {

	private String extenddate;
	private String sitename;
	private String billcodebegin;
	private String billcodeend;
	private String releasequantity;

	public String getExtenddate() {
		return extenddate;
	}

	public void setExtenddate(String extenddate) {
		this.extenddate = extenddate;
	}

	public String getSitename() {
		return sitename;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	public String getBillcodebegin() {
		return billcodebegin;
	}

	public void setBillcodebegin(String billcodebegin) {
		this.billcodebegin = billcodebegin;
	}

	public String getBillcodeend() {
		return billcodeend;
	}

	public void setBillcodeend(String billcodeend) {
		this.billcodeend = billcodeend;
	}

	public String getReleasequantity() {
		return releasequantity;
	}

	public void setReleasequantity(String releasequantity) {
		this.releasequantity = releasequantity;
	}

}
