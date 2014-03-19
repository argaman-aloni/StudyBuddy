package com.technion.studybuddy.data;


public interface Composite {
	/**
	 * Let the composite visitor go over the composited items
	 * 
	 * @param cv
	 *            CompositeVisitor
	 */
	public void accept(CompositeVisitor cv);

}
