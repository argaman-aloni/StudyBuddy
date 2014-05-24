package com.technion.studybuddy.widget;

public class WidgetItem {

	private String widgetItemCourseName;
	private String widgetItemName;

	public WidgetItem(String widgetItemCourseName, String widgetItemName) {
		this.widgetItemCourseName = widgetItemCourseName;
		this.widgetItemName = widgetItemName;
	}

	public String getWidgetItemCourseName() {
		return widgetItemCourseName;
	}

	public String getWidgetItemName() {
		return widgetItemName;
	}

}
