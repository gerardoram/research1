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
package it.pkg.oms.formula;

import com.hybris.dataonboarding.framework.exceptions.AbstractImportException.ErrorCode;


/**
 * Enum covering custom error codes for formulas.
 */
public enum FormulaErrorCode implements ErrorCode
{
	/** Problem on transforming formula line. */
	_001,

	/** Parameter \"name\" cannot be null or empty. */
	_002,

	/** Parameter \"atsId\" cannot be null or empty. */
	_003,

	/** Parameter \"formula\" cannot be null or empty. */
	_004,

	/** Invalid request for formula insert. */
	_101,

	/** Invalid request for formula update. */
	_301,

	/** Formula not found for formula update. */
	_302,

	/** Invalid request for formula deletion. */
	_401,

	/** Formula not found for formula deletion. */
	_402;

	private static final String PREFIX = "FOR";

	@Override
	public String getCode()
	{
		return PREFIX + this.name();
	}
}
