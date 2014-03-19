package com.technion.studybuddy.data;

import com.technion.studybuddy.Models.Course;
import com.technion.studybuddy.Models.ExamDate;
import com.technion.studybuddy.Models.Semester;
import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.Models.StudyResource;
import com.technion.studybuddy.Models.WorkStats;


public interface CompositeVisitor {
	public void visit(Course c);

	public void visit(DataStore ds);

	public void visit(Semester s);

	public void visit(StudyItem it);

	public void visit(StudyResource sr);

	public void visit(WorkStats sr);

	public void visit(ExamDate ed);

}
