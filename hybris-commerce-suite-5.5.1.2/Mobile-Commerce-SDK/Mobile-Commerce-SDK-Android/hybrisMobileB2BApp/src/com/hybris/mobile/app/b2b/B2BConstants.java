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
package com.hybris.mobile.app.b2b;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


/**
 * Regroup all configuration for common logic constants
 */
public class B2BConstants
{
	public static final DecimalFormat PRICE_SMALL = new DecimalFormat("##.00");
	public static final DateFormat DATE_FORMAT_CATALOG_LAST_SYNC_DATE = new SimpleDateFormat("MM/dd/yy HH:MM:ss",
			SimpleDateFormat.getAvailableLocales()[0]);
	public static final String DATE_FORMAT_FROM_CONTENT_SERVICE_HELPER = "yyyy-MM-dd'T'HH:mm:ss-SSSS"; //External

	public static final String DATE_FORMAT_COMPLETE = "dd/MM/yyyy - HH:mm";

	public static final String SETTINGS_SYNC_CATEGORY_ID_LIST = "SETTINGS_SYNC_CATEGORY_ID_LIST";
	public static final String SETTINGS_SYNC_STATUS = "SETTINGS_SYNC_STATUS";
}
