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
package com.hybris.mobile.api.geofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.hybris.mobile.api.geofence.data.GeofenceObject;
import com.hybris.mobile.api.geofence.geofencable.Geofencable;
import com.hybris.mobile.api.geofence.intent.GeofenceIntentService;


public class GeofenceUtils implements ConnectionCallbacks, OnConnectionFailedListener, OnAddGeofencesResultListener,
		OnRemoveGeofencesResultListener
{

	private LocationClient mLocationClient;
	private Geofencable mGeofencable;
	private ArrayList<GeofenceObject> mListGeofenceObjects;
	private Handler mHandler;
	private int mMsgHandlerSuccess;
	private int mMsgHandlerError;
	private REQUEST_TYPE mRequestType;

	private enum REQUEST_TYPE
	{
		ADD, REMOVE, DISABLE, ENABLE
	};

	/**
	 * Private constructor, you must call the static methods to instantiate GeofenceUtil
	 * 
	 * @param context
	 * @param handler
	 * @param msgHandlerSuccess
	 * @param msgHandlerError
	 * @param requestType
	 */
	private GeofenceUtils(Geofencable context, Handler handler, int msgHandlerSuccess, int msgHandlerError,
			REQUEST_TYPE requestType)
	{
		super();
		mGeofencable = context;
		mListGeofenceObjects = new ArrayList<GeofenceObject>();
		mLocationClient = new LocationClient(mGeofencable.getContext(), this, this);
		mHandler = handler;
		mRequestType = requestType;
	}

	/**
	 * Send a particular geofence object to the Location Services, and send the success/errors codes within the handler
	 * 
	 * @param geofence
	 * @param context
	 * @param handler
	 * @param msgHandlerSuccess
	 * @param msgHandlerError
	 */
	public static void monitorGeofence(GeofenceObject geofenceObject, Geofencable context, Handler handler, int msgHandlerSuccess,
			int msgHandlerError)
	{
		List<GeofenceObject> listGeofenceObjects = new ArrayList<GeofenceObject>();
		listGeofenceObjects.add(geofenceObject);
		monitorGeofences(listGeofenceObjects, context, handler, msgHandlerSuccess, msgHandlerError);
	}

	/**
	 * Send a list of geofences objects to the Location Services, and send the success/errors codes within the handler
	 * 
	 * @param listGeofences
	 * @param context
	 * @param handler
	 * @param msgHandlerSuccess
	 * @param msgHandlerError
	 */
	public static void monitorGeofences(List<GeofenceObject> listGeofenceObjects, Geofencable context, Handler handler,
			int msgHandlerSuccess, int msgHandlerError)
	{
		GeofenceUtils geofenceUtil = new GeofenceUtils(context, handler, msgHandlerSuccess, msgHandlerError, REQUEST_TYPE.ADD);
		geofenceUtil.addGeofences(listGeofenceObjects);
		geofenceUtil.connect();
	}

	/**
	 * Disable the monitoring of all the geofences (but does not remove them from the application)
	 */
	public static void disableGeofencesMonitoring(Geofencable context, Handler handler, int msgHandlerSuccess, int msgHandlerError)
	{
		GeofenceUtils geofenceUtil = new GeofenceUtils(context, handler, msgHandlerSuccess, msgHandlerError, REQUEST_TYPE.DISABLE);
		geofenceUtil.connect();
	}

	/**
	 * Enable the monitoring of all the geofences previously saved by the user
	 */
	public static void enableGeofencesMonitoring(Geofencable context, Handler handler, int msgHandlerSuccess, int msgHandlerError)
	{
		GeofenceUtils geofenceUtil = new GeofenceUtils(context, handler, msgHandlerSuccess, msgHandlerError, REQUEST_TYPE.ENABLE);
		geofenceUtil.addGeofences(context.getSavedAllGeofenceObjects());
		geofenceUtil.connect();
	}

	/**
	 * Cancel the monitoring of a particular geofence from the application
	 * 
	 * @param geofenceId
	 */
	public static void removeGeofenceMonitoring(Geofencable context, Handler handler, int msgHandlerSuccess, int msgHandlerError,
			String geofenceId)
	{
		List<String> listGeofencesIds = new ArrayList<String>();
		listGeofencesIds.add(geofenceId);
		removeGeofencesMonitoring(context, handler, msgHandlerSuccess, msgHandlerError, listGeofencesIds);
	}

	/**
	 * Cancel the monitoring of a list of geofences from the application
	 * 
	 * @param context
	 * @param handler
	 * @param msgHandlerSuccess
	 * @param msgHandlerError
	 * @param geofenceIds
	 */
	public static void removeGeofencesMonitoring(Geofencable context, Handler handler, int msgHandlerSuccess, int msgHandlerError,
			List<String> geofenceIds)
	{
		GeofenceUtils geofenceUtil = new GeofenceUtils(context, handler, msgHandlerSuccess, msgHandlerError, REQUEST_TYPE.REMOVE);

		// Constructing a geofence object with basic information
		List<GeofenceObject> listGeofenceObjects = new ArrayList<GeofenceObject>();

		for (String geofenceId : geofenceIds)
		{
			GeofenceObject geofenceObject = new GeofenceObject();
			geofenceObject.setId(geofenceId);
			listGeofenceObjects.add(geofenceObject);
		}

		geofenceUtil.addGeofences(listGeofenceObjects);

		geofenceUtil.connect();
	}

	/**
	 * Add a list of geofences objects to monitor
	 * 
	 * @param geofences
	 */
	private void addGeofences(List<GeofenceObject> geofences)
	{
		mListGeofenceObjects = (ArrayList<GeofenceObject>) geofences;
	}

	/**
	 * Connect the location client to begin the job
	 */
	private void connect()
	{
		// Request a connection to Location Services
		mLocationClient.connect();
	}

	/**
	 * Method automatically called after adding a list of geofence to monitor
	 */
	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds)
	{

		Message msg = new Message();

		// If adding the geofences was successful
		if (LocationStatusCodes.SUCCESS == statusCode)
		{
			msg.what = mMsgHandlerSuccess;
		}
		else
		{
			msg.what = mMsgHandlerError;
		}

		// We send the result within the handler
		msg.obj = Arrays.asList(geofenceRequestIds);
		mHandler.sendMessage(msg);

		// We disconnect the client
		mLocationClient.disconnect();
	}

	/**
	 * Method automatically if an error happens
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		// We send the error through the handler
		mHandler.sendEmptyMessage(mMsgHandlerError);
	}

	@Override
	public void onConnected(Bundle arg0)
	{

		/*
		 * 4 request type: - ADD: adding a list of geofence to monitor - ENABLE: adding a list of previously saved
		 * geofence to monitor - REMOVE: removing a list of geofences previously saved on the application - DISABLE:
		 * disable the monitoring of a list of geofences previously saved on the application (but doesn't remove them)
		 */
		switch (mRequestType)
		{

			case ADD:
			case ENABLE:
				// Constructing the Geofence list to monitor and saving them for future processing
				List<Geofence> listGeofences = new ArrayList<Geofence>();

				for (GeofenceObject geofenceObject : mListGeofenceObjects)
				{
					// Geofence object added to the geofence list for the pending intent
					listGeofences.add(geofenceObject.toGeofence());

					// Saving the geofence into the app
					if (mRequestType.equals(REQUEST_TYPE.ADD))
					{
						mGeofencable.saveGeofence(geofenceObject);
					}

				}

				addGeofencesToService();
				break;

			case REMOVE:

				// Constructing the ID list from the geofence list
				List<String> listIds = new ArrayList<String>();

				for (GeofenceObject geofenceObject : mListGeofenceObjects)
				{
					listIds.add(geofenceObject.getId());

					// Remove the saved geofences from the application
					mGeofencable.removeSavedGeofence(geofenceObject.getId());
				}

				if (!listIds.isEmpty())
				{
					mLocationClient.removeGeofences(listIds, this);
				}

				break;

			case DISABLE:
				List<String> listGeofencesIds = mGeofencable.getSavedAllGeofenceIds();

				if (!listGeofencesIds.isEmpty())
				{
					mLocationClient.removeGeofences(listGeofencesIds, this);
				}

				break;

			default:
				break;

		}


	}

	/**
	 * Add the geofences to the service for monitoring
	 * 
	 * @param listGeofences
	 */
	private void addGeofencesToService()
	{

		if (!mListGeofenceObjects.isEmpty())
		{

			// Creating an Intent to handle the result of the user interaction with one of the geofence
			Intent intent = new Intent(mGeofencable.getContext(), GeofenceIntentService.class);

			// Creating the list of geofence
			List<Geofence> listGeofences = new ArrayList<Geofence>();

			for (GeofenceObject geofenceObject : mListGeofenceObjects)
			{
				// Geofence object added to the geofence list for the pending intent
				listGeofences.add(geofenceObject.toGeofence());

				// Creating the extras for the intent
				Bundle bundle = new Bundle();
				bundle.putString(GeofenceConstants.INTENT_CLASS_DESTINATION, geofenceObject.getIntentClassDestination());
				bundle.putString(GeofenceConstants.NOTIFICATION_TITLE, geofenceObject.getNotificationTitle());
				bundle.putString(GeofenceConstants.NOTIFICATION_TEXT, geofenceObject.getNotificationText());
				bundle.putString(GeofenceConstants.INTENT_OBJECT_KEY_NAME, geofenceObject.getIntentObjectKeyName());

				intent.putExtra(GeofenceConstants.PREFIX_BUNDLE_INTENT + geofenceObject.getId(), bundle);
			}

			// Generating a unique pending intent
			PendingIntent geofencePendingIntent = PendingIntent.getService(mGeofencable.getContext(), UUID.randomUUID().hashCode(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			// Send a request to add the current geofences
			mLocationClient.addGeofences(listGeofences, geofencePendingIntent, this);
		}

	}

	@Override
	public void onDisconnected()
	{
		mLocationClient = null;
	}

	/**
	 * Method automatically after removing a list of geofences by their PendingIntent
	 */
	@Override
	public void onRemoveGeofencesByPendingIntentResult(int arg0, PendingIntent arg1)
	{
		mLocationClient.disconnect();
	}

	/**
	 * Method automatically after removing a list of geofences by their ids
	 */
	@Override
	public void onRemoveGeofencesByRequestIdsResult(int arg0, String[] arg1)
	{
		mLocationClient.disconnect();
	}

}
