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
package de.hybris.platform.financialfacades.strategies.impl;

import de.hybris.platform.commercefacades.quotation.QuotationItemRequestData;
import de.hybris.platform.commercefacades.quotation.QuotationItemResponseData;
import de.hybris.platform.commercefacades.quotation.QuotationRequestData;
import de.hybris.platform.financialfacades.constants.FinancialfacadesConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class TravelInsuranceQuotationPricingStrategy extends AbstractQuotationPricingStrategy
{
	private static final Logger LOG = Logger.getLogger(TravelInsuranceQuotationPricingStrategy.class);

	protected static final int BASE_FACTOR = 5;

	private static final Map<String, Double> formulaFactors = new HashMap<String, Double>();

	protected static final String TRA_WINTER = "TRA_WINTER";
	protected static final String TRA_GOLF = "TRA_GOLF";
	protected static final String TRA_BUSINESS = "TRA_BUSINESS";
	protected static final String TRA_VALUABLES = "TRA_VALUABLES";
	protected static final String TRA_HAZARDOUS = "TRA_HAZARDOUS";
	protected static final String TRA_EXCESS = "TRA_EXCESS";


	//Static information used to calculate the paynow price based on the request data.
	static
	{
		//Destination Factors
		formulaFactors.put("Europe", 0.1);
		formulaFactors.put("Australia and New Zealand", 0.2);
		formulaFactors.put("Worldwide (excluding USA, Canada, and the Caribbean)", 0.2);
		formulaFactors.put("Worldwide (including USA, Canada and the Caribbean)", 0.3);
		formulaFactors.put("UK", 0.2);

		//Number of days Factors
		formulaFactors.put("0-31", 0.1);
		formulaFactors.put("31-100", 0.2);
		formulaFactors.put("100-365", 0.3);

		//Age Factors
		formulaFactors.put("0-17", 0.1);
		formulaFactors.put("18-65", 0.2);
		formulaFactors.put("65+", 0.5);

		//Number of travel Factors
		formulaFactors.put("Travellers", 10.0);

		//Coverage Factors
		formulaFactors.put("cancellation", 1000.0);
		formulaFactors.put("wintersports", 20.0);
		formulaFactors.put("businesscover", 20.0);
		formulaFactors.put("valuablesextensioncover", 20.0);
		formulaFactors.put("hazardousactivitiescover", 20.0);
		formulaFactors.put("golfcover", 20.0);
		formulaFactors.put("excesswaivercoverage", 20.0);

		// Optional product factors
		formulaFactors.put(TRA_WINTER, .1);
		formulaFactors.put(TRA_GOLF, .15);
		formulaFactors.put(TRA_BUSINESS, .2);
		formulaFactors.put(TRA_VALUABLES, .05);
		formulaFactors.put(TRA_HAZARDOUS, .1);
		formulaFactors.put(TRA_EXCESS, .15);

	}

	@Override
	// temporary method implementation for demo to calculate optional product based on base price.
	protected List<QuotationItemResponseData> executeAlternativeAlgorithm(final QuotationRequestData quotationRequestData,
			final String basePrice)
	{
		final List<QuotationItemResponseData> productList = new ArrayList<QuotationItemResponseData>();
		QuotationItemResponseData productData;

		for (final QuotationItemRequestData itemData : quotationRequestData.getItems())
		{
			productData = new QuotationItemResponseData();

			// main product plan
			productData.setId(itemData.getId());
			Double value = 2d;

			if (itemData.getId().indexOf(TRA_WINTER) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_WINTER);
			}
			else if (itemData.getId().indexOf(TRA_GOLF) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_GOLF);
			}
			else if (itemData.getId().indexOf(TRA_BUSINESS) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_BUSINESS);
			}
			else if (itemData.getId().indexOf(TRA_VALUABLES) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_VALUABLES);
			}
			else if (itemData.getId().indexOf(TRA_HAZARDOUS) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_HAZARDOUS);
			}
			else if (itemData.getId().indexOf(TRA_EXCESS) != -1)
			{
				value = Double.valueOf(basePrice) * formulaFactors.get(TRA_EXCESS);
			}

			value = (double) Math.round(value * 100) / 100;

			if (value < 1)
			{
				value = 2d;
			}

			productData.setPayNowPrice(Double.toString(Math.round(value * 100) / 100));


			productList.add(productData);
		}

		return productList;
	}

	/**
	 * Method used to evaluate the QuotationRequestData.
	 *
	 * @param requestData
	 * @return true if it is valid request
	 */
	@Override
	protected boolean isValidQuote(final QuotationRequestData requestData)
	{
		boolean isValid = false;

		if (requestData.getProperties().get(FinancialfacadesConstants.TRIP_DETAILS_NO_OF_DAYS) != null
				&& requestData.getProperties().get(FinancialfacadesConstants.TRIP_DETAILS_DESTINATION) != null
				&& requestData.getProperties().get(FinancialfacadesConstants.TRIP_DETAILS_TRAVELLER_AGES) != null
				&& requestData.getItems() != null)
		{
			isValid = true;
		}

		return isValid;
	}

	/**
	 * Method which encapsulates the algorithm to calculate paynow price based on the static factors and the request
	 * data.
	 *
	 * @param requestData
	 * @param itemData
	 * @return final paynow price.
	 */
	@Override
	protected String executeAlgorithm(final QuotationRequestData requestData, final QuotationItemRequestData itemData)
	{
		/*
		 * The formula for travel each item is ( ( destinationFactor + daysfactor + (ageFactor for each traveller ) +
		 * (travellersFactor) + ) base price )
		 * 
		 * ((item.getCoverage(cancellation/1000)+ item.getCoverage(wintersports/20) + item.getCoverage(golfcover/20) +
		 * item.getCoverage(businesscover/20) + item.getCoverage(hazardous/20) + item.getCoverage(valuablesextension/20) +
		 * item.getCoverage(excesswaiver/20) )
		 */

		final double price = (getDestinationFactor(requestData) + getDayFactor(requestData) + getAgeFactor(requestData) + getTravellerFactor(requestData))
				* BASE_FACTOR * getCoverageFactor(itemData);

		return Double.toString(Math.round(price * 100) / 100);
	}

	/**
	 * Helper method used to extract destination calculation factor based on request data
	 *
	 * @param requestData
	 * @return destination calculation factor
	 */
	protected double getDestinationFactor(final QuotationRequestData requestData)
	{
		final String destination = requestData.getProperties().get(FinancialfacadesConstants.TRIP_DETAILS_DESTINATION);

		return formulaFactors.get(destination);
	}

	/**
	 * Helper method used to extract number of travel days calculation factor based on request data
	 *
	 * @param requestData
	 * @return number of days calculation factor
	 */
	protected double getDayFactor(final QuotationRequestData requestData)
	{
		final int days = MapUtils.getIntValue(requestData.getProperties(), FinancialfacadesConstants.TRIP_DETAILS_NO_OF_DAYS, 1);

		final String daysKey = days < 32 ? "0-31" : days > 100 ? "100-365" : "31-100";

		return formulaFactors.get(daysKey);
	}

	/**
	 * Helper method used to extract ages(all travellers) calculation factor based on request data
	 *
	 * @param requestData
	 * @return age calculation factor
	 */
	protected double getAgeFactor(final QuotationRequestData requestData)
	{
		final String[] ages = StringUtils.split(
				requestData.getProperties().get(FinancialfacadesConstants.TRIP_DETAILS_TRAVELLER_AGES), DEFAULT_DATA_SEPERATOR);

		double factor = 0;

		for (final String age : ages)
		{
			final String ageKey = Integer.parseInt(age) < 18 ? "0-17" : Integer.parseInt(age) > 65 ? "65+" : "18-65";

			factor = factor + formulaFactors.get(ageKey);
		}

		return factor;
	}

	/**
	 * Helper method used to extract number of travellers calculation factor based on request data
	 *
	 * @param requestData
	 * @return number of travellers calculation factor
	 */
	protected double getTravellerFactor(final QuotationRequestData requestData)
	{
		final int noOfTravellers = Integer.parseInt(requestData.getProperties().get(
				FinancialfacadesConstants.TRIP_DETAILS_NO_OF_TRAVELLERS));

		return noOfTravellers / formulaFactors.get(FinancialfacadesConstants.TRIP_DETAILS_NO_OF_TRAVELLERS);
	}

	/**
	 * Helper method used to extract coverage calculation factor based on request data
	 *
	 * @param itemData
	 * @return coverage calculation factor
	 */
	protected double getCoverageFactor(final QuotationItemRequestData itemData)
	{
		double factor = 0;

		for (final String key : itemData.getProperties().keySet())
		{
			if (formulaFactors.get(key) != null)
			{
				factor = factor + (Double.parseDouble(itemData.getProperties().get(key)) / formulaFactors.get(key));
			}
		}
		return factor;
	}

}
