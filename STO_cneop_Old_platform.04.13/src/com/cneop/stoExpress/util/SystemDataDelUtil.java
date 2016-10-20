package com.cneop.stoExpress.util;

import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.dao.ScanDataService;

import android.content.Context;

public class SystemDataDelUtil {
	ScanDataService scanDataService;
	OrderOperService orderOperService;
	MsgSendService msgSendService;

	public SystemDataDelUtil(Context context) {
		scanDataService = new ScanDataService(context);
		orderOperService = new OrderOperService(context);
		msgSendService = new MsgSendService(context);
	}

	public boolean clearData() {
		boolean flag = true;
		try {
			orderOperService.delData();
			msgSendService.delData();
			for (int j = 8; j > 0; j--) {
				int day = j * 7;
				scanDataService.delData(day);
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
