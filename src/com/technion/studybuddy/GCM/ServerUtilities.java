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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.technion.studybuddy.Views.StbSettingsActivity;
import com.technion.studybuddy.utils.Constants;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities
{
	// private static String name;

	private static Activity activity;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final int MAX_ATTEMPTS = 5;
	private static final Random random = new Random();
	// TODO chenge to actual user selection
	public static String username = "4ortik@gmail.com";

	private static String capitalize(String s)
	{
		if (s == null || s.length() == 0)
			return "";
		char first = s.charAt(0);
		if (Character.isUpperCase(first))
			return s;
		else
			return Character.toUpperCase(first) + s.substring(1);
	}

	public static String getDeviceName()
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer))
			return capitalize(model);
		else
			return capitalize(manufacturer) + " " + model;
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	public static void post(String endpoint, String regID) throws IOException
	{
		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", ServerUtilities.activity);
		try
		{
			GoogleHttpContext httpContext = CommonUtilities.getContext(
					ServerUtilities.activity, ServerUtilities.username,
					Constants.SERVER_URL);
			HttpPost httpPost = new HttpPost(endpoint);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("regid", regID));
			nameValuePairs
					.add(new BasicNameValuePair("model", getDeviceName()));
			nameValuePairs.add(new BasicNameValuePair("deviceID", Secure
					.getString(ServerUtilities.activity.getContentResolver(),
							Secure.ANDROID_ID)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse res = client.execute(httpPost, httpContext);
			res.toString();
		} finally
		{
			client.close();
		}
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	public static boolean register(final Context context, final String regId)
	{
		Log.i(CommonUtilities.TAG, "registering device (regId = " + regId + ")");
		String serverUrl = Constants.SERVER_URL + "/register";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		long backoff = ServerUtilities.BACKOFF_MILLI_SECONDS
				+ ServerUtilities.random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.

		for (int i = 1; i <= ServerUtilities.MAX_ATTEMPTS; i++)
		{
			Log.d(CommonUtilities.TAG, "Attempt #" + i + " to register");
			try
			{

				post("http://" + serverUrl, regId);
				GCMRegistrar.setRegisteredOnServer(context, true);

				// TODO update server with regid
				return true;
			} catch (IOException e)
			{
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(CommonUtilities.TAG,
						"Failed to register on attempt " + i, e);
				if (i == ServerUtilities.MAX_ATTEMPTS)
				{
					break;
				}
				try
				{
					Log.d(CommonUtilities.TAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1)
				{
					// Activity finished before we complete - exit.
					Log.d(CommonUtilities.TAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		String message = "could not register check connection";
		CommonUtilities.displayMessage(context, message);
		return false;
	}

	public static void registerToServer(Activity activity)
	{
		Object regid;
		setActivity(activity);
		// check if account was configured
		SharedPreferences prefs = activity.getSharedPreferences(
				Constants.PrefsContext, 0);
		String accountName = prefs.getString(Constants.ACCOUNT_NAME, "");
		if ("".equals(accountName))
		{
			Toast.makeText(activity, "please choose account to sign with",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(activity, StbSettingsActivity.class);
			activity.startActivity(intent);
			return;

		}
		regid = CommonUtilities.register(activity);
		if (regid == null)
		{
			GCMRegistrar.register(activity, CommonUtilities.SENDER_ID);
		}
	}

	/**
	 * @param activity
	 *            the activity to set
	 */
	public static synchronized void setActivity(Activity activity)
	{
		ServerUtilities.activity = activity;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId)
	{
		Log.i(CommonUtilities.TAG, "unregistering device (regId = " + regId
				+ ")");
		final String serverUrl = Constants.SERVER_URL + "/unregister";
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					post("http://" + serverUrl, regId);
					GCMRegistrar.setRegisteredOnServer(context, false);
					String message = "unregistering";
					CommonUtilities.displayMessage(context, message);
				} catch (IOException e)
				{
					// At this point the device is unregistered from GCM, but
					// still
					// registered in the server.
					// We could try to unregister again, but it is not
					// necessary:
					// if the server tries to send a message to the device, it
					// will get
					// a "NotRegistered" error message and should unregister the
					// device.
					String message = e.getMessage();
					CommonUtilities.displayMessage(context, message);
				}

			}
		}).start();

	}
}
