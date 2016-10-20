package com.cneop.stoExpress.util;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

/*
 * �ؼ�������
 */
public class ControlUtil {

	/*
	 * ����EditViewΪ��ǰ����
	 */
	public static void setEditVeiwFocus(EditText et) {
		if (et == null) {
			return;
		}
		et.setFocusableInTouchMode(true);
		et.requestFocus();
	}

	/*
	 * ���ò��ֿ�ȣ�ֻ���������Բ����ֿؼ�
	 */
	public void setControlLayoutWidth(View v, float dpValue, Context context) {
		v.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(context, dpValue), LayoutParams.WRAP_CONTENT));
	}
}
