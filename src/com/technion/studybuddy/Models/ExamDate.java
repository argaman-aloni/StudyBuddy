package com.technion.studybuddy.Models;

import java.util.Date;

import com.technion.studybuddy.persisters.Persistable;


public interface ExamDate extends Comparable<ExamDate>, StudyStrategy,
				Persistable<Course>
{

	public abstract boolean shouldStudy(Date today);

}