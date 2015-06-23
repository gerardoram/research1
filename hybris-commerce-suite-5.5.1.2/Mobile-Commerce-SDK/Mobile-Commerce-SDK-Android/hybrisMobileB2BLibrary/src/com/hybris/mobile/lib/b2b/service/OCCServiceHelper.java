/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.lib.b2b.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap.Config;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.Constants;
import com.hybris.mobile.lib.b2b.R;
import com.hybris.mobile.lib.b2b.data.Category;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.DataError.ErrorMessage;
import com.hybris.mobile.lib.b2b.data.DeliveryMode;
import com.hybris.mobile.lib.b2b.data.User;
import com.hybris.mobile.lib.b2b.data.UserInformation;
import com.hybris.mobile.lib.b2b.data.cart.Cart;
import com.hybris.mobile.lib.b2b.data.cart.ProductAdded;
import com.hybris.mobile.lib.b2b.data.costcenter.CostCenter;
import com.hybris.mobile.lib.b2b.data.order.Order;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.data.product.ProductList;
import com.hybris.mobile.lib.b2b.data.store.Store;
import com.hybris.mobile.lib.b2b.data.store.StoreList;
import com.hybris.mobile.lib.b2b.helper.SecurityHelper;
import com.hybris.mobile.lib.b2b.helper.UrlHelper;
import com.hybris.mobile.lib.b2b.provider.CatalogContract;
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
import com.hybris.mobile.lib.http.PersistenceHelper;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.manager.PersistenceManager;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.http.response.ResponseCallback;
import com.hybris.mobile.lib.http.utils.HttpUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * OCC Implementation to retrieve the application data
 */
public class OCCServiceHelper implements ContentServiceHelper {
    private static final String TAG = OCCServiceHelper.class.getCanonicalName();
    private static final Config CONFIG_IMAGES_QUALITY = Config.ALPHA_8;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_AUTHORIZATION_BEARER = "Bearer";
    private static final String TOKEN_REFRESH = "TOKEN_REFRESH";
    private static final String USER_ID = "USER_ID";
    private static final String CATALOG_LAST_SYNC_DATE = "CATALOG_LAST_SYNC_DATE";

    protected UserInformation mUserInformation;
    protected PersistenceHelper mPersistenceHelper;
    protected DataConverter mDataConverter;
    protected Context mContext;
    protected Configuration mConfiguration;

    private enum CartActionEnum {
        ADD, UPDATE, DELETE
    }

    public OCCServiceHelper(Context context, Configuration configuration, PersistenceManager persistenceManager,
                            DataConverter dataConverter, boolean uiRelated) {
        if (context == null || configuration == null || persistenceManager == null || dataConverter == null) {
            throw new IllegalArgumentException();
        }

        this.mContext = context;
        this.mConfiguration = configuration;
        this.mDataConverter = dataConverter;
        this.mPersistenceHelper = new PersistenceHelper(context, persistenceManager, dataConverter, uiRelated);

        // We initiate the user's information with the date previously saved if any
        this.mUserInformation = new UserInformation(SecurityHelper.getStringFromSecureSharedPreferences(getSharedPreferences(),
                USER_ID, ""), SecurityHelper.getStringFromSecureSharedPreferences(getSharedPreferences(), TOKEN_REFRESH, ""));
    }

    /**
     * Update the UserAuthorization object used by authenticated requests
     *
     * @param userInformation contain user session token
     * @param userId          user unique identifier e.i email
     */
    protected void saveUserInformation(UserInformation userInformation, String userId) {
        userInformation.setIssuedOn(Calendar.getInstance().getTimeInMillis());
        this.mUserInformation = userInformation;
        this.mUserInformation.setUserId(userId);

        // Save needed user information in the shared preferences in case the app is restarted
        SecurityHelper.setStringToSecureSharedPreferences(getSharedPreferences(), TOKEN_REFRESH,
                userInformation.getSecureRefreshToken());
        SecurityHelper.setStringToSecureSharedPreferences(getSharedPreferences(), USER_ID, userId);
    }

    /**
     * Delete the user information
     */
    protected void deleteUserInformation() {
        mUserInformation.reset();
        Editor editor = getSharedPreferences().edit();
        editor.remove(TOKEN_REFRESH);
        editor.apply();
    }

