package com.cneop.util;

import com.cneop.stoExpress.activity.LoginActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(action_boot)) {
			Intent ootStartIntent = new Intent(context, LoginActivity.class);
			ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(ootStartIntent);
		}
	}

}
