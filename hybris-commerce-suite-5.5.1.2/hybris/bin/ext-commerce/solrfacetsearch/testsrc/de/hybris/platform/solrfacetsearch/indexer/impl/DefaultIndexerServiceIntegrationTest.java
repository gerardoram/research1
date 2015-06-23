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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultIndexerServiceIntegrationTest extends ServicelayerTest
{
	private static final String FACET_SEARCH_CONFIG_NAME = "testFacetSearchConfig";
	private static final String PRODUCT_CODE = "HW1100-0024";
	private static final String PRICE_INDEXED_PROPERTY = "price";
	private static final String CATALOG_ID = "hwcatalog";
	private static final String CATALOG_VERSION_STAGED = "Staged";
	private static final String CATALOG_VERSION_ONLINE = "Online";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Resource
	private FacetSearchConfigService facetSearchConfigService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/test/solrBasics.csv", "windows-1252");
		importCsv("/test/solrHwcatalogStaged.csv", "utf-8");
		importCsv("/test/solrHwcatalogOnline.csv", "utf-8");
	}

	protected FacetSearchConfig createAndLoadConfig(final String... resources) throws IOException, ImpExException,
			FacetConfigServiceException
	{
		final String testId = Long.toString(System.currentTimeMillis());

		importConfig("/test/solrConfigBase.csv", "utf-8", testId);

		for (final String resource : resources)
		{
			importConfig(resource, "utf-8", testId);
		}

		return facetSearchConfigService.getConfiguration(FACET_SEARCH_CONFIG_NAME + testId);
	}

	protected List<ProductModel> getExistingProducts()
	{
		final List<ProductModel> products = new ArrayList<ProductModel>();

		final CatalogVersionModel catalogVersionStaged = catalogVersionService
				.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGED);
		final ProductModel productStaged = productService.getProductForCode(catalogVersionStaged, PRODUCT_CODE);

		final CatalogVersionModel catalogVersionOnline = catalogVersionService
				.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_ONLINE);
		final ProductModel productOnline = productService.getProductForCode(catalogVersionOnline, PRODUCT_CODE);

		products.add(productStaged);
		products.add(productOnline);

		return products;
	}

	protected void importConfig(final String resource, final String encoding, final String testId) throws IOException,
			ImpExException
	{
		final InputStream inputStream = DefaultIndexerServiceIntegrationTest.class.getResourceAsStream(resource);

		final String impexContent = IOUtils.toString(inputStream, encoding);
		final String newImpexContent = impexContent.replaceAll("\\$\\{testId\\}", testId);

		final InputStream newInputStream = IOUtils.toInputStream(newImpexContent, encoding);

		importStream(newInputStream, encoding, resource);
	}

	@Test
	public void performFullIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();

		// when
		indexerService.performFullIndex(facetSearchConfig);
	}

	@Test
	public void updateIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.updateIndex(facetSearchConfig);
	}

	@Test
	public void updateTypeIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.updateTypeIndex(facetSearchConfig, indexedType);
	}

	@Test
	public void updateTypeIndexWithPks() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final List<PK> pks = new ArrayList<PK>();

		for (final ProductModel product : getExistingProducts())
		{
			pks.add(product.getPk());
		}

		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.updateTypeIndex(facetSearchConfig, indexedType, pks);
	}

	@Test
	public void updatePartialTypeIndexWithPks() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedType.getIndexedProperties().get(
				PRICE_INDEXED_PROPERTY));
		final List<PK> pks = new ArrayList<PK>();

		for (final ProductModel product : getExistingProducts())
		{
			pks.add(product.getPk());
		}

		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.updatePartialTypeIndex(facetSearchConfig, indexedType, indexedProperties, pks);
	}

	@Test
	public void deleteFromIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.deleteFromIndex(facetSearchConfig);
	}

	@Test
	public void deleteTypeIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.deleteTypeIndex(facetSearchConfig, indexedType);
	}

	@Test
	public void deleteTypeIndexWithPks() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = createAndLoadConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final List<PK> pks = new ArrayList<PK>();

		for (final ProductModel product : getExistingProducts())
		{
			pks.add(product.getPk());
		}

		indexerService.performFullIndex(facetSearchConfig);

		// when
		indexerService.deleteTypeIndex(facetSearchConfig, indexedType, pks);
	}
}
