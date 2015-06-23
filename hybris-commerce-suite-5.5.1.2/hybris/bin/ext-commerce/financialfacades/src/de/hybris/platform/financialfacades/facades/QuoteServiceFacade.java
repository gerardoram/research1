/*
 *
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
 */
package de.hybris.platform.financialfacades.facades;

import de.hybris.platform.commercefacades.insurance.data.QuoteRequestData;
import de.hybris.platform.commercefacades.insurance.data.QuoteResponseData;


public interface QuoteServiceFacade
{
	/**
	 * Request to update a quote to a external system, and return to hybris the information about an unbind quote quote
	 *
	 * @param requestData
	 *           the quote request data
	 * @return quote response data
	 */
	QuoteResponseData requestQuoteUnbind(final QuoteRequestData requestData);

	/**
	 * Request to update a quote to an external system, and return to hybris the information about an unbind quote quote
	 *
	 * @param requestData
	 *           the quote request data
	 * @return quote response data
	 */
	QuoteResponseData requestQuoteBind(final QuoteRequestData requestData);
}
