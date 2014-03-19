package com.technion.studybuddy.Models;

import java.util.Date;

public interface StudyStrategy {

	void setStartDate(Date d);

	void setEndDate(Date d);

	Date getStartDate();

	Date getEndDate();

	int getPeriodGone(Date d);

	int getPeriodRemaining(Date d);

	int getTotalPeriod();

	// int[] getBurndown();

}
