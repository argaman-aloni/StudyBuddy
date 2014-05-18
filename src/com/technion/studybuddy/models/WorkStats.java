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
import android.util.Log;

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
	private final double[] lecturesStats = new double[DAYS_IN_WEEK];
	private final double[] tutorialsStats = new double[DAYS_IN_WEEK];
	private int rescourseToUpdate = 0;

	public void loadStats(Collection<Date> dates)
	{
		for (Date d : dates)
			increase(d);
		loadChartsStats();
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
		List<Integer> values = new ArrayList<Integer>();

		for (Date d : DateUtils.getRange(n1, n2))
			if (statsMap.containsKey(d))
				values.add(statsMap.get(d));
			else
				values.add(0);

		return values.toArray(new Integer[values.size()]);
	}

	public Integer[] getStatsLastXDays(Date today, int days)
	{
		Date last = DateUtils.getMidnight(today);

		Calendar cal = Calendar.getInstance();
		cal.setTime(last);
		cal.add(Calendar.DAY_OF_MONTH, 1 - days);

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
				if (it.getParent().getName().equals(Constants.LECTURE))
					rescourseToUpdate = 0;
				else
					rescourseToUpdate = 1;
				increase(d);
				updateStatistics(1, DateUtils.getDayOfWeekFromDate(d));
				getCourseItemSyncIntent(it);

			}

		});

		it.onUnDone(new Action()
		{

			@Override
			public void run()
			{
				if (it.getParent().getName().equals(Constants.LECTURE))
					rescourseToUpdate = 0;
				else
					rescourseToUpdate = 1;
				decrease(d);
				updateStatistics(-1, DateUtils.getDayOfWeekFromDate(d));
				getCourseItemSyncIntent(it);
			}
		});
	}

	public double[] getLecturesStats()
	{
		return lecturesStats;
	}

	public double[] getTutorialsStats()
	{
		return tutorialsStats;
	}

	public double[] getTotalStats()
	{
		double[] arr = new double[DAYS_IN_WEEK];
		for (int i = 0; i < arr.length; i++)
			arr[i] = lecturesStats[i] + tutorialsStats[i];
		return arr;
	}

	public void loadChartsStats()
	{
		int i = 0;
		for (Course course : DataStore.coursesList)
		{
			for (StudyItem item : course.getItems(course.getResourceName(i)))
			{
				Log.d("loadChartsStats", "i'm starting to load the data.");
				if (item.getParent().getName().equals(Constants.LECTURE))
					rescourseToUpdate = 0;
				else
					rescourseToUpdate = 1;
				try
				{
					updateStatistics(1,
							DateUtils.getDayOfWeekFromDate(item.getDoneDate()));
				} catch (ItemNotDoneError e)
				{
					// Item isn't done - continue to the next item.
					Log.d("loadChartsStats", "i'm not done");
				}

			}
			i++;
		}
	}

	private void updateStatistics(int updateVal, int day)
	{
		if (rescourseToUpdate == 0)
		{
			if (lecturesStats[day] == 0 && updateVal == -1)
				return;
			lecturesStats[day] += updateVal;

		} else
		{
			if (tutorialsStats[day] == 0 && updateVal == -1)
				return;
			tutorialsStats[day] += updateVal;
		}
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
