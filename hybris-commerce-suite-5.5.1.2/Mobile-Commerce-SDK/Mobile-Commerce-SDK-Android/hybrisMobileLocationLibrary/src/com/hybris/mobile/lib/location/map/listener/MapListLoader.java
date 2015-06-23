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

package com.hybris.mobile.lib.location.map.listener;

import android.widget.ArrayAdapter;

import com.hybris.mobile.lib.location.map.data.MapItem;


/**
 * Implement this interface when using a MapListFragment
 */
public interface MapListLoader
{
	/**
	 * List adapter for the item list
	 * 
	 * @return Array of MapItem
	 */
	public ArrayAdapter<MapItem> getListAdapter();

	/**
	 * Action on item list click
	 * 
	 * @param item MapItem
	 */
	public void onListItemClick(MapItem item);

}
