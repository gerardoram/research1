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
package ${package}.oms.inventory;

import com.hybris.dataonboarding.framework.transform.exceptions.Map2PojoTransformationException;
import com.hybris.dataonboarding.oms.inventory.Map2InventoryTransformer;
import com.hybris.oms.api.inventory.OmsInventory;

import java.io.Serializable;
import java.util.Map;


/**
 * Custom transformer for converting a map to the {@link OmsInventory} domain object.
 */
public class CustomMap2InventoryTransformer
{
	private Map2InventoryTransformer map2InventoryTransformer;

	/**
	 * converts a map to an OmsInventory object.
	 * 
	 * @param map
	 *           the map to be converted to an OmsInventory object
	 * @return the OmsInventory object
	 * @throws Map2PojoTransformationException
	 *            if error occurs while converting map
	 */
	public OmsInventory transform(final Map<String, Serializable> map) throws Map2PojoTransformationException
	{
		// do yout fancy stuff here

		return map2InventoryTransformer.transform(map);
	}

	public void setMap2InventoryTransformer(final Map2InventoryTransformer map2InventoryTransformer)
	{
		this.map2InventoryTransformer = map2InventoryTransformer;
	}
}
