package com.technion.studybuddy.models;

import com.technion.studybuddy.utils.Utils.Mapper;
import com.technion.studybuddy.utils.Utils.Predicate;


public abstract class StudyItems {

	static Predicate<StudyItem> isDoneMatcher = new Predicate<StudyItem>() {
		@Override
		public boolean isTrueFor(StudyItem item) {
			return item.isDone();
		}

	};
	static Predicate<StudyItem> isNotDoneMatcher = new Predicate<StudyItem>() {

		@Override
		public boolean isTrueFor(StudyItem item) {
			return !item.isDone();
		}
	};
	static Mapper<StudyItem, Integer> mapToNum = new Mapper<StudyItem, Integer>()
	{

		@Override
		public Integer map(StudyItem item) {
			return item.getNum();
		}
	};

	public static Mapper<StudyItem, String> mapToLabel = new Mapper<StudyItem, String>()
	{

		@Override
		public String map(StudyItem item) {
			return item.getLabel();
		}

	};
	public static final int STUDY_ITEMS_START_INDEX = 1;

	static Predicate<StudyItem> idEquals(final int num) {
		return new Predicate<StudyItem>() {

			@Override
			public boolean isTrueFor(StudyItem item) {
				return item.getNum() == num;
			}
		};
	}

	static Predicate<StudyItem> idLargerThan(final int max) {
		return new Predicate<StudyItem>() {

			@Override
			public boolean isTrueFor(StudyItem item) {
				return item.getNum() > max;
			}
		};
	}

	public static String newName(String resourceName, int i) {
		return resourceName + " " + String.valueOf(i);
	}

}
