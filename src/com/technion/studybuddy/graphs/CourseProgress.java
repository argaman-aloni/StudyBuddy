package com.technion.studybuddy.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Paint.Align;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;


public class CourseProgress
{
	private Map<String, Integer>	name2progress;
	private int						currentWeek;
	private int						semesterLength;

	CourseProgress(Map<String, Integer> name2progress, int currentWeek,
			int semesterLength)
	{
		this.name2progress = name2progress;
		this.currentWeek = currentWeek;
		this.semesterLength = semesterLength;
	}

	XYMultipleSeriesDataset getDataset()
	{
		String[] titles = new String[] { "Total", "Done" };
		List<double[]> values = new ArrayList<double[]>();

		double[] currentWeeks = new double[name2progress.size()];
		Arrays.fill(currentWeeks, currentWeek);
		values.add(currentWeeks);

		double[] progressWeeks = new double[name2progress.size()];
		int i = 0;
		for (Integer progress : name2progress.values())
		{
			progressWeeks[i++] = progress.doubleValue();
		}
		values.add(progressWeeks);
		return Progress.buildBarDataset(titles, values);
	}

	XYMultipleSeriesRenderer getRenderer()
	{
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer()
		{
			private static final long	serialVersionUID	= -1671341831565113605L;

			{
				setOrientation(Orientation.VERTICAL);
				setShowLegend(false);
				setZoomEnabled(true,false);
				
				setInScroll(true);
				setPanEnabled(true);
				setBarSpacing(0.5);
				setAxisTitleTextSize(16);
				setChartTitleTextSize(20);
// 				setLabelsTextSize(19);
				setLabelsTextSize(17);
				setMargins(new int[] { 25, 20, 100, 20 }); // right, top, left,
															// bottom

				setShowGrid(true);
				setShowAxes(false);

				setXLabelsAlign(Align.RIGHT);
				setYLabelsAlign(Align.CENTER);

				setXLabelsPadding(0);

				setYLabelsPadding(20);
				setYLabelsVerticalPadding(-10);
				// renderer.setYLabelsVerticalPadding(10);
				// setLabelsTextSize(12);
				// setTextTypeface(Typeface.MONOSPACE);

				setYAxisMin(0);
				setYAxisMax(semesterLength);

				setXAxisMin(0.5);
				setXAxisMax(name2progress.size() + 0.5);

				int i = 0;
				for (String name : name2progress.keySet())
				{
					addXTextLabel(++i, name);
				}

				setXLabels(0); // Hides numbers from, leaves only text
				setYLabels(15);

				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				r.setColor(Color.argb(0xff * 35 / 100, 0xff, 0, 0)); // Soft red
				addSeriesRenderer(r);
				r = new SimpleSeriesRenderer();
				r.setColor(Color.argb(0xff * 75 / 100, 0, 0xff, 0)); // Soft green
				addSeriesRenderer(r);

				setApplyBackgroundColor(true);
				setMarginsColor(Color.argb(0xff, 0xf3, 0xf3, 0xf3));
				setBackgroundColor(Color.WHITE);
				setXLabelsColor(Color.BLACK);
				setYLabelsColor(0, Color.BLACK);
			}
		};
		return renderer;
	}
}
