package com.technion.studybuddy.factories;

import java.util.Date;

import com.technion.studybuddy.Models.ExamDateImpl;

public class ExamFactory {

	public ExamDateImpl newExam(String name, Date due) {

		ExamDateImpl e = new ExamDateImpl(name, due);

		// if (lectures.size() > 0) {
		// e.addResource(resourceFactory.createWithStringItems(
		// StudyResource.LECTURES, lectures));
		// }
		// if (tutorials.size() > 0) {
		// e.addResource(resourceFactory.createWithStringItems(
		// StudyResource.TUTORIALS, tutorials));
		// }

		return e;
	}
}
