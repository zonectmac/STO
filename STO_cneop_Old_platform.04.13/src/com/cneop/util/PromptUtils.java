package com.cneop.util;

import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.util.device.DeviceUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Toast;

public class PromptUtils {
	private AlertDialog sAlertDialog;
	private static PromptUtils sIntance;

	public void closeAlertDialog() {
		if (sAlertDialog != null) {
			this.paramOnClickListener1 = null;
			sAlertDialog.dismiss();
			sAlertDialog = null;
		}
	}

	public static PromptUtils getInstance() {
		if (sIntance == null) {
			sIntance = new PromptUtils();
		}
		return sIntance;
	}

	public void showToast(String msg, Context context) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

	}

	public void showAlertDialog(Context paramContext, int paramInt, DialogInterface.OnClickListener paramOnClickListener) {
		showAlertDialog(paramContext, paramContext.getString(paramInt), paramOnClickListener);
	}

	public void showAlertDialogHasFeel(Context paramContext, String paramString, DialogInterface.OnClickListener paramOnClickListener, EVoiceType voiceType, double seconds) {
		VibratorUtil.getIntance(paramContext).startVibrator(seconds);
		SoundUtil.getIntance(paramContext).PlayVoice(voiceType);
		showAlertDialog(paramContext, paramString, paramOnClickListener);
	}

	public void showToastHasFeel(String msg, Context context, EVoiceType voiceType, double seconds) {
		VibratorUtil.getIntance(context).startVibrator(seconds);
		SoundUtil.getIntance(context).PlayVoice(voiceType);
		showToast(msg, context);
	}

	public void showAlertDialog(Context paramContext, int paramInt, DialogInterface.OnClickListener paramOnClickListener1, DialogInterface.OnClickListener paramOnClickListener2) {
		showAlertDialog(paramContext, paramContext.getString(paramInt), paramOnClickListener1, paramOnClickListener2);
	}

	public void showAlertDialog(Context paramContext, String paramString, DialogInterface.OnClickListener paramOnClickListener) {
		try {
			closeAlertDialog();
			this.paramOnClickListener1 = paramOnClickListener1;
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
			localBuilder.setIcon(android.R.drawable.ic_dialog_info);
			localBuilder.setTitle("提示");
			localBuilder.setMessage(paramString);
			localBuilder.setPositiveButton("确定", paramOnClickListener);
			localBuilder.setOnKeyListener(onLeftKeyDownListner);

			sAlertDialog = localBuilder.create();
			// sAlertDialog.setOnKeyListener(onLeftKeyDownListner);
			sAlertDialog.show();
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	protected OnKeyListener onLeftKeyDownListner = new OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == DeviceUtil.getLeftKeyCode() && event.getRepeatCount() == 0) {
				if (paramOnClickListener1 != null) {

					paramOnClickListener1.onClick(dialog, 0);
					closeAlertDialog();
				}

				return false;
			}
			return false;
		}

	};

	private DialogInterface.OnClickListener paramOnClickListener1;

	public void showAlertDialog(Context paramContext, String paramString, DialogInterface.OnClickListener paramOnClickListener1, DialogInterface.OnClickListener paramOnClickListener2) {
		try {
			closeAlertDialog();
			this.paramOnClickListener1 = paramOnClickListener1;
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
			localBuilder.setIcon(android.R.drawable.ic_dialog_info);
			localBuilder.setTitle("详情");
			localBuilder.setMessage(paramString);
			localBuilder.setPositiveButton("确定", paramOnClickListener1);
			localBuilder.setNegativeButton("取消", paramOnClickListener2);
			localBuilder.setOnKeyListener(onLeftKeyDownListner);
			sAlertDialog = localBuilder.create();
			sAlertDialog.show();
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
}
