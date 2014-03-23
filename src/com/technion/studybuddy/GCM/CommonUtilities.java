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
package com.technion.studybuddy.GCM;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.technion.studybuddy.utils.Constants;
import com.google.android.gcm.GCMRegistrar;
import com.technion.studybuddy.exceptions.AccessException;


/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities
{

	/**
	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
	 */
	
	
	// public static final String SERVER_URL_DEV = "192.168.1.104:8888";

	
	/**
	 * Google API project id registered to use GCM.
	 */
	public static final String SENDER_ID = "807527031024";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMDemo";

	/**
	 * Intent used to display a message in the screen.
	 */
	static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	static final String EXTRA_MESSAGE = "message";

	public static GoogleHttpContext getContext(Activity activity,
			String username, String baseUrl)
	{
		try
		{
			return Constants.debug ? new GoogleHttpContextDev(activity, username, baseUrl)
					: new GoogleHttpContextProduction(activity, username,
							baseUrl);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (OperationCanceledException e)
		{
			e.printStackTrace();
		} catch (AuthenticatorException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (AccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	static void displayMessage(Context context, String message)
	{
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	private static void checkNotNull(Object reference, String name)
	{
		if (reference == null)
		{
			throw new NullPointerException(name + " is null");
		}
	}

	public static String register(final Activity context)
	{
		checkNotNull(Constants.SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(context);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(context);

		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals(""))
		{
			// Automatically registers application on startup.
			GCMRegistrar.register(context, SENDER_ID);

		} else
		{
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(context))
			{
				// Skips registration.
				return null;
			} else
			{
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.

				final AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>()
				{

					@Override
					protected Void doInBackground(Void... params)
					{
						boolean registered = ServerUtilities.register(context,
								regId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered)
						{
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result)
					{
						// mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}

		}
		return regId;
	}
}
