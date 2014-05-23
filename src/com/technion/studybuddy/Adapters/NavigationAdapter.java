package com.technion.studybuddy.Adapters;

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

public class NavigationAdapter extends BaseExpandableListAdapter
{

	private final LayoutInflater mInflater;
	/**
	 * 
	 */

	private final Context context;

	public NavigationAdapter(Context context)
	{
		super();
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		switch (groupPosition)
		{
		case 0:
			return DataStore.getMainPresenter()
					.getNameByPosition(childPosition);
		default:
			break;
		}
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		TextView childText = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.stb_view_list_item, null);
			childText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_text);
			childText.setOnClickListener(new OpenCourseOnClick(childPosition));
			convertView.setTag(new ViewHolder(childText));
		}
		childText = ((ViewHolder) convertView.getTag()).getTextView();
		String courseName = getChild(groupPosition, childPosition).toString();
		if (courseName.length() > 12)
			courseName = courseName.substring(0, 12) + "...";
		childText.setText("	" + courseName);
		convertView.setTag(new ViewHolder(childText));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		switch (groupPosition)
		{
		case 0:
			return DataStore.getMainPresenter().getCount();
		default:
			break;
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return DataStore.getMenu(groupPosition);
	}

	@Override
	public int getGroupCount()
	{
		return DataStore.getMenuSize();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		TextView groupText = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.stb_view_list_item, null);
			groupText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_text);
			convertView.setTag(new ViewHolder(groupText));

		}
		groupText = ((ViewHolder) convertView.getTag()).getTextView();
		groupText.setText((String) getGroup(groupPosition));
		convertView.setTag(new ViewHolder(groupText));
		return convertView;
	}

	@Override
	public boolean hasStableIds()
	{

		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{

		return false;
	}

	private final class OpenCourseOnClick implements OnClickListener
	{
		private final int childPosition;

		private OpenCourseOnClick(int childPosition)
		{
			this.childPosition = childPosition;
		}

		@Override
		public void onClick(View v)
		{
			// fragment.selectItem(childPosition);
			Intent intent = new Intent(context, CourseActivity.class);
			intent.putExtra(CourseActivity.COURSE_ID,
					DataStore.getCourseIdByPosition(childPosition));
			context.startActivity(intent);
		}
	}

	private class ViewHolder
	{
		private final TextView textView;

		/**
		 * @param textView
		 */
		public ViewHolder(TextView textView)
		{
			super();
			this.textView = textView;
		}

		/**
		 * @return the textView
		 */
		public synchronized TextView getTextView()
		{
			return textView;
		}
	}

}
