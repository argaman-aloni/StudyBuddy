package com.technion.studybuddy.Views.Framgments;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Tasks.RegisterCourseCallback;
import com.technion.studybuddy.Tasks.RegisterCourseTask;
import com.technion.studybuddy.utils.Constants;

public class NewCourseFragment extends Fragment implements
		RegisterCourseCallback
{

	private EditText courseNum;
	private String error;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.stb_view_fragment_new_course, null);
		courseNum = (EditText) v.findViewById(R.id.stb_course_num);
		((Button) v.findViewById(R.id.stb_create_course))
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						new AddCourseTask(courseNum.getText().toString())
								.execute();
					}
				});
		return v;

	}

	private class AddCourseTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog dialog;
		private final String id;
		private boolean result;

		/**
		 * @param id
		 */
		public AddCourseTask(String id)
		{
			super();
			this.id = id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result)
		{
			dialog.dismiss();
			if (this.result)
				new RegisterCourseTask(id, NewCourseFragment.this).execute();
			if (!this.result)
				Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(getActivity(), "", "Creating course "
					+ id + ". Please wait...", true);
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			AndroidHttpClient client = AndroidHttpClient.newInstance(
					"GetAuthCookieClient", getActivity());
			try
			{

				HttpGet httpPost = new HttpGet(Constants.SERVER_URL_FULL
						+ "/addcourse?id=" + id);
				HttpResponse res = client.execute(httpPost);
				result = res.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
				switch (res.getStatusLine().getStatusCode())
				{
				case HttpStatus.SC_BAD_REQUEST:
					// no suck course
					error = "No such course number " + id
							+ " or the course does not run this simester";

					return null;
				case HttpStatus.SC_CONFLICT:
					error = "Course " + id + " allready exist in course list";
					return null;
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			} finally
			{
				client.close();
			}
			return null;

		}
	}

	@Override
	public Context getContext()
	{
		return getActivity();
	}

	@Override
	public void update()
	{

	}
}
