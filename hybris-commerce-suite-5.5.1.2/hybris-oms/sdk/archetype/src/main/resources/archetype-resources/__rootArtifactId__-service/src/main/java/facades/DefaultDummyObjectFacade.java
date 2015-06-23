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
package ${package}.facades;

import com.hybris.commons.conversion.Converter;
import com.hybris.commons.conversion.util.Converters;
import com.hybris.oms.domain.DummyObjectData;
import com.hybris.oms.domain.order.Order;
import ${package}.api.DummyObjectFacade;
import ${package}.api.dto.DummyObject;
import ${package}.conversion.dummy.DummyObjectDataToModelConverter;
import ${package}.conversion.dummy.DummyObjectModelToDataConverter;
import ${package}.services.DummyObjectService;
import com.hybris.oms.service.managedobjects.order.OrderData;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implementation of {@link DummyObjectFacade}.
 */
public class DefaultDummyObjectFacade implements DummyObjectFacade
{
	private DummyObjectService dummyObjectService;
	private Converter<OrderData, Order> orderConverter;
	private DummyObjectModelToDataConverter dummyObjectModelToDataConverter;
	private DummyObjectDataToModelConverter dummyObjectDataToModelConverter;
	private Converters converters;

	@Override
	public Order getMyFavoriteOrder()
	{
		final OrderData orderData = this.dummyObjectService.getMyFavoriteOrder();
		return this.orderConverter.convert(orderData);
	}

	@Override
	@Transactional
	public void create(final DummyObject dummyObject)
	{
		dummyObjectModelToDataConverter.convert(dummyObject);
	}

	@Override
	@Transactional
	public DummyObject get(final String dummyObjectName)
	{

		final DummyObjectData dummyObjectData = dummyObjectService.get(dummyObjectName);

		return dummyObjectDataToModelConverter.convert(dummyObjectData);
	}

	@Override
	public Collection<DummyObject> getAll()
	{
		final List<DummyObjectData> dummyObjects = dummyObjectService.getAll();

		return converters.convertAll(dummyObjects, dummyObjectDataToModelConverter);
	}

	@Required
	public void setDummyObjectService(final DummyObjectService dummyObjectService)
	{
		this.dummyObjectService = dummyObjectService;
	}

	@Required
	public void setOrderConverter(final Converter<OrderData, Order> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	@Required
	public void setDummyObjectModelToDataConverter(final DummyObjectModelToDataConverter dummyObjectModelToDataConverter)
	{
		this.dummyObjectModelToDataConverter = dummyObjectModelToDataConverter;
	}

	@Required
	public void setDummyObjectDataToModelConverter(final DummyObjectDataToModelConverter dummyObjectDataToModelConverter)
	{
		this.dummyObjectDataToModelConverter = dummyObjectDataToModelConverter;
	}

	@Required
	public void setConverters(final Converters converters)
	{
		this.converters = converters;
	}

}
