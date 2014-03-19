package com.technion.studybuddy.persisters;

import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Listenable;

public interface Persistable<PARENT> extends Listenable {
	public static final String PARENT_COLUMN_ID = "parent_id";
	public static final String PARENT_FIELD_NAME = "parent";

	void onDelete(Action a);

	void delete();

	/**
	 * set the containing object for persitance linking
	 * 
	 * @param p
	 *            - containing object
	 */
	void setParent(PARENT p);

	/**
	 * get the containing object
	 * 
	 * @return
	 */
	PARENT getParent();
}
