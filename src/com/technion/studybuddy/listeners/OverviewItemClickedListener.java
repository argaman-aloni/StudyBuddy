package com.technion.studybuddy.listeners;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.technion.studybuddy.Views.LinksViewActivity;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.StudyItem;

public class OverviewItemClickedListener implements OnClickListener {

	StudyItem currentItem;
	Context context;

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(context, LinksViewActivity.class);
		Course course = currentItem.getParent().getParent();
		intent.putExtra("CourseName", course.getName());
		intent.putExtra("courseId", course.getId());
		intent.putExtra("LinksList", formatToSend(currentItem.getLinks()));
		context.startActivity(intent);
	}

	public void setCurrentItem(StudyItem item) {
		if (item != null)
			currentItem = item;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String formatToSend(List<String> links) {
		StringBuilder builder = new StringBuilder();
		if (!links.isEmpty()) {
			for (String link : links)
				builder.append(link).append(" ");
			return builder.toString().substring(0, builder.length() - 1);
		}
		return null;
	}

}
