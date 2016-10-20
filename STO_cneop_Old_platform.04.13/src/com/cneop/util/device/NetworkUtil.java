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
		if (activeNetwork == null) {// û������
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

	public ENetworkType curUsingNetType = ENetworkType.Wifi;// ��ǰ����ʹ�õ���������

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
	 * �ж������Ƿ�����
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
	 * �ж������Ƿ����ӣ����δ���ӣ��򵯳���ʾ
	 * 
	 * @return
	 */
	public boolean isNetworkConnectedAndNotice() {
		boolean flag = isNetworkConnected();
		if (!flag) {
			PromptUtils.getInstance().showToast("����ʧ��:����δ���ӣ�", this.context);
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
							if (activeNetwork == null) {// û������

								if (curUsingNetType == ENetworkType.Wifi) {// ˵��wifiδ����
									if (wifiConfigHandler != null && !wifiNoticed) {
										wifiNoticed = true;
										Message msg = new Message();
										msg.what = 1023;
										wifiConfigHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
										// wifiConfigHandler.showConfig("WIFIδ���ӣ�����ѡ��WIFI����������������");
									}
									// Toast.makeText(context,
									// "WIFIδ���ӣ�����ѡ��WIFI����������������",
									// Toast.LENGTH_LONG).show();
									// Wifi.getInstance(context).DoConfig();
								}
								continue;
							}

							android.net.NetworkInfo.State state = activeNetwork.getState();

							Log.i("��ǰ����״̬:", state.toString().trim() + ",��������:" + activeNetwork.getTypeName());
							if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
								if (state == android.net.NetworkInfo.State.DISCONNECTED) {

									if (wifiConfigHandler != null && !wifiNoticed) {
										Message msg = new Message();
										msg.what = 1023;
										wifiConfigHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
										wifiNoticed = true;
									}
									continue;
								}
							}

							if (state == android.net.NetworkInfo.State.CONNECTED) {
								// thread = null;
								Log.i("��ǰ����״̬", "��ǰ����������");
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
							Log.i("��ǰ����״̬", "��������ʱ���������쳣");
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
	 * �ж������Ƿ����ӣ����δ���ӣ��򵯳���ѡ���������ӷ�ʽ
	 */
	public void getIsConnectedAndShowConfig() {
		if (!getIsConnected()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("����δ����").setIcon(android.R.drawable.ic_dialog_info).setSingleChoiceItems(new String[] { "WIFI", "GPRS" }, 0, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectType = (which == 1) ? ENetworkType.GPRS : ENetworkType.Wifi;

				}
			});

			builder.setPositiveButton("ȷ��", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					pd = ProgressDialog.show(context, "��������", "��������,���Ժ�...");
					// if (wifiConfigHandler == null) {
					wifiConfigHandler = mHandler;
					// }
					open(selectType);
					// pd.dismiss();
				}
			});
			builder.setNegativeButton("ȡ��", new OnClickListener() {
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
				Toast.makeText(context, "WIFIδ���ӣ�����ѡ��WIFI����������������", Toast.LENGTH_LONG).show();
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
