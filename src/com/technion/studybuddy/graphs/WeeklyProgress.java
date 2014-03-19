package com.technion.studybuddy.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.text.format.DateUtils;

public class WeeklyProgress
{
	private Date firstDayOfWeek;
	// private int[] weeklyProgress;
	private Double[] dailyProgress;

	WeeklyProgress(Date firstDayOfWeek, Integer[] weeklyProgress)
	{
		this.firstDayOfWeek = new Date(firstDayOfWeek.getTime());

		dailyProgress = new Double[weeklyProgress.length];
		for (int i = 0; i < dailyProgress.length; ++i)
		{
			dailyProgress[i] = Double.valueOf(weeklyProgress[i]);
		}
	}

	XYMultipleSeriesDataset getDataset()
	{
		String[] titles = new String[] { "Daily Progress" };
		List<double[]> values = new ArrayList<double[]>();

		double[] dailyProgress = new double[this.dailyProgress.length];
		for (int i = 0; i < dailyProgress.length; ++i)
		{
			dailyProgress[i] = this.dailyProgress[i].doubleValue();
		}

		values.add(dailyProgress);

		return Progress.buildBarDataset(titles, values);
	}

	XYMultipleSeriesRenderer getRenderer()
	{
		@SuppressWarnings("serial")
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer()
		{
			{
				setChartTitle("Daily Progress");
				setOrientation(Orientation.HORIZONTAL);
				setShowLegend(false);
				setZoomEnabled(false);
				setPanEnabled(false);
				setBarSpacing(0.5);
				setAxisTitleTextSize(16);
				setChartTitleTextSize(20);
				// setLabelsTextSize(19);
				setLabelsTextSize(17);
				setMargins(new int[] { 35, 25, 25, 25 }); // right, top, left,
															// bottom

				setShowGrid(false);
				setShowGridX(false);
				setShowGridY(false);
				setShowCustomTextGrid(true);
				setGridColor(Color.BLACK);
				setShowAxes(false);

				setYLabelsAlign(Align.RIGHT);
				setXLabelsAlign(Align.CENTER);

				// setXLabelsPadding(0);

				// setYLabelsPadding(20);
				// setYLabelsVerticalPadding(-10);
				// renderer.setYLabelsVerticalPadding(10);
				// setLabelsTextSize(12);
				// setTextTypeface(Typeface.MONOSPACE);

				setYAxisMin(0);
				setYAxisMax(Collections.max(Arrays.asList(dailyProgress)) + 1);

				setXAxisMin(0.5);
				setXAxisMax(dailyProgress.length + 0.5);

				Calendar c = new GregorianCalendar();
				c.setTime(new Date(firstDayOfWeek.getTime()
						- (dailyProgress.length - 1) * DateUtils.DAY_IN_MILLIS));

				for (int i = 0; i < dailyProgress.length; ++i)
				{
					String date = c.get(Calendar.DAY_OF_MONTH) + "."
							+ (c.get(Calendar.MONTH) + 1);
					addXTextLabel(i + 1, date);
					c.add(Calendar.DAY_OF_MONTH, 1);
				}

				setXLabels(0); // Hides numbers from, leaves only text
				setYLabels(Collections.max(Arrays.asList(dailyProgress))
						.intValue());

				addSeriesRenderer(new SimpleSeriesRenderer()
				{
					{
						setDisplayChartValues(true);
						setChartValuesTextSize(25);
						setChartValuesTextAlign(Align.CENTER);
						setColor(Color.parseColor("#ff46bee6"));
					}
				});

				setApplyBackgroundColor(true);
				setMarginsColor(Color.WHITE);
				setBackgroundColor(Color.WHITE);
				setLabelsColor(Color.BLACK);
				setXLabelsColor(Color.BLACK);
				setYLabelsColor(0, Color.BLACK);
				setChartTitleTextSize(25);
			}
		};
		return renderer;
	}
}
