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
package com.hybris.mobile.api.geofence.geofencable;

import java.util.List;

import android.content.Context;

import com.hybris.mobile.api.geofence.data.GeofenceObject;


public interface Geofencable
{

	/**
	 * Method that returns all the geofences ids of the geofences currently saved on the application
	 * 
	 * @return
	 */
	public List<String> getSavedAllGeofenceIds();

	/**
	 * Method to save a geofence object on the application
	 * 
	 * @param geofenceObject
	 */
	public void saveGeofence(GeofenceObject geofenceObject);

	/**
	 * Method that returns all the geofences objects currently saved on the application
	 * 
	 * @return
	 */
	public List<GeofenceObject> getSavedAllGeofenceObjects();

	/**
	 * Method that removes a geofence saved on the application
	 * 
	 * @param geofenceId
	 */
	public void removeSavedGeofence(String geofenceId);

	/**
	 * Return a Geofence object previously saved on the application
	 * 
	 * @param geofenceId
	 * @return
	 */
	public GeofenceObject getGeofenceObject(String geofenceId);

	/**
	 * Return the application context
	 * 
	 * @return
	 */
	public Context getContext();

}
