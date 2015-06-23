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
package com.hybris.services.entitlements.facade;

import com.hybris.services.entitlements.domain.Grant;
import com.hybris.services.entitlements.sorting.GrantComparator;

import java.io.Serializable;

public class TestGrantComparator implements GrantComparator, Serializable
{
	@Override
	public int compare(final Grant o1, final Grant o2)
	{
		throw new IndexOutOfBoundsException("TestGrantComparator is called");
	}
}
