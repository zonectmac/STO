package com.cneop.stoExpress.datacenter.download;

import java.util.List;

import android.content.Context;
import com.cneop.stoExpress.dao.ServerStationService;
import com.cneop.stoExpress.model.ServerStationModel;
import com.cneop.util.json.JsonUtil;

class ServerStation implements IDownload {
	ServerStationService serverStationService;

	@Override
	public int dataProcessing(Context context, String downStr) {
		int result = 0;
		setContext(context);
		List<Object> list = JsonUtil.jsonToList(downStr, new ServerStationModel());
		if (list != null) {
			serverStationService.delRecord(null, null);
			result = serverStationService.addRecord(list);
		}
		return result;
	}

	private void setContext(Context context) {
		if (serverStationService == null) {
			serverStationService = new ServerStationService(context);
		}
	}

}
