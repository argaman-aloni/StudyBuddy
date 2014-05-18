package com.technion.studybuddy.Views;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.technion.studybuddy.R;

public class LinksViewActivity extends StudyBuddyActivity {

	TextView courseNameTv;
	TextView courseIdTv;
	TextView emptyTv;
	ListView linksLv;
	List<String> links;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.links_view_layout);
		courseNameTv = (TextView) findViewById(R.id.textView1);
		courseIdTv = (TextView) findViewById(R.id.textView2);
		linksLv = (ListView) findViewById(R.id.listView1);
		emptyTv = (TextView) findViewById(R.id.links_empty_tv);
		Intent intent = getIntent();
		courseNameTv.setText(intent.getStringExtra("CourseName"));
		courseIdTv.setText(intent.getStringExtra("courseId"));
		links = splitStringToList(intent.getStringExtra("LinksList"));
		if (links.isEmpty())
			emptyTv.setVisibility(View.VISIBLE);
		// linksLv.setAdapter(new SimpleAdapter(getApplicationContext(), data,
		// android.R.layout.simple_list_item_1, from, to));
	}

	private List<String> splitStringToList(String stringExtra) {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
}
