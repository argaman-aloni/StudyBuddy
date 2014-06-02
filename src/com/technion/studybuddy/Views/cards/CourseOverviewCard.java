package com.technion.studybuddy.Views.cards;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.Activities.CreateReviewPointActivity;
import com.technion.studybuddy.Views.Activities.LinksViewActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.utils.DateUtils;
import com.todddavies.components.progressbar.ProgressWheel;

public class CourseOverviewCard extends BaseCard
{
	// private int index;
	private final CoursePresenter presenter;
	private final String courseID;
	private final BaseAdapter baseAdapter;
	private final Activity context;

	public CourseOverviewCard(String courseID, CardUI cardUI, CardStack stack,
			int index, BaseAdapter adapter, Activity context)
	{
		super(cardUI, stack);
		baseAdapter = adapter;
		// this.index = index;
		this.courseID = courseID;
		this.context = context;
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
			done += presenter.getDoneItemsCount(resourceName);

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
					popupMenu.setOnMenuItemClickListener(new PopupMenuListner(v
							.getContext()));
					popupMenu.inflate(R.menu.stb_course_card_menu);
					popupMenu.show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
		ListView items = (ListView) convertView.findViewById(R.id.next_items);
		if (items == null)
			return;
		items.setAdapter(new NextItemsAdapter());
	}

	@Override
	protected int getCardLayoutId()
	{
		return R.layout.stb_view_course_item;
	}

	private class PopupMenuListner implements OnMenuItemClickListener
	{
		private final Context context;

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

	private class NextItemsAdapter extends BaseAdapter
	{

		private final List<String> next;

		/**
		 * 
		 */
		public NextItemsAdapter()
		{
			super();
			next = new ArrayList<>();
			for (String resourseType : presenter.getResourceNames())
			{
				String name = presenter.getNextItem(resourseType);
				if (null != name && !name.isEmpty())
					next.add(presenter.getNextItem(resourseType));
			}
		}

		@Override
		public int getCount()
		{
			return next.size();
		}

		@Override
		public Object getItem(int position)
		{
			return next.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			if (convertView == null)
				convertView = LayoutInflater.from(context).inflate(
						R.layout.stb_course_item_resourse, null);
			TextView t = (TextView) convertView;

			t.setText(getItem(position).toString());
			t.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// presenter.g
					Intent intent = new Intent(v.getContext(),
							LinksViewActivity.class);
					intent.putExtra("CourseName", getItem(position).toString());
					intent.putExtra("courseId", courseID);
					intent.putExtra("LinksList", formatToSend(presenter
							.getLinksFor(next.get(position))));
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
			});
			return convertView;
		}
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
