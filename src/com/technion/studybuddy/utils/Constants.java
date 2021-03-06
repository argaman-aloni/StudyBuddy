package com.technion.studybuddy.utils;

import android.provider.BaseColumns;

public class Constants
{
	public static final boolean debug = false;
	public static final String PrefsContext = "prefs";
	public static final String REGID_PREFS = "regid";
	public static final String IS_REGISTERED = "isRegistered";
	public static final String ACCOUNT_NAME = "account_name";
	public static final String ServerName = "studybuddyserver.appspot.com";
	public static final String ServerNamedev = "studybuddyserver.appspot.com";
	public static final String UPDATE_FROM_GCM = "update_from_GCM";
	public static final String SERVER_URL_FULL = Constants.debug ? "http://192.168.1.104:8888"
			: "http://" + Constants.ServerName;
	public static final String SERVER_URL = Constants.debug ? "192.168.1.104:8888"
			: Constants.ServerName;
	public static final String DATA_SYNC = Constants.SERVER_URL + "/data";
	public static final String JSON_ADDON = "json";
	public static final String TYPE_ADDON = "type";

	// WIDGET DATA
	public static final String EXTRA_ITEM = "com.technion.studyBuddy.EXTRA_ITEM";

	// sqlite data
	public static final String Table_Name = "networkStatus";
	public static final String DB_Name = "networkStatus.db";
	public static final String Network_fields[] = { BaseColumns._ID, "object",
			"progress", "type", "priority" };
	public static final String Network_types[] = {
			"INTEGER PRIMARY KEY AUTOINCREMENT", "VARCHAR", "INTEGER",
			"VARCHAR", "INTEGER" };

	public static class Network_fields_constants
	{
		public static final int BaseColumn = 0; // 0
		public static final int object = 1 + Network_fields_constants.BaseColumn; // 1
		public static final int progress = Network_fields_constants.object + 1; // 2
		public static final int type = Network_fields_constants.progress + 1; // 3
		public static final int priority = Network_fields_constants.type + 1; // 4

	}

	public static final int DB_ver = 1;
	public static final String LINKS_JSON = "links_arry";
	public static final String TYPE_COURSEITEM = "courseItem";
	public static final String OBJECT_TYPE_ADDON = "objectType";
	public static final String COURSE_ALL = "course.all";
	public static String SYNC_REQUEST = "sync";
	public static final String TUTORIAL = "Tutorial";
	public static final String LECTURE = "Lecture";

	// Notification shared preference data
	public static final String popupSaredPrefrence = "com.technion.studybuddy.SHARED_PREFRENCE";
	public static final String locationInArray = "com.technion.studybuddy.SHARED_PREFRENCE.location_in_array";
	public static final String DELETECOURSE = "deleteCourse";

}
