package com.technion.studybuddy.Views;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.technion.studybuddy.R;

public class LinksViewActivity extends StudyBuddyActivity
{

	TextView itemNameTv;
	TextView courseIdTv;
	TextView emptyTv;
	ListView linksLv;
	List<String> links;
	LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.links_view_layout);
		layout = (LinearLayout) findViewById(R.id.links_view_title_layout);
		itemNameTv = (TextView) findViewById(R.id.item_name);
		linksLv = (ListView) findViewById(R.id.listView1);
		emptyTv = (TextView) findViewById(R.id.links_empty_tv);
		Intent intent = getIntent();
		final Bundle bundle = intent.getExtras();
		final String PACKAGE = getPackageName();

		itemNameTv.setText(intent.getStringExtra("CourseName"));
		if (savedInstanceState == null)
		{
			ViewTreeObserver observer = layout.getViewTreeObserver();
			observer.addOnPreDrawListener(new OnPreDrawListener()
			{

				@Override
				public boolean onPreDraw()
				{
					layout.getViewTreeObserver().removeOnPreDrawListener(this);
					int left = bundle.getInt(PACKAGE + ".left");
					int top = bundle.getInt(PACKAGE + ".top");
					int width = bundle.getInt(PACKAGE + ".width");
					int height = bundle.getInt(PACKAGE + ".height");
					int[] location = new int[2];
					layout.getLocationOnScreen(location);
					int leftDelta = left - location[0];
					int topDelta = top - location[1];
					float widthScale = (float) width / layout.getWidth();
					float heightScale = (float) height / layout.getHeight();

					layout.setPivotX(0);
					layout.setPivotY(0);
					layout.setScaleX(widthScale);
					layout.setScaleY(heightScale);
					layout.setTranslationX(leftDelta);
					layout.setTranslationY(topDelta);

					layout.animate().setDuration(500).scaleX(1).scaleY(1)
							.translationX(0).translationY(0)
							.setInterpolator(new BounceInterpolator())
							.withEndAction(new Runnable()
							{

								@Override
								public void run()
								{
									if (bundle.getString("LinksList") == null)
										emptyTv.setVisibility(View.VISIBLE);
									else
									{
										links = Arrays.asList(bundle.getString(
												"LinksList").split(" "));

										linksLv.setAdapter(new LinkAdapter());
									}
								}
							});
					return true;
				}
			});

		}

		// android.R.layout.simple_list_item_1, from, to));
	}

	private class LinkAdapter extends BaseAdapter
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return links.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position)
		{
			return links.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			if (convertView == null)
				convertView = new FontFitTextView(LinksViewActivity.this);
			String[] parts = getItem(position).toString().split("/");
			((FontFitTextView) convertView).setText(parts[parts.length - 1]);
			((FontFitTextView) convertView).setTextSize(50);
			convertView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getItem(position).toString()));
					v.getContext().startActivity(intent);

				}
			});
			return convertView;
		}
	}
}
