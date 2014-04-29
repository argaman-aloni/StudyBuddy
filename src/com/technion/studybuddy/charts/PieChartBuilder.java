package com.technion.studybuddy.charts;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.ChartFullFragment;

public class PieChartBuilder extends Activity {
	ProgressPieChart chart = new ProgressPieChart();
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The chart views that displays the data. */

	private final GraphicalView[] pieCharts = new GraphicalView[3];
	private final String TAG = "TAG";
	double[] values = new double[] { 12, 14, 11, 10, 19 };

	private void addLClickListener(final GraphicalView graphicalView,
			final int index) {
		graphicalView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChartFullFragment chartFragment = new ChartFullFragment();
				chartFragment.chartIndex = index;
				chartFragment.show(getFragmentManager(), TAG);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		LinearLayout[] layouts = new LinearLayout[3];
		setOverviews(layouts);
		setLectures(layouts);
		setTutorials(layouts);
		for (int i = 0; i < pieCharts.length; i++) {
			addLClickListener(pieCharts[i], i);

		}

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

	private void setLectures(LinearLayout[] layouts) {
		pieCharts[1] = chart.getLecturesPieChart(this);
		layouts[1] = (LinearLayout) findViewById(R.id.chart2);
		layouts[1].addView(pieCharts[1], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.stb_slide_up_left);
		layouts[1].setAnimation(animation);
	}

	private void setOverviews(LinearLayout[] layouts) {
		pieCharts[0] = chart.getOverviewPieChart(this);
		layouts[0] = (LinearLayout) findViewById(R.id.chart);
		layouts[0].addView(pieCharts[0], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.stb_in);
		pieCharts[0].setAnimation(animation);
	}

	private void setTutorials(LinearLayout[] layouts) {
		pieCharts[2] = chart.getTutorialsPieChart(this);
		layouts[2] = (LinearLayout) findViewById(R.id.chart3);
		layouts[2].addView(pieCharts[2], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.stb_slide_up_right);
		layouts[2].setAnimation(animation);
	}

}
