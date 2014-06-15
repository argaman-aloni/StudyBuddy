package com.technion.studybuddy.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.fima.cardsui.views.CardUI;

public class AnimatedCardUI extends CardUI
{
	private final boolean onLoad = false;

	public AnimatedCardUI(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// initLayoutObserver();
	}

	public AnimatedCardUI(Context context)
	{
		super(context);
		// initLayoutObserver();
	}

}
