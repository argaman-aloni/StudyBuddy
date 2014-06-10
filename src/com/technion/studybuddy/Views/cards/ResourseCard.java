package com.technion.studybuddy.Views.cards;

import java.util.Date;

import android.view.View;

import com.androidquery.AQuery;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.technion.studybuddy.R;
import com.technion.studybuddy.data.ColorTable;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.todddavies.components.progressbar.ProgressWheel;

public class ResourseCard extends BaseCard
{

	private final String resourseType;
	private final String courseId;
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
	protected void applyTo(View convertView)
	{
		AQuery aq = new AQuery(convertView);
		aq.hardwareAccelerated11();
		presenter = DataStore.getInstance().getCoursePresenter(courseId);

		int numOfItemLeft = presenter.getNumOfItemsBehind(new Date(),
				resourseType);
		aq.id(R.id.stb_resourse_card_title).text(resourseType);
		aq.id(R.id.stb_resourse_card_items).text(String.valueOf(numOfItemLeft));
		aq.id(R.id.stb_resourse_card_next).text(
				"Next item : " + presenter.getNextItem(resourseType));

		int stripeColor = ColorTable.getColor(numOfItemLeft);
		aq.id(R.id.stb_resource_stripe).backgroundColor(stripeColor);
		final ProgressWheel pw = (ProgressWheel) convertView
				.findViewById(R.id.stb_rsc_progressBar);
		int done = presenter.getDoneItemsCount(resourseType);
		int total = presenter.getNumOfItemsDue(new Date(), resourseType);
		pw.setProgress(total == 0 ? 0 : 360 * done / total);
		pw.setText(String.valueOf(done) + "/" + total);
	}

	@Override
	protected int getCardLayoutId()
	{
		return R.layout.stb_view_resourse_card;
	}

}
