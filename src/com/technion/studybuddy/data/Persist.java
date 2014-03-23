package com.technion.studybuddy.data;

import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.WorkStats;
import com.technion.studybuddy.persisters.Persisters;

public enum Persist implements CompositeVisitor {
	_;

	public static Persist getInstance() {
		return _;
	}

	@Override
	public void visit(Course c) {
		Persisters.course.persist(c);
		c.accept(this);
	}

	@Override
	public void visit(DataStore ds) {
		ds.accept(this);
	}

	@Override
	public void visit(StudyItem it) {
		Persisters.studyitem.persist(it);

		DataStore.getStats().listenTo(it);
	}

	@Override
	public void visit(Semester s) {
		Persisters.semester.persist(s);
	}

	@Override
	public void visit(StudyResource sr) {
		Persisters.resource.persist(sr);
		sr.accept(this);
	}

	@Override
	public void visit(WorkStats ws) {
		return;
	}

	@Override
	public void visit(ExamDate ed) {
		Persisters.examDate.persist(ed);
	}
}
