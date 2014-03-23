package com.technion.studybuddy.exceptions;

import android.content.Intent;

public class AccessException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5918924482975264552L;
	private Intent intent;
	/**
	 * @param intent
	 */
	public AccessException(Intent intent)
	{
		super();
		this.intent = intent;
	}
	/**
	 * @return the intent
	 */
	public synchronized Intent getIntent()
	{
		return intent;
	}
}
