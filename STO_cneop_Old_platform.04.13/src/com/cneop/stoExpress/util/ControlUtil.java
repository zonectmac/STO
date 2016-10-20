package com.cneop.stoExpress.util;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

/*
 * 控件帮助类
 */
public class ControlUtil {

	/*
	 * 设置EditView为当前焦点
	 */
	public static void setEditVeiwFocus(EditText et) {
		if (et == null) {
			return;
		}
		et.setFocusableInTouchMode(true);
		et.requestFocus();
	}

	/*
	 * 设置布局宽度，只能用于线性布局字控件
	 */
	public void setControlLayoutWidth(View v, float dpValue, Context context) {
		v.setLayoutParams(new LayoutParams(ScreenUtil.dip2px(context, dpValue), LayoutParams.WRAP_CONTENT));
	}
}
