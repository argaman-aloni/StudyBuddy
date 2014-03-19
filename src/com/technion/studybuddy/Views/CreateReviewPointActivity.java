package com.technion.studybuddy.Views;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.R;

public class CreateReviewPointActivity extends StudyBuddyActivity {
	private String courseID;
	public static final String COURSEID = "courseID";
	// private List<StudyItem> lectures, tutorials;
	private DatePicker dueDate;
	private EditText name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stb_view_create_review_point);

		// lectures = new ArrayList<StudyItem>();
		// tutorials = new ArrayList<StudyItem>();
		dueDate = (DatePicker) findViewById(R.id.stb_review_date);
		name = (EditText) findViewById(R.id.stb_review_name);

		if (getIntent().getExtras().containsKey(COURSEID)) {
			courseID = getIntent().getExtras().getString(COURSEID);
		}

		setTitle("Add exam for " + courseID);

		Button cancel = (Button) findViewById(R.id.stb_review_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button saveButton = (Button) findViewById(R.id.stb_review_save);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(dueDate.getYear(), dueDate.getMonth(),
						dueDate.getDayOfMonth());
				Date due = calendar.getTime();

				DataStore.getInstance().addExam(courseID,
						name.getText().toString(), due);

				finish();

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
