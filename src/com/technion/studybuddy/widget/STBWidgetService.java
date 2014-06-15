package com.technion.studybuddy.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class STBWidgetService extends RemoteViewsService
{

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		// Log.w("onGetViewFactory",
		// "before calling the STBRemoteViewFactory.");
		return new STBRemoteViewFactory(this.getApplicationContext(), intent);
	}

}
