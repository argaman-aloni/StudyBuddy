package com.technion.studybuddy.models;

import java.util.Date;

import com.technion.studybuddy.exceptions.ItemNotDoneError;
import com.technion.studybuddy.persisters.Persistable;
import com.technion.studybuddy.utils.Action;


public interface StudyItem extends Persistable<StudyResource>,
				Comparable<StudyItem>
{

	public String getLabel();

	public int getNum();

	public boolean isDone();

	public void toggleDone();

	public void setLabel(String newName);

	public Date getDoneDate() throws ItemNotDoneError;

	public void onDone(Action a);

	public void onUnDone(Action a);

}