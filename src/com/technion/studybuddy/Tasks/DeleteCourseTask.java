package com.technion.studybuddy.Tasks;

import java.io.IOException;

import org.apache.http.client.methods.HttpDelete;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.NotRegisteredException;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.utils.Constants;

public class DeleteCourseTask extends AsyncTask<String, Void, Void>
{

	private final Context context;

	@Override
	protected Void doInBackground(String... params)
	{
		AndroidHttpClient client = AndroidHttpClient.newInstance(
				"GetAuthCookieClient", context);
		try
		{
			GoogleHttpContext httpContext = ServerUtilities.getContext(context,
					Constants.SERVER_URL);
			HttpDelete httpPost = new HttpDelete(Constants.SERVER_URL_FULL
					+ "/data");
			httpPost.setHeader("id", params[0]);
			httpPost.setHeader("type", "course.unregister");
			client.execute(httpPost, httpContext);

		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (NotRegisteredException e)
		{
			// course cannot be deleted from server if not registered
			return null;
		} finally
		{
			client.close();
		}
		return null;
	}

	/**
	 * @param id
	 * @param name
	 * @param context
	 */
	public DeleteCourseTask(Context context)
	{
		super();
		this.context = context;
	}

}
