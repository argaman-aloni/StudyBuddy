package com.technion.studybuddy.graphs;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;

import android.content.Context;
import android.view.View;


public class GraphFactory
{
	/**
	 * Creates progress graph
	 * 
	 * @param ctx
	 * @param name2progress
	 *            study resource -> num week progress
	 * @param currentWeek
	 * @param lastWeek
	 *            (usually Semester.WEEKS_IN_SEMESTER)
	 * @return
	 */
	public static View getCourseProgressGraph(Context ctx,
			Map<String, Integer> name2progress, int currentWeek,
			int semesterLength)
	{
		CourseProgress cp = new CourseProgress(name2progress, currentWeek,
				semesterLength);
		return ChartFactory.getBarChartView(ctx, cp.getDataset(),
				cp.getRenderer(), Type.STACKED);

	}

	/**
	 * Bare-bones example.
	 * 
	 * @param ctx
	 * @return
	 */
	public static View getSampleCourseProgress(Context ctx)
	{
		final int CURRENT_WEEK = 8;
		final int LAST_WEEK = 14;
		Map<String, Integer> name2progress = new LinkedHashMap<String, Integer>();

		name2progress.put("HWs", Integer.valueOf(6));
		name2progress.put("Tutorials", Integer.valueOf(4));
		name2progress.put("Lectures", Integer.valueOf(5));
		name2progress.put("Video", Integer.valueOf(9));

		return getCourseProgressGraph(ctx, name2progress, CURRENT_WEEK,
				LAST_WEEK);
	}

	public static GraphicalView getWeeklyProgressGraph(Context ctx,
			Date firstDayOfWeek, Integer[] weeklyProgress)
	{
		WeeklyProgress wp = new WeeklyProgress(firstDayOfWeek, weeklyProgress);
		return ChartFactory.getBarChartView(ctx, wp.getDataset(),
				wp.getRenderer(), Type.DEFAULT);
	}
}
