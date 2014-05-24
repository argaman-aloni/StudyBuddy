package com.technion.studybuddy.widget;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.technion.studybuddy.R;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.utils.Constants;

public class STBRemoteViewFactory implements RemoteViewsFactory {

	private Context context;
	private DataStore dataStore;
	private ArrayList<StudyItem> items = new ArrayList<>();
	int widgetId;

	public STBRemoteViewFactory(Context context, Intent intent) {
		super();
		this.context = context;
		DataStore.setContext(context);
		widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate() {
		dataStore = DataStore.getInstance();
		for (Course course : DataStore.coursesList) {
			CoursePresenter cp = dataStore.getCoursePresenter(course.getId());
			if (!cp.getRemaingItems().isEmpty())
				items.add(cp.getRemaingItems().get(0));
		}
	}

	@Override
	public void onDestroy() {
		items.clear();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_list_item);
		Intent fillIntent = new Intent();
		Course course = items.get(position).getParent().getParent();
		rv.setTextViewText(R.id.widget_item_course_name, course.getName());
		rv.setTextViewText(R.id.widget_item_behind_name, items.get(position)
				.getLabel());
		Bundle extras = new Bundle();
		extras.putInt(Constants.EXTRA_ITEM, position);
		fillIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_item_course_name, fillIntent);
		return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onDataSetChanged() {
		// Here I am trying to refresh the view so that when there is a change
		// in the database this method would be called

	}

}
