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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hybris.mobile.api.geofence.data.GeofenceObject;
import com.hybris.mobile.api.geofence.intent.GeofenceIntentService;


public class GeofenceJsonSharedPreferences implements Geofencable
{

	private Context context;
	private String prefixName;
	private Gson gson;

	public static GeofenceJsonSharedPreferences createGeofencable(Context context, String prefixName)
	{
		return new GeofenceJsonSharedPreferences(context, prefixName);
	}

	private GeofenceJsonSharedPreferences(Context context, String prefixName)
	{
		this.context = context;
		this.prefixName = prefixName;

		// Json Adapter - custom adapter because of the unknown class of the property GeofenceObject.associatedObject
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GeofenceObject.class, new GeofenceObjectJsonAdapter());
		gson = gsonBuilder.create();
	}

	/**
	 * Return the shared preferences of the context
	 * 
	 * @return
	 */
	private SharedPreferences getSharedPreferences()
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Set a pair/value on the shared preferences of the context
	 * 
	 * @param key
	 * @param value
	 */
	private void setSharedPreferenceString(String key, String value)
	{
		if (context != null)
		{
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	@Override
	public void saveGeofence(GeofenceObject geofenceObject)
	{
		// TODO - remove this for production
		saveForAutomatedTestTemp(geofenceObject.getId());

		// Saving the object in JSON format
		setSharedPreferenceString(prefixName + geofenceObject.getId(), gson.toJson(geofenceObject));
	}

	private void saveForAutomatedTestTemp(String idGeofence)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(GeofenceIntentService.NAME_SHARED_PREFERENCES, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		// Saving as a boolean to indicate that this id is not geofenced yet
		editor.putBoolean(idGeofence, false);

		// Place of this geofence in the queue
		editor.putInt("int-" + idGeofence, getSavedAllGeofenceIds().size() + 1);

		editor.commit();
	}

	@Override
	public List<String> getSavedAllGeofenceIds()
	{

		List<String> listIds = new ArrayList<String>();

		Iterator<String> iteratorSharedPreferences = getSharedPreferences().getAll().keySet().iterator();

		// We iterate through all the shared preferences
		while (iteratorSharedPreferences.hasNext())
		{
			String currentKey = iteratorSharedPreferences.next();

			// If this a geofence object
			if (StringUtils.startsWith(currentKey, prefixName))
			{
				listIds.add(StringUtils.substringAfter(currentKey, prefixName));
			}

		}

		return listIds;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GeofenceObject> getSavedAllGeofenceObjects()
	{
		List<GeofenceObject> listGeofenceObject = new ArrayList<GeofenceObject>();

		Iterator<?> iteratorSharedPreferences = getSharedPreferences().getAll().entrySet().iterator();

		// We iterate through all the shared preferences
		while (iteratorSharedPreferences.hasNext())
		{
			Entry<String, ?> currentEntry = (Entry<String, ?>) iteratorSharedPreferences.next();

			// If this a geofence object
			if (org.apache.commons.lang3.StringUtils.startsWith(currentEntry.getKey(), prefixName))
			{
				listGeofenceObject.add(gson.fromJson(currentEntry.getValue().toString(), GeofenceObject.class));
			}

		}

		return listGeofenceObject;
	}

	@Override
	public void removeSavedGeofence(String geofenceId)
	{
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.remove(prefixName + geofenceId);
		editor.commit();
	}

	@Override
	public GeofenceObject getGeofenceObject(String geofenceId)
	{

		Object geofenceObject = getSharedPreferences().getAll().get(prefixName + geofenceId);

		if (geofenceObject != null)
		{
			return gson.fromJson(geofenceObject.toString(), GeofenceObject.class);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Context getContext()
	{
		return context;
	}

}
