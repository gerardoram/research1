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
package ${package}.service.mixin;

import com.hybris.oms.service.managedobjects.order.OrderData;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test to make sure that mixins work in the extension project.
 */
public class OrderMixinTest
{

	@Test
	public void setUp() throws NoSuchFieldException, SecurityException, NoSuchMethodException
	{
		Assert.assertNotNull(OrderData.class.getDeclaredField("DUMMY"));
		Assert.assertNotNull(OrderData.class.getDeclaredMethod("getDummy"));
		Assert.assertNotNull(OrderData.class.getDeclaredMethod("setDummy", String.class));
	}
}
