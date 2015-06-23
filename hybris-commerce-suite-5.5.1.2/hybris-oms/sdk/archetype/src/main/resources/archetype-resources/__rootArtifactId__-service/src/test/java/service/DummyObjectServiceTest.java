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
package ${package}.service;

import com.hybris.kernel.api.ImportService;
import com.hybris.kernel.utils.ClasspathScanningResourceFetcher;
import com.hybris.kernel.utils.ResourceFetcher;
import com.hybris.oms.domain.order.Order;
import ${package}.api.DummyObjectFacade;
import ${package}.api.dto.DummyObject;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:${parentArtifactId}-service-test-spring.xml"})
public class DummyObjectServiceTest
{
	private static final String DUMMY_NAME_1 = "dummyName1";
	private static final String DUMMY_NAME_2 = "dummyName2";
	private static final String DUMMY_NAME_3 = "dummyName3";
	private static final String DUMMY_PROPERTY = "dummy property";
	private static final String FAVORITE_ORDER = "12";
	private final ResourceFetcher fetcher = new ClasspathScanningResourceFetcher();
	@Autowired
	private DummyObjectFacade dummyObjectFacade;
	@Autowired
	private ImportService importService;
	private DummyObject dummyObjectData;
	private DummyObject dummyObjectData2;
	private DummyObject dummyObjectData3;

	@Before
	public void init()
	{
		dummyObjectData = new DummyObject();
		dummyObjectData.setName(DUMMY_NAME_1);
		dummyObjectData.setDummyProperty(DUMMY_PROPERTY);

		dummyObjectData2 = new DummyObject();
		dummyObjectData2.setName(DUMMY_NAME_2);
		dummyObjectData3 = new DummyObject();
		dummyObjectData3.setName(DUMMY_NAME_3);
	}

	@Test
	public void createListAndRetrieveAllDummyObject()
	{
		Collection<DummyObject> allDod = dummyObjectFacade.getAll();

		Assert.assertTrue(allDod.isEmpty());

		dummyObjectFacade.create(dummyObjectData2);
		dummyObjectFacade.create(dummyObjectData3);

		allDod = dummyObjectFacade.getAll();

		allDod = dummyObjectFacade.getAll();
		Assert.assertEquals(2, allDod.size());
	}

	@Test
	public void createAndRetrieveDummyObject()
	{
		dummyObjectFacade.create(dummyObjectData);
		final DummyObject dod = dummyObjectFacade.get(DUMMY_NAME_1);
		Assert.assertEquals(dummyObjectData, dod);
	}

	@Test
	@Transactional
	public void getFavoriteOrder()
	{
		this.importService.loadMcsvResource(this.fetcher.fetchResources("/order/test-order-data-import.mcsv")[0]);

		final Order order = dummyObjectFacade.getMyFavoriteOrder();
		Assert.assertEquals(order.getOrderId(), FAVORITE_ORDER);
	}
}
