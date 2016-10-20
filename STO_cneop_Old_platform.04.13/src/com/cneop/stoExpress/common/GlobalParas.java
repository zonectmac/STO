package com.cneop.stoExpress.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.model.Order;

public class GlobalParas {
	private static GlobalParas globalParas;

	private GlobalParas() {
	}

	public static GlobalParas getGlobalParas() {
		if (globalParas == null) {
			globalParas = new GlobalParas();
		}
		return globalParas;
	}

	private String dataAccessUrl; // 数据传输地址
	private String upgradeUrl; // 版本升级IP地址
	private String deviceId = ""; // 设备编号
	private String stationId = ""; // 站点编号
	private String stationName = ""; // 站点名称
	private String userName; // 用户名
	private String userNo; // 用户工号
	private String version; // 程序版本号
	private String companyCode; // 公司代码
	private String scanMode; // 扫描模式
	private int autoUploadTimeSpilt; // 自动上传的时间间隔(分钟):0表示是手工上传,
	private String programRoleStr; // 程序角色控制
	private EProgramRole programRole; // 程序角色
	private EUserRole userRole; // 用户角色
	private boolean allowSelectOper; // 允许选择操作员
	private ESiteProperties siteProperties; // 站点属性
	private int unUploadCount = 0; // 未上传记录数
	private int msgUnUploadCount = 0;// 短信未上传数
	private int picUnUploadCount = 0;// 图片未上传数
	private int orderUnUploadCount = 0;// 订单未上传数
	private int totalRecord = 0;// 记录总数
	private List<Boolean> uploadingList = new ArrayList<Boolean>(); // 是否正在下载
	private List<Boolean> downloadingList = new ArrayList<Boolean>(); // 是否正在上传
	private String smsSendUrl; // 短信发送地址
	private String smsInfoUrl; // 短信资料下载地址
	private String imageUploadUrl;// 图片上传地址
	private String signUnUploadPath;// 签收未上传路径
	private String problemUnUploadPath;// 问题件未上传路径
	private String signUploadPath;// 签收已上传路径，有SD卡才有用
	private String problemUploadPath;// 问题件已上传路径，有SD卡才有用
	private String photoPath;// 拍照保存路径
	private String imageSuffix = ".jpg";// 图片后缀
	private HashMap<String, Order> orderList;// 催促取消订单
	private Object syncObject = new Object();

	private String upgradeCompanyCode;// 升级时用的企业Code
	private EProgramRole sitePropertyForManagerSearch = EProgramRole.other;// 再登录管理员时来根据此变量查询对应的
	private String sjMdDownloadUrl;// 收件面单下载的url
	private String lastDownMDTime;// 最后一次下载面单的时间（每天下载一次/手动时，可随时更新）

	private List<Object> lstSJMDSenedModels;// 收件面单发送记录(仅保存在内存中)
	private boolean mdControlIsOpen;// 开启面单扫描管控功能

	public boolean isMdControlIsOpen() {
		return mdControlIsOpen;
	}

	public void setMdControlIsOpen(boolean mdControlIsOpen) {
		this.mdControlIsOpen = mdControlIsOpen;
	}

	public List<Object> getLstSJMDSenedModels() {
		return lstSJMDSenedModels;
	}

	public void setLstSJMDSenedModels(List<Object> lstSJMDSenedModels) {
		this.lstSJMDSenedModels = lstSJMDSenedModels;
	}

	public String getLastDownMDTime() {
		return lastDownMDTime;
	}

	public void setLastDownMDTime(String lastDownMDTime) {
		this.lastDownMDTime = lastDownMDTime;
	}

	public String getSjMdDownloadUrl() {
		return sjMdDownloadUrl;
	}

	public void setSjMdDownloadUrl(String sjMdDownloadUrl) {
		this.sjMdDownloadUrl = sjMdDownloadUrl;
	}

	public EProgramRole getSitePropertyForManagerSearch() {
		return sitePropertyForManagerSearch;
	}

	public void setSitePropertyForManagerSearch(EProgramRole sitePropertyForManagerSearch) {
		this.sitePropertyForManagerSearch = sitePropertyForManagerSearch;
	}

	public List<Order> getOrderList() {
		List<Order> tList = new ArrayList<Order>();
		synchronized (syncObject) {
			if (orderList != null) {
				for (Order order : orderList.values()) {
					if (order.getIsUrge() == 1) {
						tList.add(0, order);
					} else {
						tList.add(order);
					}
				}
				orderList.clear();
			}
		}
		return tList;
	}

	public void setOrderList(HashMap<String, Order> mapList) {
		if (orderList == null) {
			orderList = new HashMap<String, Order>();
		}
		synchronized (syncObject) {
			for (String key : mapList.keySet()) {
				if (!orderList.containsKey(key)) {
					orderList.put(key, mapList.get(key));
				}
			}
		}
	}

	public String getUpgradeCompanyCode() {
		return upgradeCompanyCode;
	}

	public void setUpgradeCompanyCode(String upgradeCompanyCode) {
		this.upgradeCompanyCode = upgradeCompanyCode;
	}

	public int getUnUploadCount() {
		return unUploadCount;
	}

