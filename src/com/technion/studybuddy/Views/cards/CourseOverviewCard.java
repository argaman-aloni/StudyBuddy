package com.technion.studybuddy.Views.cards;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.CreateReviewPointActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.utils.DateUtils;
import com.todddavies.components.progressbar.ProgressWheel;

public class CourseOverviewCard extends BaseCard
{
	// private int index;
	private CoursePresenter presenter;
	private String courseID;
	private BaseAdapter baseAdapter;

	public CourseOverviewCard(String courseID, CardUI cardUI, CardStack stack,
			int index, BaseAdapter adapter)
	{
		super(cardUI, stack);
		this.baseAdapter = adapter;
		// this.index = index;
		this.courseID = courseID;
		presenter = DataStore.getInstance().getCoursePresenter(courseID);// DataStore.getMainPresenter();
	}

	@Override
	protected void applyTo(View convertView)
	{
		TextView titleTextView = (TextView) convertView
				.findViewById(R.id.stb_course_item_title);
		titleTextView.setText(presenter.getName());
		TextView desctiption = (TextView) convertView
				.findViewById(R.id.stb_course_item_description);
		desctiption.setText("course " + courseID);
		convertView.findViewById(R.id.stb_stripe).setBackgroundResource(
				R.drawable.stb_graient);
		final ProgressWheel pw = (ProgressWheel) convertView
				.findViewById(R.id.stb_progressBar);
		TextView lastStudied = (TextView) convertView
				.findViewById(R.id.stb_card_last_study);
		// TODO change to last time studied
		Date date = presenter.getLastDateStudied();
		Date now = new Date();
		int lastTimestudied = DateUtils.daysInRange(now, date);
		if (lastTimestudied > 0)
			lastStudied.setText("last time studied : " + lastTimestudied
					+ " days ago");
		else
			lastStudied.setText("last time studied : today");

		int total = presenter.getNumOfItemsDue(now);
		int done = 0;

		for (String resourceName : presenter.getResourceNames())
		{
			done += presenter.getDoneItemsCount(resourceName);
		}

		pw.setProgress(total == 0 ? 0 : 360 * done / total);
		pw.setText(done + "/" + total);

		final ImageView imageView = (ImageView) convertView
				.findViewById(R.id.stb_overflow);
		imageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				try
				{
					PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
					popupMenu.setOnMenuItemClickListener( new PopupMenuListner(v.getContext()));
					popupMenu.inflate(R.menu.stb_course_card_menu);
					popupMenu.show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected int getCardLayoutId()
	{
		return R.layout.stb_view_course_item;
	}

	private class PopupMenuListner implements
			OnMenuItemClickListener
	{
		private Context context;

		/**
		 * @param context
		 */
		public PopupMenuListner(Context context)
		{
			super();
			this.context = context;
		}

		@Override
		public boolean onMenuItemClick(MenuItem item)
		{
			switch (item.getItemId())
			{

			case R.id.stb_discard_curse:
				DataStore.getInstance().deleteCourse(courseID);
				baseAdapter.notifyDataSetChanged();
				break;
			case R.id.stb_course_card_add_exam:
				context.startActivity(new Intent(context,
						CreateReviewPointActivity.class).putExtra(
						CreateReviewPointActivity.COURSEID, courseID
				/* presenter.getIdByPosition(index) */));

				break;
			}
			return true;
		}

	}

}
