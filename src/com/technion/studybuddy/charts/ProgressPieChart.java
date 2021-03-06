/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.technion.studybuddy.charts;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

import com.technion.studybuddy.R;

public class ProgressPieChart extends GenericChartGenerator
{

	private DefaultRenderer createPieChartRenderer(int[] colors, String title,
			int gradStart, int intgradEnd)
	{
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setDisplayValues(false);
		renderer.setShowLabels(false);
		renderer.setClickEnabled(true);
		renderer.setZoomEnabled(false);
		renderer.setPanEnabled(false);
		renderer.setLegendTextSize(20);
		SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
		r.setGradientEnabled(true);
		r.setGradientStart(0, gradStart);
		r.setGradientStop(0, intgradEnd);
		r.setHighlighted(true);
		return renderer;
	}

	public GraphicalView getLecturesPieChart(Context context, double[] values)
	{
		int[] colors = parseColors(context.getResources().getStringArray(
				R.array.lectures_colors));
		return ChartFactory
				.getPieChartView(
						context,
						buildCategoryDataset("Project lectures", values,
								context),
						createPieChartRenderer(colors, "Lectures", Color.RED,
								Color.RED));
	}

	public GraphicalView getOverviewPieChart(Context context, double[] values)
	{
		int[] colors = parseColors(context.getResources().getStringArray(
				R.array.overview_colors));
		return ChartFactory
				.getPieChartView(
						context,
						buildCategoryDataset("Project overview", values,
								context),
						createPieChartRenderer(colors, "Overview", colors[0],
								colors[0]));
	}

	public GraphicalView getTutorialsPieChart(Context context, double[] values)
	{
		int[] colors = parseColors(context.getResources().getStringArray(
				R.array.tutorials_colors));
		return ChartFactory.getPieChartView(
				context,
				buildCategoryDataset("Project tutorials", values, context),
				createPieChartRenderer(colors, "Tutorials", Color.BLUE,
						Color.BLUE));
	}

	private int[] parseColors(String[] stringArray)
	{
		int[] result = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++)
			result[i] = Color.parseColor(stringArray[i]);
		return result;
	}

}
