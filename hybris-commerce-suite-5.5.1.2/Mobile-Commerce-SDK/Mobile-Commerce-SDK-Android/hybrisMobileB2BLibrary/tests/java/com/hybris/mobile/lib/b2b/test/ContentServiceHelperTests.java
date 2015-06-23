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

import android.content.res.AssetManager;
import android.test.AndroidTestCase;

import com.hybris.mobile.lib.b2b.BuildConfig;
import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.data.Category;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.DeliveryMode;
import com.hybris.mobile.lib.b2b.data.User;
import com.hybris.mobile.lib.b2b.data.cart.Cart;
import com.hybris.mobile.lib.b2b.data.cart.ProductAdded;
import com.hybris.mobile.lib.b2b.data.costcenter.CostCenter;
import com.hybris.mobile.lib.b2b.data.order.Order;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.data.product.ProductList;
import com.hybris.mobile.lib.b2b.data.store.Store;
import com.hybris.mobile.lib.b2b.data.store.StoreList;
import com.hybris.mobile.lib.b2b.query.QueryCart;
import com.hybris.mobile.lib.b2b.query.QueryCategory;
import com.hybris.mobile.lib.b2b.query.QueryFacet;
import com.hybris.mobile.lib.b2b.query.QueryLogin;
import com.hybris.mobile.lib.b2b.query.QueryOrderDetails;
import com.hybris.mobile.lib.b2b.query.QueryOrderHistory;
import com.hybris.mobile.lib.b2b.query.QueryPlaceOrder;
import com.hybris.mobile.lib.b2b.query.QueryProductDetails;
import com.hybris.mobile.lib.b2b.query.QueryProducts;
import com.hybris.mobile.lib.b2b.query.QueryStoreDetails;
import com.hybris.mobile.lib.b2b.query.QueryStores;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.b2b.service.OCCServiceHelper;
import com.hybris.mobile.lib.b2b.utils.JsonUtils;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.converter.JsonDataConverter;
import com.hybris.mobile.lib.http.manager.volley.VolleyPersistenceManager;
import com.hybris.mobile.lib.http.response.Response;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Content service helper tests
 */
public class ContentServiceHelperTests extends AndroidTestCase {
    private final CountDownLatch lock = new CountDownLatch(1);
    private static final int NB_SECONDS_TO_WAIT_ASYNC_FINISH = 120;
    private ContentServiceHelper contentServiceHelper;
    private DataConverter dataConverter;
    private String productCode;
    private String userName;
    private static final String USER = "anthony.lombardi@rustic-hw.com";
    private static final String PASSWORD = "12341234";
    private static final String ACCOUNT = "ACCOUNT";

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

        // Getting the username associated to the host machine in the property file
        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream ims = assetManager.open("unittests-users.properties");

            Properties properties = new Properties();
            properties.load(ims);

            userName = properties.getProperty(BuildConfig.HOSTNAME);

