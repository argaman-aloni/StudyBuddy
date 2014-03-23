package com.technion.studybuddy.data;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.types.UuidType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.technion.studybuddy.models.CourseImpl;
import com.technion.studybuddy.models.ExamDateImpl;
import com.technion.studybuddy.models.Semester;
import com.technion.studybuddy.models.StudyItemImpl;
import com.technion.studybuddy.models.StudyResourceImpl;
import com.technion.studybuddy.models.WorkStats;

public class SBDatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "stb_data.db";
	private static final int DATABASE_VERSION = 1;
	private RuntimeExceptionDao<CourseImpl, String> courseDao;
	private RuntimeExceptionDao<Semester, UuidType> semesterDao;
	private RuntimeExceptionDao<StudyResourceImpl, UuidType> resourceDao;
	private RuntimeExceptionDao<StudyItemImpl, UuidType> itemDao;
	private RuntimeExceptionDao<ExamDateImpl, UuidType> examDateDao;

	
	
    /**
     * Manage helper, such that only a single SQLite connection is kept open.
     * Replaces OpenHelperManager.
     */
    private static SBDatabaseHelper helper = null;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    public static synchronized SBDatabaseHelper getHelper(final Context context) {
        if (helper == null)
            helper = new SBDatabaseHelper(context);
        usageCounter.incrementAndGet();
        return helper;
     }

	
	private SBDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public RuntimeExceptionDao<CourseImpl, String> getCourseDao() {
		if (courseDao == null) {
			courseDao = getRuntimeExceptionDao(CourseImpl.class);
		}

		return courseDao;
	}

	public RuntimeExceptionDao<Semester, UuidType> getSemesterDao() {
		if (semesterDao == null) {
			semesterDao = getRuntimeExceptionDao(Semester.class);
		}

		return semesterDao;
	}

	public RuntimeExceptionDao<StudyItemImpl, UuidType> getStudyItemsDao() {
		if (itemDao == null) {
			itemDao = getRuntimeExceptionDao(StudyItemImpl.class);
		}

		return itemDao;
	}

	public RuntimeExceptionDao<StudyResourceImpl, UuidType> getStudyResourceDao()
	{
		if (resourceDao == null) {
			resourceDao = getRuntimeExceptionDao(StudyResourceImpl.class);
		}

		return resourceDao;
	}

	public RuntimeExceptionDao<WorkStats, String> getWorkStatsDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
		try {
			TableUtils.createTable(cs, CourseImpl.class);
			TableUtils.createTable(cs, StudyResourceImpl.class);
			TableUtils.createTable(cs, StudyItemImpl.class);
			TableUtils.createTable(cs, Semester.class);
			TableUtils.createTable(cs, ExamDateImpl.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(	SQLiteDatabase db,
							ConnectionSource cs,
							int oldVersion,
							int newVersion)
	{

	}

	@Override
	public void close() {
		if (usageCounter.decrementAndGet() > 0){
			return;
		}
		super.close();
		courseDao = null;
		resourceDao = null;
		itemDao = null;
		semesterDao = null;
		examDateDao = null;
		helper = null;
	}

	public RuntimeExceptionDao<ExamDateImpl, UuidType> getExamDateDao() {
		if (examDateDao == null) {
			examDateDao = getRuntimeExceptionDao(ExamDateImpl.class);
		}

		return examDateDao;
	}
}
