package com.technion.studybuddy.graphs;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;

import android.content.Context;

public class GraphFactory {

	public static GraphicalView getWeeklyProgressGraph(Context ctx,
			Date firstDayOfWeek, Integer[] weeklyProgress) {
		WeeklyProgress wp = new WeeklyProgress(firstDayOfWeek, weeklyProgress);
		return ChartFactory.getBarChartView(ctx, wp.getDataset(),
				wp.getRenderer(), Type.DEFAULT);
	}
}
