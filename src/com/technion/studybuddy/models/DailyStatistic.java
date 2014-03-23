package com.technion.studybuddy.models;

import java.util.Date;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.persisters.Persistable;
import com.technion.studybuddy.utils.DateUtils;


@DatabaseTable
public class DailyStatistic extends AbstractPersistable<WorkStats> implements
				Persistable<WorkStats>
{
	// id for DB
	@DatabaseField(generatedId = true)
	private UUID id;
	@DatabaseField
	private Date date;
	@DatabaseField
	private int amountDone;
	@DatabaseField
	private WorkStats parent;

	public DailyStatistic() {

	}

	public DailyStatistic(Date d) {
		date = DateUtils.getMidnight(d);
		amountDone = 0;
	}

	public Date getDate() {
		return date;
	}

	public void addDone() {
		amountDone++;
		update();
	}

	public void decreaseDone() {
		if (amountDone == 0)
			return;
		amountDone--;
		update();
	}

	public int getAmountDone() {
		return amountDone;
	}

	@Override
	public void setParent(WorkStats workStats) {
		parent = workStats;
	}

	@Override
	public WorkStats getParent() {
		return parent;
	}

 }
