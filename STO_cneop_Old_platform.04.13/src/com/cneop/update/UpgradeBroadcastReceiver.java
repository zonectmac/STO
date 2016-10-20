package com.cneop.update;

import java.io.File;

import com.cneop.util.AppContext;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class UpgradeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent == null) {
			return;
		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String apkFilePath = AppContext.getAppContext().getUpdateApkName();
			if (apkFilePath.trim().length() > 0) {
				File downloadApk = new File(apkFilePath);
				if (downloadApk.exists()) {
					downloadApk.delete();
				}
			}
			String packageName = intent.getData().getSchemeSpecificPart();
			PackageManager pm = context.getPackageManager();
			Intent inent1 = new Intent();
			inent1 = pm.getLaunchIntentForPackage(packageName);
			inent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(inent1);
		}
	}

}