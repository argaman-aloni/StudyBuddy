package com.technion.studybuddy.presenters;

import com.technion.studybuddy.data.DataStore;


public class CourseListPresenter
{

	public CourseListPresenter()
	{
	}

	public String getNameById(String id)
	{
		return DataStore.coursesById.get(id).getName();
	}

	public int getCount()
	{
		return DataStore.coursesList.size();
	}

	public String getIdByPosition(int position)
	{
		return DataStore.coursesList.get(position).getId();
	}

	public String getNameByPosition(int position)
	{
		return DataStore.coursesList.get(position).getName();
	}

}
