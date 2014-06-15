package com.technion.studybuddy;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.utils.Constants;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnClickListener
{

	public static final int USER_PERMISSION1 = 0;
	DrawerAdapter adapter;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private RelativeLayout mRelativeLayout;
	private GoogleApiClient mGoogleApiClient;
	private static final int RC_SIGN_IN = 0;
	private boolean mIntentInProgress;
	/*
	 * Track whether the sign-in button has been clicked so that we know to
	 * resolve all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;
	private int locationInArray;
	private SharedPreferences sharedPref;

	/*
	 * Store the connection result from onConnectionFailed callbacks so that we
	 * can resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;
	private AQuery aq;

	private void initloginState()
	{
		if (ServerUtilities.isRegistered(this))
			aq.id(R.id.status_tv).clicked(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					SharedPreferences prefs = getSharedPreferences(
							Constants.PrefsContext, 0);
					String regid = prefs.getString(Constants.REGID_PREFS, "");
					ServerUtilities.setActivity(MainActivity.this);
					ServerUtilities.unregister(v.getContext(), regid);
					GCMRegistrar.unregister(MainActivity.this);
					Editor editor = prefs.edit();
					editor.remove(Constants.IS_REGISTERED);
					editor.remove(Constants.REGID_PREFS);
					editor.remove(Constants.ACCOUNT_NAME);
					editor.apply();
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
					aq.id(R.id.login_panel).visible();
					aq.id(R.id.login_tv).text("Logged as:");
					aq.id("not logged in");
					ImageView view = (ImageView) findViewById(R.id.profile_pic_iv);
					view.setBackgroundResource(R.drawable.profile_pic);

				}
			});
		else
			aq.id(R.id.login_panel).clicked(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					ServerUtilities.registerToServer(MainActivity.this);
				}
			});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.CoolieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		aq = new AQuery(this);
		aq.hardwareAccelerated11();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		setContentView(R.layout.stb_navigation_drawer);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.stb_drawer_layout);
		mDrawerList = (ExpandableListView) findViewById(R.id.list_drawer_stb);
		mDrawerList.setIndicatorBounds(0, 20);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.drawer_content_layout);
		initloginState();

		adapter = new DrawerAdapter(this);

		mDrawerList.setAdapter(adapter);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		setToggleDrawerParams();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		sendBroadcast(new Intent(this, TaskReciever.class));
		if (savedInstanceState == null)
			selectItem(-1);
		findViewById(R.id.sign_in_button).setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		adapter.notifyDataSetChanged();
		mGoogleApiClient.connect();
	}

	private void selectItem(int position)
	{
		// update the main content by replacing fragments
		if (position == -1)
		{
			MainFragment fragment = new MainFragment();
			fragment.setDrawerAdapter(adapter);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mRelativeLayout);
	}

	private void setToggleDrawerParams()
	{
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close)
		{

			@Override
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		if (!mIntentInProgress)
		{
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked)
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInErrors();
		}

	}

	@Override
	public void onConnected(Bundle arg0)
	{
		mSignInClicked = false;

		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
		{
			aq.id(R.id.login_panel).visibility(View.GONE);
			aq.id(R.id.status_tv)
					.text(Html
							.fromHtml("<html><body><u>log out</u></body></html>"))
					.textColor(Color.BLUE);
			SharedPreferences prefs = getSharedPreferences(
					Constants.PrefsContext, 0);

			DataStore.getInstance().setGoogleApiClient(mGoogleApiClient);
			Person currentPerson = Plus.PeopleApi
					.getCurrentPerson(mGoogleApiClient);
			String accountName = Plus.AccountApi
					.getAccountName(mGoogleApiClient);
			if (!ServerUtilities.isRegistered(this) && !accountName.isEmpty())
			{

				prefs.edit().putString(Constants.ACCOUNT_NAME, accountName)
						.putBoolean(Constants.IS_REGISTERED, true).apply();
				ServerUtilities.registerToServer(this);
			} else
				DataStore.getInstance().getAllCourses();
			aq.id(R.id.login_tv).text(
					currentPerson.getName().getGivenName() + " "
							+ currentPerson.getName().getFamilyName(), true);

			if (currentPerson.hasImage())
				aq.id(R.id.profile_pic_iv).image(
						currentPerson.getImage().getUrl(), true, true, 0, 0,
						new BitmapAjaxCallback()
						{
							@Override
							protected void callback(String url, ImageView iv,
									Bitmap bm, AjaxStatus status)
							{
								iv.setImageBitmap(getCroppedBitmap(bm));
							}
						});
		}

	}

	@Override
	public void onConnectionSuspended(int arg0)
	{
		mGoogleApiClient.connect();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
		if (mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		sharedPref = getSharedPreferences(Constants.popupSaredPrefrence,
				Context.MODE_PRIVATE);
		if (sharedPref.getInt(Constants.locationInArray, 0) < getResources()
				.getStringArray(R.array.popup_notifications).length)
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			locationInArray = sharedPref.getInt(Constants.locationInArray, 0);
			editor.putInt(Constants.locationInArray, ++locationInArray);
			editor.apply();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MainActivity.RC_SIGN_IN)
		{
			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting())
				mGoogleApiClient.connect();
		}

	}

	@Override
	public void onClick(View view)
	{
		if (view.getId() == R.id.sign_in_button
				&& !mGoogleApiClient.isConnecting())
		{
			mSignInClicked = true;
			resolveSignInErrors();
		}

	}

	private void resolveSignInErrors()
	{
		if (mConnectionResult.hasResolution())
			try
			{
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this,
						MainActivity.RC_SIGN_IN);
			} catch (SendIntentException e)
			{
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
	}

	private Bitmap getCroppedBitmap(Bitmap bitmap)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		final int color = 0xff424242;

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
}