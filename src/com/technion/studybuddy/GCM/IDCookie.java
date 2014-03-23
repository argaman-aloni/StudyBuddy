package com.technion.studybuddy.GCM;

import java.util.Date;

import org.apache.http.cookie.Cookie;

import android.text.format.DateUtils;

public class IDCookie implements Cookie
{
	private String name;
	private String value;
	private String domain;

	@Override
	public String getComment()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommentURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain()
	{
		if (domain.contains(":"))
			return domain.substring(0, domain.indexOf(':'));
		return domain;
	}

	@Override
	public Date getExpiryDate()
	{
		// TODO Auto-generated method stub
		return new Date(System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS
				* 10);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getPath()
	{
		// TODO Auto-generated method stub
		return "/";
	}

	@Override
	public int[] getPorts()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public int getVersion()
	{
		return 1;
	}

	@Override
	public boolean isExpired(Date date)
	{
		return false;
	}

	/**
	 * @param name
	 * @param value
	 * @param domain
	 */
	public IDCookie(String name, String value, String domain)
	{
		super();
		this.name = name;
		this.value = value;
		this.domain = domain;
	}

	@Override
	public boolean isPersistent()
	{
		return false;
	}

	@Override
	public boolean isSecure()
	{
		return false;
	}

}