            if (userName == null || userName.length() == 0) {
                userName = properties.getProperty("defaultTestUser");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dataconverter
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

        // Content service helper
        contentServiceHelper = new OCCServiceHelper(getContext(), configuration, new VolleyPersistenceManager(getContext()), dataConverter, false);

        // Get the first product that is not out of stock for the tests
        final CountDownLatch lockGetProduct = new CountDownLatch(1);
        QueryProducts queryProducts = new QueryProducts();
        queryProducts.setCurrentPage(0);
        queryProducts.setPageSize(100);
        queryProducts.setQueryFacets(new ArrayList<QueryFacet>());

        contentServiceHelper.getProducts(new ResponseReceiver<ProductList>() {

            @Override
            public void onResponse(Response<ProductList> response) {
                boolean productFound = false;

                List<Product> products = response.getData().getProducts();
                Iterator<Product> iterator = products.iterator();

                while (!productFound && iterator.hasNext()) {
                    Product product = iterator.next();

                    if (!product.isOutOfStock()) {
                        productCode = product.getCode();
                        productFound = true;
                    }
                }

                assertTrue(productFound);

                lockGetProduct.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                lockGetProduct.countDown();
            }
        }, null, queryProducts, false, null, null);

        lockGetProduct.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testLogin() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();
            }

        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetProducts() throws InterruptedException {
        QueryProducts queryProducts = new QueryProducts();
        queryProducts.setCurrentPage(0);
        queryProducts.setPageSize(20);
        queryProducts.setQueryFacets(new ArrayList<QueryFacet>());

        contentServiceHelper.getProducts(new ResponseReceiver<ProductList>() {

            @Override
            public void onResponse(Response<ProductList> response) {
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();
            }
        }, null, queryProducts, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testSearchProducts() throws InterruptedException {
        QueryProducts queryProducts = new QueryProducts();
        queryProducts.setSearchText("wire");
        queryProducts.setCurrentPage(0);
        queryProducts.setPageSize(20);
        queryProducts.setQueryFacets(new ArrayList<QueryFacet>());

        contentServiceHelper.getProducts(new ResponseReceiver<ProductList>() {

            @Override
            public void onResponse(Response<ProductList> response) {

                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();
            }
        }, null, queryProducts, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetProductDetails() throws InterruptedException {
        QueryProductDetails queryProductDetails = new QueryProductDetails();
        queryProductDetails.setCode(productCode);

        contentServiceHelper.getProductDetails(new ResponseReceiver<Product>() {

            @Override
            public void onResponse(Response<Product> response) {
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryProductDetails, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetCatalog() throws InterruptedException {
        // Call the API to get the catalog list
        QueryCategory queryCategory = new QueryCategory();
        queryCategory.setId(getContext().getString(R.string.default_catalog_main_category));

        contentServiceHelper.getCategory(new ResponseReceiver<Category>() {

            @Override
            public void onResponse(Response<Category> response) {
                assertTrue(true);
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryCategory, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }


    public void testIllegalArgumentException() throws InterruptedException {

        try {
            contentServiceHelper.login(new ResponseReceiver<User>() {

                @Override
                public void onResponse(Response<User> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);

        try {
            contentServiceHelper.getProducts(new ResponseReceiver<ProductList>() {

                @Override
                public void onResponse(Response<ProductList> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        try {
            contentServiceHelper.getProductDetails(new ResponseReceiver<Product>() {

                @Override
                public void onResponse(Response<Product> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        try {
            contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                @Override
                public void onResponse(Response<ProductAdded> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        try {
            contentServiceHelper.updateCartEntry(new ResponseReceiver<ProductAdded>() {

                @Override
                public void onResponse(Response<ProductAdded> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        try {
            contentServiceHelper.deleteCartEntry(new ResponseReceiver<ProductAdded>() {

                @Override
                public void onResponse(Response<ProductAdded> response) {
                }

                @Override
                public void onError(Response<DataError> response) {
                    fail(response.getData().getErrorMessage().getMessage());
                    lock.countDown();

                }
            }, null, null, false, null, null);
        } catch (IllegalArgumentException e) {
            lock.countDown();
            assertTrue(true);
        }

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testAddToCart() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {

            @Override
            public void onResponse(Response<User> response) {
                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {
                    @Override
                    public void onResponse(Response<ProductAdded> response) {
                        lock.countDown();
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }

        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);

    }

    public void testUpdateCartEntry() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {

            @Override
            public void onResponse(Response<User> response) {

                final QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {
                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        queryCart.setProduct("0");

                        contentServiceHelper.updateCartEntry(new ResponseReceiver<ProductAdded>() {
                            @Override
                            public void onResponse(Response<ProductAdded> response) {

                                lock.countDown();
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, queryCart, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }

        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);

    }

    public void testDeleteCartEntry() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {

            @Override
            public void onResponse(Response<User> response) {
                final QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {
                    @Override
                    public void onResponse(Response<ProductAdded> response) {
                        QueryCart queryCartDelete = new QueryCart();
                        queryCartDelete.setProduct("0");

                        contentServiceHelper.deleteCartEntry(new ResponseReceiver<ProductAdded>() {
                            @Override
                            public void onResponse(Response<ProductAdded> response) {

                                lock.countDown();
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, queryCartDelete, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }

        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);

    }

    public void testCreateCart() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {

            @Override
            public void onResponse(Response<User> response) {

                contentServiceHelper.createCart(new ResponseReceiver<Cart>() {
                    @Override
                    public void onResponse(Response<Cart> response) {

                        lock.countDown();
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetCart() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {

            @Override
            public void onResponse(Response<User> response) {


                contentServiceHelper.getCart(new ResponseReceiver<Cart>() {
                    @Override
                    public void onResponse(Response<Cart> response) {

                        lock.countDown();
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }

        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetCostCenters() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {


                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {


                        contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {
                            @Override
                            public void onResponse(Response<List<CostCenter>> response) {

                                lock.countDown();
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testUpdateCostCenter() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {


                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {


                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {

                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {
                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {


                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                lock.countDown();
                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, response.getData().get(0).getCode(), false, null, null);
                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testUpdateCartDeliveryAddress() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {
                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {
                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {

                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {
                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {

                                        final CostCenter costCenter = response.getData().get(0);

                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                contentServiceHelper.updateCartDeliveryAddress(new ResponseReceiver<Cart>() {
                                                    @Override
                                                    public void onResponse(Response<Cart> response) {
                                                        lock.countDown();
                                                    }

                                                    @Override
                                                    public void onError(Response<DataError> response) {
                                                        fail(response.getData().getErrorMessage().getMessage());
                                                        lock.countDown();

                                                    }
                                                }, null, costCenter.getUnit().getAddresses().get(0).getId(), false, null, null);

                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, costCenter.getCode(), false, null, null);
                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);

    }

    public void testGetDeliveryModes() throws InterruptedException {

        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {

                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {

                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {
                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {

                                        final CostCenter costCenter = response.getData().get(0);

                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                contentServiceHelper.updateCartDeliveryAddress(new ResponseReceiver<Cart>() {
                                                    @Override
                                                    public void onResponse(Response<Cart> response) {


                                                        contentServiceHelper.getDeliveryModes(new ResponseReceiver<List<DeliveryMode>>() {

                                                            @Override
                                                            public void onResponse(Response<List<DeliveryMode>> response) {

                                                                lock.countDown();
                                                            }

                                                            @Override
                                                            public void onError(Response<DataError> response) {
                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                lock.countDown();

                                                            }
                                                        }, null, false, null, null);

                                                    }

                                                    @Override
                                                    public void onError(Response<DataError> response) {
                                                        fail(response.getData().getErrorMessage().getMessage());
                                                        lock.countDown();

                                                    }
                                                }, null, costCenter.getUnit().getAddresses().get(0).getId(), false, null, null);

                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, costCenter.getCode(), false, null, null);
                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);


        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testUpdateDeliveryMode() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {

                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {


                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {

                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {

                                        final CostCenter costCenter = response.getData().get(0);

                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                contentServiceHelper.updateCartDeliveryAddress(new ResponseReceiver<Cart>() {
                                                    @Override
                                                    public void onResponse(Response<Cart> response) {


                                                        contentServiceHelper.getDeliveryModes(new ResponseReceiver<List<DeliveryMode>>() {
                                                            @Override
                                                            public void onResponse(Response<List<DeliveryMode>> response) {


                                                                DeliveryMode deliveryMode = response.getData().get(0);

                                                                contentServiceHelper.updateCartDeliveryMode(new ResponseReceiver<Cart>() {
                                                                    @Override
                                                                    public void onResponse(Response<Cart> response) {

                                                                        lock.countDown();
                                                                    }

                                                                    @Override
                                                                    public void onError(Response<DataError> response) {
                                                                        fail(response.getData().getErrorMessage().getMessage());
                                                                        lock.countDown();

                                                                    }
                                                                }, null, deliveryMode.getCode(), false, null, null);

                                                            }

                                                            @Override
                                                            public void onError(Response<DataError> response) {
                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                lock.countDown();

                                                            }
                                                        }, null, false, null, null);

                                                    }

                                                    @Override
                                                    public void onError(Response<DataError> response) {
                                                        fail(response.getData().getErrorMessage().getMessage());
                                                        lock.countDown();

                                                    }
                                                }, null, costCenter.getUnit().getAddresses().get(0).getId(), false, null, null);
                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, costCenter.getCode(), false, null, null);

                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);

                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testUpdatePaymentTypeAccount() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {

                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {

                                lock.countDown();
                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testPlaceOrderWithAccount() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {

                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {


                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {

                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {

                                        final CostCenter costCenter = response.getData().get(0);

                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                contentServiceHelper.updateCartDeliveryAddress(new ResponseReceiver<Cart>() {
                                                    @Override
                                                    public void onResponse(Response<Cart> response) {


                                                        contentServiceHelper.getDeliveryModes(new ResponseReceiver<List<DeliveryMode>>() {
                                                            @Override
                                                            public void onResponse(Response<List<DeliveryMode>> response) {


                                                                DeliveryMode deliveryMode = response.getData().get(0);

                                                                contentServiceHelper.updateCartDeliveryMode(new ResponseReceiver<Cart>() {
                                                                    @Override
                                                                    public void onResponse(Response<Cart> response) {

                                                                        QueryPlaceOrder queryPlaceOrder = new QueryPlaceOrder();
                                                                        queryPlaceOrder.setTermsChecked(true);

                                                                        contentServiceHelper.placeOrder(new ResponseReceiver<Order>() {

                                                                            @Override
                                                                            public void onResponse(Response<Order> response) {

                                                                                lock.countDown();
                                                                            }

                                                                            @Override
                                                                            public void onError(Response<DataError> response) {
                                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                                lock.countDown();

                                                                            }
                                                                        }, null, queryPlaceOrder, false, null, null);

                                                                    }

                                                                    @Override
                                                                    public void onError(Response<DataError> response) {
                                                                        fail(response.getData().getErrorMessage().getMessage());
                                                                        lock.countDown();

                                                                    }
                                                                }, null, deliveryMode.getCode(), false, null, null);

                                                            }

                                                            @Override
                                                            public void onError(Response<DataError> response) {
                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                lock.countDown();

                                                            }
                                                        }, null, false, null, null);

                                                    }

                                                    @Override
                                                    public void onError(Response<DataError> response) {
                                                        fail(response.getData().getErrorMessage().getMessage());
                                                        lock.countDown();

                                                    }
                                                }, null, costCenter.getUnit().getAddresses().get(0).getId(), false, null, null);
                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, costCenter.getCode(), false, null, null);

                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);

                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetOrderHistory() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {
                QueryOrderHistory queryOrderHistory = new QueryOrderHistory();
                queryOrderHistory.setCurrentPage(0);
                queryOrderHistory.setPageSize(5);

                contentServiceHelper.getOrderHistory(new ResponseReceiver<List<Order>>() {
                    @Override
                    public void onResponse(Response<List<Order>> response) {
                        lock.countDown();
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryOrderHistory, false, null, null);
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetOrderDetails() throws InterruptedException {
        QueryLogin queryLogin = new QueryLogin();
        queryLogin.setUsername(userName);
        queryLogin.setPassword(PASSWORD);

        contentServiceHelper.login(new ResponseReceiver<User>() {
            @Override
            public void onResponse(Response<User> response) {

                QueryCart queryCart = new QueryCart();
                queryCart.setProduct(productCode);
                queryCart.setQuantity(5);
                contentServiceHelper.addProductToCart(new ResponseReceiver<ProductAdded>() {

                    @Override
                    public void onResponse(Response<ProductAdded> response) {

                        contentServiceHelper.updateCartPaymentType(new ResponseReceiver<Cart>() {

                            @Override
                            public void onResponse(Response<Cart> response) {


                                contentServiceHelper.getCostCenters(new ResponseReceiver<List<CostCenter>>() {

                                    @Override
                                    public void onResponse(Response<List<CostCenter>> response) {

                                        final CostCenter costCenter = response.getData().get(0);

                                        contentServiceHelper.updateCartCostCenter(new ResponseReceiver<Cart>() {

                                            @Override
                                            public void onResponse(Response<Cart> response) {

                                                contentServiceHelper.updateCartDeliveryAddress(new ResponseReceiver<Cart>() {
                                                    @Override
                                                    public void onResponse(Response<Cart> response) {


                                                        contentServiceHelper.getDeliveryModes(new ResponseReceiver<List<DeliveryMode>>() {
                                                            @Override
                                                            public void onResponse(Response<List<DeliveryMode>> response) {


                                                                DeliveryMode deliveryMode = response.getData().get(0);

                                                                contentServiceHelper.updateCartDeliveryMode(new ResponseReceiver<Cart>() {
                                                                    @Override
                                                                    public void onResponse(Response<Cart> response) {

                                                                        QueryPlaceOrder queryPlaceOrder = new QueryPlaceOrder();
                                                                        queryPlaceOrder.setTermsChecked(true);

                                                                        contentServiceHelper.placeOrder(new ResponseReceiver<Order>() {

                                                                            @Override
                                                                            public void onResponse(Response<Order> response) {

                                                                                Order order = response.getData();
                                                                                QueryOrderDetails queryOrderDetails = new QueryOrderDetails();
                                                                                queryOrderDetails.setCode(order.getCode());

                                                                                contentServiceHelper.getOrder(new ResponseReceiver<Order>() {
                                                                                    @Override
                                                                                    public void onResponse(Response<Order> response) {
                                                                                        lock.countDown();
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(Response<DataError> response) {
                                                                                        fail(response.getData().getErrorMessage().getMessage());
                                                                                        lock.countDown();

                                                                                    }
                                                                                }, null, queryOrderDetails, false, null, null);
                                                                                lock.countDown();
                                                                            }

                                                                            @Override
                                                                            public void onError(Response<DataError> response) {
                                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                                lock.countDown();

                                                                            }
                                                                        }, null, queryPlaceOrder, false, null, null);

                                                                    }

                                                                    @Override
                                                                    public void onError(Response<DataError> response) {
                                                                        fail(response.getData().getErrorMessage().getMessage());
                                                                        lock.countDown();

                                                                    }
                                                                }, null, deliveryMode.getCode(), false, null, null);

                                                            }

                                                            @Override
                                                            public void onError(Response<DataError> response) {
                                                                fail(response.getData().getErrorMessage().getMessage());
                                                                lock.countDown();

                                                            }
                                                        }, null, false, null, null);

                                                    }

                                                    @Override
                                                    public void onError(Response<DataError> response) {
                                                        fail(response.getData().getErrorMessage().getMessage());
                                                        lock.countDown();

                                                    }
                                                }, null, costCenter.getUnit().getAddresses().get(0).getId(), false, null, null);
                                            }

                                            @Override
                                            public void onError(Response<DataError> response) {
                                                fail(response.getData().getErrorMessage().getMessage());
                                                lock.countDown();

                                            }
                                        }, null, costCenter.getCode(), false, null, null);

                                    }

                                    @Override
                                    public void onError(Response<DataError> response) {
                                        fail(response.getData().getErrorMessage().getMessage());
                                        lock.countDown();

                                    }
                                }, null, false, null, null);

                            }

                            @Override
                            public void onError(Response<DataError> response) {
                                fail(response.getData().getErrorMessage().getMessage());
                                lock.countDown();

                            }
                        }, null, ACCOUNT, false, null, null);

                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();

                    }
                }, null, queryCart, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryLogin, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetStores() throws InterruptedException {

        QueryStores queryStores = new QueryStores();

        contentServiceHelper.getStores(new ResponseReceiver<StoreList>() {
            @Override
            public void onResponse(Response<StoreList> response) {
                lock.countDown();
            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryStores, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    public void testGetStoreDetails() throws InterruptedException {

        QueryStores queryStores = new QueryStores();

        contentServiceHelper.getStores(new ResponseReceiver<StoreList>() {
            @Override
            public void onResponse(Response<StoreList> response) {

                QueryStoreDetails queryStoreDetails = new QueryStoreDetails();
                queryStoreDetails.setStoreName(response.getData().getStores().get(0).getName());

                contentServiceHelper.getStoreDetails(new ResponseReceiver<Store>() {
                    @Override
                    public void onResponse(Response<Store> response) {
                        lock.countDown();
                    }

                    @Override
                    public void onError(Response<DataError> response) {
                        fail(response.getData().getErrorMessage().getMessage());
                        lock.countDown();
                    }
                }, null, queryStoreDetails, false, null, null);

            }

            @Override
            public void onError(Response<DataError> response) {
                fail(response.getData().getErrorMessage().getMessage());
                lock.countDown();

            }
        }, null, queryStores, false, null, null);

        lock.await(NB_SECONDS_TO_WAIT_ASYNC_FINISH, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        contentServiceHelper = null;
        dataConverter = null;
    }
}
