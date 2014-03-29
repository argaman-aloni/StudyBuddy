/**
 * 
 */
package com.technion.studybuddy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.technion.studybuddy.R;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.presenters.CourseListPresenter;

/**
 * @author Argaman
 *
 */
public class DrawerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private CourseListPresenter presenter;
	public DrawerAdapter(Context context)
	{
		super();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		presenter = DataStore.getMainPresenter();
	}
	
	@Override
	public int getCount() {
		return DataStore.getMainPresenter().getCount();
	}

	@Override
	public Object getItem(int position) {
		return presenter.getNameByPosition(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		String courseNumber = presenter.getIdByPosition(position);
		String courseName = presenter.getNameByPosition(position);
		if(convertView == null){
			convertView = inflateView();
		}
		view = convertView;
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.courseTitle.setText(courseName);
		holder.courseNumber.setText(courseNumber);
		return view;
	}

	private View inflateView() {
		View res = mInflater.inflate(R.layout.drawer_courses_layout, null);
		ViewHolder holder = new ViewHolder();
		holder.courseTitle = (TextView)res.findViewById(R.id.course_tv);
		holder.courseNumber = (TextView)res.findViewById(R.id.course_number_tv);
		res.setTag(holder);
		return res;
	}
	
	private class ViewHolder{
		public TextView courseTitle;
		public TextView courseNumber;
	}

}
