package com.technion.studybuddy.Tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.NotRegisteredException;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.utils.Constants;

public class RegisterCourseTask extends AsyncTask<Void, Void, Void>
{
	private ProgressDialog dialog;
	private final String id;
	private final RegisterCourseCallback callback;

	/**
	 * @param view
	 * @param string
	 */
	public RegisterCourseTask(String _id, RegisterCourseCallback context)
	{
		super();
		callback = context;
		id = _id;
	}

	@Override
	protected Void doInBackground(Void... params)
	{

		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", callback.getContext());
		try
		{
			GoogleHttpContext httpContext = ServerUtilities.getContext(
					callback.getContext(), Constants.SERVER_URL);
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL_FULL
					+ "/data");
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("type", "courseTaken"));
			parameters.add(new BasicNameValuePair("id", id));
			HttpEntity entity = new UrlEncodedFormEntity(parameters);
			httpPost.setEntity(entity);
			HttpResponse res = client.execute(httpPost, httpContext);
			res.toString();

		} catch (IOException | NotRegisteredException e)
		{
			return null;
		} finally
		{
			client.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Void result)
	{
		new DownloadCourseNoContext(callback.getContext(), id).execute();
		dialog.dismiss();
		callback.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog = ProgressDialog.show(callback.getContext(), "",
				"Registering course " + id + ". Please wait...", true);
	}
}
