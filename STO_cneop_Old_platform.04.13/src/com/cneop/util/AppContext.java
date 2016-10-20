package com.cneop.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.view.ViewConfiguration;

import com.cneop.stoExpress.dao.AreaQueryService;

public class AppContext extends Application {
	private SharedPreferences mSp;
	private static AppContext sAppContext = null;
	private String strFirstDir = "DEV";
	private String strSectDir = "STO";
	private String strTragetFileName = "regFile.txt";

	private String getRegFilePath() {
		return Environment.getExternalStorageDirectory() + "/" + strFirstDir + "/" + strSectDir + "/" + strTragetFileName;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		/**
		 * 这就是所谓的低内存运行整个系统时,和 积极运行的进程应该削减内存使用。 而 确切的点,这将是没有定义,一般 它将发生在所有后台进程被杀。
		 * 之前,杀死进程托管 服务和前台界面,我们想避免杀死。
		 * 
		 * 你应该实现这个方法 任何缓存或其他你可能持有不必要的资源。 该系统将执行垃圾收集对你回国后这个方法。
		 */
		super.onTrimMemory(level);
		Runtime.getRuntime().gc();
	}

	@Override
	public void onCreate() {
		sAppContext = this;
		this.mSp = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
		init();

		if (!new AreaQueryService(getApplicationContext()).isExist()) {
			// 预加载区域资料
			new Thread(task).start();
		}
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				new AreaQueryService(getApplicationContext()).addDataFromTxt(getResources().getAssets().open("area.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public void init() {
		ViewConfiguration config = ViewConfiguration.get(this);
		Field menuKeyField;
		try {
			menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static AppContext getAppContext() {
		return sAppContext;
	}

	public String getLoginUserNo() {
		return this.mSp.getString("loginUserNo", "");
	}

	/**
	 * 服务端MD5值
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getMD5() {
		String strResult = "";
		File file = new File(getRegFilePath());// /storage/sdcard0/DEV/STO/regFile.txt
		if (!file.exists() || file.length() == 0) {
			String strMd5 = this.mSp.getString("MD5", "");
			if (!new StrUtil().isNullOrEmpty(strMd5)) {
				if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					setMD5(strMd5);
				}
			}
			strResult = strMd5;
		} else {
			strResult = FileAccess.getInstance(getApplicationContext()).readFileSdcard(getRegFilePath());
		}
		return strResult;
	}

	/**
	 * 设置MD5值
	 * 
	 * @param md5
	 */
	public void setMD5(String md5) {
		String strTargetPath = getRegFilePath();
		File file = new File(strTargetPath);
		try {
			if (!file.exists()) {
				File strSDPath = Environment.getExternalStorageDirectory();
				createDir(strSDPath + "/" + strFirstDir);
				createDir(strSDPath + "/" + strFirstDir + "/" + strSectDir);
				file.createNewFile();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		FileAccess.getInstance(this.getApplicationContext()).chmod("777", strTargetPath);

		FileAccess.getInstance(this.getApplicationContext()).writeFileSdcard(strTargetPath, md5);

		saveData("MD5", md5);
	}

	public void setUpdateApkName(String apkName) {
		Editor editor = this.mSp.edit();
		editor.putString("updateAPKName", apkName).commit();
	}

	private void createDir(String strPath) {
		File file = new File(strPath);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	private void saveData(String key, String value) {
		Editor editor = this.mSp.edit();
		editor.putString(key, value).commit();
	}

	private void saveData(String key, boolean value) {
		Editor editor = this.mSp.edit();
		editor.putBoolean(key, value).commit();
	}

	private void saveData(String key, int value) {
		Editor editor = mSp.edit();
		editor.putInt(key, value).commit();
	}

	// 保存登入值
	public void setLoginValue(String loginUserNo) {
		Editor editor = this.mSp.edit();
		editor.putString("loginUserNo", loginUserNo).commit();
	}

	public boolean getBluetoothIsOpen() {
		return this.mSp.getBoolean("isOpenBluetooth", false);
	}

	public void setBluetoothOpen(boolean flag) {
		saveData("isOpenBluetooth", flag);
	}

	public String getBluetoothAddress() {
		return this.mSp.getString("bluetoothAddress", "");
	}

	public void setBluetoothAddress(String address) {
		saveData("bluetoothAddress", address);
	}

	public String getBluetoothName() {
		return this.mSp.getString("bluetoothName", "");
	}

	public void setBluetoothName(String deviceName) {
		saveData("bluetoothName", deviceName);
	}

	public String getBluetoothCode() {
		return this.mSp.getString("bluetoothCode", "");
	}

	public void setBluetoothCode(String code) {
		saveData("bluetoothCode", code);
	}

	public boolean getBluetoothIsReverse() {
		return this.mSp.getBoolean("isReverse", true);
	}

	public void setBluetoothIsReverse(boolean isReverse) {
		saveData("isReverse", isReverse);
	}

	public int getBluetoothScaleTypeId() {
		return this.mSp.getInt("scaleTypeId", 0);
	}

	public void setBluetoothScaleTypeId(int typeId) {
		saveData("scaleTypeId", typeId);
	}

	public String getBluetoothDataSplit() {
		return this.mSp.getString("dataSplit", "+");
	}

	public void setBluetoothDataSplit(String splitStr) {
		saveData("dataSplit", splitStr);
	}

	public String getUpdateApkName() {
		return this.mSp.getString("updateAPKName", "");
	}

	// 震动
	public void setOpenVibrator(boolean isOpen) {
		saveData("isOpenVibrator", isOpen);
	}

	public boolean getOpenVibrator() {
		return this.mSp.getBoolean("isOpenVibrator", false);
	}

	// 是否锁屏。
	public boolean getIsLockScreen() {
		return this.mSp.getBoolean("isLockScreen", false);
	}

	public void setIsLockScreen(boolean isLock) {
		saveData("isLockScreen", isLock);
	}

}
