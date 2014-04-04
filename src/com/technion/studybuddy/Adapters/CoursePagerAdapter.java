package com.technion.studybuddy.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.technion.studybuddy.Views.CourseOverViewFragment;
import com.technion.studybuddy.Views.ResourceFragment;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;

public class CoursePagerAdapter extends FragmentStatePagerAdapter
{

	private final String courseNumber;
	private final CoursePresenter presenter;

	public CoursePagerAdapter(FragmentManager fm, String courseNumber)
	{
		super(fm);
		this.courseNumber = courseNumber;
		presenter = DataStore.getInstance().getCoursePresenter(courseNumber);
	}

	@Override
	public int getCount()
	{
		return 3;
	}

	@Override
	public Fragment getItem(int itemPosition)
	{
		switch (itemPosition)
		{
		case 0:
			Fragment fragment = CourseOverViewFragment
					.newInstance(courseNumber);
			return fragment;
		case 1:
		case 2:
			ResourceFragment fragment1 = ResourceFragment.newInstance(
					presenter.getResourceName(itemPosition - 1), courseNumber);
			return fragment1;
		default:
			return null;

		}

	}

}
