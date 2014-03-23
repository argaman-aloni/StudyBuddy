package com.technion.studybuddy.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.data.CompositeVisitor;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.NoExamsForCourse;
import com.technion.studybuddy.exceptions.NoSuchResourceException;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.utils.Utils;
import com.technion.studybuddy.utils.Utils.Predicate;


@DatabaseTable
public class CourseImpl extends AbstractPersistable<DataStore> implements
				Course

{

	@DatabaseField(id = true)
	private String id;

	@DatabaseField
	private String name;

	private List<ExamDate> exams = new ArrayList<ExamDate>();

	private List<StudyResource> resources = new ArrayList<StudyResource>();

	public static void addStudyResources(Course c, List<StudyResource> resources)
	{
		c.addStudyResources(resources);
	}

	public CourseImpl() {
	}

	public CourseImpl(int id, String name) {
		this(String.valueOf(id), name);
	}

	public CourseImpl(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public void accept(CompositeVisitor cv) {
		for (StudyResource r : resources) {
			cv.visit(r);
		}

		for (ExamDate e : exams) {
			cv.visit(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#addStudyResource(com.technion
	 * .coolie.studybuddy.models.StudyResource)
	 */
	@Override
	public void addStudyResource(StudyResource r) {
		resources.add(r);
		r.setParent(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseImpl other = (CourseImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.ICourse#getAllStudyResources()
	 */
	@Override
	public Collection<StudyResource> getAllStudyResources() {
		return resources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.ICourse#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.ICourse#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getNumStudyItemsRemaining()
	 */
	@Override
	public int getNumStudyItemsRemaining() {
		int total = 0;
		for (StudyResource sr : resources) {
			total += sr.getRemainingItemsCount();
		}
		return total;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.ICourse#getResourceName(int)
	 */
	@Override
	public String getResourceName(int itemPosition) {
		if (itemPosition >= resources.size())
			return "";

		return resources.get(itemPosition).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getResourceTotalItemCount
	 * (java.lang.String)
	 */
	@Override
	public int getResourceTotalItemCount(String name) {
		try {
			return getResourceByName(name).getTotalItemCount();
		} catch (NoSuchResourceException e) {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getStudyItemsDone(java.
	 * lang.String)
	 */
	@Override
	public List<StudyItem> getStudyItemsDone(String resourceName) {
		try {
			return getResourceByName(resourceName).getDoneItems();
		} catch (NoSuchResourceException e) {
			return Collections.<StudyItem> emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getItems(java.lang.String)
	 */
	@Override
	public List<StudyItem> getItems(String resourceName) {
		try {
			return getResourceByName(resourceName).getItems();
		} catch (NoSuchResourceException e) {
			return Collections.<StudyItem> emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getRemainingItems(java.
	 * lang.String)
	 */
	@Override
	public List<StudyItem> getRemainingItems(String resourceName) {
		try {
			return getResourceByName(resourceName).getRemainingItems();
		} catch (NoSuchResourceException e) {
			return Collections.<StudyItem> emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.ICourse#getItemsCount()
	 */
	@Override
	public int getItemsCount() {
		int sum = 0;
		for (StudyResource sr : resources) {
			sum += sr.getTotalItemCount();
		}
		return sum;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public void setID(String newCourseId) {
		id = newCourseId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#setName(java.lang.String)
	 */
	@Override
	public void setName(String courseName) {
		name = courseName;
		update();
	}

	@Override
	public String toString() {
		return String.valueOf(id) + " " + name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#addStudyResources(java.
	 * util.Collection)
	 */
	@Override
	public void addStudyResources(Collection<StudyResource> list) {
		for (StudyResource r : list) {
			addStudyResource(r);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.ICourse#getResourceByName(java.
	 * lang.String)
	 */
	@Override
	public StudyResource getResourceByName(String name)
					throws NoSuchResourceException
	{

		for (StudyResource sr : resources) {
			if (sr.getName().equals(name))
				return sr;
		}

		throw new NoSuchResourceException();
	}

	@Override
	public void setParent(DataStore p) {
		return;
	}

	@Override
	public DataStore getParent() {
		return DataStore.getInstance();
	}

	@Override
	public int compareTo(Course another) {
		return another.getNumStudyItemsRemaining()
						- getNumStudyItemsRemaining();
	}

	@Override
	public Collection<Date> getDoneDates() {
		Collection<Date> $ = new ArrayList<Date>();

		for (StudyResource r : resources) {
			$.addAll(r.getDoneDates());
		}

		return $;
	}

	@Override
	public int getNumOfItemsBehind(int semesterWeek, int totalWeeks) {
		int $ = 0;

		for (StudyResource r : resources) {
			$ += r.getNumOfItemsBehind(semesterWeek, totalWeeks);
		}

		return $;
	}

	@Override
	public int getNumOfItemsDue(int semesterWeek, int totalWeeks) {
		int $ = 0;

		for (StudyResource r : resources) {
			$ += r.getNumOfItemsDue(semesterWeek, totalWeeks);
		}

		return $;
	}

	@Override
	public void addExam(final ExamDate e) {
		e.setParent(this);
		exams.add(e);
		Collections.sort(exams);
	}

	@Override
	public ExamDate getNextExam() throws NoExamsForCourse {
		if (exams.isEmpty())
			throw new NoExamsForCourse();

		return exams.get(0);
	}

	@Override
	public List<ExamDate> getRelevantExams(final Date d) {
		//Arik please check this as well.
		return Utils.filter(getAllExams(), new Predicate<ExamDate>() {

			@Override
			public boolean isTrueFor(ExamDate item) {
				return item.shouldStudy(d);
			}
		});
	}

	@Override
	public List<ExamDate> getAllExams() {
		return exams;
	}

	@Override
	public void addExams(List<ExamDate> exams) {
		for (ExamDate e : exams) {
			addExam(e);
		}
	}
}
