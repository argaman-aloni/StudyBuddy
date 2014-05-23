package com.technion.studybuddy.listeners;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.technion.studybuddy.Views.Activities.LinksViewActivity;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.StudyItem;

public class OverviewItemClickedListener implements OnClickListener
{
	StudyItem currentItem;
	Activity context;

	@Override
	public void onClick(View v)
	{
		Intent intent = new Intent(context, LinksViewActivity.class);
		Course course = currentItem.getParent().getParent();
		intent.putExtra("CourseName", currentItem.getLabel());
		intent.putExtra("courseId", course.getId());
		intent.putExtra("LinksList", formatToSend(currentItem.getLinks()));
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		String PACKAGE = context.getPackageName();
		intent.putExtra(PACKAGE + ".left", location[0]);
		intent.putExtra(PACKAGE + ".top", location[1]);
		intent.putExtra(PACKAGE + ".width", v.getWidth());
		intent.putExtra(PACKAGE + ".height", v.getHeight());
		context.startActivity(intent);
		context.overridePendingTransition(0, 0);
	}

	public void setCurrentItem(StudyItem item)
	{
		if (item != null)
			currentItem = item;
	}

	public void setContext(Activity context)
	{
		this.context = context;
	}

	public String formatToSend(List<String> links)
	{
		StringBuilder builder = new StringBuilder();
		if (!links.isEmpty())
		{
			for (String link : links)
				builder.append(link).append(" ");
			return builder.toString().substring(0, builder.length() - 1);
		}
		return null;
	}

}
