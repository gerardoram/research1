/*******************************************************************************
 * [y] hybris Platform
 *  
 *   Copyright (c) 2000-2013 hybris AG
 *   All rights reserved.
 *  
 *   This software is the confidential and proprietary information of hybris
 *   ("Confidential Information"). You shall not disclose such Confidential
 *   Information and shall use it only in accordance with the terms of the
 *   license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.hybris.mobile.InternalConstants;
import com.hybris.mobile.SDKSettings;
import com.hybris.mobile.data.HYOCCInterface;
import com.hybris.mobile.data.WebService;
import com.hybris.mobile.model.Facet;
import com.hybris.mobile.model.Sort;
import com.hybris.mobile.model.product.Product;
import com.hybris.mobile.model.product.ProductsList;
import com.hybris.mobile.utility.JsonUtils;


public class ProductTests extends AndroidTestCase
{

	private static HYOCCInterface webService;
	private static String kLogin = "aeiou@hybris.com";
	private static String kPassword = "password";
	private static boolean setUpIsDone = false;

	public void login() throws Exception
	{
		webService.loginUser(kLogin, kPassword);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Thread.sleep(1000);
		SDKSettings.setSettingValue(InternalConstants.KEY_PREF_CATALOG, "electronics/");

		if (!setUpIsDone)
		{
			WebService.initWebService(mContext);
			webService = WebService.getWebService();

			setUpIsDone = true;
		}

	}

	@Override
	protected void tearDown() throws Exception
	{
	}

	public void testSpellingSuggestion() throws Exception
	{
		assertTrue(!webService.spellingSuggestions("cafon").isEmpty());
	}

	public void testProducts() throws Exception
	{
		ArrayList<Product> allProducts = new ArrayList<Product>();

		// Get products - page size 20
		ProductsList productsList = JsonUtils.fromJson(webService.getProducts(null, null, null, null, 20, 0), ProductsList.class);

		assertTrue(productsList.getProducts().size() == 20);
		//its safe to cast
		List<Facet> facets = productsList.getFacets();
		assertTrue(facets.size() > 0);
		//its safe to cast
		List<Sort> sorts = productsList.getSorts();
		assertTrue(sorts.size() > 0);

		allProducts.addAll(productsList.getProducts());

		// Page 2
		productsList = JsonUtils.fromJson(webService.getProducts(null, null, null, null, 20, 1), ProductsList.class);

		assertTrue(productsList.getProducts().size() == 20);

		allProducts.addAll(productsList.getProducts());
		assertTrue(allProducts.size() == 40);

		// Fetch 50 products
		productsList = JsonUtils.fromJson(webService.getProducts(null, null, null, null, 50, 0), ProductsList.class);

		assertTrue(productsList.getProducts().size() == 50);

		// Specific search
		productsList = JsonUtils.fromJson(webService.getProducts("camera", null, null, null, 50, 0), ProductsList.class);

		// Check all are cameras
		//		for (Product p : productsList.getProducts())
		//		{
		//			assertTrue(p.getName().matches("(?i).*camera*"));
		//		}

		// Set a sort option
		productsList = JsonUtils.fromJson(webService.getProducts("camera",
				productsList.getSorts().get(productsList.getSorts().size() - 1).getCode(), null, null, 50, 0), ProductsList.class);

		assertTrue(productsList.getProducts().size() > 0);
	}

	public void testProductWithCode() throws Exception
	{
		// Correct code
		String options[] =
		{ "BASIC" };
		Product product = JsonUtils.fromJson(webService.getProductWithCode(options, "23355"), Product.class);

		assertTrue(!product.getCode().isEmpty());
	}

	public void testProductWithCodeIncorrect() throws Exception
	{
		// Incorrect code
		String options[] =
		{ "BASIC" };

		try
		{
			webService.getProductWithCode(options, "XXXXXsafsdfsdf4121");
		}
		catch (Exception e)
		{
			assertTrue(e != null);
		}

	}

}
