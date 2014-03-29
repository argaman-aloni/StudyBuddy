package com.technion.studybuddy;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.listeners.DrawerItemClickListener;
import com.technion.studybuddy.utils.Constants;

public class MainActivity extends Activity
{

	public static final int USER_PERMISSION1 = 0;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private RelativeLayout mRelativeLayout;
	private TextView loginTitle;
	private TextView loginState;
	private ImageView loginImage;
	DrawerAdapter adapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.CoolieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stb_navigation_drawer);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.stb_drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_drawer_stb);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.drawer_content_layout);
		initloginState();

		adapter = new DrawerAdapter(getApplicationContext());

		mDrawerList.setAdapter(adapter);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		setToggleDrawerParams();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null)
			selectItem(-1);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	private void initloginState()
	{
		loginTitle = (TextView) findViewById(R.id.login_tv);
		loginState = (TextView) findViewById(R.id.status_tv);
		loginImage = (ImageView) findViewById(R.id.profile_pic_iv);

		SharedPreferences prefs = getSharedPreferences(Constants.PrefsContext,
				0);
		if (prefs.getBoolean(Constants.IS_REGISTERED, false))
		{

			loginTitle.setText(prefs.getString(Constants.ACCOUNT_NAME, ""));
			String str="<html><body><u>log out</u></body></html>";
			loginState.setText(Html.fromHtml(str));

			loginState.setTextColor(Color.BLUE);
			
		}

	}

	private void setToggleDrawerParams()
	{
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close)
		{

			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
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

}
