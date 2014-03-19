package com.technion.studybuddy.Views;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fima.cardsui.views.CardUI;


public class AnimatedCardUI extends CardUI implements OnGlobalLayoutListener
{
	private boolean onLoad = false;

	public AnimatedCardUI(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayoutObserver();
	}

	public AnimatedCardUI(Context context)
	{
		super(context);
		initLayoutObserver();
	}

	@Override
	public void onGlobalLayout()
	{
		if (onLoad)
			return;

		final int heightPx = getContext().getResources().getDisplayMetrics().heightPixels;

		boolean inversed = false;
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++)
		{
			final View child = getChildAt(i);

			int[] location = new int[2];
			child.getLocationOnScreen(location);

			if (location[1] > heightPx)
			{
				break;
			}

			final Animation animation = inversed ? AnimationUtils
					.loadAnimation(getContext(), R.anim.stb_slide_up_left)
					: AnimationUtils.loadAnimation(getContext(),
							R.anim.stb_slide_up_right);
			animation.setStartOffset(80 * i);

			child.startAnimation(animation);
			inversed = !inversed;
		}
		onLoad = true;
	}

	private void initLayoutObserver()
	{
		// setOrientation(LinearLayout.VERTICAL);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
}
