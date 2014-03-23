package com.technion.studybuddy.persisters;

import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.CourseImpl;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.ExamDateImpl;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyItemImpl;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.StudyResourceImpl;

public enum Persisters {
	_;

	public static Persister<Course, CourseImpl, DataStore> course = new PersisterImpl<Course, CourseImpl, DataStore>(
					DataStore.getHelper().getCourseDao());

	public static Persister<StudyResource, StudyResourceImpl, Course> resource = new PersisterImpl<StudyResource, StudyResourceImpl, Course>(
					DataStore.getHelper().getStudyResourceDao());

	public static Persister<StudyItem, StudyItemImpl, StudyResource> studyitem = new PersisterImpl<StudyItem, StudyItemImpl, StudyResource>(
					DataStore.getHelper().getStudyItemsDao());

	public static Persister<Semester, Semester, DataStore> semester = new PersisterImpl<Semester, Semester, DataStore>(
					DataStore.getHelper().getSemesterDao());

	public static Persister<ExamDate, ExamDateImpl, Course> examDate = new PersisterImpl<ExamDate, ExamDateImpl, Course>(
					DataStore.getHelper().getExamDateDao());

}
