package com.technion.studybuddy.Views.cards;

import java.util.Date;

import android.R;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.technion.studybuddy.data.ColorTable;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.todddavies.components.progressbar.ProgressWheel;


public class ResourseCard extends BaseCard {

	private String resourseType;
	private String courseId;
	private CoursePresenter presenter;

	/**
	 * @param resourseName
	 * @param courseId
	 * @param presenter
	 */
	public ResourseCard(String courseID, CardUI cardUI, CardStack stack,
					String resourseName)
	{
		super(cardUI, stack);

		courseId = courseID;
		presenter = DataStore.getInstance().getCoursePresenter(courseId);
		resourseType = resourseName;

	}

	@Override
	protected void applyTo(View convertView) {
		presenter = DataStore.getInstance().getCoursePresenter(courseId);

		TextView title = (TextView) convertView
						.findViewById(R.id.stb_resourse_card_title);
		TextView numOfItems = (TextView) convertView
						.findViewById(R.id.stb_resourse_card_items);
		TextView nextItem = (TextView) convertView
						.findViewById(R.id.stb_resourse_card_next);
		title.setText(resourseType);
		int numOfItemLeft = presenter.getNumOfItemsBehind(new Date(),
						resourseType);
		numOfItems.setText(String.valueOf(numOfItemLeft));
		nextItem.setText("Next item : "+presenter.getNextItem(resourseType));

		int stripeColor = ColorTable.getColor(numOfItemLeft);

		final ProgressWheel pw = (ProgressWheel) convertView
						.findViewById(R.id.stb_rsc_progressBar);

		int done = presenter.getDoneItemsCount(resourseType);
		int total = presenter.getNumOfItemsDue(new Date(), resourseType);

		pw.setProgress(total == 0 ? 0 : 360 * done / total);
		pw.setText(String.valueOf(done) + "/" + total);

	

		convertView.findViewById(R.id.stb_resource_stripe).setBackgroundColor(
						stripeColor);
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.stb_view_resourse_card;
	}

}
