/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.services.entitlements.conversion;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts properties of a property-aware object to a string map.
 */
public final class PropertiesToMapConverter
{
	private PropertiesToMapConverter()
	{}

	/**
	 * Convert a map to a string map.
	 *
	 * @param data input map
	 * @param <T> class of map values
	 * @return string map
	 */
	public static <T> Map<String,String> convert(final Map<String,T> data)
	{
		final Map<String,String> result = new HashMap<>();
		if (data != null)
		{
			for (final Map.Entry<String,T> entry : data.entrySet())
			{
				if (entry.getValue() != null && (entry.getValue() instanceof String))
				{
					result.put(entry.getKey(), (String) entry.getValue());
				}
			}
		}
		return result;
	}
}
