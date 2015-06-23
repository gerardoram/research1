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
package com.hybris.mobile.lib.http.manager;

import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.DataResponseCallBack;

import java.util.Map;


/**
 * Interface to make the HTTP calls
 */
public interface PersistenceManager {

    /**
     * Make an asynchronous call and call ResponseReceiver.onReceiveResponse after result
     *
     * @param dataResponseCallBack Response callback result
     * @param requestId            Identifier for the call
     * @param method               Http Method
     * @param url                  Http Address
     * @param parameters           Map (name, value) of parameters to pass with the request
     * @param headers              Map (name, value) of headers to pass with the request
     * @param shouldCache          Indicator whether to get the results from the cache (if available)
     */
    public void execute(final DataResponseCallBack dataResponseCallBack, final String requestId, String method, String url,
                        final Map<String, String> parameters, final Map<String, String> headers, boolean shouldCache);

    /**
     * Get and set the image from the url to the imageView
     *
     * @param url                           Http Address for Image
     * @param requestId                     Identifier for the call
     * @param imageView                     ImageView to be updated
     * @param width                         Horizontal size in pixels (or 0 for automatic)
     * @param height                        Vertical size in pixels (or 0 for automatic)
     * @param config                        Bitmap configurations to apply on the image
     * @param shouldCache                   Indicator to use cache or not
     * @param onRequestListener             if no error in the process of executing this method. Note that this does not mean whether or not the
     *                                      request was a success.
     * @param forceImageTagToMatchRequestId if set to true, the imageView will set its tag with the requestId value and will verify after getting
     *                                      the image content from the url, that the tag is still equals to the requestId. If yes, the imageView is
     *                                      updated with the content just pulled.
     */
    public void setImageFromUrl(String url, String requestId, final ImageView imageView, int width, int height, Config config,
                                boolean shouldCache, OnRequestListener onRequestListener, boolean forceImageTagToMatchRequestId);

    /**
     * Get a cached object according to the key parameter
     *
     * @param key Unique code to get cached object
     * @return Cached object according to the key parameter
     */
    public String getCache(String key);

    /**
     * Remove cache for a specific key
     *
     * @param key Unique code to removed cached object
     */
    public void removeCache(String key);

    /**
     * Remove all items in the cache
     */
    public void removeAllCache();

    /**
     * Cancel the requests associated with the id
     *
     * @param requestId Unique Request Identifier
     */
    public void cancel(String requestId);

    /**
     * Cancel all the requests
     */
    public void cancelAll();

    /**
     * Pause any current request
     */
    public void pause();

    /**
     * Start or restart any pending request
     */
    public void start();

    /**
     * Update the cache
     */
    public void updateCache();


}
