package com.technion.studybuddy.data;

import java.util.List;

import android.util.Log;

import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.WorkStats;
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

		for (Course c : results)
			ds.addCourse(c);

		List<Semester> list = Persisters.semester.getAll();

		if (list.size() > 0)
			DataStore.semester = list.get(0);

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

		for (Course c : DataStore.coursesList)
			ws.loadStats(c.getDoneDates());
		Log.d("loadChartsStats", "i'm loading the charts");

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
