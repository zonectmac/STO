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

	private String dataAccessUrl; // ���ݴ����ַ
	private String upgradeUrl; // �汾����IP��ַ
	private String deviceId = ""; // �豸���
	private String stationId = ""; // վ����
	private String stationName = ""; // վ������
	private String userName; // �û���
	private String userNo; // �û�����
	private String version; // ����汾��
	private String companyCode; // ��˾����
	private String scanMode; // ɨ��ģʽ
	private int autoUploadTimeSpilt; // �Զ��ϴ���ʱ����(����):0��ʾ���ֹ��ϴ�,
	private String programRoleStr; // �����ɫ����
	private EProgramRole programRole; // �����ɫ
	private EUserRole userRole; // �û���ɫ
	private boolean allowSelectOper; // ����ѡ�����Ա
	private ESiteProperties siteProperties; // վ������
	private int unUploadCount = 0; // δ�ϴ���¼��
	private int msgUnUploadCount = 0;// ����δ�ϴ���
	private int picUnUploadCount = 0;// ͼƬδ�ϴ���
	private int orderUnUploadCount = 0;// ����δ�ϴ���
	private int totalRecord = 0;// ��¼����
	private List<Boolean> uploadingList = new ArrayList<Boolean>(); // �Ƿ���������
	private List<Boolean> downloadingList = new ArrayList<Boolean>(); // �Ƿ������ϴ�
	private String smsSendUrl; // ���ŷ��͵�ַ
	private String smsInfoUrl; // �����������ص�ַ
	private String imageUploadUrl;// ͼƬ�ϴ���ַ
	private String signUnUploadPath;// ǩ��δ�ϴ�·��
	private String problemUnUploadPath;// �����δ�ϴ�·��
	private String signUploadPath;// ǩ�����ϴ�·������SD��������
	private String problemUploadPath;// ��������ϴ�·������SD��������
	private String photoPath;// ���ձ���·��
	private String imageSuffix = ".jpg";// ͼƬ��׺
	private HashMap<String, Order> orderList;// �ߴ�ȡ������
	private Object syncObject = new Object();

	private String upgradeCompanyCode;// ����ʱ�õ���ҵCode
	private EProgramRole sitePropertyForManagerSearch = EProgramRole.other;// �ٵ�¼����Աʱ�����ݴ˱�����ѯ��Ӧ��
	private String sjMdDownloadUrl;// �ռ��浥���ص�url
	private String lastDownMDTime;// ���һ�������浥��ʱ�䣨ÿ������һ��/�ֶ�ʱ������ʱ���£�

	private List<Object> lstSJMDSenedModels;// �ռ��浥���ͼ�¼(���������ڴ���)
	private boolean mdControlIsOpen;// �����浥ɨ��ܿع���

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
		if (isDownloading) {// �Ƿ������ϴ�
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
		if (programRole != null) {// ����Ա�����null
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
	 * ����ID
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
