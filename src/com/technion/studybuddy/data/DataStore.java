package com.technion.studybuddy.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.technion.studybuddy.exceptions.CourseAlreadyExistsException;
import com.technion.studybuddy.factories.ExamFactory;
import com.technion.studybuddy.factories.StudyItemsFactory;
import com.technion.studybuddy.factories.StudyResourceFactory;
import com.technion.studybuddy.models.Course;
import com.technion.studybuddy.models.CourseImpl;
import com.technion.studybuddy.models.ExamDate;
import com.technion.studybuddy.models.ExamDateImpl;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyResource;
import com.technion.studybuddy.models.WorkStats;
import com.technion.studybuddy.network.CourseGrabber;
import com.technion.studybuddy.persisters.Persister;
import com.technion.studybuddy.presenters.CourseListPresenter;
import com.technion.studybuddy.presenters.CoursePresenter;
import com.technion.studybuddy.presenters.EditCoursePresenter;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Constants;

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

		if (DataStore.dbHelper == null)
			return;

		DataStore.dbHelper.close();
		DataStore.dbHelper = null;
	}

	public static EditCoursePresenter getEditCoursePresenter()
	{
		if (null == DataStore.editPresenter)
			DataStore.editPresenter = new EditCoursePresenter();

		return DataStore.editPresenter;
	}

	public static SBDatabaseHelper getHelper()
	{
		return DataStore.dbHelper;

	}

	public static DataStore getInstance()
	{
		if (DataStore.dataStore == null)
			DataStore.dataStore = new DataStore(DataStore.context);

		return DataStore.dataStore;
	}

	public static CourseListPresenter getMainPresenter()
	{
		if (null == DataStore.mainPresenter)
			DataStore.mainPresenter = new CourseListPresenter();

		return DataStore.mainPresenter;
	}

	public static String getMenu(int position)
	{
		return DataStore.menus[position];
	}

	public static int getMenuSize()
	{
		return DataStore.menus.length;
	}

	public static void initHelper(Context context)
	{
		if (DataStore.dbHelper != null)
			return;
		DataStore.dbHelper = SBDatabaseHelper.getHelper(context);
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
					context).getString(DataStore.SEMESTERSTART, "2013.10.13"));
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
		DataStore.semester.setStartDate(parseStartDateFromPreferences(context));
	}

	@Override
	public void accept(CompositeVisitor cv)
	{

		if (DataStore.semester != null)
			cv.visit(DataStore.semester);

		for (Course c : DataStore.coursesList)
			cv.visit(c);

		cv.visit(DataStore.ws);

	}

	public void addCourse(final Course c)
	{
		DataStore.coursesList.add(c);
		DataStore.coursesById.put(c.getId(), c);

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
				DataStore.coursesList.remove(c);
				DataStore.coursesById.remove(c.getId());
			}

		});

		Collections.sort(DataStore.coursesList);

	}

	public void addCourse(String newCourseId, String courseName,
			int numLectures, int numTutorials)
			throws CourseAlreadyExistsException
	{

		if (DataStore.coursesById.containsKey(newCourseId))
			throw new CourseAlreadyExistsException();

		Course c = new CourseImpl(newCourseId, courseName);

		StudyResource lectures = srFactory.createWithNumItems(
				Constants.LECTURE, numLectures);
		StudyResource tutorials = srFactory.createWithNumItems(
				Constants.TUTORIAL, numTutorials);

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

		Course c = DataStore.coursesById.get(courseID);

		if (newCourseId.equals(courseID) == false)
		{

			if (DataStore.coursesById.containsKey(newCourseId))
				throw new CourseAlreadyExistsException();

			DataStore.coursesById.remove(courseID);
			DataStore.dbHelper.getCourseDao().updateId((CourseImpl) c,
					newCourseId);
			DataStore.coursesById.put(String.valueOf(newCourseId), c);
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

		return DataStore.ws.getStatsLastXDays(today, days);

	}

	public void notifyCourseAdapters(Course course)
	{
		setChanged();
		notifyObservers();
	}

	public static String getCourseIdByPosition(int childPosition)
	{
		if (childPosition < 0 || childPosition >= DataStore.coursesList.size())
			return "";

		return DataStore.coursesList.get(childPosition).getId();
	}

	public void deleteCourse(String courseNumber)
	{
		DataStore.coursesById.get(courseNumber).delete();
		setChanged();
		notifyObservers();
	}

	public void sortCourses(Comparator<Course> comparator)
	{
		Collections.sort(DataStore.coursesList, comparator);
		setChanged();
		notifyObservers();
	}

	public static WorkStats getStats()
	{
		return DataStore.ws;
	}

	public void addExam(String courseID, String text, Date due)
	{
		ExamDateImpl e = examFactory.newExam(text, due);

		Course c = DataStore.coursesById.get(courseID);
		c.addExam(e);

		Persist.getInstance().visit(e);

		setChanged();
		notifyObservers(DataStore.CLASS_LIST);

	}

	public List<ExamDate> getRelaventExam(String courseID, Date date)
	{
		Course c = DataStore.coursesById.get(courseID);
		return c.getRelevantExams(date);
	}

	public static Context getContext()
	{
		return DataStore.context;
	}

	public void getAllCourses()
	{
		new CourseGrabber(DataStore.context).execute();
	}

	public void createCourseFromJson(JSONObject object) throws JSONException
	{
		JSONArray courses = object.getJSONArray("courses");
		for (int i = 0; i < courses.length(); i++)
		{
			JSONObject singleCourse = courses.getJSONObject(i);
			Course course = new CourseImpl();
			course.fromJson(singleCourse);
			if (!DataStore.coursesList.contains(course))
			{
				addCourse(course);
				Persist.getInstance().visit(course);
			}

		}
		setChanged();
		notifyObservers(DataStore.CLASS_LIST);

	}

	public void updateCourseFromJson(JSONObject object) throws JSONException
	{
		JSONArray array = object.getJSONArray("items");
		DataStore.getInstance().getCoursePresenter(object.getString("id"))
				.clearDoneState();
		List<String> names = new ArrayList<String>();
		Map<String, List<String>> linksByName = new HashMap<>();
		for (int i = 0; i < array.length(); i++)
		{
			String name = array.getJSONObject(i).getString("name");
			names.add(name);
			JSONArray jsonLinks = array.getJSONObject(i).getJSONArray("link");
			linksByName.put(name, new ArrayList<String>());
			for (int j = 0; j < jsonLinks.length(); j++)
				linksByName.get(name).add(jsonLinks.getString(j));
		}
		List<StudyItem> items = DataStore.getInstance()
				.getCoursePresenter(object.getString("id")).getAllItems();
		for (StudyItem studyItem : items)
		{
			if (linksByName.containsKey(studyItem.getLabel()))
				studyItem.setLinks(linksByName.get(studyItem.getLabel()));
			if (names.contains(studyItem.getLabel()))
				studyItem.setDone();
		}
		setChanged();
		notifyObservers(DataStore.CLASS_LIST);
	}

	public List<StudyItem> getAllCourseDoneItems(String courseId)
	{
		return getCoursePresenter(courseId).getAllDoneItems();
	}
}
