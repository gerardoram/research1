/*******************************************************************************
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.lib.b2b.data.product;



public class PriceRange
{
	private Price maxPrice;
	private Price minPrice;

	public Price getMaxPrice()
	{
		return maxPrice;
	}

	public void setMaxPrice(Price maxPrice)
	{
		this.maxPrice = maxPrice;
	}

	public Price getMinPrice()
	{
		return minPrice;
	}

	public void setMinPrice(Price minPrice)
	{
		this.minPrice = minPrice;
	}



}
