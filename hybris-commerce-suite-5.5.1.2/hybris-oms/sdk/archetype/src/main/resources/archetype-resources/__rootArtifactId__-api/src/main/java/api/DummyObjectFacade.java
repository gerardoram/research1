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
 */
package ${package}.api;

import com.hybris.oms.domain.exception.EntityNotFoundException;
import com.hybris.oms.domain.order.Order;
import ${package}.api.dto.DummyObject;

import java.util.Collection;

/**
 * The interface DummyObjectService API.
 */
public interface DummyObjectFacade {
    /**
     * Gets My favorite order.
     *
     * @return your favorite order
     * @throws EntityNotFoundException the DummyObject not found exception
     */
    Order getMyFavoriteOrder() throws EntityNotFoundException;

    /**
     * Creates dummy object.
     *
     * @param dummyObject the dummy object to create
     */
    void create(DummyObject dummyObject);

    /**
     * Search a dummy object by its name.
     *
     * @param name the name you are looking for
     * @return the dummy object corresponding to your search
     * @throws EntityNotFoundException the DummyObject not found exception
     */
    DummyObject get(String name) throws EntityNotFoundException;

    /**
     * Returns all dummy objects.
     *
     * @return full list of dummy objects
     */
    Collection<DummyObject> getAll();

}
