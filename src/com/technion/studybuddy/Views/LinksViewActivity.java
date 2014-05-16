package com.technion.studybuddy.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.technion.studybuddy.R;

public class LinksViewActivity extends StudyBuddyActivity {

	TextView courseNameTv;
	TextView courseIdTv;
	ListView linksLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.links_view_layout);
		courseNameTv = (TextView) findViewById(R.id.textView1);
		courseIdTv = (TextView) findViewById(R.id.textView2);
		Intent intent = getIntent();
		courseNameTv.setText(intent.getStringExtra("CourseName"));
		courseIdTv.setText(intent.getStringExtra("courseId"));
		linksLv = (ListView) findViewById(R.id.listView1);
		// linksLv.setAdapter(new SimpleAdapter(getApplicationContext(), data,
		// android.R.layout.simple_list_item_1, from, to));
	}
}
