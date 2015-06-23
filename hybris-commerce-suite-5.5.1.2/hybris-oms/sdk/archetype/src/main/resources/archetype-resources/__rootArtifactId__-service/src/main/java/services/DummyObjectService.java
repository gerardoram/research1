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
package ${package}.services;

import com.hybris.oms.domain.DummyObjectData;
import com.hybris.oms.domain.exception.EntityNotFoundException;
import ${package}.api.dto.DummyObject;
import com.hybris.oms.service.managedobjects.order.OrderData;

import java.util.List;


/**
 * Service providing methods to manipulate {@link DummyObject} object.
 */
public interface DummyObjectService
{
	/**
	 * Return the order with the order ID 12.
	 * 
	 * @return your favorite order which is order 12
	 * @throws EntityNotFoundException
	 *            the DummyObject not found exception
	 */
	OrderData getMyFavoriteOrder() throws EntityNotFoundException;

	/**
	 * Creates a dummy object.
	 * 
	 * @param dummyObject
	 *           the dummy object to create
	 */
	DummyObjectData createDummyObject();

	/**
	 * Get the dummy object by the name passed in parameter.
	 * 
	 * @return the requested dummyObject which match the name in parameter
	 * @throws EntityNotFoundException
	 *            the DummyObject not found exception
	 */
	DummyObjectData get(String name) throws EntityNotFoundException;

	/**
	 * Get all the dummy objects.
	 * 
	 * @return a list of all the existing dummyObject
	 */
	List<DummyObjectData> getAll();

}
