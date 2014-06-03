package com.technion.studybuddy.charts;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.Framgments.ChartFullFragment;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.models.WorkStats;

public class PieChartBuilder extends Activity
{
	ProgressPieChart chart = new ProgressPieChart();
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The chart views that displays the data. */
	WorkStats stats = DataStore.getStats();

	private final GraphicalView[] pieCharts = new GraphicalView[3];
	private final String TAG = "TAG";

	private void addLClickListener(final GraphicalView graphicalView,
			final int index)
	{
		graphicalView.setOnTouchListener(null);
		graphicalView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				ChartFullFragment chartFragment = new ChartFullFragment();

				chartFragment.chartIndex = index;
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.setCustomAnimations(R.anim.stb_in, 0, 0, R.anim.stb_out);
				chartFragment.show(ft, TAG);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		LinearLayout[] layouts = new LinearLayout[3];
		double[] arr = stats.getLecturesStats();
		for (int i = 0; i < 7; i++)
			Log.d("Lectures", String.valueOf(arr[i]));
		setOverviews(layouts);
		setLectures(layouts);
		setTutorials(layouts);
		for (int i = 0; i < pieCharts.length; i++)
			addLClickListener(pieCharts[i], i);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState)
	{
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	private void setLectures(LinearLayout[] layouts)
	{
		pieCharts[1] = chart
				.getLecturesPieChart(this, stats.getLecturesStats());
		layouts[1] = (LinearLayout) findViewById(R.id.chart2);
		layouts[1].addView(pieCharts[1], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.stb_slide_up_left);
		layouts[1].setAnimation(animation);
	}

	private void setOverviews(LinearLayout[] layouts)
	{
		pieCharts[0] = chart.getOverviewPieChart(this, stats.getTotalStats());
		layouts[0] = (LinearLayout) findViewById(R.id.chart);
		layouts[0].addView(pieCharts[0], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.stb_in);
		pieCharts[0].setAnimation(animation);
	}

	private void setTutorials(LinearLayout[] layouts)
	{
		pieCharts[2] = chart.getTutorialsPieChart(this,
				stats.getTutorialsStats());
		layouts[2] = (LinearLayout) findViewById(R.id.chart3);
		layouts[2].addView(pieCharts[2], new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.stb_slide_up_right);
		layouts[2].setAnimation(animation);
	}

}
