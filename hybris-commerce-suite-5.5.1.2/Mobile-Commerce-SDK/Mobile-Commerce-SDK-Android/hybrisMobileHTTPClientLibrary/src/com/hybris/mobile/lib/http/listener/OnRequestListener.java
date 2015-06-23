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
package com.hybris.mobile.lib.http.listener;

/**
 * Interface that its methods (before/after) will be called when the request is sent
 */
public interface OnRequestListener
{
	/**
	 * Before the request is sent
	 */
	public void beforeRequest();

	/**
	 * After the request is sent
	 * 
	 * @param isDataSynced
	 *           Whether or not the request is synced with remote data
	 */
	public void afterRequest(boolean isDataSynced);

}
