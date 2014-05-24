package com.technion.studybuddy.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.technion.studybuddy.R;

public class STBWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context,
				STBWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int i : allWidgetIds) {
			// TODO: update the widget's view if there is is an update to the
			// data.
			Intent intent = new Intent(context, STBWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.stb_widget_layout);
			rv.setRemoteAdapter(R.id.widget_listview, intent);
			context.startService(intent);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

}
