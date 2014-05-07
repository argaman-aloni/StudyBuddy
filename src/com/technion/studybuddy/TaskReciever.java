package com.technion.studybuddy;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technion.studybuddy.GCM.CommonUtilities;
import com.technion.studybuddy.network.NetworkDBAdapter;
import com.technion.studybuddy.network.SendAsyncTask;
import com.technion.studybuddy.network.SyncTask;
import com.technion.studybuddy.utils.Constants;

public class TaskReciever extends BroadcastReceiver
{
	public TaskReciever()
	{
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle bundle = intent.getExtras();
		CommonUtilities.Network_Type type = CommonUtilities
				.getNetworkType(context);
		NetworkDBAdapter adapter = new NetworkDBAdapter(context);
		if (bundle.containsKey(Constants.JSON_ADDON))
		{
			SyncTask task = adapter.addTask(
					bundle.getString(Constants.JSON_ADDON),
					bundle.getString(Constants.OBJECT_TYPE_ADDON));

			type.runTask(new SendAsyncTask(task, context), context);
		}
		List<SyncTask> remaningTasks = adapter.getTaskNotDone();
		for (SyncTask syncTask : remaningTasks)
		{
			type.runTask(new SendAsyncTask(syncTask, context), context);
		}

	}
}