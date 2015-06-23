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
package com.hybris.mobile.api.geofence.data;

import com.google.android.gms.location.Geofence;


public class GeofenceObject
{
	private String id;
	private double latitude;
	private double longitude;
	private float radius;
	private long expirationDuration;
	private int transitionType;
	private String associatedObjectFullClassName;
	private Object associatedObject;
	private String intentClassDestination;
	private String notificationTitle;
	private String notificationText;
	private String intentObjectKeyName;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public long getExpirationDuration()
	{
		return expirationDuration;
	}

	public void setExpirationDuration(long expirationDuration)
	{
		this.expirationDuration = expirationDuration;
	}

	public int getTransitionType()
	{
		return transitionType;
	}

	public void setTransitionType(int transitionType)
	{
		this.transitionType = transitionType;
	}

	public Object getAssociatedObject()
	{
		return associatedObject;
	}

	public void setAssociatedObject(Object associatedObject)
	{
		this.associatedObject = associatedObject;
	}

	public String getAssociatedObjectFullClassName()
	{
		return associatedObjectFullClassName;
	}

	public void setAssociatedObjectFullClassName(String associatedObjectFullClassName)
	{
		this.associatedObjectFullClassName = associatedObjectFullClassName;
	}

	public String getIntentClassDestination()
	{
		return intentClassDestination;
	}

	public void setIntentClassDestination(String intentClassDestination)
	{
		this.intentClassDestination = intentClassDestination;
	}

	public String getNotificationTitle()
	{
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle)
	{
		this.notificationTitle = notificationTitle;
	}

	public String getNotificationText()
	{
		return notificationText;
	}

	public void setNotificationText(String notificationText)
	{
		this.notificationText = notificationText;
	}

	public String getIntentObjectKeyName()
	{
		return intentObjectKeyName;
	}

	public void setIntentObjectKeyName(String intentObjectKeyName)
	{
		this.intentObjectKeyName = intentObjectKeyName;
	}

	public Geofence toGeofence()
	{
		return new Geofence.Builder().setRequestId(id).setTransitionTypes(transitionType)
				.setCircularRegion(latitude, longitude, radius).setExpirationDuration(expirationDuration).build();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associatedObject == null) ? 0 : associatedObject.hashCode());
		result = prime * result + ((associatedObjectFullClassName == null) ? 0 : associatedObjectFullClassName.hashCode());
		result = prime * result + (int) (expirationDuration ^ (expirationDuration >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((intentClassDestination == null) ? 0 : intentClassDestination.hashCode());
		result = prime * result + ((intentObjectKeyName == null) ? 0 : intentObjectKeyName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((notificationText == null) ? 0 : notificationText.hashCode());
		result = prime * result + ((notificationTitle == null) ? 0 : notificationTitle.hashCode());
		result = prime * result + Float.floatToIntBits(radius);
		result = prime * result + transitionType;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeofenceObject other = (GeofenceObject) obj;
		if (associatedObject == null)
		{
			if (other.associatedObject != null)
				return false;
		}
		else if (!associatedObject.equals(other.associatedObject))
			return false;
		if (associatedObjectFullClassName == null)
		{
			if (other.associatedObjectFullClassName != null)
				return false;
		}
		else if (!associatedObjectFullClassName.equals(other.associatedObjectFullClassName))
			return false;
		if (expirationDuration != other.expirationDuration)
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (intentClassDestination == null)
		{
			if (other.intentClassDestination != null)
				return false;
		}
		else if (!intentClassDestination.equals(other.intentClassDestination))
			return false;
		if (intentObjectKeyName == null)
		{
			if (other.intentObjectKeyName != null)
				return false;
		}
		else if (!intentObjectKeyName.equals(other.intentObjectKeyName))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (notificationText == null)
		{
			if (other.notificationText != null)
				return false;
		}
		else if (!notificationText.equals(other.notificationText))
			return false;
		if (notificationTitle == null)
		{
			if (other.notificationTitle != null)
				return false;
		}
		else if (!notificationTitle.equals(other.notificationTitle))
			return false;
		if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius))
			return false;
		if (transitionType != other.transitionType)
			return false;
		return true;
	}

}
