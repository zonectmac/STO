package com.cneop.stoExpress.datacenter.msd;

import java.util.List;
import java.util.Locale;

import com.cneop.util.StrUtil;


/**
 * ɨ�������ϴ���ʽ
 * @author Administrator
 *
 */
public class UpContentStructure {

	private List<String> dataList; // ɨ������
	private String scanType; // ɨ������
	private String expressType; // �������
	private StringBuilder sb;
	StrUtil strUtil;

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType.toUpperCase();
	}

	public void setExpressType(String expressType) {
		this.expressType = expressType.toUpperCase();
	}

	public UpContentStructure() {
		strUtil = new StrUtil();
		sb = new StringBuilder();
		sb.append("<XML>");
	}

	public String getUploadStructure() {
		if (strUtil.isNullOrEmpty(this.scanType)) {
			return "";
		}
		if (strUtil.isNullOrEmpty(this.expressType)) {
			return "";
		}
		if (dataList == null || dataList.size() == 0) {
			return "";
		}
		sb.append("<ScanType Name=\"").append(scanType).append("\">");
		sb.append("<ExpressType Name=\"").append(expressType).append("\">");
		for (String data : dataList) {
			sb.append("<Row>");
			sb.append(data);
			sb.append("</Row>");
		}
		sb.append("</ExpressType>");
		sb.append("</ScanType>");
		sb.append("</XML>");
		return sb.toString().trim();
	}

}
