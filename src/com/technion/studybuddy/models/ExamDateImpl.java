package com.technion.studybuddy.models;

import java.util.Date;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.utils.DateUtils;


@DatabaseTable
public class ExamDateImpl extends AbstractPersistable<Course> implements
		ExamDate
{

	@DatabaseField(generatedId = true)
	private UUID id;

	@DatabaseField
	private String name;

	@DatabaseField
	private Date startDate;

	private static int START_DAYS_AHEAD = 10;

	@DatabaseField
	private Date endDate;

	@DatabaseField(foreign = true, canBeNull = false, index = true, columnName = PARENT_COLUMN_ID)
	private CourseImpl parent;

	public ExamDateImpl()
	{

	}

	public ExamDateImpl(String name, Date date)
	{
		this.name = name;
		endDate = date;
		startDate = DateUtils.xDaysBefore(START_DAYS_AHEAD, date);

	}

	@Override
	public int compareTo(ExamDate another)
	{
		return endDate.compareTo(another.getEndDate());
	}

	@Override
	public Course getParent()
	{
		return parent;
	}

	@Override
	public void setParent(Course t)
	{
		parent = (CourseImpl) t;
	}

	@Override
	public void setStartDate(Date d)
	{
		startDate = d;
	}

	@Override
	public void setEndDate(Date d)
	{
		endDate = d;
	}

	@Override
	public Date getStartDate()
	{
		return startDate;
	}

	@Override
	public Date getEndDate()
	{
		return endDate;
	}

	@Override
	public int getPeriodGone(Date d)
	{
		return DateUtils.daysInRange(startDate, d);

	}

	@Override
	public int getPeriodRemaining(Date d)
	{
		return DateUtils.daysInRange(d, endDate);
	}

	@Override
	public int getTotalPeriod()
	{
		return DateUtils.daysInRange(startDate, endDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ExamDate#shouldStudy(java.util.
	 * Date)
	 */
	@Override
	public boolean shouldStudy(Date today)
	{
		return today.after(startDate) || today.before(endDate);

	}

}
