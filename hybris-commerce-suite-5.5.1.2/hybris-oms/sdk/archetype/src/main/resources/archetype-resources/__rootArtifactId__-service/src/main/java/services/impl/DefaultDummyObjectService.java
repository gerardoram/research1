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
package ${package}.services.impl;

import com.hybris.kernel.api.exceptions.ManagedObjectNotFoundException;
import com.hybris.oms.domain.DummyObjectData;
import com.hybris.oms.domain.exception.EntityNotFoundException;
import ${package}.services.DummyObjectService;
import com.hybris.oms.service.managedobjects.order.OrderData;
import com.hybris.oms.service.order.impl.OrderQueryFactory;
import com.hybris.oms.service.service.AbstractHybrisService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implementation of {@link DummyObjectService}.
 */
public class DefaultDummyObjectService extends AbstractHybrisService implements DummyObjectService
{
	private static final String ORDER_ID = "12";

	private OrderQueryFactory orderQueries;

	@Override
	@Transactional
	public OrderData getMyFavoriteOrder()
	{
		try
		{
			return this.findOneSingle(this.orderQueries.getOrdersByIdQuery(ORDER_ID));
		}
		catch (final ManagedObjectNotFoundException e)
		{
			throw new EntityNotFoundException("Order Id is not correct, " + ORDER_ID, e);
		}
	}

	@Override
	public DummyObjectData createDummyObject()
	{
		return super.getPersistenceManager().create(DummyObjectData.class);
	}

	@Override
	public DummyObjectData get(final String name)
	{
		try
		{
			return super.getPersistenceManager().getByIndex(DummyObjectData.UX_DUMMYOBJECTDATA_NAME, name);
		}
		catch (final ManagedObjectNotFoundException e)
		{
			throw new EntityNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public List<DummyObjectData> getAll()
	{
		return super.getPersistenceManager().createCriteriaQuery(DummyObjectData.class).resultList();
	}

	@Required
	public void setOrderQueries(final OrderQueryFactory orderQueries)
	{
		this.orderQueries = orderQueries;
	}

}
