package com.produce.ciro.bchart4;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	// public static final String PREFS_NAME = "MyPrefsFile";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mMenuTitles;
	
	// try with cache fragments
	private Fragment _fragment1;
	private Fragment _fragment2;
	private Fragment _fragment3;
	
	private FragmentManager _fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onCreate");

		mTitle = mDrawerTitle = getTitle();
		mMenuTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		/**
		 * // update the main content by replacing fragments Fragment fragment = new ChartFragment(); Bundle args = new Bundle();
		 * args.putInt(ChartFragment.ARG_CHART_NUMBER, position); fragment.setArguments(args);
		 * 
		 * FragmentManager fragmentManager = getFragmentManager(); fragmentManager.beginTransaction().replace(R.id.content_frame,
		 * fragment).commit();
		 **/
		String chart = getResources().getStringArray(R.array.drawer_array)[position];
		Log.i(TAG, chart);
		
		_fragmentManager = getFragmentManager();
		
		if (position == 0) {
			if (_fragment1 == null) {
				_fragment1 = new TransactionFragment();
			}
			// Bundle args = new Bundle();
			// args.putInt(TransactionFragment.ARG_POSITION, position);
			// newFragment.setArguments(args);
			_fragmentManager.beginTransaction().replace(R.id.content_frame, _fragment1).commit();
		}
		else if (position == 1) {
			if (_fragment2 == null) {
				_fragment2 = new OrderBookFragment();
			}

			// Bundle args = new Bundle();
			// args.putInt(TransactionFragment.ARG_POSITION, position);
			// newFragment.setArguments(args);
			_fragmentManager.beginTransaction().replace(R.id.content_frame, _fragment2).commit();
		}
		else if (position == 2) {
			if (_fragment3 == null) {
				_fragment3 = new TickerFragment();
			}

			// Bundle args = new Bundle();
			// args.putInt(TransactionFragment.ARG_POSITION, position);
			// newFragment.setArguments(args);
			_fragmentManager.beginTransaction().replace(R.id.content_frame, _fragment3).commit();
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");

		String CHOICE = "0";
		boolean flag = isTooOld();
		if (flag) {
			CHOICE = "1";
		}
		Intent intent = new Intent(this, BWTUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
		Intent intent = new Intent(this, BWTUpdateService.class);
		stopService(intent);

		// do you want to put it here or onPause?
		long newerTime = System.currentTimeMillis();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences sharedpreferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putLong(getString(R.string.apptime_saved), newerTime);
		// Commit the edits!
		editor.commit();

	}

	private boolean isTooOld() {
		boolean flag = false;
		long newerTime = System.currentTimeMillis();
		SharedPreferences sharedpreferences = getPreferences(Context.MODE_PRIVATE);
		long defaultTime = Long.parseLong(getResources().getString(R.string.apptime_default));
		long olderTime = sharedpreferences.getLong(getString(R.string.apptime_saved), defaultTime);
		Log.i(TAG, "new time is: " + newerTime + " olderTime is: " + olderTime);
		long diffInHours = ((newerTime - olderTime) / (1000 * 60 * 60));
		Log.i(TAG, "diff is: " + diffInHours);
		if (diffInHours >= 1) {
			Log.i(TAG, "bigger than 2 hours, reloading database with new data");
			flag = true;
		}
		return flag;
	}

	/**
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater inflater = getMenuInflater(); inflater.inflate(R.menu.main,
	 *           menu); return super.onCreateOptionsMenu(menu); }
	 */

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
