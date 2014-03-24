package com.technion.studybuddy.Views;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class AccountListPreference extends ListPreference
{

	public AccountListPreference(Context context)
	{
		super(context);
		fillAccounts(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AccountListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		fillAccounts(context);
	}
	private void fillAccounts(Context context)
	{
		Account[] accounts = AccountManager.get(context).getAccountsByType(
				"com.google");
		List<CharSequence> accountList = new ArrayList<CharSequence>();
		for (Account account : accounts)
		{
			accountList.add(account.name);
		}
		CharSequence[] entries = new CharSequence[accountList.size()];
		accountList.toArray(entries);
		setEntries(entries);
		setEntryValues(entries);
	}

	

}
