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

package com.hybris.mobile.app.b2b.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.utils.ArrayUtils;

import org.apache.commons.lang3.StringUtils;


/**
 * Fragment for the settings
 */
public class UrlSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.url_settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSharedPreferences = getPreferenceManager().getSharedPreferences();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // No value exists for the url key, we set the default values
        if (StringUtils.isEmpty(mSharedPreferences.getString(getString(R.string.preference_key_key_base_url), ""))) {

            // View
            Preference prefKeyBackendUrl = findPreference(getString(R.string.preference_key_key_base_url));
            prefKeyBackendUrl.setSummary(getResources().getStringArray(R.array.backend_url_keys)[0]);

            Preference prefValueBackendUrl = findPreference(getString(R.string.preference_key_value_base_url));
            prefValueBackendUrl.setSummary(getResources().getStringArray(R.array.backend_url_values)[0]);

            // Shared settings values
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(getString(R.string.preference_key_key_base_url), getResources().getStringArray(R.array.backend_url_keys)[0]);
            editor.putString(getString(R.string.preference_key_value_base_url), getResources().getStringArray(R.array.backend_url_values)[0]);
            editor.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // We put on the summary the url key and value
        Preference prefKeyBackendUrl = findPreference(getString(R.string.preference_key_key_base_url));
        prefKeyBackendUrl.setSummary(mSharedPreferences.getString(getString(R.string.preference_key_key_base_url), ""));

        Preference prefValueBackendUrl = findPreference(getString(R.string.preference_key_value_base_url));
        prefValueBackendUrl.setSummary(mSharedPreferences.getString(getString(R.string.preference_key_value_base_url), ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Changing the backend url selection (internal, external, etc.)
        if (StringUtils.equals(key, getString(R.string.preference_key_key_base_url)) || StringUtils.equals(key, getString(R.string.preference_key_value_base_url))) {
            String keyValue = sharedPreferences.getString(key, "");
            Preference keyPreference = findPreference(key);

            String[] keysTab = getResources().getStringArray(R.array.backend_url_keys);
            String[] valuesTab = getResources().getStringArray(R.array.backend_url_values);

            boolean resetBackendEndConnectors = true;

            if (StringUtils.equals(key, getString(R.string.preference_key_key_base_url))) {
                int indexKey = ArrayUtils.indexOf(keysTab, keyValue);

                // Setting the description to the right key
                keyPreference.setSummary(keyValue);

                // For custom key don't empty the field
                if (indexKey < keysTab.length - 1) {

                    // If something changed
                    if (!StringUtils.equals(valuesTab[indexKey], mSharedPreferences.getString(getString(R.string.preference_key_value_base_url), ""))) {
                        Preference prefValueBackendUrl = findPreference(getString(R.string.preference_key_value_base_url));
                        prefValueBackendUrl.setSummary(valuesTab[indexKey]);
                        ((EditTextPreference) prefValueBackendUrl).setText(valuesTab[indexKey]);

                        // Saving the value on the settings
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(getString(R.string.preference_key_value_base_url), valuesTab[indexKey]);
                        editor.commit();
                    } else {
                        resetBackendEndConnectors = false;
                    }

                }
            }
            // Changing the backend url value
            else {
                // Updating the description (Or custom if no value found)
                Preference prefKeyBackendUrl = findPreference(getString(R.string.preference_key_key_base_url));
                prefKeyBackendUrl.setSummary(keysTab[ArrayUtils.indexOf(valuesTab, keyValue)]);
                keyPreference.setSummary(keyValue);
            }

            // Update the url of the backend
            if (resetBackendEndConnectors) {
                B2BApplication.updateUrl(mSharedPreferences.getString(getString(R.string.preference_key_value_base_url), ""));
            }

        }

    }

    @Override
    public void onDetach() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDetach();
    }
}
