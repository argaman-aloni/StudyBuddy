package com.technion.studybuddy.widget;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.Activities.CourseActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.NoItemsLeftException;
import com.technion.studybuddy.exceptions.NoSuchResourceException;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.presenters.CoursePresenter;

public class STBRemoteViewFactory implements RemoteViewsFactory, Observer
{

	private final Context context;
	private DataStore dataStore;
	private final ArrayList<StudyItem> items = new ArrayList<>();
	int widgetId;

	public STBRemoteViewFactory(Context context, Intent intent)
	{
		super();
		this.context = context;
		DataStore.setContext(context);
		widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate()
	{
		dataStore = DataStore.getInstance();
		DataStore.getStats().addObserver(this);
		initItemsList();
	}

	@Override
	public void onDestroy()
	{
		items.clear();
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public RemoteViews getLoadingView()
	{
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_list_item);
		if (position >= items.size())
			return rv;
		Course course = items.get(position).getParent().getParent();
		rv.setTextViewText(R.id.widget_item_course_name, course.getName());
		rv.setTextViewText(R.id.widget_item_behind_name, items.get(position)
				.getLabel());
		Intent fillIntent = new Intent();
		fillIntent.putExtra(CourseActivity.COURSE_ID, course.getId()).putExtra(
				CourseActivity.FRAGMENT, items.get(position).getItemType());
		rv.setOnClickFillInIntent(R.id.widget_item_layout, fillIntent);
		return rv;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public void onDataSetChanged()
	{
		// Here I am trying to refresh the view so that when there is a change
		// in the database this method would be called
		items.clear();
		initItemsList();
	}

	private void initItemsList()
	{
		for (Course course : DataStore.coursesList)
		{
			CoursePresenter cp = dataStore.getCoursePresenter(course.getId());
			for (String resName : cp.getResourceNames())
				try
				{
					StudyItem nextItem = course.getResourceByName(resName)
							.getNextItem();
					items.add(nextItem);
				} catch (NoItemsLeftException e)
				{
					e.printStackTrace();
				} catch (NoSuchResourceException e)
				{
					e.printStackTrace();
				}
		}
	}

	@Override
	public void update(Observable observable, Object data)
	{
		// TODO Auto-generated method stub
		onDataSetChanged();
	}

}
