package com.technion.studybuddy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.Activities.CourseActivity;

public class STBWidgetProvider extends AppWidgetProvider
{

	public static String CLICK_ACTION = "com.technion.studybuddy.widget.CLICK";
	public static String REFRESH_ACTION = "com.technion.studybuddy.widget.REFRESH";
	private RemoteViews rv;

	@Override
	public void onEnabled(Context context)
	{
		context.getContentResolver();
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// if (null != rv)
		// Log.d("reached to onRecive",
		// "I got here from..... " + intent.getStringExtra("Caller"));
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		ComponentName thisWidget = new ComponentName(context,
				STBWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int i : allWidgetIds)
		{
			// TODO: update the widget's view if there is is an update to the
			// data.
			// Log.d("whidget on update", "before calling the service.");
			Intent intent = new Intent(context, STBWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			rv = new RemoteViews(context.getPackageName(),
					R.layout.stb_widget_layout);
			rv.setRemoteAdapter(R.id.widget_listview, intent);

			Intent startActivityIntent = new Intent(context,
					CourseActivity.class);
			startActivityIntent
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
			startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivityIntent.setData(Uri.parse(intent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent pendIntent = PendingIntent.getActivity(context, 0,
					startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.widget_listview, pendIntent);
			rv.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);

			appWidgetManager.updateAppWidget(i, rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions)
	{
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

}
