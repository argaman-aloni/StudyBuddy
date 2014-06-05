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
import android.os.Bundle;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technion.studybuddy.Adapters.CourseListAdapter;
import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.Views.NowLayout;
import com.technion.studybuddy.Views.Activities.AddCourseActivity;
import com.technion.studybuddy.Views.Activities.EditCourse;
import com.technion.studybuddy.Views.Activities.StbSettingsActivity;
import com.technion.studybuddy.charts.PieChartBuilder;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.graphs.GraphFactory;
import com.technion.studybuddy.models.Courses;
import com.technion.studybuddy.utils.Constants;

public class MainFragment extends Fragment implements Observer {

	public static final int USER_PERMISSION1 = 0;

	private FrameLayout frameLayout;
	private CourseListAdapter adapter;
	private LinearLayout chartLayout;
	private DrawerAdapter drawerAdapter;
	private LinearLayout emptyState;
	private GraphicalView graphView;
	private NowLayout layout;
	private View rootView;
	private View semesterData;
	private int locationInArray;

	private SharedPreferences sharedPref;

	private void initInitialView() {
		layout = (NowLayout) rootView.findViewById(R.id.course_list);
		emptyState = (LinearLayout) rootView
				.findViewById(R.id.stb_main_empty_state);
		adapter = new CourseListAdapter(getActivity());
		layout.setAdapter(adapter);
		Button btn = (Button) rootView
				.findViewById(R.id.stb_main_empty_state_button);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(v.getContext(),
						EditCourse.class), Activity.RESULT_CANCELED);
			}
		});
		DataStore.getInstance().addObserver(adapter);
		DataStore.getStats().addObserver(this);
		// WeeklyGraph
		chartLayout = (LinearLayout) rootView.findViewById(R.id.Chart_layout);
		chartLayout.setClickable(true);
	}

	private void initSemester() {
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		DataStore.getInstance().notifyObservers();
		switch (requestCode) {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.stb_main_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.stb_view_main, container, false);

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

		TextView bubbleTv = (TextView) rootView
				.findViewById(R.id.bubble_main_tv);
		if (null != bubbleTv) {
			frameLayout = (FrameLayout) rootView
					.findViewById(R.id.course_list_container);
			String[] array = getResources().getStringArray(
					R.array.popup_notifications);
			bubbleTv.setText(array[locationInArray % array.length]);
			Animation animation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.stb_in);
			bubbleTv.setAnimation(animation);
			bubbleTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setVisibility(View.GONE);
					// LayoutParams parms = (LayoutParams)
					// frameLayout.getLayoutParams();
					// parms.height = screen_height;
					// frameLayout.setLayoutParams();
				}
			});
		}

		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.popupSaredPrefrence, locationInArray);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = null;
		switch (item.getItemId()) {
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

	public void setDrawerAdapter(DrawerAdapter adapter2) {
		drawerAdapter = adapter2;
	}

	private void setVisibilityEmptyState() {
		if (adapter.getCount() == 0) {
			emptyState.setVisibility(View.VISIBLE);
			layout.setVisibility(View.GONE);
		} else {
			layout.setVisibility(View.VISIBLE);
			emptyState.setVisibility(View.GONE);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		updateGraphView();
		adapter.notifyDataSetChanged();
		if (drawerAdapter != null)
			drawerAdapter.notifyDataSetChanged();
		setVisibilityEmptyState();
		// update the widget!!!!!!!!
	}

	private void updateGraphView() {
		chartLayout.removeAllViews();
		Date today = new Date();
		graphView = GraphFactory.getWeeklyProgressGraph(rootView.getContext(),
				today, DataStore.getInstance().getWorkStats(today, 7));
		graphView.setClickable(true);
		graphView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
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

		graphView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
	public void onResume() {
		super.onResume();
		DataStore.getInstance().addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		DataStore.getInstance().deleteObserver(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Bundle bundle = new Bundle();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			onSaveInstanceState(bundle);
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			onSaveInstanceState(bundle);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(Constants.locationInArray, ++locationInArray);
		editor.commit();
	}

}
