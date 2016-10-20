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
 * 系统初始化工作
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
	 * 系统初始化
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
			case stationId:// 站点编号
				GlobalParas.getGlobalParas().setStationId(con.getConfig_value());
				break;
			case scanMode: // 扫描模式
				GlobalParas.getGlobalParas().setScanMode(con.getConfig_value());
				break;
			case autoUploadTimeSpilt: // 自动上传的时间间隔
				String strConfig = con.getConfig_value();
				int iValue = 0;
				if (CheckUtils.isNumeric(strConfig)) {
					iValue = Integer.parseInt(strConfig);
					GlobalParas.getGlobalParas().setAutoUploadTimeSpilt(iValue);
				}
				break;
			case serviceIp:// 上传服务器地址
				GlobalParas.getGlobalParas().setDataAccessUrl(con.getConfig_value());
				break;
			case companyCode:// 公司代码
				GlobalParas.getGlobalParas().setCompanyCode(con.getConfig_value());
				break;
			case upgradeIp:// 更新地址
				GlobalParas.getGlobalParas().setUpgradeUrl(con.getConfig_value());
				break;
			case programRole:// 程序角色
				programRoleStr = con.getConfig_value();
				GlobalParas.getGlobalParas().setProgramRoleStr(programRoleStr);
				break;
			case allowSelectOper:// 允许选择操作员
				GlobalParas.getGlobalParas().setAllowSelectOper(con.getConfig_value().equals("是"));
				break;
			case smsInfo:// 服务点信息下载
				GlobalParas.getGlobalParas().setSmsInfoUrl(con.getConfig_value());
				break;
			case smsSend:// 短信发送
				GlobalParas.getGlobalParas().setSmsSendUrl(con.getConfig_value());
				break;
			case imageUpload:// 图片上
				GlobalParas.getGlobalParas().setImageUploadUrl(con.getConfig_value());
				break;
			case upgradeCompanyCode:// 升级时的企业Code
				GlobalParas.getGlobalParas().setUpgradeCompanyCode(con.getConfig_value());
				break;
			case MdDownloadUrl:// 收件面单下载地址
				GlobalParas.getGlobalParas().setSjMdDownloadUrl(con.getConfig_value());
				break;
			case LastDownMDTime:// 最后一次下载面单的时间
				GlobalParas.getGlobalParas().setLastDownMDTime(con.getConfig_value());
				break;
			case MdControlIsOpen:// 收件面单管控是否开启
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
		// 设置程序角色
		setProgramRole(GlobalParas.getGlobalParas().getStationId(), programRoleStr);// "",自动,
		// 版本号
		GlobalParas.getGlobalParas().setVersion(getVersionName());// V1.30

		// 设置站点名称
		setStationName(GlobalParas.getGlobalParas().getStationId());// ""

		// 初始化拍照所有路径
		setPhotoPath();

		// 获得未上传记录数量
		setUnUploadCount();

		// 插入派送范围数据
		scopeOfDeliveryService.addDataFromTxt();

		// 初台化声音
		SoundUtil.getIntance(context).initSound();

		// 加载IMEI号

		GlobalParas.getGlobalParas().setDeviceId(Imei.getImei(context));// 359996020121177

		// 加载震子
		boolean isOpenVibrator = AppContext.getAppContext().getOpenVibrator();
		if (isOpenVibrator) {
			VibratorUtil.getIntance(context);
		}
		ScanManager.getInstance().getScanner().init(false, R.raw.ok, context);

		ScanManager.getInstance().getScanner().setHomeKeyEnable(false, context);

		ScanManager.getInstance().getScanner().setNoticeEnable(false, context);

		ScanManager.getInstance().getScanner().setPower(false);

		// 加载震子

		ScanManager.getInstance().getScanner().setIsVibrator(isOpenVibrator);

		// 设置当前扫描状态

		boolean isContinue = Boolean.valueOf(GlobalParas.getGlobalParas().getScanMode());
		ScanManager.getInstance().getScanner().setScanMode(isContinue);
		// 总记录数
		ScanDataService scanDataService = new ScanDataService(context);
		GlobalParas.getGlobalParas().setTotalRecord(scanDataService.getCount(""));
		// 删除过期数据
		new ScanDataService(context).delData2Login();
	}

	private void setPhotoPath() {

		String filedir = context.getFilesDir().getAbsolutePath();
		// 拍照路径
		String photoPath = filedir + File.separator + "photo.jpg";
		GlobalParas.getGlobalParas().setPhotoPath(photoPath);
		// 签收、问题件未上传保存路径
		String signDir = filedir + File.separator + "sign" + File.separator;
		String problemDir = filedir + File.separator + "problem" + File.separator;
		FileUtil.isExist(signDir, true);
		FileUtil.isExist(problemDir, true);
		GlobalParas.getGlobalParas().setSignUnUploadPath(signDir);
		GlobalParas.getGlobalParas().setProblemUnUploadPath(problemDir);
		// 签收、问题件已上传保存路径
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

	// 设置未上传数量
	public void setUnUploadCount() {

		ScanDataService scanDataService = new ScanDataService(context);
		MsgSendService msgSendService = new MsgSendService(context);
		OrderOperService orderOperService = new OrderOperService(context);
		// 未上传扫描数据
		int scanCount = scanDataService.getCount("", "", "0") - GlobalParas.getGlobalParas().getUnUploadCount();
		GlobalParas.getGlobalParas().setUnUploadCount(scanCount);
		// 未上传短信
		int msgCount = msgSendService.getCountByStatus("0") - GlobalParas.getGlobalParas().getMsgUnUploadCount();
		GlobalParas.getGlobalParas().setMsgUnUploadCount(msgCount);
		// 未上传图片
		int signPicCount = FileUtil.getCountInDir(GlobalParas.getGlobalParas().getSignUnUploadPath());
		int problemPicCount = FileUtil.getCountInDir(GlobalParas.getGlobalParas().getProblemUnUploadPath());
		int picCount = signPicCount + problemPicCount - GlobalParas.getGlobalParas().getPicUnUploadCount();
		GlobalParas.getGlobalParas().setPicUnUploadCount(picCount);
		// 未上传订单
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

	// 设置站点名称
	public void setStationName(String stationId) {

		Station stationModel = stationService.getStationModel(stationId);
		if (stationModel != null) {
			GlobalParas.getGlobalParas().setStationName(stationModel.getStationName());
		}
	}

	// 获得版本号
	private String getVersionName() {

		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		String version = "1.0";
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = "V" + packInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.i("获取版本号", e.getMessage());
		}

		return version;
	}

	/*
	 * 获得程序角色
	 */
	@SuppressWarnings("static-access")
	public void setProgramRole(String stationId, String programRoleStr) {

		EProgramRole programRole = EProgramRole.other;
		StrUtil strUtil = new StrUtil();
		if (!strUtil.isNullOrEmpty(stationId)) {
			if (programRoleStr.equals("自动")) {
				Station stationModel = stationService.getStationModel(stationId);
				if (stationModel != null) {
					if (stationModel.getAttribute().equals("网点")) {
						programRole = EProgramRole.station;
					} else if (stationModel.getAttribute().equals("中心")) {
						programRole = EProgramRole.center;
					} else if (stationModel.getAttribute().equals("网点航空")) {
						programRole = EProgramRole.station;
					} else if (stationModel.getAttribute().equals("中心航空")) {
						programRole = EProgramRole.air;
					}
				}
			} else if (programRoleStr.equals("网点")) {
				programRole = EProgramRole.station;
			} else if (programRoleStr.equals("中心")) {
				programRole = EProgramRole.center;
			} else if (programRoleStr.equals("航空")) {
				programRole = EProgramRole.air;
			}
		}
		GlobalParas.getGlobalParas().setProgramRole(programRole);
		setSiteProperties(programRole);
	}

	/*
	 * 设置站点属性
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
	 * 更新OR新增系统配置
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
