package com.technion.studybuddy.Adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.NoSuchResourceException;

public class ResourceReviewAdapter extends BaseAdapter
{
	// private String resourceName;
	private LayoutInflater inflater;
	// private CoursePresenter presenter;
	private List<StudyItem> items;
	private List<StudyItem> data;

	/**
	 * 
	 * 
	 */
	public ResourceReviewAdapter(Context context, String courseId,
			String resourceName, List<StudyItem> data)
	{
		super();
		inflater = LayoutInflater.from(context);

		try
		{
			items = DataStore.coursesById.get(courseId)
					.getResourceByName(resourceName).getItems();
		} catch (NoSuchResourceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.data = data;
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = new ViewHolder();
		if (convertView == null)
		{
			convertView = inflater.inflate(
					android.R.layout.simple_list_item_multiple_choice, null);
			holder.textView = (CheckedTextView) convertView;
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		final CheckedTextView view = ((ViewHolder) convertView.getTag()).textView;
		holder.textView.setText(items.get(position).getLabel());
		holder.textView.setChecked(data.contains(items.get(position)));
		holder.textView.setOnClickListener(new IndexedOnclickListener(position)
		{

			@Override
			public void onClick(View v)
			{
				view.toggle();
				if (view.isChecked())
					data.add(items.get(position));
				else
					data.remove(items.get(position));
			}
		});
		return convertView;
	}

	private class ViewHolder
	{
		public CheckedTextView textView;
	}

	private abstract class IndexedOnclickListener implements OnClickListener
	{
		protected final int position;

		private IndexedOnclickListener(int position)
		{
			this.position = position;
		}

	}
}
