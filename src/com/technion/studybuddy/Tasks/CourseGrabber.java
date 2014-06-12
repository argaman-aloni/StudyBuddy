package com.technion.studybuddy.Tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.ServerUtilities;
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

		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		int responseCode = 200;
		try
		{
			GoogleHttpContext httpContext = ServerUtilities.getContext(context,
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
				JSONObject result = new JSONObject(json);

				JSONArray courses = result.getJSONArray("courses");
				for (int i = 0; i < courses.length(); i++)
				{
					JSONObject singleCourse = courses.getJSONObject(i)
							.getJSONObject("course");
					String url = Constants.SERVER_URL_FULL
							+ "/data?type=course.status&id="
							+ singleCourse.getString("id");
					new JsonUpdater(context, "course.status").execute(url);

				}
				return result;
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
	protected void onPostExecute(final JSONObject result)
	{
		if (isError)
			Toast.makeText(context, error, Toast.LENGTH_LONG).show();
		if (result != null)
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						DataStore.getInstance().createCourseFromJson(result);
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
	}
}
