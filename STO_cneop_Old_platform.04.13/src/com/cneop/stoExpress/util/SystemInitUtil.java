package com.cneop.stoExpress.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EProgramRole;
import com.cneop.stoExpress.common.Enums.ESiteProperties;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dao.ScanDataService;
import com.cneop.stoExpress.dao.ScopeOfDeliveryService;
import com.cneop.stoExpress.dao.StationService;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.stoExpress.model.Station;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.util.AppContext;
import com.cneop.util.CheckUtils;
import com.cneop.util.DBHelper;
import com.cneop.util.DateUtil;
import com.cneop.util.SoundUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.VibratorUtil;
import com.cneop.util.device.Imei;
import com.cneop.util.file.FileUtil;
import com.cneop.util.scan.ScanManager;

/*
 * ϵͳ��ʼ������
 */
public class SystemInitUtil {

	private SysConfigService sysConfigService;
	private StationService stationService;
	private Context context;
	private ScopeOfDeliveryService scopeOfDeliveryService;

	public SystemInitUtil(Context context) {

		sysConfigService = new SysConfigService(context);
		stationService = new StationService(context);
		scopeOfDeliveryService = new ScopeOfDeliveryService(context);
		this.context = context;
	}

	/*
	 * ϵͳ��ʼ��
	 */
	String programRoleStr = "";

	@SuppressWarnings("incomplete-switch")
	public void loadSystemInit() {
		List<Object> syscnfigList = sysConfigService.getListObj(null, null, new SysConfig());
		for (int i = 0; i < syscnfigList.size(); i++) {
			SysConfig con = (SysConfig) syscnfigList.get(i);
			ESysConfig configName = ESysConfig.getEnum(con.getConfig_name());
			System.out.println("===============configName \t" + configName);
			System.out.println("===============getConfig_value \t" + con.getConfig_value());
			switch (configName) {
			case stationId:// վ����
				GlobalParas.getGlobalParas().setStationId(con.getConfig_value());
				break;
			case scanMode: // ɨ��ģʽ
				GlobalParas.getGlobalParas().setScanMode(con.getConfig_value());
				break;
			case autoUploadTimeSpilt: // �Զ��ϴ���ʱ����
				String strConfig = con.getConfig_value();
				int iValue = 0;
				if (CheckUtils.isNumeric(strConfig)) {
					iValue = Integer.parseInt(strConfig);
					GlobalParas.getGlobalParas().setAutoUploadTimeSpilt(iValue);
				}
				break;
			case serviceIp:// �ϴ���������ַ
				GlobalParas.getGlobalParas().setDataAccessUrl(con.getConfig_value());
				break;
			case companyCode:// ��˾����
				GlobalParas.getGlobalParas().setCompanyCode(con.getConfig_value());
				break;
			case upgradeIp:// ���µ�ַ
				GlobalParas.getGlobalParas().setUpgradeUrl(con.getConfig_value());
				break;
			case programRole:// �����ɫ
				programRoleStr = con.getConfig_value();
				GlobalParas.getGlobalParas().setProgramRoleStr(programRoleStr);
				break;
			case allowSelectOper:// ����ѡ�����Ա
				GlobalParas.getGlobalParas().setAllowSelectOper(con.getConfig_value().equals("��"));
				break;
			case smsInfo:// �������Ϣ����
				GlobalParas.getGlobalParas().setSmsInfoUrl(con.getConfig_value());
				break;
			case smsSend:// ���ŷ���
				GlobalParas.getGlobalParas().setSmsSendUrl(con.getConfig_value());
				break;
			case imageUpload:// ͼƬ��
				GlobalParas.getGlobalParas().setImageUploadUrl(con.getConfig_value());
				break;
			case upgradeCompanyCode:// ����ʱ����ҵCode
				GlobalParas.getGlobalParas().setUpgradeCompanyCode(con.getConfig_value());
				break;
			case MdDownloadUrl:// �ռ��浥���ص�ַ
				GlobalParas.getGlobalParas().setSjMdDownloadUrl(con.getConfig_value());
				break;
			case LastDownMDTime:// ���һ�������浥��ʱ��
				GlobalParas.getGlobalParas().setLastDownMDTime(con.getConfig_value());
				break;
			case MdControlIsOpen:// �ռ��浥�ܿ��Ƿ���
				if (con.getConfig_value().equals("1")) {
					GlobalParas.getGlobalParas().setMdControlIsOpen(true);
				} else {
					GlobalParas.getGlobalParas().setMdControlIsOpen(false);
				}
				break;
			}
		}
		// ------------------------------------------------------------------------------------------------
		if (StrUtil.isNullOrEmpty(GlobalParas.getGlobalParas().getSjMdDownloadUrl())) {
			GlobalParas.getGlobalParas().setSjMdDownloadUrl("http://140.206.185.9:22210/stLogisticsPlatform_track/releaseRecordQuery.action");
		}
		// ���ó����ɫ
		setProgramRole(GlobalParas.getGlobalParas().getStationId(), programRoleStr);// "",�Զ�,
		// �汾��
		GlobalParas.getGlobalParas().setVersion(getVersionName());// V1.30

		// ����վ������
		setStationName(GlobalParas.getGlobalParas().getStationId());// ""

		// ��ʼ����������·��
		setPhotoPath();

		// ���δ�ϴ���¼����
		setUnUploadCount();

		// �������ͷ�Χ����
		scopeOfDeliveryService.addDataFromTxt();

		// ��̨������
		SoundUtil.getIntance(context).initSound();

		// ����IMEI��

		GlobalParas.getGlobalParas().setDeviceId(Imei.getImei(context));// 359996020121177

		// ��������
		boolean isOpenVibrator = AppContext.getAppContext().getOpenVibrator();
		if (isOpenVibrator) {
			VibratorUtil.getIntance(context);
		}
		ScanManager.getInstance().getScanner().init(false, R.raw.ok, context);

		ScanManager.getInstance().getScanner().setHomeKeyEnable(false, context);

		ScanManager.getInstance().getScanner().setNoticeEnable(false, context);

		ScanManager.getInstance().getScanner().setPower(false);

		// ��������

		ScanManager.getInstance().getScanner().setIsVibrator(isOpenVibrator);

		// ���õ�ǰɨ��״̬

		boolean isContinue = Boolean.valueOf(GlobalParas.getGlobalParas().getScanMode());
		ScanManager.getInstance().getScanner().setScanMode(isContinue);
		// �ܼ�¼��
		ScanDataService scanDataService = new ScanDataService(context);
		GlobalParas.getGlobalParas().setTotalRecord(scanDataService.getCount(""));
		// ɾ����������
		new ScanDataService(context).delData2Login();
	}

