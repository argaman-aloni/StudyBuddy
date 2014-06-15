/**
 *
 */
package com.technion.studybuddy;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.technion.studybuddy.Adapters.CourseListAdapter;
import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.Tooltip.ToolTip;
import com.technion.studybuddy.Tooltip.ToolTipRelativeLayout;
import com.technion.studybuddy.Tooltip.ToolTipView;
import com.technion.studybuddy.Views.Activities.AddCourseActivity;
import com.technion.studybuddy.Views.Activities.StbSettingsActivity;
import com.technion.studybuddy.charts.PieChartBuilder;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.graphs.GraphFactory;
import com.technion.studybuddy.models.Courses;
import com.technion.studybuddy.utils.Constants;

public class MainFragment extends Fragment implements Observer,
		ToolTipView.OnToolTipViewClickedListener
{

	public static final int USER_PERMISSION1 = 0;
	private ToolTipView mRedToolTipView;
	private CourseListAdapter adapter;
	private LinearLayout chartLayout;
	private DrawerAdapter drawerAdapter;
	private GraphicalView graphView;
	private View rootView;
	private View semesterData;
	private int locationInArray;
	private AQuery aq;
	private SharedPreferences sharedPref;

	private ToolTipRelativeLayout mToolTipFrameLayout;

	private void initInitialView()
	{
		adapter = new CourseListAdapter(getActivity());
		aq.id(R.id.course_list).adapter(adapter);
		aq.id(R.id.stb_main_empty_state_button).clicked(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startActivityForResult(new Intent(v.getContext(),
						AddCourseActivity.class), Activity.RESULT_CANCELED);
			}
		});
		DataStore.getInstance().addObserver(this);
		DataStore.getStats().addObserver(this);
		chartLayout = (LinearLayout) rootView.findViewById(R.id.Chart_layout);
		chartLayout.setClickable(true);
	}

	private void initSemester()
	{
		semesterData = LayoutInflater.from(rootView.getContext()).inflate(
				R.layout.stb_view_simester_data, null);
		TextView simterTextView = (TextView) semesterData
				.findViewById(R.id.stb_simester);
		TextView weekCount = (TextView) semesterData
				.findViewById(R.id.stb_week_count);
		// TODO: change this to be dynamic.
		simterTextView.setText("Semester : Winter");
		int currentWeek = DataStore.semester.getSemesterWeek(new Date());
		weekCount.setText("week "
				+ String.valueOf(currentWeek < DataStore.semester
						.getTotalWeeks() ? currentWeek : DataStore.semester
						.getTotalWeeks()
						+ " / "
						+ DataStore.semester.getTotalWeeks()));
		updateGraphView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		DataStore.getInstance().notifyObservers();
		switch (requestCode)
		{
		// if the user approved the use of the account make another request
		// for the auth token else display a message
		case GoogleHttpContext.USER_PERMISSION:
			if (resultCode == Activity.RESULT_OK)
				Toast.makeText(getActivity(),
						"connection confirmed please register",
						Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.stb_main_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.stb_view_main, container, false);
		aq = new AQuery(rootView);
		aq.hardwareAccelerated11();
		mToolTipFrameLayout = (ToolTipRelativeLayout) rootView
				.findViewById(R.id.activity_main_tooltipframelayout);
		if (mToolTipFrameLayout != null)
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					setToolTip();
				}

			}, DataStore.getMainPresenter().getCount() * 80 + 300);
		DataStore.setContext(rootView.getContext());

		Context context = getActivity();
		sharedPref = context.getSharedPreferences(
				Constants.popupSaredPrefrence, Context.MODE_PRIVATE);
		if (savedInstanceState == null)
			locationInArray = sharedPref.getInt(Constants.locationInArray, 0);
		else
			locationInArray = savedInstanceState
					.getInt(Constants.locationInArray);
		initInitialView();
		setVisibilityEmptyState();
		DataStore.getInstance().addObserver(this);
		initSemester();

		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.popupSaredPrefrence, locationInArray);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		Intent intent = null;
		switch (item.getItemId())
		{
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		case R.id.stb_add_curse:
			intent = new Intent(getActivity(), AddCourseActivity.class);
			startActivityForResult(intent, Activity.RESULT_CANCELED);
			return true;
		case R.id.stb_main_settings:
			intent = new Intent(getActivity(), StbSettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.stb_menu_sort_progress:
			DataStore.getInstance().sortCourses(Courses.byProgress);
			return true;
		case R.id.stb_menu_sort_name:
			DataStore.getInstance().sortCourses(Courses.byName);
			return true;
		case R.id.stb_menu_sort_number:
			DataStore.getInstance().sortCourses(Courses.byNumber);

			return true;
		case R.id.stb_main_register:
			Activity activity = getActivity();
			ServerUtilities.registerToServer(activity);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void setDrawerAdapter(DrawerAdapter adapter2)
	{
		drawerAdapter = adapter2;
	}

	private void setVisibilityEmptyState()
	{
		if (adapter.getCount() == 0)
		{
			aq.id(R.id.stb_main_empty_state).visible();
			aq.id(R.id.course_list).visibility(View.GONE);
		} else
		{
			aq.id(R.id.course_list).visible();
			aq.id(R.id.stb_main_empty_state).visibility(View.GONE);
		}
	}

	@Override
	public void update(Observable observable, Object data)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// update the widget!!!!!!!!
				updateGraphView();
				adapter.notifyDataSetChanged();
				if (drawerAdapter != null)
					drawerAdapter.notifyDataSetChanged();
				setVisibilityEmptyState();

			}
		});

	}

	private void updateGraphView()
	{
		chartLayout.removeAllViews();
		Date today = new Date();
		graphView = GraphFactory.getWeeklyProgressGraph(rootView.getContext(),
				today, DataStore.getInstance().getWorkStats(today, 7));
		graphView.setClickable(true);
		graphView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setAlpha((float) 0.5);
					return false;
				case MotionEvent.ACTION_UP:
					v.setAlpha((float) 0.5);
					return false;
				case MotionEvent.ACTION_CANCEL:
					v.setAlpha((float) 0.5);
					return false;
				}
				return false;
			}

		});

		graphView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(),
						PieChartBuilder.class);
				startActivity(intent);
				v.setAlpha(1);
			}
		});
		chartLayout.addView(semesterData);
		chartLayout.addView(graphView);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		DataStore.getInstance().addObserver(this);
		DataStore.getStats().addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		DataStore.getInstance().deleteObserver(this);
		DataStore.getStats().deleteObserver(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		Bundle bundle = new Bundle();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			onSaveInstanceState(bundle);
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			onSaveInstanceState(bundle);
	}

	@Override
	public void onToolTipViewClicked(ToolTipView toolTipView)
	{
		mRedToolTipView = null;
		SharedPreferences.Editor editor = sharedPref.edit();
		locationInArray = sharedPref.getInt(Constants.locationInArray, 0);
		editor.putInt(Constants.locationInArray, ++locationInArray);
		editor.apply();
	}

	private void setToolTip()
	{
		String[] array = getActivity().getResources().getStringArray(
				R.array.popup_notifications);

		if (locationInArray < array.length)
		{
			int location = locationInArray % array.length;
			ToolTip toolTip = new ToolTip().withText(array[location])
					.withColor(Color.RED).withShadow();

			mRedToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip,
					rootView.findViewById(R.id.Chart_layout));
			mRedToolTipView.setOnToolTipViewClickedListener(this);
		}
	}
}
