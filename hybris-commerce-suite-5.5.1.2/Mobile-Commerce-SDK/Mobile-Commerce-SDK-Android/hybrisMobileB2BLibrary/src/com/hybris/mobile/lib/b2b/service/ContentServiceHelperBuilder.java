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

package com.hybris.mobile.lib.b2b.service;

import android.content.Context;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.utils.JsonUtils;
import com.hybris.mobile.lib.http.converter.JsonDataConverter;
import com.hybris.mobile.lib.http.manager.PersistenceManager;
import com.hybris.mobile.lib.http.manager.volley.VolleyPersistenceManager;

import java.lang.reflect.Type;


/**
 * Static class to instantiate a content service helper
 */
public final class ContentServiceHelperBuilder {
    private ContentServiceHelperBuilder() {
    }

    /**
     * Build a content service helper
     *
     * @param context Application-specific resources
     * @return Service used to get the application data
     */
    public static ContentServiceHelper build(Context context, Configuration configuration, boolean uiRelated) {
        PersistenceManager persistenceManager = new VolleyPersistenceManager(context);
        return new OCCServiceHelper(context, configuration, persistenceManager, new JsonDataConverter() {

            @Override
            public <T> Type getAssociatedTypeFromClass(Class<T> className) {
                return JsonUtils.getAssociatedTypeFromClass(className);
            }

            @Override
            public String createErrorMessage(String errorMessage, String errorType) {
                return JsonUtils.createErrorMessage(errorMessage, errorType);
            }

        }, uiRelated);

    }
}
