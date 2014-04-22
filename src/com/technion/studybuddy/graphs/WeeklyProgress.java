package com.technion.studybuddy.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.graphics.Color;
import android.text.format.DateUtils;

import com.technion.studybuddy.charts.AbstractDemoChart;

public class WeeklyProgress extends AbstractDemoChart {
	private final Double[] dailyProgress;
	private final Date firstDayOfWeek;

	WeeklyProgress(Date firstDayOfWeek, Integer[] weeklyProgress) {
		this.firstDayOfWeek = new Date(firstDayOfWeek.getTime());

		dailyProgress = new Double[weeklyProgress.length];
		for (int i = 0; i < dailyProgress.length; ++i) {
			dailyProgress[i] = Double.valueOf(weeklyProgress[i]);
		}
	}

	private void addRendererSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setOrientation(Orientation.HORIZONTAL);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setShowLegend(false);
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setClickEnabled(true);
		renderer.setBarSpacing(0.5);
		renderer.setXLabels(0);
	}

	XYMultipleSeriesDataset getDataset() {
		String[] titles = new String[] { "Daily Progress" };
		List<double[]> values = new ArrayList<double[]>();

		double[] dailyProgress = new double[this.dailyProgress.length];
		for (int i = 0; i < dailyProgress.length; ++i) {
			dailyProgress[i] = this.dailyProgress[i].doubleValue();
		}

		values.add(dailyProgress);

		return buildBarDataset(titles, values);
	}

	XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer renderer = buildRenderer(
				new int[] { Color.parseColor("#4169e1") }, PointStyle.values());
		setChartSettings(renderer, "Daily Progress", null, null, 0.5,
				dailyProgress.length + 0.5, 0,
				Collections.max(Arrays.asList(dailyProgress)) + 1, Color.BLACK,
				Color.BLACK);

		addRendererSettings(renderer);

		Calendar c = new GregorianCalendar();
		c.setTime(new Date(firstDayOfWeek.getTime()
				- (dailyProgress.length - 1) * DateUtils.DAY_IN_MILLIS));

		for (int i = 0; i < dailyProgress.length; ++i) {
			String date = c.get(Calendar.DAY_OF_MONTH) + "."
					+ (c.get(Calendar.MONTH) + 1);
			renderer.addXTextLabel(i + 1, date);
			c.add(Calendar.DAY_OF_MONTH, 1);
		}

		return renderer;
	}
}
