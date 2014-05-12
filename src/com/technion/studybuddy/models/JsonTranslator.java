package com.technion.studybuddy.models;

public interface JsonTranslator {

	public String toJson();

	public Object fromJson(String jsonStr);

}
