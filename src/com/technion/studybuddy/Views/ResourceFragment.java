package com.technion.studybuddy.Views;

import java.util.Date;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.technion.coolie.studybuddy.data.DataStore;
import com.technion.coolie.studybuddy.exceptions.NoSuchResourceException;
import com.technion.coolie.tmp.R;
import com.technion.coolie.tmp.studybuddy.adapters.ResourceGridAdapter;
import com.technion.coolie.tmp.studybuddy.models.StudyResource;

public class ResourceFragment extends SherlockFragment // implements
// CrossGesture
{

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{
		public void onFragmentInteraction(Uri uri);
	}

	private static final String RESOURCE_NAME = "resourceName";

	private static final String COURSEID = "courseId";

	/**
	 * Factory Method for new Fragment generation
	 * 
	 * @param resourceType
	 * @return
	 */
	public static ResourceFragment newInstance(String resourceType,
			String courseID)
	{
		ResourceFragment fragment = new ResourceFragment();
		Bundle args = new Bundle();
		args.putString(RESOURCE_NAME, resourceType);
		args.putString(COURSEID, courseID);
		fragment.setArguments(args);
		return fragment;
	}

	private OnFragmentInteractionListener mListener;

	private ResourceGridAdapter resourceAdapter;

	private StudyResource resource;

	public ResourceFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	public void onButtonPressed(Uri uri)
	{
		if (mListener != null)
		{
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getArguments() == null)
			return;

		String resourceName = getArguments().getString(RESOURCE_NAME);
		String course = getArguments().getString(COURSEID);

		try
		{

			resource = DataStore.coursesById.get(course).getResourceByName(
					resourceName);
		} catch (NoSuchResourceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.stb_view_resource, container,
				false);
		((TextView) view.findViewById(R.id.resource_type)).setText(String
				.valueOf(resource.getNumOfItemsBehind(
						DataStore.semester.getSemesterWeek(new Date()),
						DataStore.semester.getTotalWeeks())) + " " + resource.getName() + " behind");
		resourceAdapter = new ResourceGridAdapter(getActivity(),
				resource.getItems());

		((NowLayout) view.findViewById(R.id.stb_resource_list))
				.setAdapter(resourceAdapter);

		return view;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

}
