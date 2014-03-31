/**
 * 
 */
package com.technion.studybuddy;

import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technion.studybuddy.Adapters.CourseListAdapter;
import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.GCM.GoogleHttpContext;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.Views.EditCourse;
import com.technion.studybuddy.Views.NowLayout;
import com.technion.studybuddy.Views.StbSettingsActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.CourseAlreadyExistsException;
import com.technion.studybuddy.exceptions.NoSuchResourceException;
import com.technion.studybuddy.graphs.GraphFactory;
import com.technion.studybuddy.models.Courses;
import com.technion.studybuddy.models.StudyItem;

/**
 * @author Argaman
 * 
 */
public class MainFragment extends Fragment implements Observer
{

	public static final int USER_PERMISSION1 = 0;

	private CourseListAdapter adapter;
	private LinearLayout chartLayout;
	private DrawerAdapter drawerAdapter;
	private LinearLayout emptyState;
	private GraphicalView graphView;
	private NowLayout layout;
	private View rootView;
	private View semesterData;

	private SharedPreferences sharedPreferences;

	private void createExmapleCourse()
	{
		sharedPreferences.edit().putBoolean("firstUse", false).commit();
		String num = "123456";
		String name = "Example course";

		int lecturesAmount = 6;
		int tutorialsAmount = 6;

		try
		{
			DataStore.getInstance().addCourse(num, name, lecturesAmount,
					tutorialsAmount);
		} catch (CourseAlreadyExistsException e)
		{
			String errMsg = "A course with num " + num + " already exists.";
			Toast.makeText(rootView.getContext(), errMsg, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		try
		{
			List<StudyItem> lectureItems = DataStore.coursesById.get("123456")
					.getResourceByName("Lectures").getItems();

			lectureItems.get(3).toggleDone();
			lectureItems.get(0).toggleDone();
			lectureItems.get(4).toggleDone();
			lectureItems.get(5).toggleDone();
			lectureItems.get(0).setLabel("example topic");
			lectureItems.get(1).setLabel("Click for details");
			lectureItems.get(2).setLabel("Click to Mark");
			lectureItems.get(3).setLabel("Click to unmark");
			lectureItems.get(4).setLabel("try to long press to open menu");

			List<StudyItem> tutorialItems = DataStore.coursesById.get("123456")
					.getResourceByName("Tutorials").getItems();
			tutorialItems.get(3).toggleDone();
			tutorialItems.get(0).toggleDone();

			tutorialItems.get(2).toggleDone();
			tutorialItems.get(4).toggleDone();
			tutorialItems.get(5).toggleDone();
			tutorialItems.get(0).setLabel("example topic");
			tutorialItems.get(1).setLabel("Click for details");
			tutorialItems.get(2).setLabel("Click to Mark");
			tutorialItems.get(3).setLabel("Click to unmark");
			tutorialItems.get(4).setLabel("try to long press to open menu");
		} catch (NoSuchResourceException e)
		{
			e.printStackTrace();
		}
		DataStore.getInstance().addExam(
				num,
				"example exam ",
				new Date(System.currentTimeMillis() + 8
						* DateUtils.DAY_IN_MILLIS));
	}

	private void initInitialView()
	{
		layout = (NowLayout) rootView.findViewById(R.id.course_list);
		emptyState = (LinearLayout) rootView
				.findViewById(R.id.stb_main_empty_state);
		adapter = new CourseListAdapter(rootView.getContext());
		layout.setAdapter(adapter);
		Button btn = (Button) rootView
				.findViewById(R.id.stb_main_empty_state_button);
		btn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startActivityForResult(new Intent(v.getContext(),
						EditCourse.class), Activity.RESULT_CANCELED);
			}
		});
		DataStore.getInstance().addObserver(adapter);
		DataStore.getStats().addObserver(this);
		// WeeklyGraph
		chartLayout = (LinearLayout) rootView.findViewById(R.id.Chart_layout);
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
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(rootView.getContext());
		boolean firstUse = sharedPreferences.getBoolean("firstUse", true);
		if (firstUse)
		{
			createExmapleCourse();
		}
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
			{
				Toast.makeText(getActivity(),
						"connection confirmed please register",
						Toast.LENGTH_SHORT).show();
			}
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

		DataStore.setContext(rootView.getContext());

		initInitialView();
		setVisibilityEmptyState();
		DataStore.getInstance().addObserver(this);
		initSemester();

		setHasOptionsMenu(true);
		return rootView;
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
			intent = new Intent(getActivity(), EditCourse.class);
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
			emptyState.setVisibility(View.VISIBLE);
			layout.setVisibility(View.GONE);
		} else
		{
			layout.setVisibility(View.VISIBLE);
			emptyState.setVisibility(View.GONE);
		}
	}

	@Override
	public void update(Observable observable, Object data)
	{
		updateGraphView();
		adapter.notifyDataSetChanged();
		drawerAdapter.notifyDataSetChanged();
		setVisibilityEmptyState();
	}

	private void updateGraphView()
	{
		chartLayout.removeAllViews();
		Date today = new Date();
		graphView = GraphFactory.getWeeklyProgressGraph(rootView.getContext(),
				today, DataStore.getInstance().getWorkStats(today, 7));
		chartLayout.addView(semesterData);
		chartLayout.addView(graphView);
	}

}
