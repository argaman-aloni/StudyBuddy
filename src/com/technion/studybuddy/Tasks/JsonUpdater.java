package com.technion.studybuddy.Tasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.NotRegisteredException;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

public class JsonUpdater extends AsyncTask<String, Void, JSONObject>
{

	private final Context context;
	private final String type;

	/**
	 * @param context
	 * @param type
	 */
	public JsonUpdater(Context context, String type)
	{
		super();
		this.context = context;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected JSONObject doInBackground(String... params)
	{

		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		try
		{
			GoogleHttpContext httpContext = ServerUtilities.getContext(context,
					Constants.SERVER_URL);
			HttpGet httpGet = new HttpGet(params[0]);
			HttpResponse res = client.execute(httpGet, httpContext);
			String json = EntityUtils.toString(res.getEntity());
			return new JSONObject(json);
		} catch (IOException | JSONException e)
		{
			e.printStackTrace();
		} catch (NotRegisteredException e)
		{
			// cannot sync without user
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
	protected void onPostExecute(JSONObject result)
	{
		if (result == null)
			return;
		try
		{

			switch (type)
			{
			case "course.update":

				break;
			case "course":
				DataStore.getInstance().createCourseFromJson(result);

				break;
			case "course.status":
				DataStore.getInstance().updateCourseFromJson(result);
				break;
			default:
				break;
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
