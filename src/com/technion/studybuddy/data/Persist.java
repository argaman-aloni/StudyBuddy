package com.technion.studybuddy.data;

import com.technion.studybuddy.Models.Course;
import com.technion.studybuddy.Models.ExamDate;
import com.technion.studybuddy.Models.Semester;
import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.Models.StudyResource;
import com.technion.studybuddy.Models.WorkStats;
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
