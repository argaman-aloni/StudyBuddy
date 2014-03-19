package com.technion.studybuddy.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.technion.studybuddy.Models.Course;
import com.technion.studybuddy.Models.CourseImpl;
import com.technion.studybuddy.Models.ExamDate;
import com.technion.studybuddy.Models.ExamDateImpl;
import com.technion.studybuddy.Models.Semester;
import com.technion.studybuddy.Models.StudyResource;
import com.technion.studybuddy.Models.WorkStats;
import com.technion.studybuddy.exceptions.CourseAlreadyExistsException;
import com.technion.studybuddy.factories.ExamFactory;
import com.technion.studybuddy.factories.StudyItemsFactory;
import com.technion.studybuddy.factories.StudyResourceFactory;
import com.technion.studybuddy.persisters.Persister;
import com.technion.studybuddy.presenters.CourseListPresenter;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.presenters.EditCoursePresenter;
import com.technion.studybuddy.utils.Action;



public class DataStore extends Observable implements Composite
{
	private static String[] menus = new String[] { "Courses" };

	public static final String SEMESTERSTART = "stb_semester_start";
	public static final String SEMESTERLENGTH = "stb_simester_length";

	public static List<Course> coursesList = new ArrayList<Course>();
	public static Map<String, Course> coursesById = new LinkedHashMap<String, Course>();
	public static Semester semester = new Semester();
	private static SBDatabaseHelper dbHelper;

	private static CourseListPresenter mainPresenter;
	private static EditCoursePresenter editPresenter;

	public static final int taskForCourse = 14;
	public static final String CLASS_LIST = "classes";

	private static DataStore dataStore;

	private static Context context;

	public Persister<Course, CourseImpl, DataStore> coursePersister;

	private final StudyItemsFactory siFactory = new StudyItemsFactory();

	private final StudyResourceFactory srFactory = new StudyResourceFactory(
			siFactory);
	private final ExamFactory examFactory = new ExamFactory();

	private final static WorkStats ws = new WorkStats();

	public static void destroyHelper()
	{

		if (dbHelper == null)
			return;

		dbHelper.close();
		dbHelper = null;
	}

	public static EditCoursePresenter getEditCoursePresenter()
	{
		if (null == editPresenter)
		{
			editPresenter = new EditCoursePresenter();
		}

		return editPresenter;
	}

	public static SBDatabaseHelper getHelper()
	{
		return dbHelper;

	}

	public static DataStore getInstance()
	{
		if (dataStore == null)
		{
			dataStore = new DataStore(context);
		}

		return dataStore;
	}

	public static CourseListPresenter getMainPresenter()
	{
		if (null == mainPresenter)
		{
			mainPresenter = new CourseListPresenter();
		}

		return mainPresenter;
	}

	public static String getMenu(int position)
	{
		return menus[position];
	}

	public static int getMenuSize()
	{
		return menus.length;
	}

	public static void initHelper(Context context)
	{
		if (dbHelper != null)
			return;
		dbHelper = SBDatabaseHelper.getHelper(context);
	}

	public static void setContext(Context context)
	{
		DataStore.context = context;
		initHelper(context.getApplicationContext());
	}

	@SuppressLint("SimpleDateFormat")
	private static Date parseStartDateFromPreferences(Context context)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		
			try
			{
				return format.parse(PreferenceManager.getDefaultSharedPreferences(
						context).getString(SEMESTERSTART, "2013.10.13"));
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return new Date();
	}

	/**
	 * 
	 */
	private DataStore(Context context)
	{
		OpenHelperManager.setOpenHelperClass(SBDatabaseHelper.class);

		Load.getInstance().visit(this);
		semester.setStartDate(parseStartDateFromPreferences(context));
	}

	@Override
	public void accept(CompositeVisitor cv)
	{

		if (semester != null)
		{
			cv.visit(semester);
		}

		for (Course c : coursesList)
		{
			cv.visit(c);
		}

		cv.visit(ws);

	}

	public void addCourse(final Course c)
	{
		coursesList.add(c);
		coursesById.put(c.getId(), c);

		c.onUpdate(new Action()
		{

			@Override
			public void run()
			{
				notifyObservers();
			}
		});

		c.onDelete(new Action()
		{

			@Override
			public void run()
			{
				coursesList.remove(c);
				coursesById.remove(c.getId());
			}

		});

		Collections.sort(coursesList);

	}

	public void addCourse(String newCourseId, String courseName,
			int numLectures, int numTutorials)
			throws CourseAlreadyExistsException
	{

		if (coursesById.containsKey(newCourseId))
			throw new CourseAlreadyExistsException();

		Course c = new CourseImpl(newCourseId, courseName);

		StudyResource lectures = srFactory.createWithNumItems(
				StudyResource.LECTURES, numLectures);
		StudyResource tutorials = srFactory.createWithNumItems(
				StudyResource.TUTORIALS, numTutorials);

		c.addStudyResource(lectures);
		c.addStudyResource(tutorials);

		addCourse(c);

		Persist.getInstance().visit(c);

		setChanged();
		notifyObservers(DataStore.CLASS_LIST);

	}

	public void editCourse(String courseID, String newCourseId,
			String courseName, int numLectures, int numTutorials)
			throws CourseAlreadyExistsException
	{

		Course c = coursesById.get(courseID);

		if (newCourseId.equals(courseID) == false)
		{

			if (coursesById.containsKey(newCourseId))
				throw new CourseAlreadyExistsException();

			coursesById.remove(courseID);
			dbHelper.getCourseDao().updateId((CourseImpl) c, newCourseId);
			coursesById.put(String.valueOf(newCourseId), c);
		}

		c.setName(courseName);

		setChanged();
		notifyObservers(DataStore.CLASS_LIST);
	}

	public CoursePresenter getCoursePresenter(String courseNumber)
	{
		return new CoursePresenter(courseNumber);
	}

	public Integer[] getWorkStats(Date today, int days)
	{

		return ws.getStatsLastXDays(today, days);

	}

	public void notifyCourseAdapters(Course course)
	{
		setChanged();
		notifyObservers();
	}

	public static String getCourseIdByPosition(int childPosition)
	{
		if (childPosition < 0 || childPosition >= coursesList.size())
			return "";

		return coursesList.get(childPosition).getId();
	}

	public void deleteCourse(String courseNumber)
	{
		coursesById.get(courseNumber).delete();
		setChanged();
		notifyObservers();
	}

	public void sortCourses(Comparator<Course> comparator)
	{
		Collections.sort(coursesList, comparator);
		setChanged();
		notifyObservers();
	}

	public static WorkStats getStats()
	{
		return ws;
	}

	public void addExam(String courseID, String text, Date due)
	{
		ExamDateImpl e = examFactory.newExam(text, due);

		Course c = coursesById.get(courseID);
		c.addExam(e);

		Persist.getInstance().visit(e);

		setChanged();
		notifyObservers(DataStore.CLASS_LIST);

	}

	public List<ExamDate> getRelaventExam(String courseID, Date date)
	{
		Course c = coursesById.get(courseID);
		return c.getRelevantExams(date);
	}
}
