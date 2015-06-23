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
package com.hybris.mobile.app.b2b;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.hybris.mobile.app.b2b.broadcast.LogoutBroadcastReceiver;
import com.hybris.mobile.app.b2b.broadcast.UpdateCacheBroadcastReceiver;
import com.hybris.mobile.app.b2b.utils.ArrayUtils;
import com.hybris.mobile.lib.b2b.helper.SecurityHelper;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelperBuilder;
import com.hybris.mobile.lib.b2b.sync.CatalogSyncConstants;
import com.hybris.mobile.lib.http.utils.ConnectionUtils;

import org.apache.commons.lang3.StringUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Main Application class to manage and provide functionality over the apps
 */
public class B2BApplication extends Application {

    protected static final String TAG = B2BApplication.class.getCanonicalName();
    public static final String LAST_CATALOG_SYNC_DATE = "LAST_CATALOG_SYNC_DATE";
    private Configuration mConfiguration;
    private static B2BApplication mInstance;
    private ContentServiceHelper mContentServiceHelper;
    private boolean mSaveOnlineStatus = true;

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

    public void onCreate() {
        super.onCreate();
        mInstance = this;

        String urlBackend = getStringFromSharedPreferences(getString(R.string.preference_key_value_base_url), "");

        // If no URL found in the shared settings, use the one from the flavor (default) + update the settings
        if (StringUtils.isBlank(urlBackend)) {
            urlBackend = getString(R.string.url_backend);

            int indexNameBackend = ArrayUtils.indexOf(getResources().getStringArray(R.array.backend_url_values), urlBackend);

            // Update the settings
            setStringToSharedPreferences(getString(R.string.preference_key_value_base_url), urlBackend);
            setStringToSharedPreferences(getString(R.string.preference_key_key_base_url), getResources().getStringArray(R.array.backend_url_keys)[indexNameBackend]);
        }

        // Configuration for the backend url
        com.hybris.mobile.lib.b2b.Configuration configuration = new com.hybris.mobile.lib.b2b.Configuration();
        configuration.setBackendUrl(urlBackend);
        configuration.setCatalogParameterUrl(mInstance.getString(R.string.url_parameter_catalog));
        configuration.setCatalogPathUrl(mInstance.getString(R.string.url_path_catalog));
        configuration.setCatalogAuthority(getString(R.string.provider_authority));

        // Creating the content service helper
        mInstance.mContentServiceHelper = ContentServiceHelperBuilder.build(mInstance.getApplicationContext(), configuration, true);

        // Build the configuration for the app
        mConfiguration = Configuration.buildConfiguration(this);

        // Register local broadcast to Logout
        LocalBroadcastManager.getInstance(this).registerReceiver(new LogoutBroadcastReceiver(),
                new IntentFilter(getString(R.string.intent_action_logout)));

        // Register local broadcast to update cache on the content service helper
        LocalBroadcastManager.getInstance(this).registerReceiver(new UpdateCacheBroadcastReceiver(),
                new IntentFilter(getString(R.string.intent_action_update_cache)));

        // Default account for the sync adapter
        addCatalogSyncAdapterDefaultAccount();

        // We sync in advance the main category of the catalog to create the sync adapter and accelerate the process
        Bundle bundle = new Bundle();
        bundle.putString(CatalogSyncConstants.SYNC_PARAM_GROUP_ID, getString(R.string.default_catalog_main_category));
        bundle.putInt(CatalogSyncConstants.SYNC_PARAM_CURRENT_PAGE, Integer.valueOf(0));
        bundle.putInt(CatalogSyncConstants.SYNC_PARAM_PAGE_SIZE, Integer.valueOf(mConfiguration.getDefaultPageSize()));
        requestCatalogSyncAdapter(bundle);
    }

    /**
     * Update the ContentServiceHelper and SyncAdapter url
     *
     * @param url
     */
    public static void updateUrl(String url) {
        // Cancelling all the requests first
        mInstance.mContentServiceHelper.cancelAll();

        // Catalog sync adapter
        Bundle bundle = new Bundle();
        bundle.putBoolean(CatalogSyncConstants.SYNC_PARAM_CANCEL_ALL_REQUESTS, true);
        requestCatalogSyncAdapter(bundle);

        // Updating the URLs
        // Catalog sync adapter
        bundle = new Bundle();
        bundle.putString(CatalogSyncConstants.SYNC_PARAM_CONTENT_SERVICE_HELPER_URL, url);
        requestCatalogSyncAdapter(bundle);

        // Content service helper
        mInstance.mContentServiceHelper.updateUrl(url);
    }

