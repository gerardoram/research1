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

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.hybris.mobile.api.geofence.data.GeofenceObject;


public final class GeofenceObjectJsonAdapter implements JsonDeserializer<GeofenceObject>
{

	private static Gson gson = new Gson();

	@Override
	public GeofenceObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{

		GeofenceObject geofenceObject = gson.fromJson(json.getAsJsonObject(), GeofenceObject.class);

		try
		{

			// Matching an associated object
			if (geofenceObject.getAssociatedObject() != null)
			{
				// We set its associated object
				geofenceObject.setAssociatedObject(gson.fromJson(gson.toJson(geofenceObject.getAssociatedObject()),
						Class.forName(geofenceObject.getAssociatedObjectFullClassName())));
			}

		}
		catch (ClassNotFoundException e)
		{
		}


		return geofenceObject;

	}
}
