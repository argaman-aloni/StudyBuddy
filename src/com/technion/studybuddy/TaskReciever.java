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
		NetworkDBAdapter adapter = new NetworkDBAdapter(context);
		SyncTask task = adapter.addTask(bundle.getString(Constants.JSON_ADDON),
				bundle.getString(Constants.TYPE_ADDON));
		CommonUtilities.Network_Type type = CommonUtilities
				.getNetworkType(context);

		type.runTask(new SendAsyncTask(task, context), context);
		List<SyncTask> remaningTasks = adapter.getTaskNotDone();
		for (SyncTask syncTask : remaningTasks)
		{
			type.runTask(new SendAsyncTask(syncTask, context), context);
		}

	}
}
