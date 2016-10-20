package com.cneop.stoExpress.datacenter.upload;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.widget.Adapter;

import com.cneop.stoExpress.activity.scan.BaggingOutActivity;
import com.cneop.util.StrUtil;

/**
 * 扫描数据上传格式
 * 
 * @author Administrator
 * 
 */
@SuppressLint("DefaultLocale")
public class UpContentStructure {

	private List<String> dataList; // 扫描数据
	private List<String> dataList_FJ; // 发件
	private List<String> dataList_FB; // 发件

	private String scanType; // 扫描类型
	private String expressType; // 快件类型
	private StringBuilder sb;
	private StringBuilder sb_FJ;
	private StringBuilder sb_FB;
	StrUtil strUtil;
	public static String ZBFJ_ = "";
	public static String ZBFj_ = "";
	public static String ZBFB_ = "";
	
	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public void setDataList_FJ(List<String> dataList) {
		this.dataList_FJ = dataList;
	}

	public void setDataList_FB(List<String> dataList) {
		this.dataList_FB = dataList;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType.toUpperCase();
	}

	public String getScanType() {
		return this.scanType;
	}

	@SuppressLint("DefaultLocale")
	public void setExpressType(String expressType) {
		this.expressType = expressType.toUpperCase();
	}

	public UpContentStructure() {
		strUtil = new StrUtil();
		sb = new StringBuilder();
		sb.append("<XML>");

		sb_FJ = new StringBuilder();
		sb_FJ.append("<XML>");

		sb_FB = new StringBuilder();
		sb_FB.append("<XML>");
	}

	/**
	 * 自动匹配上传数据类型
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
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
		// -------------------------------------------------------------
		for (String data : dataList) {
			sb.append("<Row>");
			sb.append(data);
			sb.append("</Row>");
		}
		sb.append("</ExpressType>");
		sb.append("</ScanType>");
		sb.append("</XML>");
		System.out.println("=================sb \t" + sb.toString());
		return sb.toString().trim();
	}

	StringBuffer hkfj = new StringBuffer();

	@SuppressWarnings("static-access")
	public String getUploadStructure_hkfj() {
		if (strUtil.isNullOrEmpty(this.scanType)) {
			return "";
		}
		if (strUtil.isNullOrEmpty(this.expressType)) {
			return "";
		}
		if (dataList_FJ == null || dataList_FJ.size() == 0) {
			return "";
		}
		sb_FJ.append("<ScanType Name=\"").append("FJ").append("\">");
		sb_FJ.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (String data : dataList_FJ) {
			sb_FJ.append("<Row>");
			sb_FJ.append(data);
			sb_FJ.append("</Row>");			
		}
		sb_FJ.append("</ExpressType>");
		sb_FJ.append("</ScanType>");
		sb_FJ.append("</XML>");
		System.out.println("=================sb \t" + sb_FJ.toString());
		return sb_FJ.toString().trim();
	}

	StringBuffer hkfb = new StringBuffer();

	public String getUploadStructure_hkfb() {
		if (strUtil.isNullOrEmpty(this.scanType)) {
			return "";
		}
		if (strUtil.isNullOrEmpty(this.expressType)) {
			return "";
		}
		if (dataList_FB == null || dataList_FB.size() == 0) {
			return "";
		}
		sb_FB.append("<ScanType Name=\"").append("FB").append("\">");
		sb_FB.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (String data : dataList_FB) {
			sb_FB.append("<Row>");
			sb_FB.append(data);
			sb_FB.append("</Row>");
		}
		sb_FB.append("</ExpressType>");
		sb_FB.append("</ScanType>");
		sb_FB.append("</XML>");
		System.out.println("=================sb \t" + sb_FB.toString());
		return sb_FB.toString().trim();
	}

	// --------------------------------------------------------------------------------------
	StringBuffer fj = new StringBuffer();

	public String fj() {
		fj.append("<XML>");
		fj.append("<ScanType Name=\"").append("FJ").append("\">");
		fj.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (int i = 0; i < dataList.size(); i++) {
			fj.append("<Row>");
			fj.append("02" + BaggingOutActivity.xiayizhan + "      " + dataList.get(i).toString().substring(14));
			fj.append("</Row>");
			// ----------------------------------------------------------------------------
			fj.append("<Row>");
			fj.append("02" + BaggingOutActivity.xiayizhan + "      " + dataList.get(i).subSequence(14, 30) + " 0" + BaggingOutActivity.daihao + "  " + dataList.get(i).substring(46));
			fj.append("</Row>");
		}
		fj.append("</ExpressType>");
		fj.append("</ScanType>");
		fj.append("</XML>");
		System.out.println("===============fj \t" + fj.toString());
		return fj.toString().trim();
	}

	// -----------------------装车发件
	// --------------------------------------------------------------------------------------
	StringBuffer zcfj = new StringBuffer();

	public String zcfj() {
		zcfj.append("<XML>");
		zcfj.append("<ScanType Name=\"").append("FJ").append("\">");
		zcfj.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (int i = 0; i < dataList.size(); i++) {
			zcfj.append("<Row>");
			zcfj.append(ZBFB);
			zcfj.append("</Row>");
			// ----------------------------------------------------------------------------
			zcfj.append("<Row>");
			zcfj.append(ZBFJ);
			zcfj.append("</Row>");
		}
		zcfj.append("</ExpressType>");
		zcfj.append("</ScanType>");
		zcfj.append("</XML>");
		System.out.println("===============fj \t" + fj.toString());
		return zcfj.toString().trim();
	}

	// ---------------------------------------------------------------

	StringBuffer ZBFJ = new StringBuffer();

	public String ZBFJ() {
		ZBFJ.append("<XML>");
		ZBFJ.append("<ScanType Name=\"").append("FJ").append("\">");
		ZBFJ.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (int i = 0; i < dataList.size(); i++) {
			ZBFJ.append("<Row>");
			ZBFJ.append(ZBFJ_);
			ZBFJ.append("</Row>");
			// ----------------------------------------------------------------------------
			ZBFJ.append("<Row>");
			ZBFJ.append(ZBFj_);
			ZBFJ.append("</Row>");
		}
		ZBFJ.append("</ExpressType>");
		ZBFJ.append("</ScanType>");
		ZBFJ.append("</XML>");
		return ZBFJ.toString().trim();
	}

	// ------------------------------------------------------------
	StringBuffer ZBFB = new StringBuffer();

	public String ZBFB() {
		ZBFB.append("<XML>");
		ZBFB.append("<ScanType Name=\"").append("FB").append("\">");
		ZBFB.append("<ExpressType Name=\"").append(expressType).append("\">");
		// -------------------------------------------------------------
		for (int i = 0; i < dataList.size(); i++) {
			ZBFB.append("<Row>");
			ZBFB.append(ZBFB_);
			ZBFB.append("</Row>");
		}
		ZBFB.append("</ExpressType>");
		ZBFB.append("</ScanType>");
		ZBFB.append("</XML>");
		return ZBFB.toString().trim();
	}

}
