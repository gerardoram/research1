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
package com.hybris.mobile.api.geofence.intent;

import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.hybris.mobile.api.geofence.BuildConfig;
import com.hybris.mobile.api.geofence.GeofenceConstants;
import com.hybris.mobile.api.geofence.R;


public class GeofenceIntentService extends IntentService
{

	public static final String NAME_SHARED_PREFERENCES = "HybrisGeofenceAPI";

	private static final String LOG_TAG = GeofenceIntentService.class.getSimpleName();

	public GeofenceIntentService()
	{
		super("GeofenceIntentService");
	}

	/**
	 * Handles incoming intents
	 * 
	 * @param intent
	 *           The Intent sent by Location Services. This Intent is provided to Location Services (inside a
	 *           PendingIntent) when you call addGeofences()
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{

		// Check for errors
		if (LocationClient.hasError(intent))
		{
			if (BuildConfig.DEBUG)
			{
				Log.e(LOG_TAG, "Error with LocationClient. Error Code: " + LocationClient.getErrorCode(intent));
			}
		}
		else
		{
			// Get the type of transition (entry or exit)
			int transition = LocationClient.getGeofenceTransition(intent);

			// Test that a valid transition was reported
			if (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
			{

				// Post a notification
				List<Geofence> listGeofences = LocationClient.getTriggeringGeofences(intent);

				for (Geofence geofence : listGeofences)
				{
					sendNotification(geofence, intent.getBundleExtra(GeofenceConstants.PREFIX_BUNDLE_INTENT + geofence.getRequestId()));
				}

			}
			else
			{
				// Other cases
			}
		}
	}

	/**
	 * Send a notification to the phone saying that the user is passing close to a geofence
	 * 
	 * @param geofence
	 */
	private void sendNotification(Geofence geofence, Bundle bundle)
	{
		try
		{

			// TODO - remove this for production
			automatedTestTemp(geofence.getRequestId());

			Class<?> intentClassDestination = Class.forName(bundle.getString(GeofenceConstants.INTENT_CLASS_DESTINATION));
			String notificationContentTitle = bundle.getString(GeofenceConstants.NOTIFICATION_TITLE);
			String notificationContentText = bundle.getString(GeofenceConstants.NOTIFICATION_TEXT);

			// Create an explicit content Intent that starts the Activity defined in intentClassDestination
			Intent notificationIntent = new Intent(this, intentClassDestination);

			// Geofence Id to pass to the activity in order to retrieve the object
			notificationIntent.putExtra(bundle.getString(GeofenceConstants.INTENT_OBJECT_KEY_NAME), geofence.getRequestId());

			PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, geofence.getRequestId().hashCode(),
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			// Get a notification builder that's compatible with platform versions >= 4
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

			// Set the notification contents
			builder.setSmallIcon(R.drawable.ic_notification_geofence).setContentTitle(notificationContentTitle)
					.setContentText(notificationContentText).setContentIntent(notificationPendingIntent);

			// Constructing the Notification and setting the flag to auto remove the notification when the user click on it
			Notification notification = builder.build();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_ALL;

			// Get an instance of the Notification manager
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			// Issue the notification
			mNotificationManager.notify(geofence.getRequestId().hashCode(), notification);

		}
		catch (ClassNotFoundException e)
		{
			if (BuildConfig.DEBUG)
			{
				Log.e(LOG_TAG,
						"Unable to find class " + bundle.getString(GeofenceConstants.INTENT_CLASS_DESTINATION) + "."
								+ e.getLocalizedMessage());
			}
		}
	}

	private void automatedTestTemp(String geofenceId)
	{

		SharedPreferences sharedPreferences = getSharedPreferences(NAME_SHARED_PREFERENCES, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		// Saving as a boolean to indicate that this id was geofenced
		editor.putBoolean(geofenceId, true);

		// Saving as an int that this id in the queue was geofenced
		int geofencedQueue = sharedPreferences.getInt("int-" + geofenceId, -1);
		editor.putBoolean(geofencedQueue + "", true);

		editor.commit();
	}
}
