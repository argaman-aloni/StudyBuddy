package com.technion.studybuddy.models;

import org.json.JSONObject;

public interface JsonTranslator
{

	public JSONObject toJson();

	public void fromJson(JSONObject jsonStr);

}
