package com.technion.studybuddy.factories;

import java.util.ArrayList;
import java.util.List;

import com.technion.studybuddy.models.StudyItem;
import com.technion.studybuddy.models.StudyItemImpl;
import com.technion.studybuddy.models.StudyItems;

public class StudyItemsFactory {

	/**
	 * Make a list of StudyItems named accordingly to list of strings
	 * 
	 * @param list
	 *            - list of strings to make StudyItems from
	 * @return list of StudyItems numbered [1..list.size()] with labels
	 */
	public List<StudyItem> createFromStringList(List<String> list) {
		List<StudyItem> $ = new ArrayList<StudyItem>(list.size());

		int i = StudyItems.STUDY_ITEMS_START_INDEX;
		for (String s : list) {
			$.add(newStudyItem(i, s));
			++i;
		}

		return $;
	}

	public List<StudyItem> createRange(int first, int last, String resourceName)
	{
		List<StudyItem> $ = new ArrayList<StudyItem>(last - first + 1);

		for (int i = first; i <= last; ++i) {
			$.add(newStudyItem(i, StudyItems.newName(resourceName, i)));
		}
		return $;
	}

	/**
	 * Make a list of StudyItems with the <b>resourceName</b> label
	 * 
	 * @param amount
	 *            - amount of StudyItems to return
	 * @param resourceName
	 * @return list of StudyItems numbered [1..amount]
	 */
	public List<StudyItem> createAmount(int amount, String resourceName) {
		return createRange(StudyItems.STUDY_ITEMS_START_INDEX, amount,
						resourceName);
	}

	private StudyItem newStudyItem(int num, String label) {
		StudyItem $ = new StudyItemImpl(num, label);
		return $;
	}
}
