package com.cneop.stoExpress.datacenter.xml;

import android.content.Context;

import com.cneop.util.StrUtil;

public interface IDownloadXml {

	StrUtil strUtil = new StrUtil();

	/*
	 * Êý¾Ý½âÎö
	 */
	public int dataProcessing(Context context,String downStr) throws Exception;
}
