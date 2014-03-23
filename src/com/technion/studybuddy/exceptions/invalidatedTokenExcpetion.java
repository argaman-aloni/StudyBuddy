package com.technion.studybuddy.exceptions;

public class invalidatedTokenExcpetion extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2146708048504765429L;
	private String token;
	/**
	 * @param token
	 */
	public invalidatedTokenExcpetion(String token)
	{
		super();
		this.token = token;
	}
	/**
	 * @return the token
	 */
	public synchronized String getToken()
	{
		return token;
	}
	
}
