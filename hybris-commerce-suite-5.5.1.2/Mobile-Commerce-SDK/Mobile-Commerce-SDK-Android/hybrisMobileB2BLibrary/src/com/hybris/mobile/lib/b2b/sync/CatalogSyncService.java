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

package com.hybris.mobile.lib.b2b.sync;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hybris.mobile.lib.b2b.Configuration;
import com.hybris.mobile.lib.b2b.R;
import com.hybris.mobile.lib.b2b.provider.CatalogProvider;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelperBuilder;

import org.apache.commons.lang3.StringUtils;


/**
 * Service to init and bind the catalog sync adapter
 */
public class CatalogSyncService extends Service {
    private static final String TAG = CatalogSyncService.class.getCanonicalName();
    private static CatalogSyncAdapter mSyncAdapter = null;
    private static final Object mSingletonLock = new Object();

    @Override
    public void onCreate() {
        Log.i(TAG, "Service for the CatalogSyncAdapter starting");

        // The sync adapter is created as a singleton
        synchronized (mSingletonLock) {
            if (mSyncAdapter == null) {

                try {
                    ComponentName componentName = new ComponentName(this, this.getClass());
                    Bundle bundle = getPackageManager().getServiceInfo(componentName, PackageManager.GET_META_DATA).metaData;

                    String urlBackend = bundle.getString(getApplicationContext().getString(R.string.sync_url_backend_metadata_name));
                    String urlParameterCatalogMetadataName = bundle.getString(getApplicationContext().getString(R.string.sync_url_parameter_catalog_metadata_name));
                    String urlPathCatalogMetadataName = bundle.getString(getApplicationContext().getString(R.string.sync_url_path_catalog_metadata_name));

                    if (StringUtils.isBlank(urlBackend) || StringUtils.isBlank(urlBackend) || StringUtils.isBlank(urlBackend)) {
                        throw new IllegalArgumentException("You must provide the metadata " + getApplicationContext().getString(R.string.sync_url_backend_metadata_name) + ", " + getApplicationContext().getString(R.string.sync_url_parameter_catalog_metadata_name) + ", " + getApplicationContext().getString(R.string.sync_url_path_catalog_metadata_name) + " for " + this.getClass());
                    }

                    Configuration configuration = new Configuration();
                    configuration.setBackendUrl(urlBackend);
                    configuration.setCatalogPathUrl(urlPathCatalogMetadataName);
                    configuration.setCatalogParameterUrl(urlParameterCatalogMetadataName);

                    // Provider authority
                    String provider_authority = getString(R.string.sync_default_authority);

                    try {
                        componentName = new ComponentName(this, CatalogProvider.class);

                        // Authority name from the manifest file
                        String tmpAuthority = getPackageManager().getProviderInfo(componentName, PackageManager.GET_META_DATA).authority;

                        if (StringUtils.isNotBlank(tmpAuthority)) {
                            provider_authority = tmpAuthority;
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "Package name not found. Details:" + e.getLocalizedMessage());
                    }

                    configuration.setCatalogAuthority(provider_authority);

                    // Create the sync adapter
                    mSyncAdapter = new CatalogSyncAdapter(getApplicationContext(), true,
                            ContentServiceHelperBuilder.build(getApplicationContext(), configuration, false));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Package name not found. Details:" + e.getLocalizedMessage());
                    throw new IllegalArgumentException("Error getting the information from the metadata of " + this.getClass());
                }

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Get the object that allows external processes to call onPerformSync()
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
