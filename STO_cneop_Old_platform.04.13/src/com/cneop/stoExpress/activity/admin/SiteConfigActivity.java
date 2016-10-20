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
		setTitle("������");
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
				PromptUtils.getInstance().showToastHasFeel("�����Ų���ȷ������������!", this, EVoiceType.fail, 0);
				return;
			}
			// ���ز�һ��δ�ϴ�������
			new SystemInitUtil(this).setUnUploadCount();
			// ����δ�ϴ��������л�վ��
			if (GlobalParas.getGlobalParas().getUnUploadCount() > 0) {
				PromptUtils.getInstance().showToastHasFeel("�����ϴ����ݣ��ڸ��������ţ�", SiteConfigActivity.this, EVoiceType.fail, 0);
				return;
			}
			PromptUtils.getInstance().showAlertDialog(SiteConfigActivity.this, "ȷ��Ҫ�������������?", okEvent, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final int UPDATE_PROGRESS = 0;// ���½�����״̬��
	private final int DOWNLOAD_END = 1; // ���ؽ���������
	private final String finishFlag = "finishFlag";
	private final String ERRORMSG = "errorMsg";

	private ProgressDialogEx pd;
	/*
	 * ���½������
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
					// ���»���
					PromptUtils.getInstance().showAlertDialogHasFeel(SiteConfigActivity.this, "���óɹ���", new OnClickListener() {

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
			final String stationId = etStationId.getText().toString().trim();// ������
			pd = ProgressDialogEx.createProgressDialogEx(context, 10 * 1000, "��ʾ", "�������ӷ�����,���Ժ�...", new com.cneop.util.ProgressDialogEx.OnTimeOutListener() {
				@Override
				public void onTimeOut(ProgressDialogEx dialog) {
					Toast.makeText(SiteConfigActivity.this, "��ѯ��ʱ", Toast.LENGTH_SHORT).show();
				}
			});
			pd.setCancelable(false);
			pd.show();
			// �̲߳���
			new Thread() {
				@Override
				public void run() {
					try {
						brocastUtil.sendDownloadPicStatus(true);
						// ���ռ��浥��¼���
						GlobalParas.getGlobalParas().setLstSJMDSenedModels(null);
						// ���ռ��浥ʱ��ĳ���һ��ģ��Ա��ٴε�¼ʱ�ܽ������أ�
						String endtime = DateUtil.addDay(-1, "yyyy-MM-dd");
						sysConfigService.UpdateLastMDTime(endtime);

						GlobalParas.getGlobalParas().setLastDownMDTime(endtime);
						GlobalParas.getGlobalParas().setMdControlIsOpen(true);
						// ɾ��ԭ��������
						delData();
						SystemInitUtil systemInitUtil = new SystemInitUtil(SiteConfigActivity.this);
						// ��������
						saveConfig(stationId, systemInitUtil);
						// ���»���
						updateCache(stationId, systemInitUtil);
						// -----------------------------------------------------------------------------------------------------------
						// ��ʼ���������
						MSDServer.getInstance(context).SetParam(GlobalParas.getGlobalParas().getStationId(), GlobalParas.getGlobalParas().getCompanyCode(), GlobalParas.getGlobalParas().getDeviceId(), GlobalParas.getGlobalParas().getVersion());
						// �������ط���
						DownloadManager downloadManager = new DownloadManager(context, GlobalParas.getGlobalParas().getStationId());
						// ע��
						ERegResponse regResponse = downloadManager.register();

						if (regResponse != ERegResponse.success) {
							if (regResponse == ERegResponse.fail) {
								sendMsg(DOWNLOAD_END, false, "ע����Ϣ��ʧ������ϵ����Ա��");
							} else {
								sendMsg(DOWNLOAD_END, false, "�����쳣������ʧ�ܣ�");
							}
							return;
						}
						// ---------------------------------------------------------------------------------------------------------------------
						int count = 0;
						final String[] dataName = { "�û�", "·��", "�������Ϣ", "���������", "Ŀ�ĵ�", "��һվ", "���ԭ��", "վ��" };
						final EDownType[] downTypeArray = { EDownType.User, EDownType.Route, EDownType.ServerStatoin, EDownType.Abnormal, EDownType.Destination, EDownType.NextStation, EDownType.OrderAbnormal, EDownType.Station };
						// --------------------------------------------------------------------
						for (int i = 0; i < downTypeArray.length; i++) {
							if (downTypeArray[i] == EDownType.ServerStatoin) {
								count = downloadManager.downloadServerStation(GlobalParas.getGlobalParas().getSmsInfoUrl());
							} else {
								count = downloadManager.downloadData(downTypeArray[i]);
							}
							if (count == -1) {
								sendMsg(DOWNLOAD_END, false, "����ʧ��:�����쳣��ע����Ϣ����");
								return;
							} else {
								if (downTypeArray[i] == EDownType.Station) {
									// վ�����سɹ�
									// ���»���
									updateCache(stationId, systemInitUtil);
								}
								sendMsg(UPDATE_PROGRESS, "����" + dataName[i] + "����\t" + count + "\t��");
							}
						}
						// --------------------------------------------------------------------
						sendMsg(DOWNLOAD_END, true, "");
					} catch (Exception e) {
						// TODO: handle exception
						sendMsg(DOWNLOAD_END, false, "����ʧ�ܣ�");
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
		// �ָ���ɫĬ������ �����������ݿ���
		SysConfig model = new SysConfig(ESysConfig.stationId.toString().trim(), stationId);
		sysConfigList.add(model);
		// ------------------------------------------------------
		model = new SysConfig(ESysConfig.programRole.toString().trim(), "�Զ�");
		sysConfigList.add(model);
		// -------------------------------------------------------
		model = new SysConfig(ESysConfig.allowSelectOper.toString().trim(), "��");
		sysConfigList.add(model);

		model = new SysConfig(ESysConfig.MdControlIsOpen.toString().trim(), "1");// �ռ��浥�Ƿ���������
		sysConfigList.add(model);
		// ---------------------------------------------------------------
		systemInitUtil.replaceSystemSet(sysConfigList);
	}

	// ���»���
	private void updateCache(String stationId, SystemInitUtil systemInitUtil) {

		GlobalParas.getGlobalParas().setStationId(stationId);
		GlobalParas.getGlobalParas().setProgramRoleStr("�Զ�");
		// ���³����ɫ
		systemInitUtil.setProgramRole(stationId, "�Զ�");
		// ����վ������
		systemInitUtil.setStationName(stationId);
	}
}
