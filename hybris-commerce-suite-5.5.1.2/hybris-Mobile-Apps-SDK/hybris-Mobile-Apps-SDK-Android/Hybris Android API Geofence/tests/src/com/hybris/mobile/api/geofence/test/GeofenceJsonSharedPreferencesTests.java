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
package com.hybris.mobile.api.geofence.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.test.AndroidTestCase;

import com.google.android.gms.location.Geofence;
import com.hybris.mobile.api.geofence.data.GeofenceObject;
import com.hybris.mobile.api.geofence.geofencable.Geofencable;
import com.hybris.mobile.api.geofence.geofencable.GeofenceJsonSharedPreferences;



public class GeofenceJsonSharedPreferencesTests extends AndroidTestCase
{

	private List<GeofenceObject> listGeofenceObjects;
	private static final int NB_GEOFENCES = 10;
	private Geofencable geofencable;
	private final static String GEOFENCE_ID = "myId";

	@Override
	protected void setUp() throws Exception
	{

		super.setUp();

		listGeofenceObjects = new ArrayList<GeofenceObject>();

		for (int i = 0; i < NB_GEOFENCES; i++)
		{

			String myObject = "I've been geofenced! " + i;

			GeofenceObject geofence = new GeofenceObject();
			geofence.setId(GEOFENCE_ID + i);
			geofence.setExpirationDuration(Geofence.NEVER_EXPIRE);
			geofence.setLatitude(1000);
			geofence.setLongitude(1000);
			geofence.setRadius(1000);
			geofence.setTransitionType(Geofence.GEOFENCE_TRANSITION_ENTER);
			geofence.setAssociatedObjectFullClassName("java.lang.String");
			geofence.setAssociatedObject(myObject);
			geofence.setIntentClassDestination("com.hybris.mobile.demo.geofence.GeofenceDestinationActivity");
			geofence.setIntentObjectKeyName("GEOFENCE");
			geofence.setNotificationText("Geofence text blabla");
			geofence.setNotificationTitle("Geofence title bis");

			listGeofenceObjects.add(geofence);
		}


		geofencable = GeofenceJsonSharedPreferences.createGeofencable(getContext(), "geofence_");

	}

	public void testSaveGeofence()
	{
		geofencable.saveGeofence(listGeofenceObjects.get(0));
		assertNotNull(geofencable.getGeofenceObject(GEOFENCE_ID + 0));
	}

	public void testGetGeofenceObject()
	{
		geofencable.saveGeofence(listGeofenceObjects.get(0));
		assertEquals(geofencable.getGeofenceObject(GEOFENCE_ID + 0), listGeofenceObjects.get(0));
	}

	public void testGetSavedAllGeofenceIds()
	{
		for (int i = 0; i < NB_GEOFENCES; i++)
		{
			geofencable.saveGeofence(listGeofenceObjects.get(i));
		}

		List<String> listIds = geofencable.getSavedAllGeofenceIds();
		List<String> listIdsGeofence = new ArrayList<String>();

		for (GeofenceObject geofenceObject : listGeofenceObjects)
		{
			listIdsGeofence.add(geofenceObject.getId());
		}

		Collections.sort(listIds);
		Collections.sort(listIdsGeofence);

		assertEquals(listIds, listIdsGeofence);

	}

	public void testGetSavedAllGeofenceObjects()
	{
		for (int i = 0; i < NB_GEOFENCES; i++)
		{
			geofencable.saveGeofence(listGeofenceObjects.get(i));
		}

		List<GeofenceObject> listGeofenceObjects = geofencable.getSavedAllGeofenceObjects();

		assertEquals(listGeofenceObjects.size(), this.listGeofenceObjects.size());

		for (GeofenceObject geofenceObject : listGeofenceObjects)
		{
			assertEquals(this.listGeofenceObjects.contains(geofenceObject), true);
		}


	}

	@Override
	protected void tearDown() throws Exception
	{
		for (int i = 0; i < NB_GEOFENCES; i++)
		{
			geofencable.removeSavedGeofence(GEOFENCE_ID + 0);
		}

		super.tearDown();
	}

}
