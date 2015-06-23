/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/

package com.hybris.mobile.lib.location.map.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hybris.mobile.lib.location.R;
import com.hybris.mobile.lib.location.map.adapter.MapItemListAdapter;
import com.hybris.mobile.lib.location.map.data.MapItem;
import com.hybris.mobile.lib.location.map.listener.MapListLoader;


/**
 * Fragment to display a list of map elements
 */
public class MapListFragment extends Fragment
{
	private ListView mItemList;
	private ArrayAdapter<MapItem> mItemListAdapter;
	private MapListLoader mMapListLoader;

	//	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			mMapListLoader = (MapListLoader) activity;
		}
		catch (ClassCastException e)
		{
			throw new IllegalStateException(activity.getClass().getCanonicalName()
					+ " must implement the MapListLoader interface. Details: " + e.getLocalizedMessage());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_item_list, container, false);

		// We init the list view here in order to be able to get it directly from the activity onCreate()
		mItemList = (ListView) view.findViewById(R.id.hybris_map_items_list);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// Configure the list
		initList();
	}

	/**
	 * Configure the list to display
	 */
	private void initList()
	{
		// Custom adapter provided
		if (mMapListLoader.getListAdapter() != null)
		{
			mItemListAdapter = mMapListLoader.getListAdapter();
		}
		else
		{
			mItemListAdapter = new MapItemListAdapter(getActivity(), new ArrayList<MapItem>());
		}

		mItemList.setAdapter(mItemListAdapter);

		// On click on the items
		mItemList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				mMapListLoader.onListItemClick(mItemListAdapter.getItem(position));
			}
		});
	}

	/**
	 * Update the data list
	 * 
	 * @param items List of Map Data
	 */
	public void updateData(List<MapItem> items)
	{
		mItemListAdapter.clear();
		mItemListAdapter.addAll(items);
		mItemListAdapter.notifyDataSetChanged();
	}

	/**
	 * Return the list view of the fragment
	 * 
	 * @return  ListView
	 */
	public ListView getListView()
	{
		return mItemList;
	}

}
