package com.cneop.util.bluetooth;

import android.annotation.SuppressLint;

/**
 * 负责连接mac，并接收数据
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class BlueToothClientUtils {
	// 固定的UUID
	final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

	public static int mBufferSize = 1024;// 32;

}
