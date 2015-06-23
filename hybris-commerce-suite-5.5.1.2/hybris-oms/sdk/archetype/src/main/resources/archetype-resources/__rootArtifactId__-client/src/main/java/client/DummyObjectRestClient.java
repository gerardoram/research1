#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.client;

import com.hybris.commons.client.GenericRestClient;
import com.hybris.commons.client.RestResponseException;
import com.hybris.oms.domain.exception.HybrisSystemException;
import com.hybris.oms.domain.order.Order;
import ${package}.api.DummyObjectFacade;
import ${package}.api.dto.DummyObject;

import java.util.Collection;

import com.sun.jersey.api.client.GenericType;


public class DummyObjectRestClient implements DummyObjectFacade
{
	private static final GenericType<Collection<DummyObject>> DUMMY_OBJECTS = new GenericType<Collection<DummyObject>>()
	{

	};

	private GenericRestClient client;

	public void setClient(final GenericRestClient genericRestClient)
	{
		this.client = genericRestClient;
	}

	public GenericRestClient getClient()
	{
		return client;
	}

	@Override
	public void create(final DummyObject dummyObject)
	{
		try
		{
			getClient().call("/dummy").post(dummyObject).getResult();
		}
		catch (final RestResponseException e)
		{
			e.unwrap(HybrisSystemException.class);
		}
	}

	@Override
	public DummyObject get(final String dummyObjectName)
	{
		try
		{
			return getClient().call("/dummy/" + dummyObjectName).get(DummyObject.class).getResult();
		}
		catch (final RestResponseException e)
		{
			return e.unwrap(HybrisSystemException.class);
		}
	}

	@Override
	public Collection<DummyObject> getAll()
	{
		try
		{
			return getClient().call("/dummy").get(DUMMY_OBJECTS).getResult();
		}
		catch (final RestResponseException e)
		{
			return e.unwrap(HybrisSystemException.class);
		}
	}

	@Override
	public Order getMyFavoriteOrder()
	{
		try
		{
			return getClient().call("/dummy/order").get(Order.class).result();
		}
		catch (final RestResponseException e)
		{
			return e.unwrap(HybrisSystemException.class);
		}
	}
}
