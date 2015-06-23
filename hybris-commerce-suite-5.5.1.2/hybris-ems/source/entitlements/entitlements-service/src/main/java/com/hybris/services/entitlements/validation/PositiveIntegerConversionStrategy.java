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

import org.springframework.util.Assert;

/**
 * Numeric validator.
 */
public class PositiveIntegerConversionStrategy extends AbstractConversionStrategy<Integer>
{
    @Override
    public void validate(final String field, final String value, final ValidationViolations errors)
    {
        try
        {
            final int quantity = Integer.parseInt(value);
            if (quantity < 0)
            {
                errors.add(field, value, "Negative values are not allowed");
            }
        }
        catch (final NumberFormatException e)
        {
            errors.add(field, value, "Is not a number");
        }
    }

	@Override
	public Integer convert(final Object value)
	{
		Assert.notNull(value);
		try
		{
			final int i =  Integer.parseInt((String) value);
			Assert.isTrue(i >= 0, "Only positive is allowed");
			return i;
		}
		catch (final NumberFormatException e)
		{
			Assert.isTrue(false, "Value must be positive");
		}
		catch (final ClassCastException e)
		{
			Assert.isTrue(false, "Value is not a string");
		}
		return 0;
	}
}
