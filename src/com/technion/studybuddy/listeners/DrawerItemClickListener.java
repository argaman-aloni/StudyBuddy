/**
 * 
 */
package com.technion.studybuddy.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.technion.studybuddy.Views.Activities.CourseActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CourseListPresenter;

/**
 * @author Argaman
 * 
 */
public class DrawerItemClickListener implements OnClickListener
{

	public int position;
	private final CourseListPresenter presenter = DataStore.getMainPresenter();
	public String resourceName = null;

	@Override
	public void onClick(View v)
	{
		Intent intent = new Intent(v.getContext(), CourseActivity.class);
		intent.putExtra(CourseActivity.COURSE_ID,
				presenter.getIdByPosition(position)).putExtra(
				CourseActivity.FRAGMENT, resourceName);
		((Activity) v.getContext()).startActivityForResult(intent,
				Activity.RESULT_CANCELED);
	}

}
