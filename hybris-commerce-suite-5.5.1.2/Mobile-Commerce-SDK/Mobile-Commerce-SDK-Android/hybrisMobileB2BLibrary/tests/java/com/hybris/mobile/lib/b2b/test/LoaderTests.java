/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/

package com.hybris.mobile.lib.b2b.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.LoaderTestCase;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.loader.ProductListLoader;
import com.hybris.mobile.lib.b2b.loader.ProductLoader;
import com.hybris.mobile.lib.b2b.provider.CatalogContract;
import com.hybris.mobile.lib.b2b.query.QueryProductDetails;
import com.hybris.mobile.lib.b2b.query.QueryProducts;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.b2b.service.OCCServiceHelper;
import com.hybris.mobile.lib.b2b.utils.JsonUtils;
import com.hybris.mobile.lib.http.converter.JsonDataConverter;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.manager.volley.VolleyPersistenceManager;
import com.hybris.mobile.lib.http.response.Response;

import junit.framework.Assert;

import java.lang.reflect.Type;


/**
 * Loader tests
 */
public class LoaderTests extends LoaderTestCase {

    private ContentServiceHelper mContentServiceHelper;

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new Configuration();
        configuration.setBackendUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_backend));
        configuration.setCatalogParameterUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_parameter_catalog));
        configuration.setCatalogPathUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_path_catalog));
        configuration.setCatalogAuthority(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.provider_authority));

        mContentServiceHelper = new OCCServiceHelper(getContext(), configuration, new VolleyPersistenceManager(getContext()), new JsonDataConverter() {

            @Override
            public <T> Type getAssociatedTypeFromClass(Class<T> className) {
                return JsonUtils.getAssociatedTypeFromClass(className);
            }

            @Override
            public String createErrorMessage(String errorMessage, String errorType) {
                return JsonUtils.createErrorMessage(errorMessage, errorType);
            }
        }, false);

        // Insert data in content provider
        for (int i = 0; i < 20; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CatalogContract.DataBaseData.ATT_DATA_ID, i);
            contentValues.put(CatalogContract.DataBaseData.ATT_DATA, "Test data number " + i);
            getContext().getContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriData(configuration.getCatalogAuthority()), i + ""),
                    contentValues);
            getContext().getContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(configuration.getCatalogAuthority()), i + ""),
                    contentValues);

            for (int j = 0; j < 3; j++) {
                contentValues = new ContentValues();
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_DATA_ID, i);
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_GROUP_ID, j);
                getContext().getContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(configuration.getCatalogAuthority()), i + ""),
                        contentValues);
            }
        }
    }

    public void testProductLoader() throws InterruptedException {
        QueryProductDetails queryProductDetails = new QueryProductDetails();
        queryProductDetails.setCode(1 + "");

        ProductLoader productLoader = new ProductLoader(getContext(), mContentServiceHelper, new ResponseReceiver<Product>() {
            @Override
            public void onResponse(Response<Product> response) {
            }

            @Override
            public void onError(Response<DataError> response) {
            }
        }, queryProductDetails, new OnRequestListener() {

            @Override
            public void beforeRequest() {
            }

            @Override
            public void afterRequest(boolean isDataSynced) {
            }
        });

        Cursor cursor = getLoaderResultSynchronously(productLoader.onCreateLoader(0, null));
        Assert.assertTrue(cursor.getCount() > 0);
    }

    public void testProductListLoader() throws InterruptedException {
        QueryProducts queryProducts = new QueryProducts();
        queryProducts.setIdCategory(1 + "");
        ProductListLoader productListLoader = new ProductListLoader(getContext(), mContentServiceHelper, null, queryProducts, null);

        Cursor cursor = getLoaderResultSynchronously(productListLoader.onCreateLoader(0, null));
        Assert.assertTrue(cursor.getCount() > 0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        for (int i = 0; i < 20; i++) {
            getContext().getContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriData(mContentServiceHelper.getConfiguration().getCatalogAuthority()), i + ""), null,
                    null);
            getContext().getContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(mContentServiceHelper.getConfiguration().getCatalogAuthority()), i + ""), null,
                    null);
        }
    }
}
