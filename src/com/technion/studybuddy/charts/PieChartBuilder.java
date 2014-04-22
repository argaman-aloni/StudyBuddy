package com.technion.studybuddy.charts;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.technion.studybuddy.R;

public class PieChartBuilder extends Activity {
	ProgressPieChart chart = new ProgressPieChart();
	/** The chart views that displays the data. */
	private GraphicalView mChartViewLectures;
	private GraphicalView mChartViewOverview;
	private GraphicalView mChartViewTutorials;
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** Edit text field for entering the slice value. */
	/** Button for adding entered data to the current series. */
	double[] values = new double[] { 12, 14, 11, 10, 19 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		mChartViewOverview = chart.getOverviewPieChart(this);
		LinearLayout layout1 = (LinearLayout) findViewById(R.id.chart);
		layout1.addView(mChartViewOverview, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mChartViewLectures = chart.getLecturesPieChart(this);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.chart2);
		layout2.addView(mChartViewLectures, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mChartViewTutorials = chart.getTutorialsPieChart(this);
		LinearLayout layout3 = (LinearLayout) findViewById(R.id.chart3);
		layout3.addView(mChartViewTutorials, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}
}
