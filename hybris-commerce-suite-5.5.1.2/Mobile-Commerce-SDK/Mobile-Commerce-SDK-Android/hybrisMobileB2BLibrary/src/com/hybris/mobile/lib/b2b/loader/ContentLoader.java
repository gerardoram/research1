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

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.provider.CatalogContract;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.http.Constants;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.converter.DataConverter.Helper;
import com.hybris.mobile.lib.http.converter.exception.DataConverterException;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;


/**
 * Generic content loader
 *
 * @param <T>
 */
public abstract class ContentLoader<T> implements LoaderCallbacks<Cursor> {
    private static final String TAG = ContentLoader.class.getCanonicalName();
    protected final ContentServiceHelper mContentServiceHelper;
    private final OnRequestListener mOnRequestListener;
    private final ResponseReceiver<T> mResponseReceiver;
    private final Helper<T, DataError> mDataConverterHelper;
    protected final Context mContext;

    ContentLoader(Context context, ContentServiceHelper contentServiceHelper, ResponseReceiver<T> responseReceiver,
                  DataConverter.Helper<T, DataError> dataConverterHelper, OnRequestListener onRequestListener) {
        mContext = context;
        mContentServiceHelper = contentServiceHelper;
        mResponseReceiver = responseReceiver;
        mDataConverterHelper = dataConverterHelper;
        mOnRequestListener = onRequestListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Before doing the request
        if (mOnRequestListener != null) {
            mOnRequestListener.beforeRequest();
        }

        return new CursorLoader(mContext, getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "Loader finished to load " + data.getCount() + " result(s)");

        boolean isDataSynced = false;

        // Return the response
        if (mResponseReceiver != null && data.getCount() > 0) {

            try {
                isDataSynced = data.getInt(data.getColumnIndex(
                        CatalogContract.DataBaseData.ATT_STATUS)) == CatalogContract.SyncStatus.UPTODATE.getValue();

                mResponseReceiver.onResponse(Response.createResponse(
                        mContentServiceHelper.getDataConverter().convertFrom(mDataConverterHelper.getClassName(),
                                data.getString(data.getColumnIndex(CatalogContract.DataBaseData.ATT_DATA))), null, isDataSynced));
            }
            // Conversion error, we return the response with the error message
            catch (DataConverterException e) {
                try {
                    isDataSynced = data.getInt(data.getColumnIndex(
                            CatalogContract.DataBaseData.ATT_STATUS)) == CatalogContract.SyncStatus.UPTODATE.getValue();

                    mResponseReceiver.onError(Response.createResponse(
                            mContentServiceHelper.getDataConverter().convertFrom(
                                    mDataConverterHelper.getErrorClassName(),
                                    mContentServiceHelper.getDataConverter().createErrorMessage(
                                            data.getString(data.getColumnIndex(CatalogContract.DataBaseData.ATT_DATA)),
                                            Constants.ERROR_TYPE_UNKNOWN)), null, isDataSynced));
                } catch (DataConverterException e1) {
                    throw new IllegalArgumentException(e1.getLocalizedMessage());
                }
            }

        }

        // After doing the request
        if (mOnRequestListener != null) {
            mOnRequestListener.afterRequest(isDataSynced);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Return the URI of the content provider
     *
     * @return String of characters used to identify a name of a resource
     */
    protected abstract Uri getUri();

}
