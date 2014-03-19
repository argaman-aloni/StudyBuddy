package com.technion.studybuddy.presenters;

import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.CourseAlreadyExistsException;


public class EditCoursePresenter
{

	private String oldCourseID = "";
	private boolean isEditMode = false;

	public EditCoursePresenter()
	{

	}

	public void commitCourse(	String newCourseId,
								String courseName,
								int numLectures,
								int numTutorials)
					throws CourseAlreadyExistsException
	{
		if (isEditMode)
		{
			DataStore.getInstance().editCourse(oldCourseID, newCourseId,
							courseName, numLectures, numTutorials);
		} else
		{
			DataStore.getInstance().addCourse(newCourseId, courseName,
							numLectures, numTutorials);
		}

	}

	public String getCourseIdAsString()
	{
		if (!isEditMode)
			return "";

		return DataStore.coursesById.get(oldCourseID).getId();

	}

	public String getCourseName()
	{
		if (!isEditMode)
			return "";

		return DataStore.coursesById.get(oldCourseID).getName();

	}

	public int getCourseResourceAmount(String name)
	{
		if (!isEditMode)
			return 0;

		return DataStore.coursesById.get(oldCourseID)
						.getResourceTotalItemCount(name);
	}

	public void reset()
	{
		oldCourseID = "";
		isEditMode = false;

	}

	public void setCourse(String courseID)
	{
		if (!DataStore.coursesById.containsKey(courseID))
			return;

		oldCourseID = courseID;
		isEditMode = true;
	}

}
