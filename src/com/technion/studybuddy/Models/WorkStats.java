package com.technion.studybuddy.Models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.DateUtils;


public class WorkStats extends Observable {

	private Map<Date, Integer> statsMap = new HashMap<Date, Integer>();

	public void loadStats(Collection<Date> dates) {
		for (Date d : dates) {
			increase(d);
		}
	}

	public void decrease(Date d) {
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

	public int getStatsForDate(Date date) {
		Date n = DateUtils.getMidnight(date);
		if (statsMap.containsKey(n))
			return statsMap.get(n);

		return 0;
	}

	public Integer[] getStatsForRange(Date d1, Date d2) {
		Date n1 = DateUtils.getMidnight(d1);
		Date n2 = DateUtils.getMidnight(d2);
		List<Integer> values = new ArrayList<Integer>();

		for (Date d : DateUtils.getRange(n1, n2)) {
			if (statsMap.containsKey(d)) {
				values.add(statsMap.get(d));
			} else {
				values.add(0);
			}

		}

		return values.toArray(new Integer[values.size()]);
	}

	public Integer[] getStatsLastXDays(Date today, int days) {
		Date last = DateUtils.getMidnight(today);

		Calendar cal = Calendar.getInstance();
		cal.setTime(last);
		cal.add(Calendar.DAY_OF_MONTH, 1 - days);

		Date first = cal.getTime();

		return getStatsForRange(first, last);

	}

	public void increase(Date d) {
		Date u = DateUtils.getMidnight(d);
		int old = 0;
		if (statsMap.containsKey(u)) {
			old = statsMap.get(u);
		}

		statsMap.put(u, old + 1);
		setChanged();
		notifyObservers();
	}

	public void clear() {
		statsMap.clear();
	}

	public void listenTo(StudyItem it) {
		it.onDone(new Action() {

			@Override
			public void run() {
				increase(new Date());
			}
		});

		it.onUnDone(new Action() {

			@Override
			public void run() {
				decrease(new Date());
			}
		});
	}

}
