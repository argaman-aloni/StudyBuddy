/**
 * 
 */
package com.technion.studybuddy.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.Views.CourseActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CourseListPresenter;

/**
 * @author Argaman
 *
 */
public class DrawerItemClickListener implements OnItemClickListener {

	private CourseListPresenter presenter = DataStore.getMainPresenter();
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		String courseName = (String)adapter.getItemAtPosition(position);
		Intent intent = new Intent(view.getContext().getApplicationContext(), CourseActivity.class);
		intent.putExtra(CourseActivity.COURSE_ID,
				presenter.getIdByPosition(position)).putExtra(
				CourseActivity.FRAGMENT, courseName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		(view.getContext()).startActivity(intent);
	}

}
