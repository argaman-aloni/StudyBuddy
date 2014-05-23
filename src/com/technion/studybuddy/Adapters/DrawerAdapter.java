/**
 * 
 */
package com.technion.studybuddy.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.Activities.CourseActivity;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.listeners.DrawerItemClickListener;
import com.technion.studybuddy.presenters.CourseListPresenter;

/**
 * @author Argaman
 * 
 */
public class DrawerAdapter extends BaseExpandableListAdapter
{

	private class ChildViewHolder
	{
		public TextView child;
	}

	private class GroupViewHolder
	{
		public TextView courseNumber;
		public TextView courseTitle;
	}

	private final Context mContext;

	private final LayoutInflater mInflater;
	private final CourseListPresenter presenter;

	public DrawerAdapter(Context context)
	{
		super();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		presenter = DataStore.getMainPresenter();
		mContext = context;
		new DrawerItemClickListener();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return 3;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
			v = inflateChildView();
		ChildViewHolder holder = (ChildViewHolder) v.getTag();
		final String[] resources = v.getResources().getStringArray(
				R.array.resources_array);
		holder.child.setText(resources[childPosition]);
		v.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), CourseActivity.class);
				intent.putExtra(CourseActivity.COURSE_ID,
						presenter.getIdByPosition(groupPosition)).putExtra(
						CourseActivity.FRAGMENT, resources[childPosition]);
				((Activity) mContext).startActivityForResult(intent,
						Activity.RESULT_CANCELED);

			}
		});
		return v;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return null;
	}

	@Override
	public int getGroupCount()
	{
		return presenter.getCount();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		View view = null;
		String courseNumber = presenter.getIdByPosition(groupPosition);
		String courseName = presenter.getNameByPosition(groupPosition);
		if (convertView == null)
			convertView = inflateGroupView();
		view = convertView;
		GroupViewHolder holder = (GroupViewHolder) view.getTag();
		holder.courseTitle.setText(courseName);
		holder.courseNumber.setText(courseNumber);
		return view;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	private View inflateChildView()
	{
		View res = mInflater.inflate(R.layout.stb_drawer_child_layout, null);
		ChildViewHolder holder = new ChildViewHolder();
		holder.child = (TextView) res.findViewById(R.id.stb_child_title);
		res.setTag(holder);
		return res;
	}

	private View inflateGroupView()
	{
		View res = mInflater.inflate(R.layout.stb_drawer_group_layout, null);
		GroupViewHolder holder = new GroupViewHolder();
		holder.courseTitle = (TextView) res.findViewById(R.id.course_tv);
		holder.courseNumber = (TextView) res
				.findViewById(R.id.course_number_tv);
		res.setTag(holder);
		return res;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		presenter.getNameByPosition(groupPosition);
		return true;
	}

}
