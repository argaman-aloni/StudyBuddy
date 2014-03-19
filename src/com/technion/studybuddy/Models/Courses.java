package com.technion.studybuddy.Models;

import java.util.Comparator;

public class Courses
{
	public static Comparator<Course> byProgress = new Comparator<Course>()
	{

		@Override
		public int compare(Course lhs, Course rhs)
		{
			return lhs.compareTo(rhs);
		}
	};
	public static Comparator<Course> byName = new Comparator<Course>()
	{

		@Override
		public int compare(Course lhs, Course rhs)
		{
			return lhs.getName().compareTo(rhs.getName());
		}
	};
	public static Comparator<Course> byNumber = new Comparator<Course>()
	{

		@Override
		public int compare(Course lhs, Course rhs)
		{
			return lhs.getId().compareTo(rhs.getId());
		}
	};
}
