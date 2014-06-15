package com.technion.studybuddy.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.technion.studybuddy.TaskReciever;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.ItemNotDoneError;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Constants;
import com.technion.studybuddy.utils.DateUtils;

public class WorkStats extends Observable
{

	private final int DAYS_IN_WEEK = 7;

	private final Map<Date, Integer> statsMap = new HashMap<Date, Integer>();

	public void loadStats(Collection<Date> dates)
	{
		for (Date d : dates)
			increase(d);
	}

	public void decrease(Date d)
	{
		Date u = DateUtils.getMidnight(d);
		if (!statsMap.containsKey(u))
			return;

		int old = statsMap.get(u);

		if (old == 0)
			return;

		assert (old > 0);
		statsMap.put(u, old - 1);
		setChanged();
		notifyObservers();
	}

	public int getStatsForDate(Date date)
	{
		Date n = DateUtils.getMidnight(date);
		if (statsMap.containsKey(n))
			return statsMap.get(n);

		return 0;
	}

	public Integer[] getStatsForRange(Date d1, Date d2)
	{
		Date n1 = DateUtils.getMidnight(d1);
		Date n2 = DateUtils.getMidnight(d2);
		new ArrayList<Integer>();

		Integer[] arr = new Integer[DAYS_IN_WEEK];
		initArray(arr);
		for (Course course : DataStore.coursesList)
			for (StudyItem item : DataStore.getInstance()
					.getAllCourseDoneItems(course.getId()))
				try
				{
					Date doneDate = item.getDoneDate();
					if (!(doneDate.before(n1) || doneDate.after(n2)))
					{
						int location = getLocationFromDate(n2, doneDate);
						arr[location]++;
					}
				} catch (ItemNotDoneError e)
				{
				}
		System.out.println(arr);
		return arr;
	}

	private int getLocationFromDate(Date n2, Date doneDate)
	{
		Calendar end = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		end.setTime(n2);
		start.setTime(doneDate);
		end.add(Calendar.DAY_OF_WEEK, -start.get(Calendar.DAY_OF_WEEK));
		return DAYS_IN_WEEK - end.get(Calendar.DAY_OF_WEEK) ;
	}

	private void initArray(Integer[] values)
	{
		for (int i = 0; i < DAYS_IN_WEEK; i++)
			values[i] = 0;
	}

	public Integer[] getStatsLastXDays(Date today, int days)
	{
		Date last = DateUtils.getMidnight(today);
		Calendar cal = Calendar.getInstance();
		cal.setTime(last);
		cal.add(Calendar.DATE, 1);
		last = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1 - (days + 1));

		Date first = cal.getTime();

		return getStatsForRange(first, last);

	}

	public void increase(Date d)
	{
		Date u = DateUtils.getMidnight(d);
		int old = 0;
		if (statsMap.containsKey(u))
			old = statsMap.get(u);
		statsMap.put(u, old + 1);
		setChanged();
		notifyObservers();
	}

	public void clear()
	{
		statsMap.clear();
	}

	public void listenTo(final StudyItem it)
	{
		final Date d = new Date();
		it.onDone(new Action()
		{

			@Override
			public void run()
			{
				increase(d);
				getCourseItemSyncIntent(it);
			}
		});

		it.onUnDone(new Action()
		{

			@Override
			public void run()
			{
				decrease(d);
				getCourseItemSyncIntent(it);
			}
		});
	}

	public double[] getLecturesStats()
	{
		return getStatsArray(Constants.LECTURE);
	}

	public double[] getTutorialsStats()
	{
		return getStatsArray(Constants.TUTORIAL);
	}

	private double[] getStatsArray(String itemType)
	{
		double[] arr = new double[DAYS_IN_WEEK];
		for (Course course : DataStore.coursesList)
			for (StudyItem item : DataStore.getInstance()
					.getAllCourseDoneItems(course.getId()))
				try
				{
					if (item.getItemType().equals(itemType))
						arr[DateUtils.getDayOfWeekFromDate(item.getDoneDate())]++;
				} catch (ItemNotDoneError e)
				{
					// TODO Auto-generated catch block
				}
		return arr;
	}

	public double[] getTotalStats()
	{
		double[] arr = new double[DAYS_IN_WEEK];
		for (Course course : DataStore.coursesList)
			for (StudyItem item : DataStore.getInstance()
					.getAllCourseDoneItems(course.getId()))
				try
				{
					arr[DateUtils.getDayOfWeekFromDate(item.getDoneDate())]++;
				} catch (ItemNotDoneError e)
				{
					// TODO Auto-generated catch block
				}
		return arr;
	}

	private void getCourseItemSyncIntent(final StudyItem it)
	{
		Context context = DataStore.getContext();
		Intent intent = new Intent(context, TaskReciever.class);
		Course course = it.getParent().getParent();
		List<StudyItem> doneItems = new ArrayList<>();
		JSONArray si = new JSONArray();
		for (StudyResource r : course.getAllStudyResources())
			for (StudyItem item : course.getStudyItemsDone(r.getName()))
				if (!doneItems.contains(item))
					doneItems.add(item);

		for (StudyItem studyItem : doneItems)
			si.put(studyItem.toJson());
		JSONObject json = new JSONObject();
		try
		{
			json.put("id", it.getParent().getParent().getId());
			json.put("items", si);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		intent.putExtra(Constants.JSON_ADDON, json.toString());
		intent.putExtra(Constants.TYPE_ADDON, Constants.TYPE_COURSEITEM);
		context.sendBroadcast(intent);
	}

}
