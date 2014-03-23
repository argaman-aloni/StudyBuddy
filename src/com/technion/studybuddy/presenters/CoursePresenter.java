package com.technion.studybuddy.presenters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.NoItemsLeftException;
import com.technion.studybuddy.exceptions.NoSuchResourceException;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;

public class CoursePresenter extends Observable {
	String courseNumber;
	Course course;
	// Arik please check this.... I added this field so that the file would
	// compile but I don't know if it's what I was supposed to do.
	private Semester semester;

	public CoursePresenter(String courseNumber) {
		this.courseNumber = courseNumber;
		course = DataStore.coursesById.get(courseNumber);
	}

	public String getName() {
		return course.getName();
	}

	public int getCurrentWeekNum(Date today) {
		return DataStore.semester.getSemesterWeek(today);
	}

	public Collection<String> getResourceNames() {
		Collection<StudyResource> srs = course.getAllStudyResources();
		Collection<String> $ = new ArrayList<String>(srs.size());
		for (StudyResource sr : srs) {
			$.add(sr.getName());
		}
		return $;
	}

	public String getResourceName(int itemPosition) {

		return course.getResourceName(itemPosition);
	}

	public int getSemesterLength() {
		return Semester.WEEKS_IN_SEMESTER;
	}

	public int getDoneItemsCount(String resourceName) {
		try {
			return course.getResourceByName(resourceName).getDoneItemsCount();
		} catch (NoSuchResourceException e) {
			return 0;
		}
	}

	public int getTotalItemCount(String resourceName) {
		try {
			return course.getResourceByName(resourceName).getTotalItemCount();
		} catch (NoSuchResourceException e) {
			return 0;
		}
	}

	public int getNumOfItemsBehind(Date date, String resourceName) {

		try {
			return course.getResourceByName(resourceName).getNumOfItemsBehind(
					semester.getSemesterWeek(date), semester.getTotalWeeks());
		} catch (NoSuchResourceException e) {
			return 0;
		}
	}

	public int getNumOfItemsDue(Date date, String resourceName) {

		try {
			return course.getResourceByName(resourceName).getNumOfItemsDue(
					semester.getSemesterWeek(date), semester.getTotalWeeks());
		} catch (NoSuchResourceException e) {
			return 0;
		}
	}

	public int getNumOfItemsBehind(Date date) {

		return course.getNumOfItemsBehind(semester.getSemesterWeek(date),
				semester.getTotalWeeks());
	}

	public int getNumOfItemsDue(Date date) {

		return course.getNumOfItemsDue(semester.getSemesterWeek(date),
				semester.getTotalWeeks());
	}

	public String getNextItem(String resourseName) {
		try {
			return course.getResourceByName(resourseName).getNextItem()
					.getLabel();
		} catch (NoSuchResourceException e) {
			return "";
		} catch (NoItemsLeftException e) {
			return "";
		}
	}

	public List<StudyItem> getRemaingItems() {
		List<StudyItem> allItems = new ArrayList<StudyItem>();
		for (StudyResource r : course.getAllStudyResources()) {
			allItems.addAll(course.getRemainingItems(r.getName()));
		}
		return allItems;

	}

	public Date getLastDateStudied() {
		List<Date> doneDates = new ArrayList<Date>(course.getDoneDates());
		Comparator<Date> dateComparator = new Comparator<Date>() {

			@Override
			public int compare(Date lhs, Date rhs) {

				return (int) (lhs.getTime() - rhs.getTime());
			}
		};
		Collections.sort(doneDates, dateComparator);
		if (doneDates.isEmpty())
			return new Date();
		return doneDates.get(0);
	}
}