    /**
     * Get the String value associated with the key on the shared settings (Encrypted)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getStringFromSharedPreferencesSecure(String key, String defaultValue) {
        return SecurityHelper.getStringFromSecureSharedPreferences(getSharedPreferences(), key, defaultValue);
    }

    /**
     * Get the String value associated with the key on the shared settings
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getStringFromSharedPreferences(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    /**
     * Get the int value associated with the key on the shared settings
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntFromSharedPreferences(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    /**
     * Get the long value associated with the key on the shared settings
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static long getLongFromSharedPreferences(String key, long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    /**
     * Get the boolean value associated with the key on the shared settings
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanFromSharedPreferences(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    /**
     * Get the String set value associated with the key on the shared settings
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static Set<String> getStringSetFromSharedPreferences(String key, Set<String> defaultValue) {
        return getSharedPreferences().getStringSet(key, defaultValue);
    }

    /**
     * Set a String pair key/value on the shared settings (Encrypted)
     *
     * @param key
     * @param value
     */
    public static void setStringToSharedPreferencesSecure(String key, String value) {
        SecurityHelper.setStringToSecureSharedPreferences(getSharedPreferences(), key, value);
    }

    /**
     * Set a String pair key/value on the shared settings
     *
     * @param key
     * @param value
     */
    public static void setStringToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Set a Long pair key/value on the shared settings
     *
     * @param key
     * @param value
     */
    public static void setLongToSharedPreferences(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * Set a int pair key/value on the shared settings
     *
     * @param key
     * @param value
     */
    public static void setIntToSharedPreferences(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Set a boolean pair key/value on the shared settings
     *
     * @param key
     * @param value
     */
    public static void setBooleanToSharedPreferences(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Set a String set pair key/value on the shared settings
     *
     * @param key
     * @param value
     */
    public static void setStringSetToSharedPreferences(String key, Set<String> value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    /**
     * Get the shared settings
     *
     * @return
     */
    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mInstance);
    }

    /**
     * Return the configuration instance
     *
     * @return
     */
    public static Configuration getConfiguration() {
        return mInstance.mConfiguration;
    }

    /**
     * Return the content service helper
     *
     * @return
     */
    public static ContentServiceHelper getContentServiceHelper() {
        return mInstance.mContentServiceHelper;
    }

    /**
     * Return the application context
     *
     * @return
     */
    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    /**
     * Save the current online status
     *
     * @param onlineStatus
     */
    public static void saveCurrentOnlineStatus(boolean onlineStatus) {
        mInstance.mSaveOnlineStatus = onlineStatus;
    }

    /**
     * Return true if the status
     *
     * @param onlineStatus
     * @return
     */
    public static boolean isOnlineStatusChanged(boolean onlineStatus) {
        return mInstance.mSaveOnlineStatus != onlineStatus;
    }

    /**
     * Return true if the app is in online mode
     *
     * @return
     */
    public static boolean isOnline() {
        return ConnectionUtils.isConnectedToInternet(getContext());
    }

    /**
     * Return the catalog last sync date
     *
     * @return
     */
    public static String getCatalogLastSyncDate() {
        long lastSyncDateInMillis = getContentServiceHelper().getCatalogLastSyncDate();
        String lastSyncDateString;

        if (lastSyncDateInMillis > 0) {
            Date lastSyncDate = new Date(lastSyncDateInMillis);
            lastSyncDateString = B2BConstants.DATE_FORMAT_CATALOG_LAST_SYNC_DATE.format(lastSyncDate);
        } else {
            lastSyncDateString = mInstance.getString(R.string.unknown);
        }

        return lastSyncDateString;
    }

    /**
     * Add a default account to the sync adapter
     */
    private void addCatalogSyncAdapterDefaultAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(new Account(getString(R.string.account_name), getString(R.string.account_type)), null, null);
    }

    /**
     * Request a sync of the catalog sync adapter
     *
     * @param bundle
     */
    public static void requestCatalogSyncAdapter(Bundle bundle) {
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(new Account(mInstance.getString(R.string.account_name), mInstance.getString(R.string.account_type)), mInstance.getString(R.string.provider_authority), bundle);
    }

}
