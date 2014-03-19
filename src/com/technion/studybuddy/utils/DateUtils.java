package com.technion.studybuddy.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public enum DateUtils {
	INSTANCE;

	public static Date getMidnight(Date d) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Iterable<Date> getRange(Date n1, Date n2) {
		return new DateUtils.DaysRange(n1, n2);
	}

	public static Date xDaysBefore(int n, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -n);
		return cal.getTime();
	}

	public static int daysInRange(Date start, Date end) {

		if (start.after(end))
			return 0;

		return getRangeAux(start, end).size();
	}

	private static List<Date> getRangeAux(Date start, Date end) {
		List<Date> $ = new ArrayList<Date>();

		start = getMidnight(start);
		end = getMidnight(end);

		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		while (cal.getTime().before(end)) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			$.add(cal.getTime());
		}
		return $;
	}

	public static class DaysRange implements Iterable<Date> {

		private final List<Date> list;

		public DaysRange(Date startDate, Date endDate) {
			list = getRangeAux(startDate, endDate);
		}

		@Override
		public Iterator<Date> iterator() {
			return list.iterator();
		}
	}
}
