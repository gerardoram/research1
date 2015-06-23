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
package com.hybris.mobile.lib.b2b.helper;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.R;


/**
 * Helper for url operations
 */
public class UrlHelper
{

	/**
	 * Return the webservice Http Address that take into account the catalog + the method path to call
	 *
	 * @param context Application-specific resources
	 * @param configuration URL settings
	 * @param pathUrlStringResource Webservice path
	 * @param formatArgs Values to replace on the final returned String   @return Formatted String Url for WebService
	 */
	public static String getWebserviceCatalogUrl(Context context, Configuration configuration, int pathUrlStringResource,
			Object... formatArgs)
	{
		if (configuration == null || StringUtils.isBlank(configuration.getBackendUrl()))
		{
			throw new IllegalArgumentException();
		}

		return buildWebserviceUrl(context, configuration, true, pathUrlStringResource, formatArgs);
	}

	/**
	 * Return the webservice Http Address + the method path to call
	 *
	 * @param context Application-specific resources
	 * @param configuration URL settings
	 * @param pathUrlStringResource Webservice path
	 * @param formatArgs Values to replace on the final returned String
	 * @return Formatted String Url for WebService
	 */
	public static String getWebserviceUrl(Context context, Configuration configuration, int pathUrlStringResource,
			Object... formatArgs)
	{
		if (configuration == null || StringUtils.isBlank(configuration.getBackendUrl()))
		{
			throw new IllegalArgumentException();
		}

		return buildWebserviceUrl(context, configuration, false, pathUrlStringResource, formatArgs);
	}

	/**
	 * Return the webservice Http Address for token calls
	 *
	 * @param context Application-specific resources
	 * @return Formatted String Url for WebService
	 */
	public static String getWebserviceTokenUrl(Context context, Configuration configuration)
	{
		if (configuration == null || StringUtils.isBlank(configuration.getBackendUrl()))
		{
			throw new IllegalArgumentException();
		}

		return configuration.getBackendUrl() + context.getString(R.string.path_token);
	}

	/**
	 * Build the webservice Http Address
	 *
	 * @param context Application-specific resources
	 * @param configuration URL settings
	 * @param hasCatalog Whether or not to add the catalog path
	 * @param pathUrlStringResource Webservice path
	 * @param formatArgs Values to replace on the final returned String
	 * @return Formatted String Url for WebService
	 */
	private static String buildWebserviceUrl(Context context, Configuration configuration, boolean hasCatalog,
			int pathUrlStringResource, Object... formatArgs)
	{
		if (configuration == null || StringUtils.isBlank(configuration.getBackendUrl()))
		{
			throw new IllegalArgumentException();
		}

		String url = configuration.getBackendUrl() + context.getString(R.string.path_webservice);

		if (hasCatalog)
		{
			if (StringUtils.isBlank(configuration.getCatalogPathUrl()))
			{
				throw new IllegalArgumentException();
			}

			url += configuration.getCatalogPathUrl();
		}

		if (formatArgs != null && formatArgs.length > 0)
		{
			return url + context.getString(pathUrlStringResource, formatArgs);
		}
		else
		{
			return url + context.getString(pathUrlStringResource);
		}
	}

	/**
	 * Return the image Http Address
	 *
	 * @param context Application-specific resources
	 * @param configuration URL settings
	 * @param pathUrl Url path of the image  @return Formatted String Url for Image
	 */
	public static String getImageUrl(Context context, Configuration configuration, String pathUrl)
	{
		if (configuration == null || StringUtils.isBlank(configuration.getBackendUrl()))
		{
			throw new IllegalArgumentException();
		}

		return configuration.getBackendUrl() + pathUrl;
	}
}
