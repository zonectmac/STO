package com.cneop.stoExpress.dataValidate;

import android.widget.EditText;

public interface IDataValidate {
	
	/*
	 * ��ʾ����
	 */
	public void showName(EditText et);
	
	/*
	 * ��ԭ�����ʾ
	 */
	public void restoreNo(EditText et);
	
	/*
	 * ������֤��������������
	 */
	public boolean vlidateInputData(EditText et);
	
}
