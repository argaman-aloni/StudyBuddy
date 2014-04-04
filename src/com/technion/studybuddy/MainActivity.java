package com.technion.studybuddy;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.technion.studybuddy.Adapters.DrawerAdapter;
import com.technion.studybuddy.GCM.ServerUtilities;
import com.technion.studybuddy.listeners.DrawerItemClickListener;
import com.technion.studybuddy.utils.Constants;

public class MainActivity extends Activity {

	public static final int USER_PERMISSION1 = 0;
	DrawerAdapter adapter;
	private TextView loginState;
	private TextView loginTitle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private RelativeLayout mRelativeLayout;

	private void initloginState() {
		RelativeLayout loginLayout = (RelativeLayout) findViewById(R.id.login_panel);
		loginTitle = (TextView) findViewById(R.id.login_tv);
		loginState = (TextView) findViewById(R.id.status_tv);

		SharedPreferences prefs = getSharedPreferences(Constants.PrefsContext,
				0);
		if (prefs.getBoolean(Constants.IS_REGISTERED, false)) {
			loginLayout.setVisibility(View.GONE);
			loginTitle.setText(prefs.getString(Constants.ACCOUNT_NAME, ""));
			String str = "<html><body><u>log out</u></body></html>";
			loginState.setText(Html.fromHtml(str));

			loginState.setTextColor(Color.BLUE);
			loginState.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences prefs = getSharedPreferences(
							Constants.PrefsContext, 0);
					String regid = prefs.getString(Constants.REGID_PREFS, "");
					ServerUtilities.setActivity(MainActivity.this);
					ServerUtilities.unregister(v.getContext(), regid);
					GCMRegistrar.unregister(MainActivity.this);
					Editor editor = prefs.edit();
					editor.putBoolean(Constants.IS_REGISTERED, false);
					editor.putString(Constants.REGID_PREFS, "");
					editor.commit();
				}
			});
		} else {
			loginLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ServerUtilities.registerToServer(MainActivity.this);
				}
			});
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.CoolieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		if (savedInstanceState == null) {
			selectItem(-1);
		}

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		adapter.notifyDataSetChanged();
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		if (position == -1) {
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

	private void setToggleDrawerParams() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
	}

}