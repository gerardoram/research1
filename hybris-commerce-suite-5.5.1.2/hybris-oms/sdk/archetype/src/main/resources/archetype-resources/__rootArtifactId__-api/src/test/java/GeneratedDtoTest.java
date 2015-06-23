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
package ${package};



import com.hybris.oms.domain.order.Order;

import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class GeneratedDtoTest
{

	@Test
	public void orderShouldHaveDummyProp() throws NoSuchFieldException, SecurityException
	{
		Assert.assertTrue(Order.class.getDeclaredField("dummyNewProp") != null);
	}
}
