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
package com.hybris.mobile.lib.b2b.data.order;

import java.util.List;

import com.hybris.mobile.lib.b2b.data.Pagination;


public class OrderList
{
	private List<Order> orders;
	private Pagination pagination;

	public List<Order> getOrders()
	{
		return orders;
	}

	public void setProducts(List<Order> orders)
	{
		this.orders = orders;
	}

	public Pagination getPagination()
	{
		return pagination;
	}

	public void setPagination(Pagination pagination)
	{
		this.pagination = pagination;
	}
}
