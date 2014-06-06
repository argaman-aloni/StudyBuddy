package com.technion.studybuddy.Tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.Toast;

import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

public class DownloadCourseNoContext extends AsyncTask<Void, Void, JSONObject>
{

	private boolean isError;
	private String error;
	private final Context context;
	private final String id;
	ProgressDialog dialog;

	public DownloadCourseNoContext(Context context2, String id)
	{
		context = context2;
		this.id = id;
	}

	@Override
	protected JSONObject doInBackground(Void... params)
	{
		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		int responseCode = 200;
		try
		{

			HttpGet httpGet = new HttpGet("http://" + Constants.DATA_SYNC + "?"
					+ Constants.TYPE_ADDON + "=course.single&id=" + id);

			HttpResponse res = client.execute(httpGet);
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
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("courses", new JSONArray().put(new JSONObject()
						.put("course", result)));
				DataStore.getInstance().createCourseFromJson(jsonObject);
			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog.dismiss();
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
		dialog = ProgressDialog.show(context, "", "Downloading course " + id
				+ ". Please wait...", true);
	}

}
