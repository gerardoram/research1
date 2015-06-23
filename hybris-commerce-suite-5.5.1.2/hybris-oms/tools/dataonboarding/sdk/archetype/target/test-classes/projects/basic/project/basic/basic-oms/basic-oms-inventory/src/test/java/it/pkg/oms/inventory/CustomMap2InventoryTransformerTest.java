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
package it.pkg.oms.inventory;

import com.hybris.dataonboarding.framework.extract.SchemalessAttributesExtractor;
import com.hybris.dataonboarding.framework.transform.exceptions.Map2PojoTransformationException;
import com.hybris.dataonboarding.framework.transform.map.impl.DefaultMap2PojoTransformer;
import com.hybris.dataonboarding.framework.transform.map.impl.SchemalessMap2PojoTransformer;
import com.hybris.dataonboarding.oms.inventory.Map2InventoryTransformer;
import com.hybris.oms.api.inventory.OmsInventory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test covering high level {@link CustomMap2InventoryTransformer} logic.
 */
public class CustomMap2InventoryTransformerTest
{
	private CustomMap2InventoryTransformer transformer;

	@Before
	public void setUp()
	{
		final SchemalessMap2PojoTransformer schemalessMap2PojoTransformer = new SchemalessMap2PojoTransformer();
		schemalessMap2PojoTransformer.setSchemalessAttributesExtractor(new SchemalessAttributesExtractor());
		schemalessMap2PojoTransformer.setMap2PojoTransformer(new DefaultMap2PojoTransformer());

		final Map2InventoryTransformer map2InventoryTransformer = new Map2InventoryTransformer();
		map2InventoryTransformer.setMap2PojoTransformer(schemalessMap2PojoTransformer);

		transformer = new CustomMap2InventoryTransformer();
		transformer.setMap2InventoryTransformer(map2InventoryTransformer);
	}

	@Test
	public void map2InventoryNoBin() throws Map2PojoTransformationException
	{
		final Date date = new Date();

		final Map<String, Serializable> map = new HashMap<>();
		map.put("skuId", "AAA");
		map.put("locationId", "999");
		map.put("deliveryDate", formatDate(date));
		map.put("quantity", "33");
		map.put("unitCode", "units");
		map.put("status", "ON_HAND");

		final OmsInventory inventory = this.transformer.transform(map);
		Assert.assertEquals("skuId", "AAA", inventory.getSkuId());
		Assert.assertEquals("locationId", "999", inventory.getLocationId());
		Assert.assertEquals("deliveryDate", formatDate(date), formatDate(inventory.getDeliveryDate()));
		Assert.assertEquals("quantity", 33, inventory.getQuantity());
		Assert.assertEquals("unitCode", "units", inventory.getUnitCode());
		Assert.assertEquals("status", "ON_HAND", inventory.getStatus());
		Assert.assertNull("binCode", inventory.getBinCode());
	}

	private String formatDate(final Date date)
	{
		return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
	}
}
