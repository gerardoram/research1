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

package com.hybris.mobile.lib.b2b.loader;

import android.content.Context;
import android.net.Uri;

import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.provider.CatalogContract;
import com.hybris.mobile.lib.b2b.query.QueryProductDetails;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.listener.OnRequestListener;

import org.apache.commons.lang3.StringUtils;


/**
 * Loader for product
 */
public class ProductLoader extends ContentLoader<Product> {

    private QueryProductDetails mQueryProductDetails;

    public ProductLoader(Context context, ContentServiceHelper contentServiceHelper, ResponseReceiver<Product> responseReceiver,
                         QueryProductDetails queryProductDetails, OnRequestListener onRequestListener) {
        super(context, contentServiceHelper, responseReceiver, DataConverter.Helper.build(Product.class, DataError.class, null),
                onRequestListener);

        if (queryProductDetails == null || StringUtils.isBlank(queryProductDetails.getCode())) {
            throw new IllegalArgumentException();
        }

        updateQuery(queryProductDetails);
    }

    public void updateQuery(QueryProductDetails queryProductDetails) {
        mQueryProductDetails = queryProductDetails;
    }

    @Override
    public Uri getUri() {
        return Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(mContentServiceHelper.getConfiguration().getCatalogAuthority()), mQueryProductDetails.getCode());
    }

}
