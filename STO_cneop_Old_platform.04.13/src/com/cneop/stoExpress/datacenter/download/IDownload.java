package com.cneop.stoExpress.datacenter.download;

import com.cneop.util.StrUtil;

import android.content.Context;

interface IDownload {
	
	StrUtil strUtil=new StrUtil();
	
	/*
	 * ���ݽ���
	 */
	public int dataProcessing(Context context,String downStr);
		
}
