package com.technion.studybuddy.Adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LinksAdapter extends ArrayAdapter<String> {

	Context context;
	List<String> links;

	public LinksAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.context = context;
		links = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
		// Add the intent that displays the pdf.
	}

}
