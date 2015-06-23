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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.B2BConstants;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.adapter.CategoryListPreferenceAdapter;
import com.hybris.mobile.lib.b2b.data.Category;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.query.QueryCategory;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.sync.CatalogSyncConstants;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.ui.view.Alert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SyncCategoryPreference extends Preference implements CategoryListPreferenceAdapter.OnSelectCategoryListener {

    private static final String TAG = SyncCategoryPreference.class.getCanonicalName();
    private CategoryListPreferenceAdapter mCategoryAdapter;
    private ViewGroup mCategoryView;
    private View mDownloadButton;
    private Category mMainCategory;

    public SyncCategoryPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SyncCategoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SyncCategoryPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.fragment_sync_settings, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mCategoryAdapter = new CategoryListPreferenceAdapter(getContext(), this, new ArrayList<Category>());
        mCategoryView = (ViewGroup) view.findViewById(R.id.sync_settings_categories);

        mDownloadButton = view.findViewById(R.id.sync_settings_download);

        // Enable/disable the download button
        Set<String> setCategoryToSync = B2BApplication.getStringSetFromSharedPreferences(
                B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, new HashSet<String>());

        mDownloadButton.setEnabled(B2BApplication.isOnline() && setCategoryToSync != null && !setCategoryToSync.isEmpty());

        // Trigger a sync when clicking on the download button
        mDownloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Set<String> setCategoryToSync = B2BApplication.getStringSetFromSharedPreferences(
                        B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, new HashSet<String>());

                // Creating the bundle with the id list
                Bundle bundle = new Bundle();
                StringBuilder categoryList = new StringBuilder();
                for (String idCategory : setCategoryToSync) {
                    categoryList.append(idCategory).append(CatalogSyncConstants.SYNC_PARAM_GROUP_ID_LIST_SEPARATOR);
                }

                bundle.putString(CatalogSyncConstants.SYNC_PARAM_GROUP_ID_LIST, categoryList.toString());

                // Asking for a sync
                B2BApplication.requestCatalogSyncAdapter(bundle);

                Alert.showSuccess((Activity) getContext(), "Sync request successful!");
            }
        });

        if (mMainCategory != null) {
            createCategoriesView(mMainCategory);
        }
        // We load the category the first time
        else {
            // Getting the catalog
            QueryCategory queryCategory = new QueryCategory();
            queryCategory.setId(getContext().getString(R.string.default_catalog_main_category));

            B2BApplication.getContentServiceHelper().getCategory(new ResponseReceiver<Category>() {

                @Override
                public void onResponse(Response<Category> response) {
                    mMainCategory = response.getData();
                    createCategoriesView(mMainCategory);
                }

                @Override
                public void onError(Response<DataError> response) {
                    Log.e(TAG, "Error loading the categories");
                }
            }, null, queryCategory, true, null, null);

        }
    }

    /**
     * Create the categories view
     *
     * @param category
     */
    private void createCategoriesView(Category category) {
        mCategoryAdapter.clear();
        addCategoryToAdapter(category);

        for (int i = 0; i < mCategoryAdapter.getCount(); i++) {
            mCategoryView.addView(mCategoryAdapter.getView(i, null, null));
        }
    }

    /**
     * Add a category to the adapter and do the same for the sub-categories
     *
     * @param category
     */
    private void addCategoryToAdapter(Category category) {
        mCategoryAdapter.add(category);

        if (category.hasSubCategories()) {
            for (Category category2 : category.getSubcategories()) {
                category2.setParent(category);
                addCategoryToAdapter(category2);
            }
        }

    }

    @Override
    public void onSelectCategory(boolean enableDownload) {
        mDownloadButton.setEnabled(enableDownload);
    }
}