    /**
     * Get the shared preferences
     *
     * @return sharedPreferences from the context
     */
    protected SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#login(com.hybris.mobile.lib.http.response.ResponseReceiver,
     * java.lang.String, com.hybris.mobile.lib.b2b.query.QueryLogin, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean login(final ResponseReceiver<User> responseReceiver, final String requestId, final QueryLogin queryLogin,
                         boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener)
            throws IllegalArgumentException {

        if (queryLogin == null || StringUtils.isBlank(queryLogin.getUsername())) {
            throw new IllegalArgumentException();
        }

        // Constructing the parameters map
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("client_secret", "secret");
        parameters.put("client_id", "mobile_android");
        parameters.put("username", queryLogin.getUsername());
        parameters.put("password", queryLogin.getPassword());

        // Constructing the headers map
        Map<String, String> headers = new HashMap<>();
        String authString = "mobile_android:secret";
        String authValue = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", authValue);

        // We want to save the user information before sending back the result
        ResponseReceiver<UserInformation> responseReceiverBeforeCallback = new ResponseReceiver<UserInformation>() {

            @Override
            public void onResponse(Response<UserInformation> response) {
                // Saving the user information for future authorized requests
                saveUserInformation(response.getData(), queryLogin.getUsername());

                User user = new User();
                user.setUid(queryLogin.getUsername());

                Response<User> userResponse = Response.createResponse(user, requestId, true);
                responseReceiver.onResponse(userResponse);
            }

            @Override
            public void onError(Response<DataError> response) {
                responseReceiver.onError(response);
            }
        };

        return execute(responseReceiverBeforeCallback, DataConverter.Helper.build(UserInformation.class, DataError.class, null),
                shouldUseCache, requestId, UrlHelper.getWebserviceTokenUrl(mContext, mConfiguration), parameters, headers, false,
                HttpUtils.HTTP_METHOD_POST, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#refreshToken(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String)
     */
    @Override
    public boolean refreshToken(ResponseReceiver<UserInformation> responseReceiver, String refreshToken)
            throws IllegalArgumentException {
        if (mUserInformation == null || mUserInformation.isTokenExpired() || mUserInformation.isTokenInvalid()) {
            // Constructing the parameters map
            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "refresh_token");
            parameters.put("refresh_token", refreshToken);
            parameters.put("client_id", "mobile_android");
            parameters.put("client_secret", "secret");

            return execute(responseReceiver, DataConverter.Helper.build(UserInformation.class, DataError.class, null), false,
                    null, UrlHelper.getWebserviceTokenUrl(mContext, mConfiguration), parameters, null, false,
                    HttpUtils.HTTP_METHOD_POST, null, null);
        } else if (responseReceiver != null) {
            responseReceiver.onResponse(Response.createResponse(mUserInformation, null, true));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Send a broadcast message for logout
     */
    protected void sendLogoutBroadcast() throws IllegalArgumentException {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(mContext.getString(R.string.intent_action_logout)));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#logout()
     */
    @Override
    public boolean logout() {
        Log.i(TAG, "Logging out");
        deleteUserInformation();
        deleteCatalogData();
        cancelAll();
        return true;
    }

    /**
     * Delete all the catalog data of the content provider
     */
    protected void deleteCatalogData() {
        mContext.getContentResolver().delete(CatalogContract.Provider.getUriData(mConfiguration.getCatalogAuthority()), null, null);
        mContext.getContentResolver().delete(CatalogContract.Provider.getUriDataDetails(mConfiguration.getCatalogAuthority()), null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getCategory(com.hybris.mobile.lib.b2b.utils.ResponseReceiver
     * , java.lang.String, com.hybris.mobile.lib.b2b.query.QueryCategory, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getCategory(ResponseReceiver<Category> responseReceiver, String requestId, QueryCategory queryCategory,
                               boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {
        if (queryCategory == null || StringUtils.isBlank(queryCategory.getId())) {
            throw new IllegalArgumentException();
        }

        return execute(responseReceiver, DataConverter.Helper.build(Category.class, DataError.class, null), shouldUseCache,
                requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_category,
                        mConfiguration.getCatalogParameterUrl(), queryCategory.getId()), null, null, false, HttpUtils.HTTP_METHOD_GET,
                viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getProducts(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, com.hybris.mobile.lib.b2b.query.QueryProducts, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getProducts(ResponseReceiver<ProductList> responseReceiver, String requestId, QueryProducts queryProducts,
                               boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener)
            throws IllegalArgumentException {
        if (queryProducts == null) {
            throw new IllegalArgumentException();
        }

        // Getting the facets from the query object
        StringBuilder query = new StringBuilder();

        // Free text
        if (StringUtils.isNotBlank(queryProducts.getSearchText())) {
            query.append(queryProducts.getSearchText());
        }

        // Facets
        if (queryProducts.getQueryFacets() != null) {
            for (QueryFacet queryFacet : queryProducts.getQueryFacets()) {
                query.append(":").append(queryFacet.getName()).append(":").append(queryFacet.getValue());
            }
        }

        // Constructing the parameters map
        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", query.toString());
        parameters.put("pageSize", queryProducts.getPageSize() + "");
        parameters.put("currentPage", queryProducts.getCurrentPage() + "");

        return execute(responseReceiver, DataConverter.Helper.build(ProductList.class, DataError.class, null), shouldUseCache,
                requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_products,
                        StringUtils.isBlank(queryProducts.getIdCategory()) ? mContext.getString(
                                R.string.default_catalog_main_category) : queryProducts.getIdCategory()), parameters, null,
                false,
                HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getProductDetails(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, com.hybris.mobile.lib.b2b.query.QueryProductDetails, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getProductDetails(ResponseReceiver<Product> responseReceiver, String requestId,
                                     QueryProductDetails queryProductDetails, boolean shouldUseCache, List<View> viewsToDisable,
                                     OnRequestListener onRequestListener) throws IllegalArgumentException {

        if (queryProductDetails == null || StringUtils.isBlank(queryProductDetails.getCode())) {
            throw new IllegalArgumentException();
        }

        return execute(responseReceiver, DataConverter.Helper.build(Product.class, DataError.class, null), shouldUseCache,
                requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_product_details,
                        queryProductDetails.getCode()), null, null, false, HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#addProductToCart(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, com.hybris.mobile.lib.b2b.query.QueryCart, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean addProductToCart(final ResponseReceiver<ProductAdded> responseReceiver, final String requestId,
                                    final QueryCart queryCart, final boolean shouldUseCache, final List<View> viewsToDisable,
                                    final OnRequestListener onRequestListener) throws IllegalArgumentException {
        return addUpdateDeleteProductToCart(CartActionEnum.ADD, responseReceiver, requestId, queryCart, shouldUseCache,
                viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCartEntry(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, com.hybris.mobile.lib.b2b.query.QueryCart, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean updateCartEntry(final ResponseReceiver<ProductAdded> responseReceiver, final String requestId,
                                   final QueryCart queryCart, final boolean shouldUseCache, final List<View> viewsToDisable,
                                   final OnRequestListener onRequestListener) throws IllegalArgumentException {
        return addUpdateDeleteProductToCart(CartActionEnum.UPDATE, responseReceiver, requestId, queryCart, shouldUseCache,
                viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#deleteCartEntry(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, com.hybris.mobile.lib.b2b.query.QueryCart, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean deleteCartEntry(final ResponseReceiver<ProductAdded> responseReceiver, final String requestId,
                                   final QueryCart queryCart, final boolean shouldUseCache, final List<View> viewsToDisable,
                                   final OnRequestListener onRequestListener) throws IllegalArgumentException {
        return addUpdateDeleteProductToCart(CartActionEnum.DELETE, responseReceiver, requestId, queryCart, shouldUseCache,
                viewsToDisable, onRequestListener);
    }

    /**
     * Add, Update, or Delete a product
     *
     * @param cartActionEnum    The action: ADD, UPDATE, DELETE
     * @param responseReceiver  Response callback result
     * @param requestId         Identifier for the call
     * @param queryCart         Parameters needed for the request
     * @param shouldUseCache    Indicator to use cache or not
     * @param viewsToDisable    Views to disable/enable before/after the request
     * @param onRequestListener Its methods will be called when the request is sent
     * @return true if no error in the process of executing this method. Note that this does not mean whether or not the
     * request was a success.
     * @throws IllegalArgumentException
     */
    protected boolean addUpdateDeleteProductToCart(final CartActionEnum cartActionEnum,
                                                   final ResponseReceiver<ProductAdded> responseReceiver, final String requestId, final QueryCart queryCart,
                                                   final boolean shouldUseCache, final List<View> viewsToDisable, final OnRequestListener onRequestListener)
            throws IllegalArgumentException {

        if (queryCart == null || StringUtils.isBlank(queryCart.getProduct())) {
            throw new IllegalArgumentException();
        }

        // For the add and update we need a quantity
        if (!cartActionEnum.equals(CartActionEnum.DELETE) && queryCart.getQuantity() <= 0) {
            throw new IllegalArgumentException();
        }

        // We get the user's cart first if it was not already pulled
        if (StringUtils.isBlank(mUserInformation.getCartId())) {

            ResponseReceiver<Cart> responseReceiverGetCart = new ResponseReceiver<Cart>() {

                @Override
                public void onResponse(Response<Cart> response) {
                    addUpdateDeleteProductToCart(cartActionEnum, responseReceiver, requestId, queryCart, shouldUseCache,
                            viewsToDisable, onRequestListener);
                }

                @Override
                public void onError(Response<DataError> response) {
                    responseReceiver.onError(response);
                }
            };

            return getCart(responseReceiverGetCart, requestId, shouldUseCache, viewsToDisable, onRequestListener);
        } else {

            // Intermediate receiver to handle errors
            ResponseReceiver<ProductAdded> responseReceiverCheckErrors = new ResponseReceiver<ProductAdded>() {

                @Override
                public void onResponse(Response<ProductAdded> response) {
                    responseReceiver.onResponse(response);
                }

                @Override
                public void onError(Response<DataError> response) {
                    // Cart not found error, probably because the cart had been checked out from another endpoint
                    if (StringUtils.equals(response.getData().getErrorMessage().getReason(),
                            Constants.ERROR_REASON_CART_NOT_FOUND) && StringUtils.equals(
                            response.getData().getErrorMessage().getType(), Constants.ERROR_TYPE_CART_ERROR)) {
                        // We reset the cart and re-call the method
                        mUserInformation.setCartId(null);
                        addUpdateDeleteProductToCart(cartActionEnum, responseReceiver, requestId, queryCart, shouldUseCache,
                                viewsToDisable, onRequestListener);
                    } else {
                        responseReceiver.onError(response);
                    }
                }
            };

            boolean returnResult = true;

            // Constructing the parameters map
            final Map<String, String> parameters = new HashMap<>();

            switch (cartActionEnum) {
                case ADD:
                    parameters.put("product", queryCart.getProduct());
                    parameters.put("quantity", queryCart.getQuantity() + "");

                    returnResult = execute(responseReceiverCheckErrors, DataConverter.Helper.build(ProductAdded.class,
                                    DataError.class, null), shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl
                                    (mContext,
                                            mConfiguration, R.string.path_add_to_cart, mUserInformation.getUserId(),
                                            mUserInformation.getCartId()), parameters, null, true, HttpUtils.HTTP_METHOD_POST,
                            viewsToDisable, onRequestListener);
                    break;

                case UPDATE:
                    parameters.put("quantity", queryCart.getQuantity() + "");

                    returnResult = execute(responseReceiverCheckErrors, DataConverter.Helper.build(ProductAdded.class,
                                    DataError.class, null), shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl
                                    (mContext,
                                            mConfiguration, R.string.path_update_delete_cart_entry, mUserInformation.getUserId(),
                                            mUserInformation.getCartId(), queryCart.getProduct()), parameters, null, true,
                            HttpUtils.HTTP_METHOD_PUT, viewsToDisable, onRequestListener);
                    break;

                case DELETE:
                    returnResult = execute(responseReceiverCheckErrors, DataConverter.Helper.build(ProductAdded.class,
                                    DataError.class, null), shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl
                                    (mContext,
                                            mConfiguration, R.string.path_update_delete_cart_entry, mUserInformation.getUserId(),
                                            mUserInformation.getCartId(), queryCart.getProduct()), null, null, true,
                            HttpUtils.HTTP_METHOD_DELETE, viewsToDisable, onRequestListener);
                    break;

                default:
                    break;
            }

            return returnResult;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#createCart(com.hybris.mobile.lib.http.response.ResponseReceiver
     * , java.lang.String, boolean, java.util.List, com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean createCart(ResponseReceiver<Cart> responseReceiver, String requestId, boolean shouldUseCache,
                              List<View> viewsToDisable, OnRequestListener onRequestListener) {
        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache,
                requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_carts, mUserInformation.getUserId()),
                null, null, true, HttpUtils.HTTP_METHOD_POST, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getCart(com.hybris.mobile.lib.http.response.ResponseReceiver
     * , java.lang.String, boolean, java.util.List, com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getCart(final ResponseReceiver<Cart> responseReceiver, final String requestId, final boolean shouldUseCache,
                           final List<View> viewsToDisable, final OnRequestListener onRequestListener) {

        // If a cart already exists for the user, we retrieve if
        if (StringUtils.isNotBlank(mUserInformation.getCartId())) {
            ResponseReceiver<Cart> responseReceiverGetCart = new ResponseReceiver<Cart>() {

                @Override
                public void onResponse(Response<Cart> response) {
                    responseReceiver.onResponse(response);
                }

                @Override
                public void onError(Response<DataError> response) {
                    // Error with the cart, we delete the reference and we re-call the method to get an updated cart
                    mUserInformation.setCartId(null);

                    // Re-calling the method to get an updated cart
                    getCart(responseReceiver, requestId, shouldUseCache, viewsToDisable, onRequestListener);
                }
            };

            // Getting the cart saved on the user information
            return execute(responseReceiverGetCart, DataConverter.Helper.build(Cart.class, DataError.class, null),
                    shouldUseCache,
                    requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_cart,
                            mUserInformation.getUserId(), mUserInformation.getCartId()), null, null, true,
                    HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
        }
        // No saved cart, we get the user's current cart if any of or we create a default one
        else {

            ResponseReceiver<Cart> responseReceiverGetCurrentCart = new ResponseReceiver<Cart>() {

                @Override
                public void onResponse(Response<Cart> response) {
                    // Saving the cart information and re-calling the method to get the cart from the user information
                    mUserInformation.setCartId(response.getData().getCode());
                    getCart(responseReceiver, requestId, shouldUseCache, viewsToDisable, onRequestListener);
                }

                @Override
                public void onError(Response<DataError> response) {
                    ResponseReceiver<Cart> responseReceiverCreateCart = new ResponseReceiver<Cart>() {

                        @Override
                        public void onResponse(Response<Cart> response) {
                            // Saving the cart information and re-calling the method to get the cart from the user information
                            mUserInformation.setCartId(response.getData().getCode());
                            getCart(responseReceiver, requestId, shouldUseCache, viewsToDisable, onRequestListener);
                        }

                        @Override
                        public void onError(Response<DataError> response) {
                            responseReceiver.onError(response);
                        }
                    };

                    createCart(responseReceiverCreateCart, requestId, shouldUseCache, viewsToDisable, onRequestListener);
                }
            };

            // We retrieve the user's current cart
            return getCurrentCart(responseReceiverGetCurrentCart, shouldUseCache);
        }
    }

    /**
     * Get the user's current cart
     *
     * @param responseReceiver Response callback result
     * @param shouldUseCache   Indicator to use cache or not
     * @return true if request is executed else false
     */
    protected boolean getCurrentCart(ResponseReceiver<Cart> responseReceiver, boolean shouldUseCache) {
        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache, null,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_carts_current,
                        mUserInformation.getUserId()), null, null, true, HttpUtils.HTTP_METHOD_GET, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getDeliveryModes(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getDeliveryModes(ResponseReceiver<List<DeliveryMode>> responseReceiver, String requestId,
                                    boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {
        return executeForList(responseReceiver, DataConverter.Helper.build(DeliveryMode.class, DataError.class, "deliveryModes"),
                shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration,
                        R.string.path_delivery_modes, mUserInformation.getUserId(), mUserInformation.getCartId()), null, null,
                true, HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCartDeliveryMode(com.hybris.mobile.lib.http.response
     * .ResponseReceiver, java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean updateCartDeliveryMode(ResponseReceiver<Cart> responseReceiver, String requestId, String deliveryMode,
                                          boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {

        if (StringUtils.isBlank(deliveryMode)) {
            throw new IllegalArgumentException();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("deliveryModeId", deliveryMode);

        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache,
                requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_cart_delivery_mode,
                        mUserInformation.getUserId(), mUserInformation.getCartId()), parameters, null, true,
                HttpUtils.HTTP_METHOD_PUT, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getCostCenters(com.hybris.mobile.lib.http.response.
     * ResponseReceiver, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getCostCenters(ResponseReceiver<List<CostCenter>> responseReceiver, String requestId, boolean shouldUseCache,
                                  List<View> viewsToDisable, OnRequestListener onRequestListener) {
        return executeForList(responseReceiver, DataConverter.Helper.build(CostCenter.class, DataError.class, "costCenters"),
                shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration,
                        R.string.path_cost_center),
                null, null, true, HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCartCostCenter(com.hybris.mobile.lib.http.response
     * .ResponseReceiver, java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean updateCartCostCenter(ResponseReceiver<Cart> responseReceiver, String requestId, String costCenterId,
                                        boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {

        if (StringUtils.isBlank(costCenterId)) {
            throw new IllegalArgumentException();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("costCenterId", costCenterId);

        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache,
                requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_cart_cost_center,
                        mUserInformation.getUserId(), mUserInformation.getCartId()), parameters, null, true,
                HttpUtils.HTTP_METHOD_PUT, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCartPaymentType(com.hybris.mobile.lib.http.response
     * .ResponseReceiver, java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean updateCartPaymentType(ResponseReceiver<Cart> responseReceiver, String requestId, String paymentType,
                                         boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {
        if (StringUtils.isBlank(paymentType)) {
            throw new IllegalArgumentException();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("paymentType", paymentType);

        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache,
                requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_cart_payment_type,
                        mUserInformation.getUserId(), mUserInformation.getCartId()), parameters, null, true,
                HttpUtils.HTTP_METHOD_PUT, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCartDeliveryAddress(com.hybris.mobile.lib.http.response
     * .ResponseReceiver, java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean updateCartDeliveryAddress(ResponseReceiver<Cart> responseReceiver, String requestId, String addressId,
                                             boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(addressId)) {
            throw new IllegalArgumentException();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("addressId", addressId);

        return execute(responseReceiver, DataConverter.Helper.build(Cart.class, DataError.class, null), shouldUseCache,
                requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_cart_delivery_address,
                        mUserInformation.getUserId(), mUserInformation.getCartId()), parameters, null, true,
                HttpUtils.HTTP_METHOD_PUT, viewsToDisable, onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#placeOrder(com.hybris.mobile.lib.http.response.ResponseReceiver
     * , java.lang.String, boolean, java.util.List, com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean placeOrder(final ResponseReceiver<Order> responseReceiver, String requestId, QueryPlaceOrder queryPlaceOrder,
                              boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener)
            throws IllegalArgumentException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cartId", mUserInformation.getCartId());
        parameters.put("termsChecked", String.valueOf(queryPlaceOrder.isTermsChecked()));

        //  We want to remove the  current cart reference before sending back the result
        ResponseReceiver<Order> responseReceiverPlaceOrder = new ResponseReceiver<Order>() {

            @Override
            public void onResponse(Response<Order> response) {
                mUserInformation.setCartId(null);
                responseReceiver.onResponse(response);
            }

            @Override
            public void onError(Response<DataError> response) {
                responseReceiver.onError(response);
            }
        };

        return execute(responseReceiverPlaceOrder, DataConverter.Helper.build(Order.class, DataError.class, null),
                shouldUseCache,
                requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_place_order,
                        mUserInformation.getUserId()), parameters, null, true, HttpUtils.HTTP_METHOD_POST, viewsToDisable,
                onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getOrder(com.hybris.mobile.lib.http.response.ResponseReceiver
     * , java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getOrder(ResponseReceiver<Order> responseReceiver, String requestId, QueryOrderDetails queryOrderDetails,
                            boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {

        if (StringUtils.isBlank(queryOrderDetails.getCode())) {
            throw new IllegalArgumentException();
        }

        return execute(responseReceiver, DataConverter.Helper.build(Order.class, DataError.class, null), shouldUseCache,
                requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_order_details,
                        mUserInformation.getUserId(), queryOrderDetails.getCode()), null, null, true, HttpUtils.HTTP_METHOD_GET,
                viewsToDisable, onRequestListener);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getOrderHistory(com.hybris.mobile.lib.http.response.
     * ResponseReceiver , java.lang.String, java.lang.String, boolean, java.util.List,
     * com.hybris.mobile.lib.http.listener.OnRequestListener)
     */
    @Override
    public boolean getOrderHistory(ResponseReceiver<List<Order>> responseReceiver, String requestId,
                                   QueryOrderHistory queryOrderHistory, boolean shouldUseCache, List<View> viewsToDisable,
                                   OnRequestListener onRequestListener) {
        // Constructing the parameters map
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pageSize", queryOrderHistory.getPageSize() + "");
        parameters.put("currentPage", queryOrderHistory.getCurrentPage() + "");

        return executeForList(responseReceiver, DataConverter.Helper.build(Order.class, DataError.class, "orders"),
                shouldUseCache, requestId, UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration,
                        R.string.path_order_history, mUserInformation.getUserId()), parameters, null, true, HttpUtils.HTTP_METHOD_GET,
                viewsToDisable, onRequestListener);
    }


    @Override
    public boolean getStores(ResponseReceiver<StoreList> responseReceiver, String requestId, QueryStores queryStores,
                             boolean shouldUseCache, List<View> viewsToDisable, OnRequestListener onRequestListener) {

        // Constructing the parameters map
        Map<String, String> parameters = new HashMap<>();

        if (queryStores != null) {
            // Free text search
            if (StringUtils.isNotBlank(queryStores.getSearchText())) {
                parameters.put("query", queryStores.getSearchText());
            }

            // Search based on location
            QueryStores.Location location = queryStores.getLocation();
            if (location != null) {
                parameters.put("longitude", location.getLongitude() + "");
                parameters.put("latitude", location.getLatitude() + "");
                parameters.put("radius", location.getRadius() + "");
                parameters.put("accuracy", location.getAccuracy() + "");
            }
        }

        // Pagination
        if (queryStores != null) {
            parameters.put("pageSize", queryStores.getPageSize() + "");
            parameters.put("currentPage", queryStores.getCurrentPage() + "");
        }

        return execute(responseReceiver, DataConverter.Helper.build(StoreList.class, DataError.class), shouldUseCache, requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_stores), parameters, null, false,
                HttpUtils.HTTP_METHOD_GET, viewsToDisable, onRequestListener);
    }

    @Override
    public boolean getStoreDetails(ResponseReceiver<Store> responseReceiver, String requestId,
                                   QueryStoreDetails queryStoreDetails, boolean shouldUseCache, List<View> viewsToDisable,
                                   OnRequestListener onRequestListener) {

        if (queryStoreDetails == null || StringUtils.isBlank(queryStoreDetails.getStoreName())) {
            throw new IllegalArgumentException();
        }

        return execute(responseReceiver, DataConverter.Helper.build(Store.class, DataError.class), shouldUseCache, requestId,
                UrlHelper.getWebserviceCatalogUrl(mContext, mConfiguration, R.string.path_store_details, HttpUtils.encode(
                        queryStoreDetails.getStoreName())), null, null, false, HttpUtils.HTTP_METHOD_GET, viewsToDisable,
                onRequestListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#loadImage(java.lang.String, java.lang.String,
     * android.widget.ImageView, int, int, boolean, com.hybris.mobile.lib.http.listener.OnRequestListener, boolean)
     */
    @Override
    public boolean loadImage(String url, String requestId, ImageView imageView, int width, int height, boolean shouldUseCache,
                             OnRequestListener onRequestListener, boolean forceImageTagToMatchRequestId) throws IllegalArgumentException {

        if (StringUtils.isBlank(url) || imageView == null) {
            throw new IllegalArgumentException();
        }

        // We set the image tag if we want the request id to match the tag before loading the image within the image view
        if (forceImageTagToMatchRequestId) {
            imageView.setTag(requestId);
        }

        return mPersistenceHelper.setImageFromUrl(UrlHelper.getImageUrl(mContext, mConfiguration, url), requestId, imageView,
                width, height, CONFIG_IMAGES_QUALITY, shouldUseCache, onRequestListener, forceImageTagToMatchRequestId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#cancel(java.lang.String)
     */
    @Override
    public void cancel(String requestId) {
        mPersistenceHelper.cancel(requestId);
    }

    @Override
    public void cancelAll() {
        mPersistenceHelper.cancelAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#pause()
     */
    @Override
    public void pause() {
        mPersistenceHelper.pause();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#start()
     */
    @Override
    public void start() {
        mPersistenceHelper.start();
    }

    /**
     * Execute the request for a generic T response
     *
     * @param responseReceiver    Response callback result
     * @param dataConverterHelper Helper to convert the result into a POJO
     * @param getCachedResult     Indicator to use cache or not
     * @param requestId           Identifier for the call
     * @param url                 Url to call
     * @param parameters          Call parameters
     * @param headers             Call parameters headers
     * @param isAuthorizedRequest Flag for calls that need the user token
     * @param httpMethod          Http method: GET, POST, PUT, DELETE
     * @param viewsToDisable      List of views to disable/enable before/after the call
     * @param onRequestListener   Request listener for before/after call actions
     * @return true if request is executed else false
     */
    protected <T, Z> boolean execute(final ResponseCallback<T, Z> responseReceiver,
                                     final DataConverter.Helper<T, Z> dataConverterHelper, boolean getCachedResult, final String requestId, String url,
                                     Map<String, String> parameters, Map<String, String> headers, boolean isAuthorizedRequest, String httpMethod,
                                     List<View> viewsToDisable, OnRequestListener onRequestListener) {
        return executeRequest(responseReceiver, null, dataConverterHelper, getCachedResult, requestId, url, parameters, headers,
                isAuthorizedRequest, httpMethod, viewsToDisable, onRequestListener);
    }

    /**
     * Execute the request for a generic List<T> response
     *
     * @param responseReceiver    Response callback result
     * @param dataConverterHelper Helper to convert the result into a POJO
     * @param getCachedResult     Indicator to use cache or not
     * @param requestId           Identifier for the call
     * @param url                 Url to call
     * @param parameters          Call parameters
     * @param headers             Call parameters headers
     * @param isAuthorizedRequest Flag for calls that need the user token
     * @param httpMethod          Http method: GET, POST, PUT, DELETE
     * @param viewsToDisable      List of views to disable/enable before/after the call
     * @param onRequestListener   Request listener for before/after call actions
     * @return true if request is executed else false
     */
    protected <T, Z> boolean executeForList(final ResponseCallback<List<T>, Z> responseReceiver,
                                            final DataConverter.Helper<T, Z> dataConverterHelper, boolean getCachedResult, final String requestId, String url,
                                            Map<String, String> parameters, Map<String, String> headers, boolean isAuthorizedRequest, String httpMethod,
                                            List<View> viewsToDisable, OnRequestListener onRequestListener) {
        return executeRequest(null, responseReceiver, dataConverterHelper, getCachedResult, requestId, url, parameters, headers,
                isAuthorizedRequest, httpMethod, viewsToDisable, onRequestListener);
    }

    /**
     * Execute a request
     *
     * @param responseReceiver     Response callback result
     * @param responseReceiverList Response callback result (list case)
     * @param dataConverterHelper  Helper to convert the result into a POJO
     * @param getCachedResult      Indicator to use cache or not
     * @param requestId            Identifier for the call
     * @param url                  Url to call
     * @param parameters           Call parameters
     * @param headers              Call parameters headers
     * @param isAuthorizedRequest  Flag for calls that need the user token
     * @param httpMethod           Http method: GET, POST, PUT, DELETE
     * @param viewsToDisable       List of views to disable/enable before/after the call
     * @param onRequestListener    Request listener for before/after call actions
     * @return true if request is executed else false
     */
    protected <T, Z> boolean executeRequest(final ResponseCallback<T, Z> responseReceiver,
                                            final ResponseCallback<List<T>, Z> responseReceiverList, final DataConverter.Helper<T, Z> dataConverterHelper,
                                            final boolean getCachedResult, final String requestId, final String url, final Map<String, String> parameters,
                                            final Map<String, String> headers, final boolean isAuthorizedRequest, final String httpMethod,
                                            final List<View> viewsToDisable, final OnRequestListener onRequestListener) {
        boolean refreshTokenNeeded = false;
        final Map<String, String> finalHeader = new HashMap<>();

        // We initialize the header Map
        if (headers != null) {
            finalHeader.putAll(headers);
        }

        // We pass the access token for authorized requests
        if (isAuthorizedRequest) {

            // The token is expired, we refresh it
            if (mUserInformation.isTokenExpired() || mUserInformation.isTokenInvalid()) {

                refreshTokenNeeded = true;

                // No refresh token, we send a logout message
                if (StringUtils.isBlank(mUserInformation.getSecureRefreshToken())) {
                    Log.e(TAG, "Refresh token empty");
                    sendLogoutBroadcast();
                }
                // We refresh the token
                else {
                    refreshToken(new ResponseReceiver<UserInformation>() {

                        @Override
                        public void onResponse(Response<UserInformation> response) {

                            // Getting the new token
                            String savedUserId = mUserInformation.getUserId();
                            String savedCartId = mUserInformation.getCartId();

                            mUserInformation = response.getData();
                            mUserInformation.setIssuedOn(Calendar.getInstance().getTimeInMillis());
                            mUserInformation.setUserId(savedUserId);
                            mUserInformation.setCartId(savedCartId);

                            executeRequest(responseReceiver, responseReceiverList, dataConverterHelper, getCachedResult,
                                    requestId, url, parameters, finalHeader, isAuthorizedRequest, httpMethod, viewsToDisable,
                                    onRequestListener);
                        }

                        @Override
                        public void onError(Response<DataError> response) {
                            Log.e(TAG, "Error refreshing the user token. Details:" + response.getData().getErrorMessage()
                                    .getMessage());
                            sendLogoutBroadcast();
                        }
                    }, mUserInformation.getSecureRefreshToken());
                }
            } else {
                finalHeader.put(HEADER_AUTHORIZATION,
                        HEADER_AUTHORIZATION_BEARER + " " + mUserInformation.getSecureAccessToken());
            }
        }

        if (!refreshTokenNeeded) {

            // Before doing the request
            if (onRequestListener != null) {
                onRequestListener.beforeRequest();
            }

            // Disabling the views before the call
            if (viewsToDisable != null) {
                for (View view : viewsToDisable) {
                    view.setEnabled(false);
                    view.setActivated(false);
                }
            }

            // Generic T case
            if (responseReceiver != null) {
                ResponseCallback<T, Z> responseReceiverActionsBeforeCallback = new ResponseCallback<T, Z>() {

                    @Override
                    public void onResponse(Response<T> dataResponse) {
                        afterRequestActions(responseReceiver, responseReceiverList, dataResponse, null, null,
                                dataConverterHelper,
                                getCachedResult, requestId, url, parameters, finalHeader, isAuthorizedRequest, httpMethod,
                                viewsToDisable, onRequestListener);
                    }

                    @Override
                    public void onError(Response<Z> response) {
                        afterRequestActions(responseReceiver, responseReceiverList, null, null, response, dataConverterHelper,
                                getCachedResult, requestId, url, parameters, finalHeader, isAuthorizedRequest, httpMethod,
                                viewsToDisable, onRequestListener);
                    }
                };

                return mPersistenceHelper.execute(responseReceiverActionsBeforeCallback, dataConverterHelper, getCachedResult,
                        requestId, url, parameters, finalHeader, httpMethod);
            }
            // Generic List<T> case
            else {
                ResponseCallback<List<T>, Z> responseReceiverActionsBeforeCallback = new ResponseCallback<List<T>, Z>() {

                    @Override
                    public void onResponse(Response<List<T>> dataResponse) {
                        afterRequestActions(null, responseReceiverList, null, dataResponse, null,
                                dataConverterHelper,
                                getCachedResult, requestId, url, parameters, finalHeader, isAuthorizedRequest, httpMethod,
                                viewsToDisable, onRequestListener);
                    }

                    @Override
                    public void onError(Response<Z> response) {
                        afterRequestActions(null, responseReceiverList, null, null, response, dataConverterHelper,
                                getCachedResult, requestId, url, parameters, finalHeader, isAuthorizedRequest, httpMethod,
                                viewsToDisable, onRequestListener);
                    }
                };

                return mPersistenceHelper.executeForList(responseReceiverActionsBeforeCallback, dataConverterHelper,
                        getCachedResult, requestId, url, parameters, finalHeader, httpMethod);
            }
        } else {
            return false;
        }
    }

    /**
     * Actions for after request
     *
     * @param responseReceiver     Response callback result
     * @param responseReceiverList Response callback result (list case)
     * @param dataResponse         The response to return
     * @param dataResponseList     The response to return (list case)
     * @param dataConverterHelper  Helper to convert the result into a POJO
     * @param getCachedResult      Indicator to use cache or not
     * @param requestId            Identifier for the call
     * @param url                  Url to call
     * @param parameters           Call parameters
     * @param headers              Call parameters headers
     * @param isAuthorizedRequest  Flag for calls that need the user token
     * @param httpMethod           Http method: GET, POST, PUT, DELETE
     * @param viewsToDisable       List of views to disable/enable before/after the call
     * @param onRequestListener    Request listener for before/after call actions
     */
    protected <T, Z> void afterRequestActions(final ResponseCallback<T, Z> responseReceiver,
                                              final ResponseCallback<List<T>, Z> responseReceiverList, Response<T> dataResponse,
                                              Response<List<T>> dataResponseList,
                                              Response<Z> dataResponseError, final DataConverter.Helper<T, Z> dataConverterHelper, final boolean getCachedResult,
                                              final String requestId, final String url, final Map<String, String> parameters, final Map<String, String> headers,
                                              final boolean isAuthorizedRequest, final String httpMethod, final List<View> viewsToDisable,
                                              final OnRequestListener onRequestListener) {
        boolean refreshTokenNeeded = false;

        // Checking if some error occured
        if (dataResponseError != null && dataResponseError.getData() != null && dataResponseError.getData() instanceof DataError) {
            ErrorMessage error = ((DataError) dataResponseError.getData()).getErrorMessage();

            // Token not valid
            refreshTokenNeeded = error != null && (StringUtils.equals(error.getType(),
                    Constants.ERROR_TYPE_INVALIDTOKENERROR) || (StringUtils.equals(error.getType(),
                    Constants.ERROR_TYPE_UNAUTHORIZEDERROR)));
        }

        // We need to refresh the token so we re-send the request by invalidating the token for the user
        if (refreshTokenNeeded) {
            // Specific case when the app has already logged out the user and the response comes,
            // mUserInformation is null and we don't do anything
            mUserInformation.setTokenInvalid(true);
            executeRequest(responseReceiver, responseReceiverList, dataConverterHelper, getCachedResult, requestId, url,
                    parameters, headers, isAuthorizedRequest, httpMethod, viewsToDisable, onRequestListener);
        } else {
            if (viewsToDisable != null) {
                for (View view : viewsToDisable) {
                    view.setEnabled(true);
                    view.setActivated(true);
                }
            }

            boolean isDataSynced = false;

            // Generic T case
            if (responseReceiver != null) {

                if (dataResponseError != null) {
                    responseReceiver.onError(dataResponseError);
                    isDataSynced = dataResponseError.isSync();
                } else {
                    responseReceiver.onResponse(dataResponse);
                    isDataSynced = dataResponse.isSync();
                }
            }
            // Generic List<T> case
            else if (responseReceiverList != null) {
                if (dataResponseError != null) {
                    responseReceiverList.onError(dataResponseError);
                    isDataSynced = dataResponseError.isSync();
                } else {
                    responseReceiverList.onResponse(dataResponseList);
                    isDataSynced = dataResponseList.isSync();
                }
            }

            // After doing the request
            if (onRequestListener != null) {
                onRequestListener.afterRequest(isDataSynced);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getDataConverter()
     */
    @Override
    public DataConverter getDataConverter() {
        return mDataConverter;
    }

    @Override
    public Configuration getConfiguration() {
        return mConfiguration;
    }

    /*
         * (non-Javadoc)
         *
         * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#updateCache()
         */
    @Override
    public void updateCache() {
        mPersistenceHelper.updateCache();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#getCatalogLastSyncDate()
     */
    @Override
    public long getCatalogLastSyncDate() {
        return getSharedPreferences().getLong(CATALOG_LAST_SYNC_DATE, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hybris.mobile.lib.b2b.service.ContentServiceHelper#saveCatalogLastSyncDate(long)
     */
    @Override
    public void saveCatalogLastSyncDate(long timeInMillis) {
        Editor editorSharedPreferences = getSharedPreferences().edit();
        editorSharedPreferences.putLong(CATALOG_LAST_SYNC_DATE, timeInMillis);
        editorSharedPreferences.apply();
    }

    @Override
    public void updateUrl(String url) {
        this.mConfiguration.setBackendUrl(url);
    }

}
