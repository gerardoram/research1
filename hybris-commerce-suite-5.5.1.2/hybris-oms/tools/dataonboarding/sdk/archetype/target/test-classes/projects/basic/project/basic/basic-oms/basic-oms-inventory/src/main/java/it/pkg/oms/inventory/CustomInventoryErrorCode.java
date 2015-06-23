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
 *
 *  
 */
package it.pkg.oms.inventory;

import com.hybris.dataonboarding.framework.exceptions.AbstractImportException.ErrorCode;


/**
 * Represents a custom error code specific to OMS Inventory functionality.
 */
public enum CustomInventoryErrorCode implements ErrorCode
{

	/** My custom error code. */
	_777;

	@Override
	public String getCode()
	{
		return "INV" + this.name();
	}
}
