package com.technion.studybuddy.models;
//package com.technion.coolie.studybuddy.models;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import com.j256.ormlite.field.DatabaseField;
//import com.technion.coolie.studybuddy.data.CompositeVisitor;
//import com.technion.coolie.studybuddy.persisters.AbstractPersistable;
//import com.technion.coolie.studybuddy.utils.DateUtils;
//
//public class ExamImpl extends AbstractPersistable<Course> implements Exam {
//
//	@DatabaseField(generatedId = true)
//	private UUID id;
//
//	@DatabaseField
//	private String name;
//
//	@DatabaseField
//	private Date endDate;
//
//	@DatabaseField(foreign = true, canBeNull = false, index = true, columnName = PARENT_COLUMN_ID)
//	private CourseImpl parent;
//
//	private List<StudyResource> resources = new ArrayList<StudyResource>();
//
//	private Date startDate;
//
//	public ExamImpl(String name, Date due) {
//		startDate = new Date();
//		endDate = due;
//		this.name = name;
//	}
//
//	@Override
//	public int compareTo(Exam another) {
//		return endDate.compareTo(another.getEndDate());
//	}
//
//	@Override
//	public void accept(CompositeVisitor cv) {
//		for (StudyResource r : resources) {
//			cv.visit(r);
//		}
//	}
//
//	@Override
//	public void setStartDate(Date d) {
//		startDate = d;
//	}
//
//	@Override
//	public void setEndDate(Date d) {
//		endDate = d;
//	}
//
//	@Override
//	public Date getStartDate() {
//		return startDate;
//	}
//
//	@Override
//	public Date getEndDate() {
//		return endDate;
//	}
//
//	@Override
//	public int getPeriodGone(Date d) {
//		return DateUtils.daysInRange(startDate, d);
//	}
//
//	@Override
//	public int getPeriodRemaining(Date d) {
//		return DateUtils.daysInRange(d, endDate);
//	}
//
//	@Override
//	public int getTotalPeriod() {
//		return DateUtils.daysInRange(startDate, endDate);
//	}
//
//	@Override
//	public void setParent(Course p) {
//		parent = (CourseImpl) p;
//	}
//
//	@Override
//	public Course getParent() {
//		return parent;
//	}
//
//	@Override
//	public void addResource(StudyResource r) {
//
//		// TODO r.setParent(parent)
//		resources.add(r);
//	}
// }
