package com.technion.studybuddy.Views.Activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Adapters.CoursePagerAdapter;
import com.technion.studybuddy.data.DataStore;

public class CourseActivity extends FragmentActivity {
	public static final String COURSE_ID = "COURSE_ID";
	public static final String FRAGMENT = "fragment";
	private String courseNumber;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stb_activity_course);
		Bundle data = getIntent().getExtras();

		if (data.containsKey(CourseActivity.COURSE_ID)) {
			courseNumber = data.getString(CourseActivity.COURSE_ID);
			getActionBar().setTitle(courseNumber);
		}

		CoursePagerAdapter pagerAdapter = new CoursePagerAdapter(
				getSupportFragmentManager(), courseNumber);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);
		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		if (data.containsKey(CourseActivity.FRAGMENT)) {
			data.getString(CourseActivity.FRAGMENT);
			List<String> resourses = new ArrayList<String>(DataStore
					.getInstance().getCoursePresenter(courseNumber)
					.getResourceNames());

			mViewPager.setCurrentItem(resourses.indexOf(data
					.getString(CourseActivity.FRAGMENT)) + 1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stb_course, menu);

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.technion.coolie.CoolieActivity#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.stb_edit_curse:
			Intent intent = new Intent(this, EditCourse.class);
			intent.putExtra(EditCourse.COURSE_ID, courseNumber);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

}
