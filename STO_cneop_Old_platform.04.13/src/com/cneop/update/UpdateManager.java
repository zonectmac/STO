package com.cneop.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cneop.update.Enums.EUpdateState;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.device.NetworkUtil;
import com.cneop.util.encrypt.MD5;
import com.cneop.util.zip.ZipUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class UpdateManager {
	static Context context;
	private String updateUrl;
	private String companyId;
	boolean isNewVersionTip = false; // ��ǰΪ���°汾�Ƿ���ʾ���û��ֶ�����£�
	private String EnterprisePath; // ����·��
	static boolean isUpdate = false;
	private List<DownItemEnt> lstDownItem;
	static Object waitObject = new Object();
	static private long totalFileLength = 0;
	static private String installApkPath = "";
	static ProgressDialog pd;
	/* ���ر���·�� */
	private String savefolder;
	private static DecimalFormat perFormat = new DecimalFormat("#.##");
	private static boolean currentState = false;

	public UpdateManager(Context context1, String updateUrl, String companyId) {
		context = context1;
		this.updateUrl = updateUrl;
		this.companyId = companyId;
	}

	/**
	 * ��ǰ����״̬
	 * 
	 * @return
	 */
	public boolean getCurrentState() {
		return currentState;
	}

	/*
	 * ���û��ֶ������ʱ��������Ϊtrue
	 */
	public void setNewVersionTip(boolean flag) {
		this.isNewVersionTip = flag;
	}

	// private int totalUnUploadCount =0;
	/*
	 * ����������
	 */
	public void checkUpdate() {
		currentState = true;
		if (!NetworkUtil.getInstance(context).isNetworkAvailable()) {
			if (isNewVersionTip) {
				// ��ǰ���粻���ã�������������!
				sendMsg(EUpdateState.NetworkDisable);
			}
			return;
		}
		// -----------------------------------------
		Thread updateThread = new Thread() {
			@Override
			public void run() {
				if (isUpdate()) {
					// ��ʾ����
					sendMsg(EUpdateState.Discovery);
					synchronized (waitObject) {
						try {
							waitObject.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (isUpdate) {
						// ���ȷ��
						// ��ʼ����
						sendMsg(EUpdateState.Downing);
						downFile();
					} else {
						currentState = false;
					}

				} else {
					if (isNewVersionTip) {
						// �û��ֶ�����£���ʾ��ǰΪ���°汾
						sendMsg(EUpdateState.NewVersion);
					}
					currentState = false;
				}
			}
		};
		updateThread.setName("UpdateThread");
		updateThread.start();
	}

	/*
	 * �ļ�����
	 */
	private void downFile() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			savefolder = Environment.getExternalStorageDirectory().toString().trim();
		} else {
			savefolder = context.getFilesDir().getAbsolutePath();
		}
		// storage/sdcard0
		File fileTemp = new File(savefolder);
		if (!fileTemp.exists()) {
			fileTemp.mkdirs();
		}
		new DownloadApkThread().start();
	}

	/*
	 * ������Ϣ�����½�����ʾ
	 */
	static void sendMsg(EUpdateState updateState) {
		Message msg = handler.obtainMessage();
		msg.what = updateState.value();
		handler.sendMessage(msg);
	}

	static void sendMsg(EUpdateState updateState, int downCount) {
		Message msg = handler.obtainMessage();
		msg.what = updateState.value();
		msg.arg1 = downCount;
		handler.sendMessage(msg);
	}

	static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == EUpdateState.NewVersion.value()) {
				// ��ʾ��ǰ�汾Ϊ����
				currentState = false;
				PromptUtils.getInstance().showToast("��ǰ�������°汾!", context);
			} else if (what == EUpdateState.Discovery.value()) {
				PromptUtils.getInstance().showAlertDialog(context, "�����°汾���Ƿ��������ظ��£�(����ǰ������ϴ�����)", clickEvent,
						clickEvent);
			} else if (what == EUpdateState.Install.value()) {
				pd.dismiss();
				PromptUtils.getInstance().showAlertDialog(context, "�°汾������ɣ��Ƿ����ڰ�װ��", installEvent, null);
				currentState = false;
			} else if (what == EUpdateState.NetworkDisable.value()) {
				currentState = false;
				PromptUtils.getInstance().showToast("��ǰ���粻���ã�������������!", context);
			} else if (what == EUpdateState.Fail.value()) {
				currentState = false;
				// PromptUtils.getInstance().showToast("�ó���IDδ���Ͽ�!", context);
			} else if (what == EUpdateState.other.value()) {
				if (pd != null) {
					pd.dismiss();
				}
				currentState = false;
				PromptUtils.getInstance().showToast("����ʧ�ܣ�������!", context);
			} else if (what == EUpdateState.Downing.value()) {
				pd = ProgressDialog.show(context, "��ʾ", "�������������������Ժ�...");
			} else if (what == EUpdateState.DownProgress.value()) {
				int count = msg.arg1;
				double per = ((double) count / totalFileLength) * 100;
				System.out.println("================update \t" + perFormat.format(per));
				pd.setMessage("�����أ�" + perFormat.format(per) + "%");
			}
		}
	};

	private static OnClickListener installEvent = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				installApk(installApkPath);
				break;
			default:
				break;
			}
		}
	};

	/*
	 * ��װAPP
	 */
	private static void installApk(String packagePath) {
		changePathPopedom(packagePath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(packagePath)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private static void changePathPopedom(String path) {
		String permission = "777";
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener clickEvent = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				isUpdate = true;
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				isUpdate = false;
				break;
			default:
				break;
			}
			synchronized (waitObject) {
				waitObject.notify();
			}
		}
	};

	/*
	 * �Ƿ����
	 */
	private boolean isUpdate() {
		double clientVersionCode = getClientVersionCode();// 1.36

		double serverVersionCode = getServerVersionCode(updateUrl, companyId);// 1.352
		if (serverVersionCode == -1 || clientVersionCode == -1) {
			return false;
		}
		if (serverVersionCode > clientVersionCode) {
			return true;
		}
		return false;
	}

	/*
	 * �ͻ��˰汾��
	 */
	private double getClientVersionCode() {
		double code = -1;
		String versionCode = "";
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			code = Double.parseDouble(versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}

	/*
	 * ��ȡ����˰汾��
	 */
	private double getServerVersionCode(String url, String eid) {
		double dServerVersin = -1;
		NodeList nodeList = httpStringGet("Ver", url, eid);
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element e = (Element) nodeList.item(i);
				String elName = e.getNodeName();
				if (elName.equals("Ver")) {
					String strServerVer = e.getAttribute("ID");
					dServerVersin = Double.valueOf(strServerVer);
					break;
				}
			}
		}
		return dServerVersin;
	}

	private NodeList httpStringGet(String nodeName, String updateUrl, String eid) {
		// This method for HttpConnection
		NodeList nodeList = null;
		try {
			String url = updateUrl + "/pos.update?eid=" + eid;
			System.out.println("==========url \t" + url);
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			HttpGet request = new HttpGet();
			request.addHeader("charset", HTTP.UTF_8);

			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);

			org.apache.http.Header[] headArray = response.getHeaders("Result");
			if (headArray != null && headArray.length > 0) {
				String str4 = headArray[0].getValue();
				if (str4.equals("OK")) {
					org.apache.http.Header[] headEnterisePath = response.getHeaders("Path");
					if (headEnterisePath != null && headEnterisePath.length > 0) {
						String strTemp = headEnterisePath[0].getValue();
						this.EnterprisePath = strTemp;
					}
					DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbfactory.newDocumentBuilder();
					Document document = db.parse(response.getEntity().getContent());// ������ʹ��DOM����XML��׼���������̶�����·
					Element root = document.getDocumentElement();// ��ȡ���ĵ�
					nodeList = root.getElementsByTagName(nodeName);
				} else {
					sendMsg(EUpdateState.Fail);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

		return nodeList;
	}

	/**
	 * ��ȡ�����ļ��б�
	 * 
	 * @param downItemList
	 */
	private boolean getFileListItem() {
		boolean flag = false;
		if (lstDownItem == null) {
			lstDownItem = new ArrayList<DownItemEnt>();
		}
		NodeList nodeList = httpStringGet("File", updateUrl, companyId);
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element e = (Element) nodeList.item(i);
				if (e.getNodeName().equals("File")) {
					DownItemEnt item = new DownItemEnt();
					item.setName(e.getAttribute("Name"));
					item.setLength(Integer.parseInt(e.getAttribute("Length")));
					item.setMd5(e.getAttribute("MD5"));
					lstDownItem.add(item);
					totalFileLength += Long.parseLong(e.getAttribute("Length"));
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * �����߳�
	 * 
	 * @author Administrator
	 * 
	 */
	private class DownloadApkThread extends Thread {

		final int BUFFER_SIZE = 1024;
		private File file;
		// private int endPosition;
		private int completePostion;
		private int curPosition;

		@Override
		public void run() {
			super.run();
			if (!getFileListItem()) {
				// ȡ������
				sendMsg(EUpdateState.other);
				return;
			}

			StringBuilder sbURL = null;
			String fileName = "";
			for (int i = 0; i < lstDownItem.size(); i++) {
				fileName = lstDownItem.get(i).getName();
				sbURL = new StringBuilder();
				sbURL.append(updateUrl).append(File.separator);
				sbURL.append(EnterprisePath).append("/");
				sbURL.append(fileName);
				// http://222.66.109.144/posupgrade//Update/wf/STO_cneop_Old_platform_1.39.zip
				file = new File(savefolder, fileName);
				completePostion = (int) file.length();

				if (downFile(sbURL.toString().trim())) {
					// У�飬��ѹ
					if (lstDownItem.get(i).getMd5().equals(MD5.getMd5Str(file))) {
						if (fileName.endsWith("zip")) {
							try {
								// STO_cneop_Old_platform_1.39.apk
								ZipUtil.unzip(file.toString().trim(), savefolder);
								file.delete();
								File[] files = new File(savefolder).listFiles();
								// -------------------------------------------------------
								for (int j = 0; j < files.length; j++) {
									String tFileName = files[j].getName();

									if (tFileName.endsWith("apk") && tFileName.equals(ZipUtil.AppName)) {
										/**
										 * 2016-4-15�޸� &&
										 * tFileName.equals(ZipUtil.AppName)
										 */
										installApkPath = files[j].getAbsolutePath();
										AppContext.getAppContext().setUpdateApkName(installApkPath);
										sendMsg(EUpdateState.Install);
										System.out.println("installApkPath \t" + installApkPath);
										System.out.println("tFileName \t" + tFileName);
										System.out.println("ZipUtil.AppName \t" + ZipUtil.AppName);

										break;
									}
								}

								// -------------------------------------------------------------------
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							file.delete();
							sendMsg(EUpdateState.other);
						}
					} else {
						file.delete();
						sendMsg(EUpdateState.other);
					}
				} else {
					sendMsg(EUpdateState.other);
				}
			}
		}

		private boolean downFile(String urlStr) {
			boolean flag = false;
			BufferedInputStream bis = null;
			RandomAccessFile fos = null;
			byte[] buf = new byte[BUFFER_SIZE];
			URLConnection con = null;
			try {

				if (completePostion == totalFileLength) {
					return true;
				}
				URL url = new URL(urlStr);
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				// ���õ�ǰ�߳����ص���㣬�յ�
				con.setRequestProperty("range", "bytes=" + completePostion + "-");
				// int endPosition = con.getContentLength();
				curPosition = completePostion;
				fos = new RandomAccessFile(file, "rwd");
				// ���ÿ�ʼд�ļ���λ��
				fos.seek(curPosition);
				bis = new BufferedInputStream(con.getInputStream());
				while (true) {
					int len = bis.read(buf, 0, BUFFER_SIZE);
					if (len == -1) {
						break;
					}
					fos.write(buf, 0, len);
					curPosition += len;
					sendMsg(EUpdateState.DownProgress, curPosition);
				}
				bis.close();
				fos.close();
				flag = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return flag;
		}

	}
}
