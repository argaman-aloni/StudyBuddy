package com.technion.studybuddy.factories;

import java.util.List;

import com.technion.studybuddy.Models.StudyItem;
import com.technion.studybuddy.Models.StudyResource;
import com.technion.studybuddy.Models.StudyResourceImpl;

public class StudyResourceFactory {

	private final StudyItemsFactory itemFactory;

	public StudyResourceFactory(StudyItemsFactory factory) {
		itemFactory = factory;
	}

	public StudyResource createWithNumItems(String name, int num) {
		List<StudyItem> items = itemFactory.createAmount(num, name);
		return createStudyResource(name, items);

	}

	public StudyResource createWithItems(String name, List<StudyItem> items) {
		StudyResource $ = createStudyResource(name, items);
		return $;
	}

	public StudyResource createWithStringItems(String name, List<String> strings)
	{
		List<StudyItem> items = itemFactory.createFromStringList(strings);
		return createStudyResource(name, items);
	}

	private StudyResource createStudyResource(String name, List<StudyItem> items)
	{
		StudyResource $ = new StudyResourceImpl(name, items);
		return $;
	}
}
