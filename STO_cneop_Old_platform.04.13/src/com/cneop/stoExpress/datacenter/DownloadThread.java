package com.cneop.stoExpress.datacenter;

import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.SystimeSyncThread.ISyncSuc;
import com.cneop.stoExpress.datacenter.download.DownloadManager;
import com.cneop.stoExpress.datacenter.download.SJMDSendedRecords;
import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.util.BrocastUtil;
import android.content.Context;

public class DownloadThread extends Thread {

	public static DownloadThread downloadThread;
	private Context context;
	private BrocastUtil brocastUtil;
	private boolean isHandleDownload = false;

	public void setHandleDownload(boolean isHandleDownload) {
		this.isHandleDownload = isHandleDownload;
	}

	private DownloadThread(Context context) {
		this.context = context;
		brocastUtil = new BrocastUtil(context);
	}

	private ISyncSuc syncSuc = null;

	public void setSyncSuc(ISyncSuc _sync) {
		syncSuc = _sync;
	}

	public static DownloadThread getIntance(Context context) {
		if (downloadThread == null) {
			downloadThread = new DownloadThread(context);
		}
		return downloadThread;
	}

	/*
	 * 启动线程
	 */
	public void StartDownload() {
		if (downloadThread != null) {
			if (downloadThread.getState() == Thread.State.NEW) {
				downloadThread.start();
			}
		} else {
			getIntance(context).start();
		}
	}

	@Override
	public void run() {
		brocastUtil.sendDownloadPicStatus(true);

		// 后台增量下载基础表
		DownloadManager downloadManager = new DownloadManager(context, GlobalParas.getGlobalParas().getStationId());
		ERegResponse regResponse = downloadManager.register();
		if (regResponse == ERegResponse.success) {
			downloadManager.downloadData(EDownType.Station);
			downloadManager.downloadData(EDownType.Abnormal);
			downloadManager.downloadData(EDownType.User);
			downloadManager.downloadData(EDownType.Route);
			downloadManager.downloadData(EDownType.Destination);
			downloadManager.downloadData(EDownType.NextStation);
			downloadManager.downloadData(EDownType.OrderAbnormal);
			SJMDSendedRecords.download(context, this.isHandleDownload);
			setHandleDownload(false);
			if (syncSuc != null) {
				syncSuc.OnHandleResult(true, "数据更新成功", SystimeSyncThread.HANDLE_SYNC_WHAT);
			}
		} else {
			if (syncSuc != null) {
				syncSuc.OnHandleResult(true, "数据更新失败", SystimeSyncThread.HANDLE_SYNC_WHAT);
			}
		}

		brocastUtil.sendDownloadPicStatus(false);

		downloadThread = null;
	}

}
