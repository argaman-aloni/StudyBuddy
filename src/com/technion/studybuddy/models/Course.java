package com.technion.studybuddy.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.technion.studybuddy.data.Composite;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.NoExamsForCourse;
import com.technion.studybuddy.exceptions.NoSuchResourceException;
import com.technion.studybuddy.persisters.Persistable;

public interface Course extends Comparable<Course>, Persistable<DataStore>,
		Composite, JsonTranslator {

	public void addStudyResource(StudyResource r);

	public Collection<StudyResource> getAllStudyResources();

	public String getId();

	public String getName();

	public int getNumStudyItemsRemaining();

	public int getNumOfItemsBehind(int semesterWeek, int totalWeeks);

	public int getNumOfItemsDue(int semesterWeek, int totalWeeks);

	public String getResourceName(int itemPosition);

	public int getResourceTotalItemCount(String name);

	public List<StudyItem> getStudyItemsDone(String resourceName);

	public List<StudyItem> getItems(String resourceName);

	public List<StudyItem> getRemainingItems(String resourceName);

	public int getItemsCount();

	public void setID(String newCourseId);

	public void setName(String courseName);

	public void addStudyResources(Collection<StudyResource> list);

	public StudyResource getResourceByName(String name)
			throws NoSuchResourceException;

	public Collection<Date> getDoneDates();

	public void addExam(ExamDate e);

	public ExamDate getNextExam() throws NoExamsForCourse;

	public List<ExamDate> getRelevantExams(Date d);

	public List<ExamDate> getAllExams();

	public void addExams(List<ExamDate> exams);

}