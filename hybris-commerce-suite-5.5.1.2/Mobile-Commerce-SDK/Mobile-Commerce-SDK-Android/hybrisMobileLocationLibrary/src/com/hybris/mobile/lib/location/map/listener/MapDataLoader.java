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

import android.location.Location;
import android.view.View;

import com.hybris.mobile.lib.location.map.MapConfiguration;
import com.hybris.mobile.lib.location.map.data.MapItem;


/**
 * Implement this interface when using a MapFragment
 */
public interface MapDataLoader {
    /**
     * Return the map configuration
     *
     * @return the map configuration
     */
    public MapConfiguration getMapConfiguration();

    /**
     * Action on map item click
     *
     * @param item clicked MapItem
     */
    public void onMapItemClick(MapItem item);

    /**
     * View for the map item content
     *
     * @param item  MapItem to be viewed
     * @return  View
     */
    public View getMapItemViewContent(MapItem item);

    /**
     * View for the map item window
     *
     * @param item  MapItem to be viewed
     * @return  View
     */
    public View getMapItemViewWindow(MapItem item);

    /**
     * Called when the map finish to load
     */
    public void onMapLoaded();

    /**
     * Called when the map service is ready
     *
     * @param deviceLocation Reported coordinates of the current connected device
     */
    public void onConnected(Location deviceLocation);

}
