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

import com.hybris.commons.conversion.impl.AbstractPopulatingConverter;
import com.hybris.oms.domain.DummyObjectData;
import ${package}.api.dto.DummyObject;


/**
 * Implementation of {@link AbstractPopulatingConverter} to convert {@link DummyObjectData} to {@link DummyObject}.
 */
public class DummyObjectDataToModelConverter extends AbstractPopulatingConverter<DummyObjectData, DummyObject>
{

	@Override
	public DummyObject createTarget()
	{
		return new DummyObject();
	}

}
