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

package com.hybris.mobile.app.b2b.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.R;


public class BackendUrlValuePreference extends Preference {

    private static final String TAG = BackendUrlValuePreference.class.getCanonicalName();
    private TextView mTextViewServerUrl;

    public BackendUrlValuePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BackendUrlValuePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackendUrlValuePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.fragment_backend_url_value, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mTextViewServerUrl = (TextView) view.findViewById(R.id.app_options_server_url);
        mTextViewServerUrl.setText(B2BApplication.getStringFromSharedPreferences(getContext().getString(R.string.preference_key_value_base_url, ""), ""));

    }

}
