package com.cneop.stoExpress.datacenter.msd;

import com.cneop.stoExpress.datacenter.AESEncrypt;

/**
 * ´«Êä²ÎÊý
 * 
 * @author Administrator
 * 
 */
public class TransferParam {

	private String enterpriseID;
	private String stationID;
	private String pdaId;
	private String md5 = "";
	private String version;
	private boolean defaultLogic = true;
	private boolean compress = false;
	private int fileType = 0;
	private int dbType = 0;

	public String getEnterpriseID() {
		return enterpriseID;
	}

	public void setEnterpriseID(String enterpriseID) {
		this.enterpriseID = enterpriseID;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getPdaId() {
		return pdaId;
	}

	public void setPdaId(String pdaIdD) {
		this.pdaId = pdaIdD;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isDefaultLogic() {
		return defaultLogic;
	}

	public void setDefaultLogic(boolean defaultLogic) {
		this.defaultLogic = defaultLogic;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public int getDbType() {
		return dbType;
	}

	public void setDbType(int dbType) {
		this.dbType = dbType;
	}

	public String ToJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"EnterpriseID\":");
		sb.append("\"").append(getEnterpriseID()).append("\",");
		sb.append("\"StationID\":");
		sb.append("\"").append(getStationID()).append("\",");
		sb.append("\"PDAID\":");
		sb.append("\"").append(getPdaId()).append("\",");
		sb.append("\"MD5\":");
		sb.append("\"").append(getMd5()).append("\",");
		sb.append("\"Version\":");
		sb.append("\"").append(getVersion()).append("\",");
		sb.append("\"DefaultLogic\":");
		sb.append(isDefaultLogic()).append(",");
		sb.append("\"Compress\":");
		sb.append(isCompress()).append(",");
		sb.append("\"FileType\":");
		sb.append(getFileType());
		sb.append("}");
		System.out.println("==========sb \t" + sb.toString());
		return AESEncrypt.encrypt(sb.toString().trim());
	}
}
