package com.technion.studybuddy.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.technion.studybuddy.data.Composite;
import com.technion.studybuddy.exceptions.NoItemsLeftException;
import com.technion.studybuddy.persisters.Persistable;

public interface StudyResource extends Composite, Persistable<Course> {

	public static final String LECTURES = "Lectures";
	public static final String TUTORIALS = "Tutorials";

	public void addItem(StudyItem item);

	public int getDoneItemsCount();

	public List<StudyItem> getDoneItems();

	public List<StudyItem> getRemainingItems();

	public String getName();

	public int getRemainingItemsCount();

	public int getTotalItemCount();

	public void setItems(List<StudyItem> list);

	public List<StudyItem> getItems();

	public int getNumOfItemsBehind(int semesterWeek, int totalWeeks);

	public int getNumOfItemsDue(int semesterWeek, int totalWeeks);

	public void toggleDone(int i);

	public StudyItem getNextItem() throws NoItemsLeftException;

	public Collection<Date> getDoneDates();

}