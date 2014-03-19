package com.technion.studybuddy.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.technion.studybuddy.R;


public class StrikeThroughTextView extends FontFitTextView
{
	private boolean isStriked;
	private Bitmap bitmap;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public StrikeThroughTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public StrikeThroughTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public StrikeThroughTextView(Context context)
	{
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (bitmap == null)
			try
			{
				bitmap = Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.stb_strikethrough), getWidth(),
						getHeight(), true);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		if (isStriked)
			canvas.drawBitmap(bitmap, 0, 0, null);
	}

	/**
	 * @param isStriked
	 *            the isStriked to set
	 */
	public synchronized void setStriked(boolean isStriked)
	{
		this.isStriked = isStriked;
		postInvalidate();
	}

	/**
	 * @return the isStriked
	 */
	public synchronized boolean isStriked()
	{
		return isStriked;
	}

}
