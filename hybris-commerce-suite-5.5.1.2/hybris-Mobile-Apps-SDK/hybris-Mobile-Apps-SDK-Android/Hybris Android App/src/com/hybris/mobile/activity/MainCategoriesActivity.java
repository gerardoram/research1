/*******************************************************************************
 * [y] hybris Platform
 *  
 *   Copyright (c) 2000-2013 hybris AG
 *   All rights reserved.
 *  
 *   This software is the confidential and proprietary information of hybris
 *   ("Confidential Information"). You shall not disclose such Confidential
 *   Information and shall use it only in accordance with the terms of the
 *   license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.activity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.hybris.mobile.ExternalConstants;
import com.hybris.mobile.Hybris;
import com.hybris.mobile.R;
import com.hybris.mobile.adapter.MainCategoriesAdapter;
import com.hybris.mobile.adapter.SortOptionsAdapter;
import com.hybris.mobile.controller.MainCategoriesListController;
import com.hybris.mobile.controller.ProductListController;
import com.hybris.mobile.data.CategoryManager;
import com.hybris.mobile.model.Category;


public class MainCategoriesActivity extends HybrisListActivity implements SearchView.OnQueryTextListener, Handler.Callback
{

	public static final String INTENT_EXTRA_CATEGORY_MAIN = "INTENT_EXTRA_CATEGORY_MAIN";
	private static final String INTENT_EXTRA_SEARCH = "INTENT_EXTRA_SEARCH";

	/**
	 * Controller for getting data form the web service
	 */
	private MainCategoriesListController controller;

	/**
	 * Local reference to the main list view adapter Got from the ObjectListController
	 */
	private MainCategoriesAdapter mAdapter;

	/**
	 * Current search string
	 */
	private String mCurrentQueryString = "";

	/**
	 * Search suggestions list view
	 */
	private ListView mSearchList;

	/**
	 * Search suggestions timer
	 */
	private long mStartTime = 0;


	/**
	 * Dialof for the splash screen
	 */
	protected Dialog mSplashDialog;

	/**
	 * Runnable for updating search suggestions Fetches suggestions from the server and previous searches from local
	 * storage and concatanates.
	 */
	private Runnable mRunSearchSuggestions = new Runnable()
	{
		@Override
		public void run()
		{
			if (System.currentTimeMillis() - mStartTime > ExternalConstants.SEARCH_SUGGESTION_DELAY)
			{

				controller.getSpellingSuggestions(MainCategoriesActivity.this, mCurrentQueryString);
			}
			else
			{
				mSearchHandler.postDelayed(this, 100);
			}
		}
	};


	/**
	 * Search suggestion hanfler
	 */
	private Handler mSearchHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().setHomeButtonEnabled(false);

		setContentView(R.layout.main);

		// HockeyApp integration for crash reports & updates
		checkForUpdates();

		controller = new MainCategoriesListController();
		controller.addOutboxHandler(new Handler(this));
		mAdapter = new MainCategoriesAdapter(this, controller.getModel());
		setListAdapter(mAdapter);

		// onClick listener
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long l)
			{

				Intent intent = new Intent(MainCategoriesActivity.this, ProductListActivity.class);
				Category selectedCategory = (Category) mAdapter.getItem(position);
				intent.putExtra(INTENT_EXTRA_CATEGORY_MAIN, selectedCategory);
				startActivity(intent);

			}

		});

		// Create the search suggestions list
		mSearchList = (ListView) findViewById(R.id.searchList);
		mSearchList.setAdapter(new SortOptionsAdapter(this, controller.getSearchSuggestions()));
		mSearchList.setVisibility(View.GONE);
		mSearchList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg)
			{
				String searchString = (String) mSearchList.getItemAtPosition(position);
				onQueryTextSubmit(searchString);
			}
		});

	}

	@Override
	public void onResume()
	{
		super.onResume();

		// HockeyApp integration for crash reports & updates
		checkForCrashes();

		controller.getCategories(CategoryManager.getRootCategory());

		mAdapter.notifyDataSetChanged();

	}


	/**
	 * Handle Action Menu interactions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean handled = super.onOptionsItemSelected(item);
		if (!handled)
		{
			switch (item.getItemId())
			{
				case R.id.search:
					mSearchList.setVisibility(View.VISIBLE);
					mSearchList.bringToFront();
					onSearchRequested();
					return true;
				default:
					return false;
			}
		}
		return handled;
	}

	/**
	 * Set up the action menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		// Set up the search box and table
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setQueryHint(getResources().getString(R.string.search_products_hint));

		// Workaround to set the color of the searchView text
		LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
		LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
		LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
		autoComplete.setTextColor(getResources().getColor(R.color.editTextDark));

		searchView.setOnQueryTextListener(this);
		searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					mSearchList.setVisibility(View.VISIBLE);
				}
				else
				{
					mSearchList.setVisibility(View.GONE);
				}
			}
		});

		menu.removeItem(R.id.refine);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String query)
	{
		mCurrentQueryString = query;

		if (query.length() == 0)
		{
			controller.getSearchSuggestions().clear();
		}

		// Restart the handler
		mSearchHandler.removeCallbacks(mRunSearchSuggestions);
		mStartTime = System.currentTimeMillis();
		mSearchHandler.postDelayed(mRunSearchSuggestions, 2000);

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String strQuery)
	{
		Hybris.addPreviousSearch(strQuery);
		Intent intent = new Intent(this, ProductListActivity.class);
		intent.putExtra(INTENT_EXTRA_SEARCH, strQuery);

		startActivity(intent);
		return false;
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		boolean returnValue = false;

		switch (msg.what)
		{
			case ProductListController.MESSAGE_MODEL_UPDATED:

				// Notify the list to be updated
				mAdapter.notifyDataSetChanged();

				returnValue = true;

				break;

			case ProductListController.MESSAGE_SEARCH_SUGGESTIONS:
				((BaseAdapter) mSearchList.getAdapter()).notifyDataSetChanged();

				returnValue = true;
				break;
		}

		return returnValue;
	}

	/**
	 * HockeyApp integration
	 */
	private void checkForCrashes()
	{
		CrashManager.register(this, getString(R.string.hockeyapp_key));
	}

	/**
	 * HockeyApp integration
	 */
	private void checkForUpdates()
	{
		// Remove this for store builds!
		UpdateManager.register(this, getString(R.string.hockeyapp_key));
	}

	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit_title)
				.setMessage(R.string.exit_text).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						startActivity(intent);
						finish();
					}

				}).setNegativeButton(android.R.string.no, null).show();
	}

}
