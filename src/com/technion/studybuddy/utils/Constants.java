package com.technion.studybuddy.utils;


public class Constants
{
	public static final boolean debug = false;
	public static final String PrefsContext = "prefs";
	public static final String REGID_PREFS = "regid";
	public static final String IS_REGISTERED = "isRegistered";
	public static final String ACCOUNT_NAME = "account_name";
	public static final String ServerName = "swarlygeotracer.appspot.com";
	public static final String ServerNamedev = "studybuddyserver.appspot.com";
	public static final String UPDATE_FROM_GCM = "update_from_GCM";
	public static final String SERVER_URL_FULL = debug ? "http://192.168.1.104:8888"
			: "http://"+ServerName;
	public static final String SERVER_URL = debug ? "192.168.1.104:8888"
			: ServerName;
}