	private void setPhotoPath() {

		String filedir = context.getFilesDir().getAbsolutePath();
		// ����·��
		String photoPath = filedir + File.separator + "photo.jpg";
		GlobalParas.getGlobalParas().setPhotoPath(photoPath);
		// ǩ�ա������δ�ϴ�����·��
		String signDir = filedir + File.separator + "sign" + File.separator;
		String problemDir = filedir + File.separator + "problem" + File.separator;
		FileUtil.isExist(signDir, true);
		FileUtil.isExist(problemDir, true);
		GlobalParas.getGlobalParas().setSignUnUploadPath(signDir);
		GlobalParas.getGlobalParas().setProblemUnUploadPath(problemDir);
		// ǩ�ա���������ϴ�����·��
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		String signedDir = "";
		String sdRootPath = "";
		String problemedDir = "";
		if (sdCardExist) {
			sdRootPath = Environment.getExternalStorageDirectory().toString().trim();

		} else {

			sdRootPath = this.context.getPackageResourcePath() + "/files/";

		}

		signedDir = sdRootPath + File.separator + "CNEOP" + File.separator + "STO" + File.separator + "sign" + File.separator;
		problemedDir = sdRootPath + File.separator + "CNEOP" + File.separator + "STO" + File.separator + "problem" + File.separator;

		FileUtil.isExist(signedDir, true);
		FileUtil.isExist(problemedDir, true);
		GlobalParas.getGlobalParas().setSignUploadPath(signedDir);
		GlobalParas.getGlobalParas().setProblemUploadPath(problemedDir);
	}

