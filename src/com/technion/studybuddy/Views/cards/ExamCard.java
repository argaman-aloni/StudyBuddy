package com.technion.studybuddy.Views.cards;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.R;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.technion.studybuddy.Models.ExamDate;
import com.technion.studybuddy.data.ColorTable;

public class ExamCard extends BaseCard
{
	private ExamDate examDate;

	public ExamCard(String courseNumber, ExamDate examDate, CardUI cardUI,
			CardStack stack)
	{
		super(cardUI, stack);
		this.examDate = examDate;
		this.title = courseNumber;

	}

	@Override
	protected void applyTo(View convertView)
	{
		int numOfItemsLeft = 7;

		TextView title = (TextView) convertView
				.findViewById(R.id.stb_exam_item_title);
		TextView itemsLeft = (TextView) convertView
				.findViewById(R.id.stb_exam_card_items);
		title.setText(this.title);
		itemsLeft.setText(String.valueOf(numOfItemsLeft));

		((View) convertView.findViewById(R.id.stb_exam_stripe))
				.setBackgroundColor(Color.parseColor("red"));
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",
				Locale.getDefault());
		title.setText("Exam on the " + sdf.format(examDate.getEndDate()));
		Date today = new Date();
		int daysLeft = examDate.getPeriodRemaining(today);
		int totalDays = examDate.getPeriodGone(today);
		int stripeColor = ColorTable.getColor(totalDays - 3 * daysLeft);
		itemsLeft.setText(String.valueOf(daysLeft));
		convertView.findViewById(R.id.stb_exam_stripe).setBackgroundColor(
				stripeColor);
	}

	@Override
	protected int getCardLayoutId()
	{
		return R.layout.stb_view_exam_card;
	}
}
