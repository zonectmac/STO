package com.cneop.util.device;

import com.cneop.util.PromptUtils;
import com.cneop.util.model.Enums.ENetworkType;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetworkUtil {

	private static Object INSTANCE_LOCK = new Object();
	private static NetworkUtil INSTANCE;
	private Context context;
	private Thread thread;
	public Handler wifiConfigHandler;

	public static NetworkUtil getInstance(Context context) {
		if (INSTANCE == null) {
			synchronized (INSTANCE_LOCK) {
				INSTANCE = new NetworkUtil();
			}
			INSTANCE.init(context);

		}
		return INSTANCE;
	}

	public void init(Context context) {
		this.context = context;
		Wifi.getInstance(context);
		GPRS.getInstance(context);
	}

	public ENetworkType getActiveNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
		if (activeNetwork == null) {// 没有网络
			return ENetworkType.None;
		}
		android.net.NetworkInfo.State state = activeNetwork.getState();
		if (state == android.net.NetworkInfo.State.CONNECTED) {
			return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) ? ENetworkType.Wifi : ENetworkType.GPRS;
		} else {
			return ENetworkType.None;
		}
	}

	public boolean isNetworkAvailable() {
		try {
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
			return (activeNetwork != null && activeNetwork.isAvailable());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ENetworkType curUsingNetType = ENetworkType.Wifi;// 当前正在使用的网络类型

	public void open(ENetworkType netType) {
		if (netType == ENetworkType.GPRS) {
			Wifi.getInstance(context).Close();
			GPRS.getInstance(context).Open();
		} else {
			GPRS.getInstance(context).Close();
			if (!Wifi.getInstance(context).Open())
				Wifi.getInstance(context).DoConfig();

		}
		curUsingNetType = netType;
		startListen();
	}

	/**
	 * 判断网络是否下载
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;

	}

	/**
	 * 判断网络是否连接，如果未连接，则弹出提示
	 * 
	 * @return
	 */
	public boolean isNetworkConnectedAndNotice() {
		boolean flag = isNetworkConnected();
		if (!flag) {
			PromptUtils.getInstance().showToast("操作失败:网络未连接！", this.context);
		}
		return flag;
	}

	private boolean wifiNoticed = false;

	private void startListen() {
		if (thread == null || !thread.isAlive()) {
			stopFlag = false;
			thread = new Thread() {

				@Override
				public void run() {
					wifiNoticed = false;

					while (!stopFlag) {
						try {
							ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
							if (activeNetwork == null) {// 没有网络

								if (curUsingNetType == ENetworkType.Wifi) {// 说明wifi未连接
									if (wifiConfigHandler != null && !wifiNoticed) {
										wifiNoticed = true;
										Message msg = new Message();
										msg.what = 1023;
										wifiConfigHandler.sendMessage(msg); // 向Handler发送消息,更新UI
										// wifiConfigHandler.showConfig("WIFI未连接，请先选择WIFI连接名及输入密码");
									}
									// Toast.makeText(context,
									// "WIFI未连接，请先选择WIFI连接名及输入密码",
									// Toast.LENGTH_LONG).show();
									// Wifi.getInstance(context).DoConfig();
								}
								continue;
							}

							android.net.NetworkInfo.State state = activeNetwork.getState();

							Log.i("当前网络状态:", state.toString().trim() + ",网络类型:" + activeNetwork.getTypeName());
							if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
								if (state == android.net.NetworkInfo.State.DISCONNECTED) {

									if (wifiConfigHandler != null && !wifiNoticed) {
										Message msg = new Message();
										msg.what = 1023;
										wifiConfigHandler.sendMessage(msg); // 向Handler发送消息,更新UI
										wifiNoticed = true;
									}
									continue;
								}
							}

							if (state == android.net.NetworkInfo.State.CONNECTED) {
								// thread = null;
								Log.i("当前网络状态", "当前网络已连接");
								if (wifiConfigHandler != null) {
									Message msg = new Message();
									msg.what = 1024;
									wifiConfigHandler.sendMessage(msg);
								}
								stopFlag = true;
								thread = null;
								break;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Log.i("当前网络状态", "连接网络时出现如上异常");
							thread = null;
							break;
						} finally {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}
			};
			thread.setName("CheckNetworkThread");
			thread.start();
		}
	}

	private volatile boolean stopFlag = false;

	private void stopListen() {
		stopFlag = true;
		if (thread != null) {
			try {
				thread.join(1000);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			} finally {
				thread = null;
			}
		}
	}

	public boolean getIsConnected() {
		if (curUsingNetType == ENetworkType.GPRS) {
			return GPRS.getInstance(context).isConnected();
		} else {
			return Wifi.getInstance(context).isConnected();
		}
	}

	private ProgressDialog pd;
	ENetworkType selectType = ENetworkType.Wifi;

	/**
	 * 判断网络是否连接，如果未连接，则弹出框，选择网络连接方式
	 */
	public void getIsConnectedAndShowConfig() {
		if (!getIsConnected()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("网络未连接").setIcon(android.R.drawable.ic_dialog_info).setSingleChoiceItems(new String[] { "WIFI", "GPRS" }, 0, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectType = (which == 1) ? ENetworkType.GPRS : ENetworkType.Wifi;

				}
			});

			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					pd = ProgressDialog.show(context, "连接网络", "正在网络,请稍候...");
					// if (wifiConfigHandler == null) {
					wifiConfigHandler = mHandler;
					// }
					open(selectType);
					// pd.dismiss();
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
		}
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1023: {
				Toast.makeText(context, "WIFI未连接，请先选择WIFI连接名及输入密码", Toast.LENGTH_LONG).show();
				Wifi.getInstance(context).DoConfig();
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}
			}

				break;
			default: {
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}
			}
				break;

			}
		}
	};

	public void close() {
		stopListen();
		if (curUsingNetType == ENetworkType.GPRS) {
			GPRS.getInstance(context).Close();
		} else {
			Wifi.getInstance(context).Close();
		}

	}
}
