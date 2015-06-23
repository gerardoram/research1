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
package de.hybris.platform.financialfacades.populators;

import de.hybris.platform.commercefacades.quotation.InsuranceQuoteData;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public abstract class InsuranceDataPopulatorStrategy
{
	private String dateFormatForDisplay;

	/**
	 * Used to extract data from the <infoMap> object which is a <Map> of strings and objects and these are used to
	 * derive the appropriate values for the <InsuranceQuoteData> object supplied.
	 *
	 * @param quoteData
	 *           <InsuranceQuoteData> object
	 * @param infoMap
	 *           <Map> object
	 */
	public abstract void processInsuranceQuoteData(final InsuranceQuoteData quoteData, final Map<String, Object> infoMap);

	public String getDateFormatForDisplay()
	{
		return dateFormatForDisplay;
	}

	@Required
	public void setDateFormatForDisplay(final String dateFormatForDisplay)
	{
		this.dateFormatForDisplay = dateFormatForDisplay;
	}
}
