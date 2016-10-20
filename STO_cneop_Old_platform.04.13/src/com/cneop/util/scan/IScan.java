package com.cneop.util.scan;

import com.cneop.util.scan.V6ScanManager.IScanResult;

import android.content.Context;
import android.view.KeyEvent;

public interface IScan {
	public void setPower(boolean isPowerOn);

	public void init(boolean isVibrator, int scanRawResID, Context context);

	public void setIsVibrator(boolean isVibrator);

	public boolean scan(int keyCode, KeyEvent event);

	public void setisDoingOtherOpt(boolean isDoingOtherOpt);

	public void setScanResultHandler(IScanResult scanResult);

	public void setScanMode(boolean isContinue);

	public void stop();

	public void setHomeKeyEnable(boolean isEn, Context context);

	public void setNoticeEnable(boolean isEn, Context context);

}
