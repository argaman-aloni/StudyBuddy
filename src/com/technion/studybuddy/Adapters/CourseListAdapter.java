package com.technion.studybuddy.Adapters;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.objects.RecyclableCard;
import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.AnimatedCardUI;
import com.technion.studybuddy.Views.Activities.CourseActivity;
import com.technion.studybuddy.Views.cards.CourseOverviewCard;
import com.technion.studybuddy.Views.cards.ExamCard;
import com.technion.studybuddy.Views.cards.ResourseCard;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.presenters.CourseListPresenter;
import com.technion.studybuddy.presenters.CoursePresenter;

public class CourseListAdapter extends BaseAdapter
{
	private final LayoutInflater mInflater;
	private final CourseListPresenter presenter;
	private final Activity context;

	// private Object coursePresenter;

	/**
	 * 
	 */
	public CourseListAdapter(Activity context)
	{
		super();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		presenter = DataStore.getMainPresenter();
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return DataStore.getMainPresenter().getCount();
	}

	@Override
	public Object getItem(int position)
	{
		return presenter.getNameByPosition(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view = null;
		String courseNumber = presenter.getIdByPosition(position);
		String courseName = presenter.getNameByPosition(position);

		if (convertView == null)
			view = createView();
		else
		{
			view = convertView;
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.cardUI.clearCards();

		}
		ViewHolder holder = (ViewHolder) view.getTag();
		AnimatedCardUI cardUI = holder.cardUI;

		cardUI.setSwipeable(true);

		CoursePresenter coursePresenter = DataStore.getInstance()
				.getCoursePresenter(courseNumber);

		CardStack stack = new CardStack(courseName);
		cardUI.addStack(stack);

		for (final String resourceName : coursePresenter.getResourceNames())
		{
			ResourseCard card = new ResourseCard(courseNumber, cardUI, stack,
					resourceName);
			card.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(v.getContext(),
							CourseActivity.class);
					intent.putExtra(CourseActivity.COURSE_ID,
							presenter.getIdByPosition(position)).putExtra(
							CourseActivity.FRAGMENT, resourceName);
					((Activity) v.getContext()).startActivityForResult(intent,
							Activity.RESULT_CANCELED);

				}
			});
			int numBehind = coursePresenter.getNumOfItemsBehind(new Date(),
					resourceName);

			if (numBehind > 0)
				cardUI.addCardToLastStack(card);
		}
		OnClickListener courseClickListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				try
				{
					Intent intent = new Intent(v.getContext(),
							CourseActivity.class);
					intent.putExtra(CourseActivity.COURSE_ID,
							presenter.getIdByPosition(position));
					v.getContext().startActivity(intent);
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		};
		Date today = new Date();
		for (ExamDate examDate : DataStore.getInstance().getRelaventExam(
				courseNumber, today))
		{
			RecyclableCard exam = new ExamCard(courseNumber, examDate, cardUI,
					stack);
			exam.setOnClickListener(courseClickListener);
			if (examDate.getStartDate().getTime() < today.getTime()
					&& examDate.getEndDate().getTime() > today.getTime())
				cardUI.addCardToLastStack(exam);
		}
		RecyclableCard details = new CourseOverviewCard(courseNumber, cardUI,
				stack, position, this, context);
		details.setOnClickListener(courseClickListener);
		cardUI.addCardToLastStack(details);
		cardUI.refresh();
		return view;
	}

	private View createView()
	{
		View view;

		view = mInflater.inflate(R.layout.stb_view_course_card, null);

		ViewHolder holder = new ViewHolder();
		holder.cardUI = (AnimatedCardUI) view.findViewById(R.id.card_ui);
		view.setTag(holder);
		return view;
	}

	private class ViewHolder
	{
		public AnimatedCardUI cardUI;
	}

}
