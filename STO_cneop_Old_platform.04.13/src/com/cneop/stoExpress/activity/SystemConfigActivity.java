package com.cneop.stoExpress.activity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cneop.stoExpress.activity.admin.AutoUpdateSetActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EUserRole;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.stoExpress.datacenter.DownloadThread;
import com.cneop.stoExpress.datacenter.SystimeSyncThread;
import com.cneop.stoExpress.datacenter.SystimeSyncThread.ISyncSuc;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.update.UpdateManager;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.BluetoothSetActivity;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.controls.ListViewEx.IItemClickEvent;
import com.cneop.util.device.NetworkUtil;
import com.cneop.util.device.Wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.Toast;

public class SystemConfigActivity extends CommonTitleActivity {
	private Context context = this;
	private ListViewEx lvx;
	private String FuntionName = "FuntionName";
	private String FuntionValue = "FuntionValue";
	private SysConfigService configService = null;
	private List<SysConfig> list = null;
	private ISyncSuc systimeSyncSuc = new SyncSuc();
	private Handler handlerSyncTime;

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_system_config);
		preferences = getSharedPreferences("scaner", Context.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		configService = new SysConfigService(this);
		setTitle("系统设置");
		DownloadThread.getIntance(SystemConfigActivity.this).setSyncSuc(systimeSyncSuc);

	}

	@Override
	protected void initializeComponent() {
		super.initializeComponent();
		lvx = (ListViewEx) this.findViewById(R.id.lv_system_config_lvMain);
		lvx.inital(R.layout.list_item_one, new String[] { FuntionName }, new int[] { R.id.tv_list_item_one_tvhead1 });
		lvx.SetOnIItemClick(clickEvent);

	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		Intent intent = this.getIntent();
		boolean isLoginSet = intent.getBooleanExtra("isLoginSet", false);
		parser();
		if (isLoginSet) {
			for (int i = list.size() - 1; i >= 0; i--) {
				if (list.get(i).getConfig_desc().toString().trim().equals("0")) {
					list.remove(i);
				}
			}
		}
		scanmode = GlobalParas.getGlobalParas().getScanMode().equals("single") ? "单扫" : "连扫";
		sourceList = new ArrayList<Map<String, Object>>();
		Map<String, Object> firstMap = new HashMap<String, Object>();
		firstMap.put(FuntionName,
				"1." + list.get(0).getConfig_value() + "  " + GlobalParas.getGlobalParas().getDeviceId());
		firstMap.put(FuntionValue, list.get(0).getConfig_name());
		sourceList.add(firstMap);
		Map<String, Object> secondMap = new HashMap<String, Object>();
		secondMap.put(FuntionName, "2." + list.get(1).getConfig_value() + "  " + preferences.getString("sm", ""));
		secondMap.put(FuntionValue, list.get(1).getConfig_name());
		sourceList.add(secondMap);
		for (int i = 2; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FuntionName, (i + 1) + "." + list.get(i).getConfig_value());
			map.put(FuntionValue, list.get(i).getConfig_name());
			sourceList.add(map);
		}

		lvx.add(sourceList);
		initSyncTimeNoticeHandler();
	}

	@Override
	protected void onResume() {

		// TODO Auto-generated method stub
		super.onResume();
		lvx.GetValue(1).put(FuntionName, "2." + list.get(1).getConfig_value() + "  " + preferences.getString("sm", ""));
		lvx.notifyDataSetChanged();
	}

	@Override
	public String toString() {

		return "SystemConfigActivity [lvx=" + lvx + ", FuntionName=" + FuntionName + ", FuntionValue=" + FuntionValue
				+ ", configService=" + configService + ", list=" + list + ", systimeSyncSuc=" + systimeSyncSuc
				+ ", handlerSyncTime=" + handlerSyncTime + ", scanmode=" + scanmode + ", sourceList=" + sourceList
				+ ", clickEvent=" + clickEvent + "]";
	}

	private void initSyncTimeNoticeHandler() {

		handlerSyncTime = new Handler(SystemConfigActivity.this.getMainLooper()) {

			@Override
			public void handleMessage(android.os.Message msg) {

				super.handleMessage(msg);
				Toast.makeText(SystemConfigActivity.this, msg.obj.toString().trim(), Toast.LENGTH_LONG).show();
			};
		};
	}

	private List<SysConfig> parser() {

		SysConfig config = null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream file;
		try {
			file = getAssets().open("SystemConfig.xml");
			parser.setInput(file, "utf-8");
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					list = new ArrayList<SysConfig>();
					break;
				case XmlPullParser.START_TAG:
					if ("config".equalsIgnoreCase(parser.getName())) {
						config = new SysConfig();
					} else if (parser.getName().equalsIgnoreCase("configname")) {
						config.setConfig_name(parser.nextText());
					} else if (parser.getName().equalsIgnoreCase("configdesc")) {
						config.setConfig_desc(parser.nextText());
					} else if (parser.getName().equalsIgnoreCase("configvalue")) {
						config.setConfig_value(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("config".equalsIgnoreCase(parser.getName())) {
						if (config != null && list != null) {
							list.add(config);
							config = null;
						}
					}
					break;
				}
				event = parser.next();
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	protected void uiOnClick(View v) {
		super.uiOnClick(v);
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {

		SystimeSyncThread.setISystimeSyncSuc(null);
		DownloadThread.getIntance(SystemConfigActivity.this).setSyncSuc(null);
		super.onDestroy();
	}

	String scanmode = "";
	List<Map<String, Object>> sourceList;

	IItemClickEvent clickEvent = new IItemClickEvent() {

		@Override
		public void onItemClick(int position) {

			// TODO Auto-generated method stub
			listViewOnClick(position);
		}
	};

	private String FLAG = "";

	public void showmsg(String msg) {

		Toast.makeText(getApplicationContext(), msg, 1).show();
	}

	/**
	 * 点击
	 * 
	 * @param position
	 */
	SysConfig sysConfig = new SysConfig();

	@SuppressWarnings("static-access")
	private void listViewOnClick(int position) {
		String funtion = sourceList.get(position).get(FuntionValue).toString().trim().trim();
		Intent intent;
		if (funtion.equals("ScannerSet")) {
			startWindow(ScanModleActivity.class);
		} else if (funtion.equals("SignerSet")) {
			intent = new Intent(this, SignerManagerActivity.class);
			startActivity(intent);
		} else if (funtion.equals("UpdateByhands")) {
			startActivity(new Intent(this, System_Activity.class));
		} else if (funtion.equals("UploadSet")) {
			intent = new Intent(this, AutoUpdateSetActivity.class);
			startActivity(intent);
		} else if (funtion.equals("PasswordSet")) {
			if (GlobalParas.getGlobalParas().getUserRole() == null
					|| GlobalParas.getGlobalParas().getUserRole() == EUserRole.other) {
				PromptUtils.getInstance().showToastHasFeel("管理员不能修改密码", this, EVoiceType.fail, 0);
				return;
			}
			intent = new Intent(SystemConfigActivity.this, RePasswordActivity.class);
			startActivity(intent);
		}
		// -------------------------------------------------------------升级
		else if (funtion.equals("UpdateVersion")) {
			// http://222.66.109.144/posupgrade
			// WF
			UpdateManager update = new UpdateManager(this, GlobalParas.getGlobalParas().getUpgradeUrl(),
					GlobalParas.getGlobalParas().getCompanyCode());
			update.checkUpdate();
			update.setNewVersionTip(true);
			// UpdateManager update = new UpdateManager(context,
			// GlobalParas.getGlobalParas().getUpgradeUrl(),
			// GlobalParas.getGlobalParas().getUpgradeCompanyCode());
			// update.setNewVersionTip(true);
			// update.checkUpdate();
		} else if (funtion.equals("WifiSet")) {
			Wifi.getInstance(this).DoConfig();
		} else if (funtion.equals("SoundSet")) {
			startActivity(new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS));
		} else if (funtion.equals("LightSet")) {
			showDialog();
		} else if (funtion.equals("TimeSet")) {
			// MsgNoticeActivity
			if (!NetworkUtil.getInstance(SystemConfigActivity.this).isNetworkConnected()) {
				PromptUtils.getInstance().showToastHasFeel("网络未连接", this, EVoiceType.fail, 0.1);
				return;
			}
			new SystimeSyncThread(this).setISystimeSyncSuc(systimeSyncSuc);
			new SystimeSyncThread(this).start();
		} else if (funtion.equals("BlueToothSet")) {
			intent = new Intent(SystemConfigActivity.this, BluetoothSetActivity.class);
			startActivity(intent);
		} else if (funtion.equals("InputmethodSet")) {// 输入法设置
			try {
				((InputMethodManager) SystemConfigActivity.this.getApplicationContext()
						.getSystemService("input_method")).showInputMethodPicker();
			} catch (Exception e) {
				Toast.makeText(SystemConfigActivity.this, "无法打开，请进入系统界面手动设置！", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else if (funtion.equals("Network_settings")) {
			// setMobileNetEnable();
			startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
			// Settings.ACTION_SETTINGS
			// ACTION_DATA_ROAMING_SETTINGS
		}
		Runtime.getRuntime().gc();
	}

	public final void setMobileNetEnable() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Object[] arg = null;
		try {
			boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
			if (!isMobileDataEnable) {
				invokeBooleanArgMethod("setMobileDataEnabled", true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object invokeBooleanArgMethod(String methodName, boolean value) throws Exception {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(mConnectivityManager, value);
	}

	public boolean invokeMethod(String methodName, Object[] arg) throws Exception {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = null;
		if (arg != null) {
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}

		Method method = ownerClass.getMethod(methodName, argsClass);

		Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

		return isOpen;
	}

	public class SyncSuc implements com.cneop.stoExpress.datacenter.SystimeSyncThread.ISyncSuc {
		@Override
		public void OnHandleResult(boolean isSuc, String msg, int what) {
			Message msg2 = new Message();
			msg2.what = what;
			msg2.obj = msg;
			handlerSyncTime.sendMessage(msg2);
		}
	}

	private void showDialog() {
		SeekBar seekbar = new SeekBar(SystemConfigActivity.this);
		seekbar.setProgress((int) (android.provider.Settings.System.getInt(getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, 255)));
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				Integer tmpInt = seekBar.getProgress();
				android.provider.Settings.System.putInt(getContentResolver(),
						android.provider.Settings.System.SCREEN_BRIGHTNESS, tmpInt); // 0-255
				tmpInt = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);

				WindowManager.LayoutParams lp = getWindow().getAttributes();

				if (0 <= tmpInt && tmpInt <= 255) {
					lp.screenBrightness = tmpInt;
				}

				getWindow().setAttributes(lp);
			}
		});
		new AlertDialog.Builder(SystemConfigActivity.this).setTitle("  背光设置  ").setIcon(R.drawable.sto_loading)
				.setView(seekbar).show();
	}

	private void startWindow(Class<?> className) {

		Intent intent = new Intent(this, className);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// TODO Auto-generated method stub
		switch (keyCode) {
		case 9:
			// 2
			listViewOnClick(1);
			break;
		case 10:
			// 3
			listViewOnClick(2);
			break;
		case 11:
			// 4
			listViewOnClick(3);
			break;
		case 12:
			// 5
			listViewOnClick(4);
			break;
		case 13:
			// 6
			listViewOnClick(5);
			break;
		case 14:
			// 7
			listViewOnClick(6);
			break;
		case 15:
			// 8
			listViewOnClick(7);
			break;
		case KeyEvent.KEYCODE_9:
			listViewOnClick(8);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
