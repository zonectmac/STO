package com.cneop.stoExpress.dataValidate;

import android.content.Context;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.StrUtil;

public class DataValidate {
	StrUtil strUtil;
	Context context;
	ControlUtil controlUtil;

	public DataValidate(Context context) {
		strUtil = new StrUtil();
		controlUtil=new ControlUtil();
		this.context=context;
	}
}
