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
package ${package}.conversion.dummy;

import com.hybris.commons.conversion.impl.AbstractPopulator;
import com.hybris.oms.domain.DummyObjectData;
import ${package}.api.dto.DummyObject;


/**
 * Implementation of {@link AbstractPopulator} to populate a {@link DummyObject} from {@link DummyObject}.
 */
public class DummyObjectDataToModelPopulator extends AbstractPopulator<DummyObjectData, DummyObject>
{
	@Override
	public void populate(final DummyObjectData source, final DummyObject target)
	{
		target.setName(source.getName());
		target.setDummyProperty(source.getDummyProperty());
	}

}
