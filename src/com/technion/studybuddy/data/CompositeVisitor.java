package com.technion.studybuddy.data;

import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.WorkStats;


public interface CompositeVisitor {
	public void visit(Course c);

	public void visit(DataStore ds);

	public void visit(Semester s);

	public void visit(StudyItem it);

	public void visit(StudyResource sr);

	public void visit(WorkStats sr);

	public void visit(ExamDate ed);

}