	public void setUnUploadCount(int unUploadCount) {
		this.unUploadCount += unUploadCount;
		if (this.unUploadCount < 0) {
			this.unUploadCount = 0;
		}
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public String getImageSuffix() {
		return imageSuffix;
	}

	public void setImageSuffix(String imageSuffix) {
		this.imageSuffix = imageSuffix;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getSignUnUploadPath() {
		return signUnUploadPath;
	}

	public void setSignUnUploadPath(String signUnUploadPath) {
		this.signUnUploadPath = signUnUploadPath;
	}

	public String getProblemUnUploadPath() {
		return problemUnUploadPath;
	}

	public void setProblemUnUploadPath(String problemUnUploadPath) {
		this.problemUnUploadPath = problemUnUploadPath;
	}

	public String getSignUploadPath() {
		return signUploadPath;
	}

	public void setSignUploadPath(String signUploadPath) {
		this.signUploadPath = signUploadPath;
	}

	public String getProblemUploadPath() {
		return problemUploadPath;
	}

	public void setProblemUploadPath(String problemUploadPath) {
		this.problemUploadPath = problemUploadPath;
	}

	public int getMsgUnUploadCount() {
		return msgUnUploadCount;
	}

	public void setMsgUnUploadCount(int msgUnUploadCount) {
		this.msgUnUploadCount += msgUnUploadCount;
		if (this.msgUnUploadCount < 0) {
			this.msgUnUploadCount = 0;
		}
	}

	public int getOrderUnUploadCount() {
		return orderUnUploadCount;
	}

	public void setOrderUnUploadCount(int orderUnUploadCount) {
		this.orderUnUploadCount += orderUnUploadCount;
		if (this.orderUnUploadCount < 0) {
			this.orderUnUploadCount = 0;
		}
	}

	public int getPicUnUploadCount() {
		return picUnUploadCount;
	}

	public void setPicUnUploadCount(int picUnUploadCount) {
		this.picUnUploadCount += picUnUploadCount;
		if (this.picUnUploadCount < 0) {
			this.picUnUploadCount = 0;
		}
	}

	public String getImageUploadUrl() {
		return imageUploadUrl;
	}

	public void setImageUploadUrl(String imageUploadUrl) {
		this.imageUploadUrl = imageUploadUrl;
	}

	public String getSmsSendUrl() {
		return smsSendUrl;
	}

	public void setSmsSendUrl(String smsSendUrl) {
		this.smsSendUrl = smsSendUrl;
	}

	public String getSmsInfoUrl() {
		return smsInfoUrl;
	}

	public void setSmsInfoUrl(String smsInfoUrl) {
		this.smsInfoUrl = smsInfoUrl;
	}

	public boolean isUploading() {
		if (uploadingList.size() > 0) {
			return true;
		}
		return false;
	}

	public void setUploading(boolean isUploading) {
		if (isUploading) {
			uploadingList.add(isUploading);
		} else {
			if (uploadingList.size() > 0) {
				uploadingList.remove(0);
			}
		}
	}

	public boolean isDownloading() {
		if (downloadingList.size() > 0) {
			return true;
		}
		return false;
	}

	public void setDownloading(boolean isDownloading) {
		if (isDownloading) {// 是否正在上传
			downloadingList.add(isDownloading);
		}
	}

	public ESiteProperties getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(ESiteProperties siteProperties) {
		this.siteProperties = siteProperties;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public boolean getAllowSelectOper() {
		return allowSelectOper;
	}

	public void setAllowSelectOper(boolean allowSelOperation) {
		this.allowSelectOper = allowSelOperation;
	}

	public EProgramRole getProgramRole() {
		return programRole;
	}

	public void setProgramRole(EProgramRole programRole) {
		this.programRole = programRole;
		if (programRole != null) {// 管理员才设成null
			this.setSitePropertyForManagerSearch(programRole);
		}
	}

	public String getProgramRoleStr() {
		return programRoleStr;
	}

	public void setProgramRoleStr(String programRoleStr) {
		this.programRoleStr = programRoleStr;
	}

	public EUserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(EUserRole userRole) {
		this.userRole = userRole;
	}

	public String getUpgradeUrl() {
		return upgradeUrl;
	}

	public void setUpgradeUrl(String upgradeUrl) {
		this.upgradeUrl = upgradeUrl;
	}

	public int upauto() {
		return autoUploadTimeSpilt;
	}

	public void setAutoUploadTimeSpilt(int autoUploadTimeSpilt) {
		this.autoUploadTimeSpilt = autoUploadTimeSpilt;
	}

	public int getAutoUploadTimeSpilt() {
		return autoUploadTimeSpilt;
	}

	public String getScanMode() {
		return scanMode;
	}

	public void setScanMode(String scanMode) {
		this.scanMode = scanMode;
	}

	public String getDataAccessUrl() {
		return dataAccessUrl;
	}

	public void setDataAccessUrl(String url) {
		this.dataAccessUrl = url;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public int getSiteNoMaxLen() {

		return 4;
	}

	public int getUserNoLen() {
		return 4;
	}

	public int getNextSiteLen() {

		return 6;
	}

	/**
	 * 车辆ID
	 * 
	 * @return
	 */
	public int getCarLotNumberLen() {

		return 6;
	}

	public int getRouteMinLen() {

		return 6;
	}

}
