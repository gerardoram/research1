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

package com.hybris.services.entitlements.api;

import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.constraints.NotNull;

/**
 * DTO to pass property.
 */
@XmlRootElement
public class PropertyData
{
	/**
	 * If true, {@link #value} will be added to current value of property.
	 * Obviously, <i>value</i> must be a number in that case. If false,
	 * <i>value</i> can be any string, and will override the current value.
	 */
	private boolean relative;

	/**
	 * New value.
	 */
	@NotNull
	private String value;

	public boolean isRelative()
	{
		return relative;
	}

	public void setRelative(final boolean relative)
	{
		this.relative = relative;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}
}
