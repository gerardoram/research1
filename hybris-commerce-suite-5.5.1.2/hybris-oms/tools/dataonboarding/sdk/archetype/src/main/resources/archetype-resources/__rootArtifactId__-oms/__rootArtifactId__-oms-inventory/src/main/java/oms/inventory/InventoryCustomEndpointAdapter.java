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

import com.hybris.dataonboarding.framework.exceptions.ImportLineException;
import com.hybris.dataonboarding.framework.processor.AbstractTenantAwareEndpoint;
import com.hybris.oms.api.inventory.InventoryFacade;
import com.hybris.oms.api.inventory.OmsInventory;
import com.hybris.oms.domain.exception.EntityValidationException;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Endpoint adapter for custom Inventory routes.
 */
public class InventoryCustomEndpointAdapter extends AbstractTenantAwareEndpoint<OmsInventory>
{
	@Autowired
	private InventoryFacade inventoryFacade;

	@SuppressWarnings("PMD.PreserveStackTrace")
	@Override
	protected void doInTenant(final OmsInventory inventory)
	{
		try
		{
			// do your custom oms operation here
			inventoryFacade.toString();
		}
		catch (final EntityValidationException e)
		{
			throw new ImportLineException(CustomInventoryErrorCode._777, "Invalid data for custom inventory operation", e);
		}
	}
}
