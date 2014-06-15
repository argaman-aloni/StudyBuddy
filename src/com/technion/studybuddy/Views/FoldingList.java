package com.technion.studybuddy.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.technion.studybuddy.R;

public class FoldingList extends ListView implements OnGlobalLayoutListener
{
	private boolean onLoad = false;

	public FoldingList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayoutObserver();

	}

	public FoldingList(Context context)
	{
		super(context);
		initLayoutObserver();
	}

	private void initLayoutObserver()
	{
		// setOrientation(LinearLayout.VERTICAL);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void onGlobalLayout()
	{
		if (onLoad)
			return;

		final int heightPx = getContext().getResources().getDisplayMetrics().heightPixels;

		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++)
		{
			final View child = getChildAt(i);

			int[] location = new int[2];
			child.getLocationOnScreen(location);

			if (location[1] > heightPx)
				break;

			final Animation animation = AnimationUtils.loadAnimation(
					getContext(), R.anim.fold);
			animation.setStartOffset(250 * i);

			child.startAnimation(animation);
		}
		onLoad = true;
	}
}
