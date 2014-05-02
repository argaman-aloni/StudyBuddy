package com.technion.studybuddy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.Settings.Secure;

import com.technion.studybuddy.GCM.CommonUtilities;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.utils.Constants;

public class SendTask extends AsyncTask<Void, Void, Void>
{
	private final SyncTask task;
	private final Context context;
	private final NetworkDBAdapter adapter;

	/**
	 * @param task
	 * @param context
	 */
	public SendTask(SyncTask task, Context context)
	{
		super();
		this.task = task;
		this.context = context;
		adapter = new NetworkDBAdapter(context);
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		String username = context.getSharedPreferences(Constants.PrefsContext,
				0).getString(Constants.ACCOUNT_NAME, "");
		task.markOnProgress(adapter);
		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		try
		{
			GoogleHttpContext httpContext = CommonUtilities.getContext(context,
					username, Constants.SERVER_URL);
			HttpPost httpPost = new HttpPost(Constants.DATA_SYNC);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(Constants.JSON_ADDON,
					task.getJSONObject().toString()));
			nameValuePairs.add(new BasicNameValuePair(Constants.TYPE_ADDON,
					task.getType()));
			nameValuePairs
					.add(new BasicNameValuePair("deviceID", Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse res = client.execute(httpPost, httpContext);
			res.toString();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			client.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		task.markQueued(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
		task.markCompleted(adapter);
	}

}
