package com.technion.studybuddy.Models;

import java.util.Date;
import java.util.UUID;

import android.text.format.DateUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.persisters.Persistable;

@DatabaseTable(tableName = "semesters")
public class Semester extends AbstractPersistable<DataStore> implements
		Persistable<DataStore>
{

	public final static int WEEKS_IN_SEMESTER = 14;

	public static int countWeeksBetween(Date start, Date end)
	{
		int result = (int) ((end.getTime() - start.getTime()) / DateUtils.WEEK_IN_MILLIS);
		return result;
	}

	@SuppressWarnings("unused")
	@DatabaseField(generatedId = true)
	private UUID id;
	@DatabaseField
	private Date startDate;

	@DatabaseField
	private Date endDate;

	public Semester()
	{

	}

	public Date getEndDate()
	{
		return endDate;
	}

	public int getSemesterWeek(Date today)
	{
		return Semester.countWeeksBetween(startDate, today) + 1;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public void setStartDate(Date startDate)
	{
		// this.startDate = (startDate.getTime() == 0) ? new Date(113, 9, 13)
		// : startDate;
		this.startDate = startDate;

		// preset endDate
		endDate = new Date(startDate.getTime() + WEEKS_IN_SEMESTER
				* DateUtils.WEEK_IN_MILLIS);

		update();
	}

	@Override
	public void setParent(DataStore p)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public DataStore getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getTotalWeeks()
	{
		return WEEKS_IN_SEMESTER;
	}

}
