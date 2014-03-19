package com.technion.studybuddy.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.Views.StrikeThroughView;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CoursePresenter;


public class TaskAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;

	// private boolean selectMode;
	// private boolean[] selected;
	private CoursePresenter presenter;
	private List<StudyItem> items;
	private Map<Integer, StudyItem> deleted;

	/**
	 * @param context
	 */
	public TaskAdapter(Context context, String courseNumber)
	{
		super();
		deleted = new HashMap<Integer, StudyItem>();
		presenter = DataStore.getInstance().getCoursePresenter(courseNumber);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = presenter.getRemaingItems();
	}

	@Override
	public int getCount()
	{
		// return DataStore.getTaskSize();
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

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		StrikeThroughView view = null;
		if (convertView == null)
		{
			view = (StrikeThroughView) mInflater.inflate(
					R.layout.stb_view_task, null);

			convertView = view;

		} else
			view = (StrikeThroughView) convertView;
		view.setText(items.get(position).getLabel());
		return convertView;
	}

	public void remove(int item)
	{
		StudyItem studyItem = items.remove(item);
		studyItem.toggleDone();
		deleted.put(item, studyItem);
		notifyDataSetChanged();
	}

	public void restore(int id)
	{
		StudyItem studyItem = deleted.remove(id);
		studyItem.toggleDone();
		items.add(studyItem);
		notifyDataSetChanged();
	}
}
