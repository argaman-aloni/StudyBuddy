package com.technion.studybuddy.GCM;

public class NotRegisteredException extends Exception
{

	/**
	 * 
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = -3634392863234589736L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage()
	{
		// TODO Auto-generated method stub
		return "No registered User";
	}

}
