package com.technion.studybuddy.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Handler;

import com.technion.studybuddy.R;

public class STBWidgetDataObserver extends ContentObserver {

	private final AppWidgetManager mAppWidgetManager;
	private final ComponentName mComponentName;

	public STBWidgetDataObserver(AppWidgetManager mgr, ComponentName cn,
			Handler h) {
		super(h);
		mAppWidgetManager = mgr;
		mComponentName = cn;
	}

	@Override
	public void onChange(boolean selfChange) {
		mAppWidgetManager.notifyAppWidgetViewDataChanged(
				mAppWidgetManager.getAppWidgetIds(mComponentName),
				R.id.widget_listview);
	}

}