	// ����δ�ϴ�����
	public void setUnUploadCount() {

		ScanDataService scanDataService = new ScanDataService(context);
		MsgSendService msgSendService = new MsgSendService(context);
		OrderOperService orderOperService = new OrderOperService(context);
		// δ�ϴ�ɨ������
		int scanCount = scanDataService.getCount("", "", "0") - GlobalParas.getGlobalParas().getUnUploadCount();
		GlobalParas.getGlobalParas().setUnUploadCount(scanCount);
		// δ�ϴ�����
		int msgCount = msgSendService.getCountByStatus("0") - GlobalParas.getGlobalParas().getMsgUnUploadCount();
		GlobalParas.getGlobalParas().setMsgUnUploadCount(msgCount);
		// δ�ϴ�ͼƬ
		int signPicCount = FileUtil.getCountInDir(GlobalParas.getGlobalParas().getSignUnUploadPath());
		int problemPicCount = FileUtil.getCountInDir(GlobalParas.getGlobalParas().getProblemUnUploadPath());
		int picCount = signPicCount + problemPicCount - GlobalParas.getGlobalParas().getPicUnUploadCount();
		GlobalParas.getGlobalParas().setPicUnUploadCount(picCount);
		// δ�ϴ�����
		int orderCount = orderOperService.getCountByStatus("0") - GlobalParas.getGlobalParas().getOrderUnUploadCount();
		GlobalParas.getGlobalParas().setOrderUnUploadCount(orderCount);

		Date date = DateUtil.addDay(-7);
		delImageFile(date, GlobalParas.getGlobalParas().getSignUploadPath());
		delImageFile(date, GlobalParas.getGlobalParas().getProblemUploadPath());
		// GlobalParas.getGlobalParas().getSignUploadPath()

	}

	private void delImageFile(Date date, String path) {

		if (path == null) {
			return;
		}
		File dir = new File(path);
		if (!dir.exists()) {
			return;
		}
		File[] listFiles = dir.listFiles();
		for (int i = listFiles.length - 1; i >= 0; i--) {
			if (listFiles[i].lastModified() < date.getTime()) {
				listFiles[i].delete();
				break;
			}
		}
	}

	// ����վ������
	public void setStationName(String stationId) {

		Station stationModel = stationService.getStationModel(stationId);
		if (stationModel != null) {
			GlobalParas.getGlobalParas().setStationName(stationModel.getStationName());
		}
	}

	// ��ð汾��
	private String getVersionName() {

		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		String version = "1.0";
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = "V" + packInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.i("��ȡ�汾��", e.getMessage());
		}

		return version;
	}

	/*
	 * ��ó����ɫ
	 */
	@SuppressWarnings("static-access")
	public void setProgramRole(String stationId, String programRoleStr) {

		EProgramRole programRole = EProgramRole.other;
		StrUtil strUtil = new StrUtil();
		if (!strUtil.isNullOrEmpty(stationId)) {
			if (programRoleStr.equals("�Զ�")) {
				Station stationModel = stationService.getStationModel(stationId);
				if (stationModel != null) {
					if (stationModel.getAttribute().equals("����")) {
						programRole = EProgramRole.station;
					} else if (stationModel.getAttribute().equals("����")) {
						programRole = EProgramRole.center;
					} else if (stationModel.getAttribute().equals("���㺽��")) {
						programRole = EProgramRole.station;
					} else if (stationModel.getAttribute().equals("���ĺ���")) {
						programRole = EProgramRole.air;
					}
				}
			} else if (programRoleStr.equals("����")) {
				programRole = EProgramRole.station;
			} else if (programRoleStr.equals("����")) {
				programRole = EProgramRole.center;
			} else if (programRoleStr.equals("����")) {
				programRole = EProgramRole.air;
			}
		}
		GlobalParas.getGlobalParas().setProgramRole(programRole);
		setSiteProperties(programRole);
	}

	/*
	 * ����վ������
	 */
	private void setSiteProperties(EProgramRole programRole) {

		switch (programRole) {
		case station:
		case center:
			GlobalParas.getGlobalParas().setSiteProperties(ESiteProperties.C_N);
			break;
		case air:
			GlobalParas.getGlobalParas().setSiteProperties(ESiteProperties.C_H);
			break;
		}
	}

	/*
	 * ����OR����ϵͳ����
	 */
	public boolean replaceSystemSet(List<SysConfig> configModelList) {

		int result = sysConfigService.addRecord(configModelList);
		boolean flag = false;
		if (result > 0) {
			flag = true;
		}
		return flag;
	}

	public boolean replaceSystemSet(SysConfig configModel) {

		List<SysConfig> configModelList = new ArrayList<SysConfig>();
		configModelList.add(configModel);
		return replaceSystemSet(configModelList);

	}
}
