package com.technion.studybuddy.GCM;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.provider.Settings.Secure;

import com.google.android.gms.plus.Plus;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.AccessException;

/**
 * @author Arik
 * 
 */
public final class GoogleHttpContextDev extends GoogleHttpContext
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
	public GoogleHttpContextDev(final Context context, final String baseUrl)
			throws ClientProtocolException, IOException,
			OperationCanceledException, AuthenticatorException, AccessException
	{
		super(context);
		httpContext = new BasicHttpContext();
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				AndroidHttpClient httpClient = null;
				try
				{
					String email = Plus.AccountApi.getAccountName(DataStore
							.getInstance().getGoogleApiClient());
					Account account = getAccountForName(context, email);
					String token = getAuthToken(account);

					httpClient = AndroidHttpClient.newInstance(
							"GetAuthCookieClient", context);
					String url = "http://" + baseUrl + "/_ah/login?continue="
							+ URLEncoder.encode(baseUrl, "UTF-8");
					url += "&auth=" + token;
					BasicCookieStore cookieStore_ = new BasicCookieStore();

					setAttribute(ClientContext.COOKIE_STORE, cookieStore_);
					List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
					parameters.add(new BasicNameValuePair("email", email));
					parameters.add(new BasicNameValuePair("admin", "True"));
					parameters.add(new BasicNameValuePair("action", "Login"));
					HttpEntity entity = new UrlEncodedFormEntity(parameters);
					HttpPost request = new HttpPost(url);
					request.setEntity(entity);

					HttpParams params = new BasicHttpParams();
					HttpClientParams.setRedirecting(params, false);
					request.setParams(params);
					HttpResponse response = httpClient.execute(request, this_);
					Cookie cookie = new IDCookie("http.id", Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID),
							baseUrl);
					cookieStore_.addCookie(cookie);
					Header[] headers = response.getHeaders("Set-Cookie");
					if (response.getStatusLine().getStatusCode() != 302
							|| headers.length == 0)
						// failed. Response should be a redirect.
						return;

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
					// context.startActivityForResult(e.getIntent(),
					// MainActivity.USER_PERMISSION1);
					context.startActivity(e.getIntent());

				} catch (Exception e)
				{
					e.printStackTrace();
				} finally
				{
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
			for (Account account : accounts)
				if (account.name.equals(username))
					return account;
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
			throw new AccessException(intent);
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
