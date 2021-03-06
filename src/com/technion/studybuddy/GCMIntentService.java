/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.technion.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.technion.studybuddy.GCM.Action;
import com.technion.studybuddy.GCM.CommonUtilities;
import com.technion.studybuddy.GCM.GCMAction;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.Tasks.JsonUpdater;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{
	private static final Map<String, Action> actions = new HashMap<String, Action>();
	private static final String TAG = "GCMIntentService";
	static
	{
		GCMIntentService.actions.put(GCMAction.ACK.toString(), new Action()
		{

			@Override
			public void execute(Context context, Bundle bundle)
			{
				// generateNotification(context, bundle.getString("msg"));
				SharedPreferences preferences = context.getSharedPreferences(
						Constants.PrefsContext, 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(Constants.IS_REGISTERED, true);
				editor.putString(Constants.REGID_PREFS,
						bundle.getString("callback"));
				editor.apply();
				DataStore.getInstance().getAllCourses();
			}
		});
		GCMIntentService.actions.put(GCMAction.UPDATE.toString(), new Action()
		{

			@Override
			public void execute(Context context, Bundle bundle)
			{
				String path = bundle.getString("callback");
				new JsonUpdater(context, "course.status").execute(path);
			}
		});
		GCMIntentService.actions.put(GCMAction.DELETE.toString(), new Action()
		{

			@Override
			public void execute(Context context, Bundle bundle)
			{
				Intent intent = new Intent();
				intent.setAction(Constants.DELETECOURSE);
				intent.putExtra("id", bundle.getString("callback"));
				context.sendBroadcast(intent);
			}
		});
	}

	// /**
	// * Issues a notification to inform the user that server has sent a
	// message.
	// */
	// private static void generateNotification(Context context, String message)
	// {
	// int icon = R.drawable.ic_launcher;
	// // long when = System.currentTimeMillis();
	// NotificationManager notificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	//
	// Intent notificationIntent = new Intent(context, MainActivity.class);
	// // set intent so it does not start a new activity
	// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// PendingIntent intent = PendingIntent.getActivity(context, 0,
	// notificationIntent, 0);
	// NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
	// context);
	// mBuilder.setSmallIcon(icon);
	// mBuilder.setContentText(message);
	// mBuilder.setContentTitle(message);
	// mBuilder.setContentIntent(intent);
	// Notification notification = mBuilder.build();
	// notification.flags |= Notification.FLAG_AUTO_CANCEL;
	// notification.flags |= Notification.FLAG_INSISTENT;
	// notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	// notificationManager.notify(0, notification);
	// }

	public GCMIntentService()
	{
		super(CommonUtilities.SENDER_ID);
	}

	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		Log.i(GCMIntentService.TAG, "Received deleted messages notification");

	}

	@Override
	public void onError(Context context, String errorId)
	{
		Log.i(GCMIntentService.TAG, "Received error: " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		GCMIntentService.actions.get(
				intent.getExtras().get("action").toString()).execute(context,
				intent.getExtras());
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId)
	{
		// log message
		Log.i(GCMIntentService.TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		Log.i(GCMIntentService.TAG, "Device registered: regId = "
				+ registrationId);
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId)
	{
		Log.i(GCMIntentService.TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context))
			ServerUtilities.unregister(context, registrationId);
		else
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(GCMIntentService.TAG, "Ignoring unregister callback");
	}

}
