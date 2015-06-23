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
package de.hybris.platform.integration.oms.ats.stock.impl;

import com.hybris.commons.tenant.TenantContextService;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.integration.commons.model.OndemandBaseStorePreferenceModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;


@ManualTest
public class DefaultOmsStockLevelDaoTest extends ServicelayerTest
{
    private static final Logger LOG = Logger.getLogger(DefaultOmsStockLevelDaoTest.class);
	private static final String SITE_NAME = "testSite";
	private static final String PRODUCT_CODE = "1934793";
	private static final String WAREHOUSE_CODE = "warehouse_s";
	private static final String ATS_FORMULA = "ON_HAND";
    public static final String VENDOR_CODE = "vendorCode";

    @Resource
	private DefaultOmsStockLevelDao defaultOmsStockLevelDao;
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private BaseStoreService baseStoreService;


	@Before
	public void prepare() throws Exception
	{
		importCsv("/omsats/test/testStockLevels.csv", "UTF-8");

		final BaseSiteModel site = baseSiteService.getBaseSiteForUID(SITE_NAME);
		Assert.assertNotNull(String.format("no baseSite with uid: %s", SITE_NAME), site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);

		createOndemandBaseStorePreference();
	}

	protected void createOndemandBaseStorePreference()
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();

		final OndemandBaseStorePreferenceModel tenantBaseStorePreferenceModel = modelService
				.create(OndemandBaseStorePreferenceModel.class);
		tenantBaseStorePreferenceModel.setBaseStore(baseStoreModel);
		tenantBaseStorePreferenceModel.setAtsFormula(ATS_FORMULA);
		tenantBaseStorePreferenceModel.setExternalCallsEnabled(true);
		modelService.save(tenantBaseStorePreferenceModel);
		baseStoreModel.setOndemandBaseStorePreferences(Arrays.asList(tenantBaseStorePreferenceModel));
		modelService.save(baseStoreModel);
	}

	@Test
	public void verifyProductHasStockLevels()
	{
		final String productCode = PRODUCT_CODE;
		final WarehouseModel warehouse = modelService.create(WarehouseModel.class);
		warehouse.setCode(WAREHOUSE_CODE);
	 	final VendorModel vendorModel = modelService.create(VendorModel.class);
		final StockLevelModel stockLevelModel= modelService.create(StockLevelModel.class);
		stockLevelModel.setProductCode(PRODUCT_CODE);
		stockLevelModel.setAvailable(1);
		stockLevelModel.setWarehouse(warehouse);
		vendorModel.setCode(VENDOR_CODE);
		warehouse.setVendor(vendorModel);
		Set<StockLevelModel> stockLevelModels = new HashSet();
		stockLevelModels.add(stockLevelModel);
	 	warehouse.setStockLevels(stockLevelModels);
		modelService.save(warehouse);

		final Collection<StockLevelModel> stockLevels = defaultOmsStockLevelDao.findStockLevels(productCode,
				Arrays.asList(warehouse));
		Assert.assertNotNull(stockLevels);
		Assert.assertNotSame(Integer.valueOf(stockLevels.size()), Integer.valueOf(0));
	}
}
