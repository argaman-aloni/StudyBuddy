package com.technion.studybuddy.GCM;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.provider.Settings.Secure;

import com.technion.studybuddy.exceptions.AccessException;
import com.technion.studybuddy.exceptions.invalidatedTokenExcpetion;

public class GoogleHttpContextProduction extends GoogleHttpContext
{

	/**
	 * @param context
	 *            - activity context for account manager resolve
	 * @param userName
	 *            - email of regitered username prefreed to be extracted from
	 *            the account manager
	 * @param baseUrl
	 *            - address of the appEngine site without http:// such as
	 *            example.appspot.com
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws AccessException
	 *             - thrown first time the request made should to ask for user
	 *             permission to connect to the appEngine site the activity
	 *             calling should implment onactivityresult and recreate the
	 *             this context
	 */
	public GoogleHttpContextProduction(final Activity context,
			final String userName, final String baseUrl)
			throws ClientProtocolException, IOException,
			OperationCanceledException, AuthenticatorException, AccessException
	{
		super(context);

		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				AndroidHttpClient httpClient = null;
				httpClient = AndroidHttpClient.newInstance("GetAuthCookieClient",
						context);
				try
				{
					sendRequest(userName, baseUrl,httpClient);
				} catch (invalidatedTokenExcpetion e)
				{
					AccountManager manager = AccountManager.get(context);
					manager.invalidateAuthToken("com.google", e.getToken());
					try
					{
						sendRequest(userName, baseUrl,httpClient);
					} catch (invalidatedTokenExcpetion e1)
					{
						e1.printStackTrace();
					}
				} finally
				{
					if (httpClient != null)
						httpClient.close();
				}

			}
		});
		t.start();
		try
		{
			t.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		httpContext.toString();
	}

	private void sendRequest(String userName, String baseUrl, AndroidHttpClient httpClient)
			throws invalidatedTokenExcpetion
	{
	
		String token = null;
		try
		{

			Account account = getAccountForName(context, userName);
			token = getAuthToken(account);
			httpClient.getParams().setBooleanParameter(
					ClientPNames.HANDLE_REDIRECTS, false);
			BasicCookieStore cookieStore_ = new BasicCookieStore();
			setAttribute(ClientContext.COOKIE_STORE, cookieStore_);
			HttpGet http_get = new HttpGet("http://" + baseUrl
					+ "/_ah/login?continue=http://" + baseUrl + "/&auth="
					+ token);
			HttpResponse response = httpClient.execute(http_get, this_);
			Cookie cookie = new IDCookie("http.id", Secure.getString(
					context.getContentResolver(), Secure.ANDROID_ID), baseUrl);
			cookieStore_.addCookie(cookie);
			checkResponse(cookieStore_, response);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (OperationCanceledException e)
		{
			e.printStackTrace();
		} catch (AuthenticatorException e)
		{
			e.printStackTrace();

		} catch (AccessException e)
		{
			context.startActivityForResult(e.getIntent(), USER_PERMISSION);

		} catch (AuthenticationException e)
		{
			throw new invalidatedTokenExcpetion(token);
		}
	}

	/**
	 * @param cookieStore
	 *            cookie store to check
	 * @param response
	 *            - resonse to check
	 * @throws AuthenticationException
	 */
	private void checkResponse(CookieStore cookieStore, HttpResponse response)
			throws AuthenticationException
	{
		if (response.getStatusLine().getStatusCode() != 302)
		{
			throw new AuthenticationException(
					"authentication response was not a redirect");
		}
		if (!isAuthenticationCookiePresent(cookieStore))
		{
			throw new AuthenticationException(
					"authentication cookie not found in cookie store");
		}
	}

	/**
	 * @param cookieStore
	 *            - cookie store to check
	 * @return
	 */
	private boolean isAuthenticationCookiePresent(CookieStore cookieStore)
	{
		for (Cookie cookie : cookieStore.getCookies())
		{
			if (cookie.getName().equals("ACSID")
					|| cookie.getName().equals("SACSID"))
				return true;
		}
		return false;
	}

	/**
	 * @param context
	 * @param username
	 *            - string username to find
	 * @return
	 */
	private Account getAccountForName(Context context, String username)
	{
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		if (accounts != null)
		{
			for (Account account : accounts)
			{
				if (account.name.equals(username))
				{
					return account;
				}
			}
		}
		return null;
	}

	/**
	 * @param account
	 *            account to ask token from
	 * @return - string token
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 * @throws AccessException
	 */
	private String getAuthToken(Account account)
			throws OperationCanceledException, AuthenticatorException,
			IOException, AccessException
	{
		AccountManager accountManager = AccountManager.get(context);
		AccountManagerFuture<Bundle> future = accountManager.getAuthToken(
				account, "ah", null, false, null, null);
		Bundle bundle = future.getResult();
		Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
		if (intent != null)
		{
			throw new AccessException(intent);
		}
		return bundle.getString(AccountManager.KEY_AUTHTOKEN);
	}

	@Override
	public Object getAttribute(String id)
	{
		return httpContext.getAttribute(id);
	}

	@Override
	public Object removeAttribute(String id)
	{
		return httpContext.removeAttribute(id);
	}

	@Override
	public void setAttribute(String id, Object obj)
	{
		httpContext.setAttribute(id, obj);

	}

}
