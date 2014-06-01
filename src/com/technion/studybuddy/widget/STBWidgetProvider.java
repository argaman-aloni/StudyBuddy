package com.technion.studybuddy.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.technion.studybuddy.R;

public class STBWidgetProvider extends AppWidgetProvider {

	public static String CLICK_ACTION = "com.technion.studybuddy.widget.CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context,
				STBWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int i : allWidgetIds) {
			// TODO: update the widget's view if there is is an update to the
			// data.
			Log.d("whidget on update", "before calling the service.");
			Intent intent = new Intent(context, STBWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.stb_widget_layout);
			Log.w("STBWidgetProvider",
					"before calling the STBRemoteViewFactory.");
			rv.setRemoteAdapter(R.id.widget_listview, intent);
			Log.w("STBWidgetProvider",
					"after calling the STBRemoteViewFactory.");
			// rv.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);

			// final Intent onClickIntent = new Intent(context,
			// STBWidgetProvider.class);
			// onClickIntent.setAction(STBWidgetProvider.CLICK_ACTION);
			// onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
			// onClickIntent.setData(Uri.parse(onClickIntent
			// .toUri(Intent.URI_INTENT_SCHEME)));
			// final PendingIntent onClickPendingIntent = PendingIntent
			// .getBroadcast(context, 0, onClickIntent,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// rv.setPendingIntentTemplate(R.id.widget_listview,
			// onClickPendingIntent);

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
