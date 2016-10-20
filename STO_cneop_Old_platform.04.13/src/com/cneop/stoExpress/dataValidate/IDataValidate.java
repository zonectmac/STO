package com.cneop.stoExpress.dataValidate;

import android.widget.EditText;

public interface IDataValidate {
	
	/*
	 * 显示名称
	 */
	public void showName(EditText et);
	
	/*
	 * 还原编号显示
	 */
	public void restoreNo(EditText et);
	
	/*
	 * 输入验证：必填或输入较验
	 */
	public boolean vlidateInputData(EditText et);
	
}
