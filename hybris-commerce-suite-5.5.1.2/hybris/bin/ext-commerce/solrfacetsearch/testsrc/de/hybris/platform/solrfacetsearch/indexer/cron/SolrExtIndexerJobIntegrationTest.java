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
package de.hybris.platform.solrfacetsearch.indexer.cron;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.integration.IndexFullProductTest;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrExtIndexerCronJobModel;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class SolrExtIndexerJobIntegrationTest extends AbstractSolrIntegrationTest
{
	private static final String PARAMETER_PROVIDER = "simpleParameterProvider";
	private static final String INDEXED_TYPE = "Product_Product";
	private static final String ONLY_NEW_PRODUCT_PK_QUERY = "select PK from {Product as p} where {p.code} = 'code123'";
	private static final String PRODUCT_PK_QUERY = "select PK from {Product as p} where {p.code} = 'HW2310-1001'";
	private SolrExtIndexerCronJobModel model;

	private static final String NEW_PRODUCT_EN_DESCRIPTION = "new test description";
	private static final String NEW_PRODUCT_NAME = "new test name";
	private static final String NEW_TEST_PRODUCT_CODE = "code123";
	private static final String EXISTING_TEST_PRODUCT_CODE = "HW2310-1001";

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(IndexFullProductTest.class.getName());
	private SolrServer solrServer;
	private Locale enLocale;
	private UnitModel unit;
	private PriceRowModel pricerow;

	@Resource
	private SolrExtIndexerJob solrExtIndexerJob;

	@Override
	@Before
	public void setUp() throws Exception
	{

		super.setUp();
		try
		{
			final ProductModel product = productService.getProductForCode(NEW_TEST_PRODUCT_CODE);
			modelService.remove(product);
			LOG.warn("Product with code " + NEW_TEST_PRODUCT_CODE + " appears to already have been in the DB. Deleting it...");
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.info("Product with code " + NEW_TEST_PRODUCT_CODE + " didn't exist in the DB.");
		}

		solrServer = getSolrService().getSolrServer(facetSearchConfig.getSolrConfig(), indexedType);

		catalogVersionService.setSessionCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		enLocale = Locale.ENGLISH;

		Thread.sleep(1100);
		dropIndex();
		indexerService.performFullIndex(facetSearchConfig);
		solrServer.commit(true, true);
		Thread.sleep(1100);
		initCronJob();
	}

	@Override
	protected void prepareIndexForTest() throws Exception
	{
		//
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		super.setUpProductData();
		unit = modelService.create(UnitModel.class);
		unit.setCode("specialunit");
		unit.setConversion(Double.valueOf(1.0));
		unit.setName("special Unit", Locale.ENGLISH);
		unit.setUnitType("something");

		pricerow = modelService.create(PriceRowModel.class);
		pricerow.setCurrency(commonI18NService.getCurrency("EUR"));
		pricerow.setMinqtd(Long.valueOf(1));
		pricerow.setNet(Boolean.TRUE);
		pricerow.setPrice(Double.valueOf(2.34));
		pricerow.setUnit(unit);
	}

	private void initCronJob()
	{
		model = modelService.create(SolrExtIndexerCronJobModel.class);
		model.setQuery(PRODUCT_PK_QUERY);
		model.setFacetSearchConfig(localConfig);
		model.setIndexedType(INDEXED_TYPE);
		model.setSessionUser((UserModel) modelService.get(JaloSession.getCurrentSession().getUser()));
		model.setQueryParameterProvider(PARAMETER_PROVIDER);
		model.setIndexerOperation(IndexerOperationValues.UPDATE);
	}

	private ProductModel createNewProduct(final CatalogVersionModel catalogVersion)
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAnonymousCustomer());
		final ProductModel newProduct = modelService.create(ProductModel.class);

		newProduct.setCatalogVersion(catalogVersion);
		newProduct.setCode(NEW_TEST_PRODUCT_CODE);
		newProduct.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		pricerow.setUnit(unit);
		pricerow.setProduct(newProduct);
		pricerow.setCatalogVersion(catalogVersion);

		return newProduct;
	}

	@Test
	public void createNewProductAndUpdate() throws Exception
	{
		try
		{
			final String productId = prepareProductSolrId(CATALOG_ID, VERSION_ONLINE, NEW_TEST_PRODUCT_CODE);
			String solrQuery = "id:\"" + productId + "\"";
			QueryResponse solrResponse = solrServer.query(new SolrQuery(solrQuery));
			assertEquals("Failed test data. New product " + productId + " already exist in indexer!", 0, solrResponse.getResults()
					.getNumFound());
			Thread.sleep(1100);
			final ProductModel newProduct = createNewProduct(hwOnline);

			modelService.saveAll(Arrays.asList(pricerow, unit, newProduct));

			final ProductModel testProduct = productService.getProductForCode(NEW_TEST_PRODUCT_CODE);
			assertNotNull("New test product can not be created.", testProduct);
			modelService.refresh(testProduct);
			assertEquals(testProduct.getCode(), NEW_TEST_PRODUCT_CODE);

			Date indexTime = (Date) solrServer.getLukeSatatisticsValue("lastModified");
			final Date productDate = testProduct.getModifiedtime();
			final DateFormat df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL);
			LOG.info(NEW_TEST_PRODUCT_CODE + " modify time : " + df.format(productDate) + " milis : " + productDate.getTime());
			LOG.info("Solr last index time : " + df.format(indexTime) + " milis : " + indexTime.getTime());
			assertTrue("LastIndexTime is not before new product modification time", indexTime.before(productDate));
			Thread.sleep(1100);

			testProduct.setName(NEW_PRODUCT_NAME, enLocale);
			testProduct.setDescription(NEW_PRODUCT_EN_DESCRIPTION, enLocale);
			modelService.save(testProduct);
			modelService.refresh(testProduct);

			model.setQuery(ONLY_NEW_PRODUCT_PK_QUERY);
			solrExtIndexerJob.perform(model);
			solrServer.commit(true, true);

			solrQuery = "id:\"" + productId + "\" AND name_text_en:\"" + NEW_PRODUCT_NAME + "\" AND description_text_en:\""
					+ NEW_PRODUCT_EN_DESCRIPTION + "\"";
			solrResponse = solrServer.query(new SolrQuery(solrQuery));
			assertEquals("Changed test product was not indexed. Missed solr document " + productId, 1, solrResponse.getResults()
					.getNumFound());
		}
		finally
		{
			final ProductModel product = productService.getProductForCode(NEW_TEST_PRODUCT_CODE);
			modelService.remove(product);
		}
	}

	@Test
	public void testUpdateExistingProduct() throws Exception
	{
		final ProductModel testProduct = productService.getProductForCode(hwOnline, EXISTING_TEST_PRODUCT_CODE);
		assertNotNull("Failed test data! Product with code " + EXISTING_TEST_PRODUCT_CODE + " does not exist.", testProduct);
		final String productName = testProduct.getName(enLocale);
		final String description = testProduct.getDescription(enLocale);

		final String productId = prepareProductSolrId(testProduct);
		String solrQuery = "id:\"" + productId + "\" AND name_text_en:\"" + productName + "\" AND description_text_en:\""
				+ description + "\"";
		QueryResponse solrResponse = solrServer.query(new SolrQuery(solrQuery));
		assertEquals("Test product was not indexed. Missed solr document " + productId, 1, solrResponse.getResults().getNumFound());

		Thread.sleep(1100);

		testProduct.setName(NEW_PRODUCT_NAME, enLocale);
		testProduct.setDescription(NEW_PRODUCT_EN_DESCRIPTION, enLocale);
		modelService.save(testProduct);
		modelService.refresh(testProduct);

		Date indexTime = (Date) solrServer.getLukeSatatisticsValue("lastModified");
		final Date productDate = testProduct.getModifiedtime();

		final DateFormat df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL);
		LOG.info(NEW_TEST_PRODUCT_CODE + " modify time : " + df.format(productDate) + " milis : " + productDate.getTime());
		LOG.info("Solr last index time : " + df.format(indexTime) + " milis : " + indexTime.getTime());

		assertTrue("LastIndexTime is not before new product modification time", indexTime.before(productDate));

		Thread.sleep(1100);

		solrExtIndexerJob.perform(model);
		solrServer.commit(true, true);


		solrResponse = solrServer.query(new SolrQuery(solrQuery));
		assertEquals("Test product is still in old version in the indexer. Product id: " + productId, 0, solrResponse.getResults()
				.getNumFound());

		solrQuery = "id:\"" + productId + "\" AND name_text_en:\"" + NEW_PRODUCT_NAME + "\" AND description_text_en:\""
				+ NEW_PRODUCT_EN_DESCRIPTION + "\"";
		solrResponse = solrServer.query(new SolrQuery(solrQuery));
		assertEquals("Changed test product was not indexed. Missed solr document " + productId, 1, solrResponse.getResults()
				.getNumFound());

	}

	@Test
	public void testDeleteProduct() throws Exception
	{
		final ProductModel testProduct = productService.getProductForCode(hwOnline, EXISTING_TEST_PRODUCT_CODE);
		assertNotNull("Failed test data! Product with code " + EXISTING_TEST_PRODUCT_CODE + " does not exist.", testProduct);
		final String productId = prepareProductSolrId(testProduct);
		final String solrQuery = "id:\"" + productId + "\"";
		QueryResponse solrResponse = solrServer.query(new SolrQuery(solrQuery));
		assertEquals("Test product was not indexed. Missed solr document " + productId, 1, solrResponse.getResults().getNumFound());

		model.setIndexerOperation(IndexerOperationValues.DELETE);
		solrExtIndexerJob.perform(model);
		solrServer.commit(true, true);

		solrResponse = solrServer.query(new SolrQuery(solrQuery));
		assertEquals("Product with soldId = " + productId + "wasn't properly deleted from indexer.", 0, solrResponse.getResults()
				.getNumFound());
	}
}
