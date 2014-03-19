package com.technion.studybuddy.data;

public class Task
{
	String courseName;
	String type;
	int number;
	
	public static final String TypeLecure = "lecture";
	public static final String TypeVideo = "video";
	public static final String TypeTutorial = "tutorial";
	/**
	 * @param courseName
	 * @param type
	 * @param number
	 */
	public Task(String courseName, String type, int number)
	{
		super();
		this.courseName = courseName;
		this.type = type;
		this.number = number;
	}

	/**
	 * @return the courseName
	 */
	public synchronized String getCourseName()
	{
		return courseName;
	}

	/**
	 * @return the type
	 */
	public synchronized String getType()
	{
		return type;
	}

	/**
	 * @return the number
	 */
	public synchronized int getNumber()
	{
		return number;
	}

}
