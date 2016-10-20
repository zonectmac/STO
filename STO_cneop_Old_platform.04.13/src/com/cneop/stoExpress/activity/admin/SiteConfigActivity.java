package com.cneop.stoExpress.activity.admin;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.ERegResponse;
import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.RouteService;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.stoExpress.dao.UserService;
import com.cneop.stoExpress.datacenter.download.DownloadManager;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.stoExpress.util.SystemInitUtil;
import com.cneop.util.DBHelper;
import com.cneop.util.DateUtil;
import com.cneop.util.ProgressDialogEx;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

public class SiteConfigActivity extends CommonTitleActivity {
	private Context context = this;
	private EditText etStationId;
	private Button btnOk;
	private Button btnBack;
	private StrUtil strUtil;
	private BrocastUtil brocastUtil;
	private SysConfigService sysConfigService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_site_config);
		setTitle("网点编号");
		super.onCreate(savedInstanceState);
		sysConfigService = new SysConfigService(this);
	}

	@Override
	protected void initializeComponent() {
		etStationId = bindEditText(R.id.site_config_etStationId, null, null);
		btnOk = bindButton(R.id.bottom_2_btnOk);
		btnBack = bindButton(R.id.bottom_2_btnBack);
	}

	@Override
	protected void initializeValues() {
		etStationId.setText(GlobalParas.getGlobalParas().getStationId());
		strUtil = new StrUtil();
		brocastUtil = new BrocastUtil(this);
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_2_btnOk:
			setStationId();
			break;
		case R.id.bottom_2_btnBack:
			back();
			break;
		}
	}

	private void setStationId() {
		try {

			String stationId = etStationId.getText().toString().trim();
			if (strUtil.isNullOrEmpty(stationId) || stationId.length() != 6) {
				PromptUtils.getInstance().showToastHasFeel("网点编号不正确，请重新输入!", this, EVoiceType.fail, 0);
				return;
			}
			// 先重查一下未上传的总数
			new SystemInitUtil(this).setUnUploadCount();
			// 数据未上传不允许切换站点
			if (GlobalParas.getGlobalParas().getUnUploadCount() > 0) {
				PromptUtils.getInstance().showToastHasFeel("请先上传数据，在更改网点编号！", SiteConfigActivity.this, EVoiceType.fail, 0);
				return;
			}
			PromptUtils.getInstance().showAlertDialog(SiteConfigActivity.this, "确定要重新设置网点号?", okEvent, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final int UPDATE_PROGRESS = 0;// 更新进度条状态码
	private final int DOWNLOAD_END = 1; // 下载结束进度条
	private final String finishFlag = "finishFlag";
	private final String ERRORMSG = "errorMsg";

	private ProgressDialogEx pd;
	/*
	 * 更新界面操作
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_PROGRESS:
				Bundle data = msg.getData();
				pd.setMessage(data.getString(finishFlag));
				break;
			case DOWNLOAD_END:
				pd.dismiss();
				Bundle b = msg.getData();
				boolean flag = b.getBoolean(finishFlag);
				if (flag) {
					// 更新缓存
					PromptUtils.getInstance().showAlertDialogHasFeel(SiteConfigActivity.this, "设置成功！", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							back();
						}
					}, EVoiceType.normal, 0);
				} else {
					String errorMsg = b.getString(ERRORMSG);
					PromptUtils.getInstance().showAlertDialogHasFeel(SiteConfigActivity.this, errorMsg, null, EVoiceType.fail, 0);
				}
				break;
			}
		}
	};

	private OnClickListener okEvent = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String stationId = etStationId.getText().toString().trim();// 网点编号
			pd = ProgressDialogEx.createProgressDialogEx(context, 10 * 1000, "提示", "正在连接服务器,请稍候...", new com.cneop.util.ProgressDialogEx.OnTimeOutListener() {
				@Override
				public void onTimeOut(ProgressDialogEx dialog) {
					Toast.makeText(SiteConfigActivity.this, "查询超时", Toast.LENGTH_SHORT).show();
				}
			});
			pd.setCancelable(false);
			pd.show();
			// 线程操作
			new Thread() {
				@Override
				public void run() {
					try {
						brocastUtil.sendDownloadPicStatus(true);
						// 将收件面单记录清空
						GlobalParas.getGlobalParas().setLstSJMDSenedModels(null);
						// 将收件面单时间改成上一天的（以便再次登录时能进行下载）
						String endtime = DateUtil.addDay(-1, "yyyy-MM-dd");
						sysConfigService.UpdateLastMDTime(endtime);

						GlobalParas.getGlobalParas().setLastDownMDTime(endtime);
						GlobalParas.getGlobalParas().setMdControlIsOpen(true);
						// 删除原来的数据
						delData();
						SystemInitUtil systemInitUtil = new SystemInitUtil(SiteConfigActivity.this);
						// 保存配置
						saveConfig(stationId, systemInitUtil);
						// 更新缓存
						updateCache(stationId, systemInitUtil);
						// -----------------------------------------------------------------------------------------------------------
						// 初始化服务参数
						MSDServer.getInstance(context).SetParam(GlobalParas.getGlobalParas().getStationId(), GlobalParas.getGlobalParas().getCompanyCode(), GlobalParas.getGlobalParas().getDeviceId(), GlobalParas.getGlobalParas().getVersion());
						// 调用下载方法
						DownloadManager downloadManager = new DownloadManager(context, GlobalParas.getGlobalParas().getStationId());
						// 注册
						ERegResponse regResponse = downloadManager.register();

						if (regResponse != ERegResponse.success) {
							if (regResponse == ERegResponse.fail) {
								sendMsg(DOWNLOAD_END, false, "注册信息丢失，请联系管理员！");
							} else {
								sendMsg(DOWNLOAD_END, false, "网络异常，设置失败！");
							}
							return;
						}
						// ---------------------------------------------------------------------------------------------------------------------
						int count = 0;
						final String[] dataName = { "用户", "路由", "服务点信息", "问题件类型", "目的地", "下一站", "打回原因", "站点" };
						final EDownType[] downTypeArray = { EDownType.User, EDownType.Route, EDownType.ServerStatoin, EDownType.Abnormal, EDownType.Destination, EDownType.NextStation, EDownType.OrderAbnormal, EDownType.Station };
						// --------------------------------------------------------------------
						for (int i = 0; i < downTypeArray.length; i++) {
							if (downTypeArray[i] == EDownType.ServerStatoin) {
								count = downloadManager.downloadServerStation(GlobalParas.getGlobalParas().getSmsInfoUrl());
							} else {
								count = downloadManager.downloadData(downTypeArray[i]);
							}
							if (count == -1) {
								sendMsg(DOWNLOAD_END, false, "设置失败:网络异常或注册信息有误");
								return;
							} else {
								if (downTypeArray[i] == EDownType.Station) {
									// 站点下载成功
									// 更新缓存
									updateCache(stationId, systemInitUtil);
								}
								sendMsg(UPDATE_PROGRESS, "下载" + dataName[i] + "数据\t" + count + "\t条");
							}
						}
						// --------------------------------------------------------------------
						sendMsg(DOWNLOAD_END, true, "");
					} catch (Exception e) {
						// TODO: handle exception
						sendMsg(DOWNLOAD_END, false, "设置失败！");
					} finally {
						brocastUtil.sendDownloadPicStatus(false);
						pd.setCancelable(true);
					}
				}
			}.start();
		}
	};

	private void sendMsg(int stausCode, String str) {

		Bundle data = new Bundle();
		data.putString(finishFlag, str);
		Message msg = handler.obtainMessage(stausCode);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private void sendMsg(int stausCode, boolean flag, String errorMsg) {

		Bundle data = new Bundle();
		data.putBoolean(finishFlag, flag);
		data.putString(ERRORMSG, errorMsg);
		Message msg = handler.obtainMessage(stausCode);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private void delData() {
		SQLiteDatabase dbHelper = new DBHelper(getApplicationContext()).getReadableDatabase();
		dbHelper.execSQL("delete from tb_dic_nextStation");
		UserService userService = new UserService(this);
		userService.delRecord("", null);
		RouteService routeService = new RouteService(this);
		routeService.delRecord("", null);
	}

	private void saveConfig(String stationId, SystemInitUtil systemInitUtil) {

		List<SysConfig> sysConfigList = new ArrayList<SysConfig>();
		// 恢复角色默认配置 ，保存在数据库中
		SysConfig model = new SysConfig(ESysConfig.stationId.toString().trim(), stationId);
		sysConfigList.add(model);
		// ------------------------------------------------------
		model = new SysConfig(ESysConfig.programRole.toString().trim(), "自动");
		sysConfigList.add(model);
		// -------------------------------------------------------
		model = new SysConfig(ESysConfig.allowSelectOper.toString().trim(), "否");
		sysConfigList.add(model);

		model = new SysConfig(ESysConfig.MdControlIsOpen.toString().trim(), "1");// 收件面单是否允许下载
		sysConfigList.add(model);
		// ---------------------------------------------------------------
		systemInitUtil.replaceSystemSet(sysConfigList);
	}

	// 更新缓存
	private void updateCache(String stationId, SystemInitUtil systemInitUtil) {

		GlobalParas.getGlobalParas().setStationId(stationId);
		GlobalParas.getGlobalParas().setProgramRoleStr("自动");
		// 更新程序角色
		systemInitUtil.setProgramRole(stationId, "自动");
		// 设置站点名称
		systemInitUtil.setStationName(stationId);
	}
}
