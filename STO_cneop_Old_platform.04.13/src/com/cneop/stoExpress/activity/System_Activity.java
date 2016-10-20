package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.ERegResponse;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.RouteService;
import com.cneop.stoExpress.dao.UserService;
import com.cneop.stoExpress.datacenter.download.DownloadManager;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.util.DBHelper;
import com.cneop.util.PromptUtils;

public class System_Activity extends Activity {

	ArrayAdapter<String> adapter;
	private List<String> list = new ArrayList<String>();
	private ListView listview_sys_up;
	BrocastUtil brocastUtil;
	private final int UPDATE_PROGRESS = 0;// ���½�����״̬��
	private final int DOWNLOAD_END = 1; // ���ؽ���������
	private final String finishFlag = "finishFlag";
	private final String ERRORMSG = "errorMsg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_activity);
		listview_sys_up = (ListView) findViewById(R.id.listview_sys_up);
		adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
		brocastUtil = new BrocastUtil(this);
		new Thread(task).start();
	}

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
				System.out.println("=============date \t" + data.getString(finishFlag));
				list.add(data.getString(finishFlag));
				listview_sys_up.setAdapter(adapter);
				listview_sys_up.setSelection(listview_sys_up.getCount());
				break;
			case DOWNLOAD_END:
				Bundle b = msg.getData();
				boolean flag = b.getBoolean(finishFlag);
				if (flag) {
					// ���»���
					PromptUtils.getInstance().showAlertDialogHasFeel(System_Activity.this, "�������....", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// back();
						}
					}, EVoiceType.normal, 0);
				} else {
					String errorMsg = b.getString(ERRORMSG);
					PromptUtils.getInstance().showAlertDialogHasFeel(System_Activity.this, errorMsg, null, EVoiceType.fail, 0);
				}
				break;
			}
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

		UserService userService = new UserService(this);
		userService.delRecord("", null);
		RouteService routeService = new RouteService(this);
		routeService.delRecord("", null);
	}

	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			try {
				brocastUtil.sendDownloadPicStatus(true);
				// ɾ��ԭ��������
				delData();

				MSDServer.getInstance(getApplicationContext()).SetParam(GlobalParas.getGlobalParas().getStationId(), GlobalParas.getGlobalParas().getCompanyCode(), GlobalParas.getGlobalParas().getDeviceId(), GlobalParas.getGlobalParas().getVersion());
				// �������ط���
				DownloadManager downloadManager = new DownloadManager(getApplicationContext(), GlobalParas.getGlobalParas().getStationId());
				// ע��
				ERegResponse regResponse = downloadManager.register();

				// --------------------------------------------------------
				int count = 0;
				final EDownType[] downTypeArray = { EDownType.User, EDownType.Route, EDownType.ServerStatoin, EDownType.Abnormal, EDownType.Destination, EDownType.NextStation, EDownType.OrderAbnormal, EDownType.Station };
				final String[] dataName = { "�û�", "·��", "�������Ϣ", "���������", "Ŀ�ĵ�", "��һվ", "���ԭ��", "վ��" };
				for (int i = 0; i < downTypeArray.length; i++) {
					sendMsg(UPDATE_PROGRESS, "���ڸ���" + dataName[i] + "����...");
					if (downTypeArray[i] == EDownType.ServerStatoin) {// 3
						count = downloadManager.downloadServerStation(GlobalParas.getGlobalParas().getSmsInfoUrl());
					} else {
						count = downloadManager.downloadData(downTypeArray[i]);
					}
					if (count == -1) {
						sendMsg(DOWNLOAD_END, false, "����ʧ��:�����쳣��ע����Ϣ����");
						return;
					} else {
						if (downTypeArray[i] == EDownType.Station) {
						}
						sendMsg(UPDATE_PROGRESS, "����" + dataName[i] + "����" + count + "��");
						Thread.sleep(500);
					}
				}
				// -------------------------------------------------------------------------------
				sendMsg(DOWNLOAD_END, true, "");
			} catch (Exception e) {
				// TODO: handle exception
				sendMsg(DOWNLOAD_END, false, "����ʧ�ܣ�");
			} finally {
				brocastUtil.sendDownloadPicStatus(false);
			}
		}
	};
}
