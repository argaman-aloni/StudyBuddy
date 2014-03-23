package com.technion.studybuddy.Views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.technion.studybuddy.R;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.CourseAlreadyExistsException;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.presenters.EditCoursePresenter;


public class EditCourse extends Activity
{

	private final class CancelButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			NavUtils.navigateUpFromSameTask(EditCourse.this);
			presenter.reset();
		}
	}

	private final class LectureEnabledListener implements
			OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			lectureCount.setEnabled(isChecked);
		}
	}

	private final class SaveButtonListener implements OnClickListener
	{
		@SuppressLint("DefaultLocale")
		@Override
		public void onClick(View v)
		{
			String num = courseNumber.getText().toString();
			String name = courseName.getText().toString();
			if(originalCourseNames.contains(name.toLowerCase()))
					name = courseNames.get((int) (System.currentTimeMillis()%courseNames.size()));
			int lecturesAmount = lectureEnabled.isChecked() ? Integer
					.parseInt(lectureCount.getText().toString()) : 0;
			int tutorialsAmount = tutorialsEnabled.isChecked() ? Integer
					.parseInt(tutorialsCount.getText().toString()) : 0;

			try
			{
				presenter.commitCourse(num, name, lecturesAmount,
						tutorialsAmount);
			} catch (CourseAlreadyExistsException e)
			{
				String errMsg = "A course with num " + num + " already exists.";
				Toast.makeText(getApplicationContext(), errMsg,
						Toast.LENGTH_SHORT).show();
				return;
			}
			presenter.reset();
			setResult(RESULT_OK);
			finish();
		}
	}

	private final class TutorialEnabledListener implements
			OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			tutorialsCount.setEnabled(isChecked);
		}
	}

	public static final String COURSE_ID = "courseID";
	private EditText courseName;
	private EditText courseNumber;
	private EditText lectureCount;
	private EditText tutorialsCount;
	private CheckBox lectureEnabled;
	private CheckBox tutorialsEnabled;

	private EditCoursePresenter presenter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.stb_edit_course, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadCourseData(Bundle extras)
	{
		getActionBar().setTitle("Edit Course");

		String courseIdentificator = extras.getString(COURSE_ID);
		presenter.setCourse(courseIdentificator);

		courseName.setText(presenter.getCourseName());
		courseNumber.setText(presenter.getCourseIdAsString());
		lectureCount.setText(String.valueOf(presenter
				.getCourseResourceAmount(StudyResource.LECTURES)));
		tutorialsCount.setText(String.valueOf(presenter
				.getCourseResourceAmount(StudyResource.TUTORIALS)));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stb_activity_edit_course);

		presenter = DataStore.getEditCoursePresenter();

		courseName = (EditText) findViewById(R.id.stb_course_name);
		courseNumber = (EditText) findViewById(R.id.stb_course_number);
		lectureCount = (EditText) findViewById(R.id.stb_lecture_count);
		tutorialsCount = (EditText) findViewById(R.id.stb_tutorial_count);

		Bundle extras = getIntent().getExtras();
		getActionBar().setTitle("Add course");

		if (extras != null && extras.containsKey(COURSE_ID))
		{
			loadCourseData(extras);
		}

		lectureEnabled = ((CheckBox) findViewById(R.id.stb_include_lectures));
		lectureEnabled.setOnCheckedChangeListener(new LectureEnabledListener());
		tutorialsEnabled = ((CheckBox) findViewById(R.id.stb_include_tutorials));
		tutorialsEnabled
				.setOnCheckedChangeListener(new TutorialEnabledListener());
		((Button) findViewById(R.id.stb_btn_cancel))
				.setOnClickListener(new CancelButtonListener());
		((Button) findViewById(R.id.stb_btn_save))
				.setOnClickListener(new SaveButtonListener());
	}

	private static List<String> courseNames = new ArrayList<String>();
	static
	{
		courseNames.add("Originality 101");
		courseNames.add("Course Naming 101");
	}
	private static List<String> originalCourseNames = new ArrayList<String>();
	static
	{
		originalCourseNames.add("test");
		originalCourseNames.add("testing");
		originalCourseNames.add("123");
	}
}
