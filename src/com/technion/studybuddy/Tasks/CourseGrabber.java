package com.technion.studybuddy.Tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.technion.studybuddy.GCM.CommonUtilities;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

public class CourseGrabber extends AsyncTask<Void, Void, JSONObject>
{
	private final Context context;
	private String error;
	private boolean isError = false;

	/**
	 * @param context
	 */
	public CourseGrabber(Context context)
	{
		super();
		this.context = context;
	}

	@Override
	protected JSONObject doInBackground(Void... params)
	{
		context.getSharedPreferences(Constants.PrefsContext, 0).getString(
				Constants.ACCOUNT_NAME, "");
		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		int responseCode = 200;
		try
		{
			GoogleHttpContext httpContext = CommonUtilities.getContext(context,
					Constants.SERVER_URL);
			HttpGet httpGet = new HttpGet("http://"
					+ Constants.DATA_SYNC
					+ "?"
					+ Constants.TYPE_ADDON
					+ "="
					+ Constants.COURSE_ALL
					+ "&deviceID="
					+ Secure.getString(context.getContentResolver(),
							Secure.ANDROID_ID));
			HttpResponse res = client.execute(httpGet, httpContext);
			responseCode = res.getStatusLine().getStatusCode();
			String json = EntityUtils.toString(res.getEntity());

			try
			{
				return new JSONObject(json);

			} catch (JSONException e)
			{
				isError = true;
				error = "error : status code is " + responseCode
						+ "\nerror msg is " + e.getMessage();
				e.printStackTrace();
			}

		} catch (Exception e)
		{
			isError = true;
			error = "error : status code is " + responseCode
					+ "\nerror msg is " + e.getMessage();

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
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(JSONObject result)
	{
		if (isError)
			Toast.makeText(context, error, Toast.LENGTH_LONG).show();
		try
		{
			if (result != null)
				DataStore.getInstance().createCourseFromJson(result);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
