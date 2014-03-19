package com.technion.studybuddy.data;

import java.util.Date;

public class DateProvider {

	static Date date = new Date();

	public static Date today() {
		return new Date();
	}

	public static void setDate(Date d) {
		date = d;
	}

	public static Date getDate() {
		return date;
	}

}
