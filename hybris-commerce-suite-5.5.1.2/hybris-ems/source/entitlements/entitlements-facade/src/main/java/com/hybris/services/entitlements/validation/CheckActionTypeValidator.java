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
package com.hybris.services.entitlements.validation;

import com.hybris.services.entitlements.api.Actions;

/**
 * Allows only CHECK or NULL execution actions.
 */
public class CheckActionTypeValidator extends ActionTypeValidator
{
	@Override
	public void validate(final Object object, final ValidationViolations errors)
	{
		if (object != null)
		{
			if (!Actions.CHECK.equals(object.toString()))
			{
				errors.add("", object, "Unsupported action");
			}
		}
	}
}
