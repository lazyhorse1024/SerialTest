package com.skypine.serialtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MountReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		Log.i("serial MountReceiver", "onReceive--->action=" + action);
		if (Intent.ACTION_MEDIA_MOUNTED.equals(action))
		{
			String mountPath = intent.getData().getPath();
			Log.i("MountReceiver", "onReceive--->mountPath=" + mountPath);
			CheckFileController checkFileController = CheckFileController
					.getInstance();
			checkFileController.setMountPath(mountPath);
			if (checkFileController.checkSerialTestFile())
			{
				Log.i("serial MountReceiver", "open SerialTestActivity");
				Intent intentActvity = new Intent(context,
						SerialTestActivity.class);
				intentActvity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentActvity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				context.startActivity(intentActvity);
			}
		}

	}

}
