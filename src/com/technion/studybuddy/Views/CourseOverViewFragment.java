package com.technion.studybuddy.Views;

import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.app.Fragment;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.R;
import com.technion.studybuddy.adapters.TaskAdapter;
import com.technion.studybuddy.Views.SwipeDismissListViewTouchListener.DismissCallbacks;
import com.wvr.widget.TextProgressBar;

public class CourseOverViewFragment extends Fragment implements
		Observer, MessageBar.OnMessageClickListener
{
	public static final String courseNumberArg = "courseNameArg";
	// private LayoutInflater inflater;
	private CoursePresenter presenter;

	public static CourseOverViewFragment newInstance(String courseNumber)
	{

		CourseOverViewFragment fragment = new CourseOverViewFragment();
		Bundle args = new Bundle();
		args.putString(courseNumberArg, courseNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private String courseNumber;

	// private Course course;

	// private CoursePresenter presenter;
	private View fragmentView;
	private TaskAdapter adapter;
	private TextProgressBar lectureProgress;
	private TextProgressBar tutorialProgress;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		DataStore.getInstance().addObserver(this);
		if (getArguments() != null)
		{
			courseNumber = getArguments().getString(courseNumberArg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		presenter = DataStore.getInstance().getCoursePresenter(courseNumber);
		fragmentView = inflater.inflate(R.layout.stb_view_course_main,
				container, false);
		TextView courseName = (TextView) fragmentView
				.findViewById(R.id.stb_simester);
		courseName.setText(DataStore.coursesById.get(courseNumber).getName());
		lectureProgress = (TextProgressBar) fragmentView
				.findViewById(R.id.stb_lecturesProgressBar);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.stb_in);
		RelativeLayout relativeLayout = (RelativeLayout) fragmentView
				.findViewById(R.id.stb_lecturesProgress);
		relativeLayout.setAnimation(animation);
		tutorialProgress = (TextProgressBar) fragmentView
				.findViewById(R.id.stb_tutorialsProgressBar);
		drawGraph();
		adapter = new TaskAdapter(getActivity(), courseNumber);
		NowLayout layout = (NowLayout) fragmentView
				.findViewById(R.id.course_list);
		SwipeDismissListViewTouchListener listener = new SwipeDismissListViewTouchListener(
				layout, new DismissCallbacks()
				{

					@Override
					public void onDismiss(NowLayout mListView,
							int[] reverseSortedPositions)
					{
						// adapter.remove(i);
						try
						{
//							MessageBar bar = new MessageBar(getActivity());
//							bar.setOnClickListener(CourseOverViewFragment.this);
//							Bundle b = new Bundle();
//							b.putInt("id", reverseSortedPositions[0]);
//							bar.show("Item marked done", "Button!",
//									R.drawable.ic_action_undo, b);
							adapter.remove(reverseSortedPositions[0]);
							DataStore.getInstance().notifyObservers();
							drawGraph();
						} catch (Exception e)
						{
							e.printStackTrace();
						}

					}

					@Override
					public boolean canDismiss(int position)
					{
						// TODO Auto-generated method stub
						return true;
					}
				});
		layout.setOnTouchListener(listener);
		layout.setAdapter(adapter);
		return fragmentView;

	}

	@Override
	public void update(Observable observable, Object data)
	{
		if (fragmentView != null)
			drawGraph();
	}

	private void drawGraph()
	{

		int done = presenter.getDoneItemsCount("Lectures");
		int total = presenter.getTotalItemCount("Lectures");
		lectureProgress.setProgress(total == 0 ? 0 : 100 * done / total);
		lectureProgress.setText(done + "/" + total);

		done = presenter.getDoneItemsCount("Tutorials");
		total = presenter.getTotalItemCount("Tutorials");
		tutorialProgress.setProgress(total == 0 ? 0 : 100 * done / total);
		tutorialProgress.setText(done + "/" + total);

	}

	@Override
	public void onMessageClick(Parcelable token)
	{
		Bundle b = (Bundle) token;
		int id = b.getInt("id");
		adapter.restore(id);
		drawGraph();
	}

}
