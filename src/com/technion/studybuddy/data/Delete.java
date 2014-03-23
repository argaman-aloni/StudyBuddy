package com.technion.studybuddy.data;

import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.WorkStats;



public enum Delete implements CompositeVisitor {
	_;

	public static Delete getInstance() {
		return _;

	}

	@Override
	public void visit(Course c) {
		c.accept(this);
		c.delete();
	}

	@Override
	public void visit(DataStore ds) {
		ds.accept(this);
	}

	@Override
	public void visit(Semester s) {
		s.delete();
	}

	@Override
	public void visit(StudyItem it) {
		it.delete();

	}

	@Override
	public void visit(StudyResource sr) {
		sr.accept(this);
		sr.delete();
	}

	@Override
	public void visit(WorkStats ws) {
		return;
	}

	@Override
	public void visit(ExamDate ed) {
		ed.delete();
	}

}
