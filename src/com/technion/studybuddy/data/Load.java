package com.technion.studybuddy.data;

import java.util.List;

import com.technion.studybuddy.Models.Course;
import com.technion.studybuddy.Models.ExamDate;
import com.technion.studybuddy.Models.Semester;
import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.Models.StudyResource;
import com.technion.studybuddy.Models.WorkStats;
import com.technion.studybuddy.persisters.Persisters;


public enum Load implements CompositeVisitor {
	dbLoader;

	public static Load getInstance() {
		return dbLoader;
	}

	@Override
	public void visit(Course c) {
		List<StudyResource> results = Persisters.resource.getByParent(c);

		c.addStudyResources(results);

		List<ExamDate> exams = Persisters.examDate.getByParent(c);

		c.addExams(exams);

		c.accept(this);

	}

	@Override
	public void visit(DataStore ds) {

		List<Course> results = Persisters.course.getAll();

		for (Course c : results) {
			ds.addCourse(c);
		}

		List<Semester> list = Persisters.semester.getAll();

		if (list.size() > 0) {
			DataStore.semester = list.get(0);
		}

		ds.accept(this);
	}

	@Override
	public void visit(Semester s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StudyResource sr) {
		List<StudyItem> list = Persisters.studyitem.getByParent(sr);
		sr.setItems(list);
		sr.accept(this);
	}

	@Override
	public void visit(WorkStats ws) {

		for (Course c : DataStore.coursesList) {
			ws.loadStats(c.getDoneDates());
		}

	}

	@Override
	public void visit(StudyItem it) {
		DataStore.getStats().listenTo(it);
	}

	@Override
	public void visit(ExamDate ed) {
		// TODO Auto-generated method stub

	}
}
