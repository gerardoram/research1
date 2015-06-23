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

import android.test.AndroidTestCase;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.data.Category;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.query.QueryCategory;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.b2b.service.OCCServiceHelper;
import com.hybris.mobile.lib.b2b.utils.JsonUtils;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.converter.JsonDataConverter;
import com.hybris.mobile.lib.http.manager.volley.VolleyPersistenceManager;
import com.hybris.mobile.lib.http.response.Response;

import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Catalog/Category pojos tests
 */
public class CatalogTests extends AndroidTestCase {

    private final CountDownLatch lock = new CountDownLatch(1);
    private static final int NB_SECONDS_TO_WAIT_ASYNC_FINISH = 60;
    private ContentServiceHelper contentServiceHelper;
    private DataConverter dataConverter;

    // Static block code allowing all SSL connections bypassing the certificates verification
    // See https://developer.android.com/training/articles/security-ssl.html to check the real certificates
    static {

        // Default cookie manager
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Trust all SSL certificates
        TrustManager[] trustAllCerts = new TrustManager[]
                {new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                        return myTrustedAnchors;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
                };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");

            if (sc != null) {
                sc.init(null, trustAllCerts, new SecureRandom());
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        if (sc != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dataConverter = new JsonDataConverter() {

            @Override
            public <T> Type getAssociatedTypeFromClass(Class<T> className) {
                return JsonUtils.getAssociatedTypeFromClass(className);
            }

            @Override
            public String createErrorMessage(String errorMessage, String errorType) {
                return JsonUtils.createErrorMessage(errorMessage, errorType);
            }
        };

        Configuration configuration = new Configuration();
        configuration.setBackendUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_backend));
        configuration.setCatalogParameterUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_parameter_catalog));
        configuration.setCatalogPathUrl(getContext().getString(com.hybris.mobile.lib.b2b.test.R.string.url_path_catalog));

        contentServiceHelper = new OCCServiceHelper(getContext(), configuration, new VolleyPersistenceManager(getContext()), dataConverter, false);
    }

    public void testCreateTreeFromCatalog() throws InterruptedException {

        // Call the API to get the catalog list
        QueryCategory queryCategory = new QueryCategory();
        queryCategory.setId(getContext().getString(R.string.default_category));

        contentServiceHelper.getCategory(new ResponseReceiver<Category>() {

            @Override
            public void onResponse(Response<Category> response) {

                // Setting the parents
                for (Category category : response.getData().getSubcategories()) {
                    category.setParent(null);
                }

                // Verifying that each category has his parent set and correct
                for (Category category : response.getData().getSubcategories()) {
                    verifyCatalogParent(category);
                }

                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();
            }
        }, "test", queryCategory, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    private void verifyCatalogParent(Category category) {
        if (category.getSubcategories() != null) {
            // Verifying the subcategories have the good parent
            for (Category subcategories : category.getSubcategories()) {
                assertTrue(subcategories.getParent().equals(category));
                verifyCatalogParent(subcategories);
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        contentServiceHelper = null;
        dataConverter = null;
    }

}
