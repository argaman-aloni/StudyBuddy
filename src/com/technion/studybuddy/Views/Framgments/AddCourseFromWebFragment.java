package com.technion.studybuddy.Views.Framgments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.technion.studybuddy.R;
import com.technion.studybuddy.GCM.CommonUtilities;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.Tasks.RegisterCourseCallback;
import com.technion.studybuddy.Tasks.RegisterCourseTask;
import com.technion.studybuddy.Views.NowLayout;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

public class AddCourseFromWebFragment extends Fragment implements
		RegisterCourseCallback
{

	private NowLayout courses;
	public AddCourseAdapter adapter;

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
		View view = inflater.inflate(R.layout.stb_view_add_course_web, null);
		courses = (NowLayout) view.findViewById(R.id.courses_list);
		new GetCourses().execute();
		return view;
	}

	private class AddCourseAdapter extends BaseAdapter
	{

		List<String> names;
		List<String> ids;

		/**
		 * @param names
		 * @param ids
		 */
		public AddCourseAdapter(List<String> names, List<String> ids)
		{
			super();
			this.names = names;
			this.ids = ids;
		}

		@Override
		public int getCount()
		{
			return names.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			Holder holder = null;
			if (convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(
						R.layout.stb_view_single_course_list_item, null);
				holder = new Holder(
						(TextView) convertView.findViewById(R.id.course_name),
						(TextView) convertView.findViewById(R.id.course_id),
						(TextView) convertView.findViewById(R.id.course_taken));
				convertView.setTag(holder);
				convertView.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (!DataStore.getInstance()
								.contains(ids.get(position)))
							new RegisterCourseTask(ids.get(position),
									AddCourseFromWebFragment.this).execute();
						else
							Toast.makeText(getActivity(),
									"Course allready registered",
									Toast.LENGTH_LONG).show();
					}
				});
			} else
				holder = (Holder) convertView.getTag();
			holder.id.setText(ids.get(position));
			holder.name.setText(names.get(position));
			holder.taken.setText((DataStore.getInstance().contains(
					ids.get(position)) ? "Taken" : ""));
			return convertView;
		}
	}

	private class Holder
	{
		TextView name;
		TextView id;
		TextView taken;

		/**
		 * @param name
		 * @param id
		 * @param taken
		 */
		public Holder(TextView name, TextView id, TextView taken)
		{
			super();
			this.name = name;
			this.id = id;
			this.taken = taken;
		}
	}

	private class GetCourses extends AsyncTask<Void, Void, JSONArray>
	{

		private ProgressDialog dialog;

		@Override
		protected JSONArray doInBackground(Void... params)
		{
			Looper.prepare();
			AndroidHttpClient client = AndroidHttpClient.newInstance(
					"GetAuthCookieClient", getActivity());
			try
			{
				GoogleHttpContext httpContext = CommonUtilities.getContext(
						getActivity(), Constants.SERVER_URL);
				HttpGet httpGet = new HttpGet(Constants.SERVER_URL_FULL
						+ "/data?type=all");
				HttpResponse res = client.execute(httpGet, httpContext);

				String json = EntityUtils.toString(res.getEntity());
				return new JSONArray(json);
			} catch (IOException | JSONException e)
			{
				Toast.makeText(getActivity(),
						"No internet conneticity please try again later",
						Toast.LENGTH_LONG).show();
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
		protected void onPostExecute(JSONArray result)
		{
			if (result == null)
			{
				dialog.dismiss();
				return;
			}
			List<String> names = new ArrayList<>();
			List<String> ids = new ArrayList<>();
			try
			{
				for (int i = 0; i < result.length(); i++)
				{
					names.add(result.getJSONObject(i).getString("name"));
					ids.add(result.getJSONObject(i).getString("id"));
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			adapter = new AddCourseAdapter(names, ids);
			courses.setAdapter(adapter);
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
			dialog = ProgressDialog.show(getActivity(), "",
					"Loading courses. Please wait...", true);
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
		adapter.notifyDataSetChanged();

	}

}
