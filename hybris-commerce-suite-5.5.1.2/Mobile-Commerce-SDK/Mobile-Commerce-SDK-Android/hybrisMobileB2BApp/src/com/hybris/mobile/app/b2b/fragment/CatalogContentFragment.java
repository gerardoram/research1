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

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.adapter.ProductGridAdapter;
import com.hybris.mobile.app.b2b.adapter.ProductListAdapter;
import com.hybris.mobile.app.b2b.adapter.ViewTypeAdapter;
import com.hybris.mobile.app.b2b.helper.SessionHelper;
import com.hybris.mobile.app.b2b.utils.UIUtils;
import com.hybris.mobile.app.b2b.view.ViewTypeItem;
import com.hybris.mobile.lib.b2b.data.Category;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.data.product.ProductList;
import com.hybris.mobile.lib.b2b.data.product.SpellingSuggestion;
import com.hybris.mobile.lib.b2b.loader.ProductListLoader;
import com.hybris.mobile.lib.b2b.provider.CatalogContract;
import com.hybris.mobile.lib.b2b.provider.CatalogContract.DataBaseDataSimple;
import com.hybris.mobile.lib.b2b.provider.CatalogDatabaseHelper;
import com.hybris.mobile.lib.b2b.query.QueryProducts;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.http.converter.exception.DataConverterException;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.http.utils.RequestUtils;
import com.hybris.mobile.lib.ui.listener.InfiniteScrollListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment to display the list of product
 */
public class CatalogContentFragment extends Fragment implements ResponseReceiver<ProductList> {
    private String mSearchRequestId = RequestUtils.generateUniqueRequestId();

    private static final String SAVED_INSTANCE_CATEGORY = "SAVED_INSTANCE_CATEGORY";
    private static final String SAVED_INSTANCE_SEARCH_TEXT = "SAVED_INSTANCE_SEARCH_TEXT";

    private static final String TAG = CatalogContentFragment.class.getCanonicalName();

    private OnSearchRequestListener mActivity;
    private Category mCurrentCategory;
    private String mCurrentSearchText;
    private int mCurrentPage;
    private TextView mCategoryBreadcrumb;

    private View mSearchHeader;
    private View mSearchHeaderDidYouMeanView;
    private TextView mSearchHeaderSearched;
    private TextView mSearchHeaderDidYouMean;
    private TextView mSearchHeaderNbResults;
    private View mNoProductFoundView;
    private View mNoProductFoundViewImage;
    private View mNoProductFoundDidYouMeanView;
    private TextView mNoProductFoundTextView;
    private TextView mNoProductFoundDidYouMeanTextView;

    private InfiniteScrollListener mInfiniteScrollListener;
    private View mFooterLoadingListView;
    private Spinner mItemViewTypeSpinner;

    private ListView mProductListView;
    private CursorAdapter mProductItemsAdapter;
    private GridView mProductGridView;

    private ProductListLoader mProductListLoader;
    private boolean mInitialCall;
    private boolean mOnCategorySelectedActivated = true;

    /**
     * Interface for activity communication
     */
    public interface OnSearchRequestListener {
        /**
         * Method for callback
         */
        public void onSearchRequest();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure that the activity implements the callback interface
        try {
            mActivity = (OnSearchRequestListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Breadcrum
        mCategoryBreadcrumb = (TextView) getView().findViewById(R.id.catalog_content_header_breadcumb);
        mCategoryBreadcrumb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Open the drawer
                DrawerLayout mCatalogDrawer = ((DrawerLayout) getActivity().findViewById(R.id.catalog_menu_drawer));
                mCatalogDrawer.openDrawer(mCatalogDrawer.getChildAt(1));
            }
        });

        List<ViewTypeItem> viewTypeTextViewList = new ArrayList<ViewTypeItem>();
        viewTypeTextViewList.add(new ViewTypeItem(getString(R.string.catalog_content_header_list_view),
                R.drawable.ic_subnav_list_blue));
        viewTypeTextViewList.add(new ViewTypeItem(getString(R.string.catalog_content_header_grid_view),
                R.drawable.ic_subnav_grid_blue));
        mItemViewTypeSpinner = (Spinner) getView().findViewById(R.id.catalog_content_header_view_type_spinner);
        ViewTypeAdapter mViewTypeAdapter = new ViewTypeAdapter(getActivity(), viewTypeTextViewList);
        mItemViewTypeSpinner.setAdapter(mViewTypeAdapter);
        mItemViewTypeSpinner.setOnItemSelectedListener(new ItemViewTypeSpinnerListener());

        // Select the default view
        mItemViewTypeSpinner.setSelection(SessionHelper.getCatalogContentViewType());

        // List/Line view
        mProductListView = (ListView) getView().findViewById(R.id.catalog_content_product_list);

        // Footer loading
        mFooterLoadingListView = getActivity().getLayoutInflater().inflate(R.layout.loading_spinner, mProductListView, false);

        // Grid view
        mProductGridView = (GridView) getView().findViewById(R.id.catalog_content_product_grid);

        // Infinite scroll listener for the list view
        mInfiniteScrollListener = new InfiniteScrollListener() {

            @Override
            public void loadNextItems(int page) {
                Log.i(TAG, "Loading the items for page " + page);
                mCurrentPage = page;
                updateProductList();
            }

        };

        mProductListView.setOnScrollListener(mInfiniteScrollListener);
        mProductGridView.setOnScrollListener(mInfiniteScrollListener);

        // Default product adapter
        mProductItemsAdapter = new ProductListAdapter(getActivity(), null, 0, B2BApplication.getContentServiceHelper());

        // No product found view
        mNoProductFoundView = getActivity().findViewById(R.id.catalog_content_product_list_no_product_found);
        mNoProductFoundTextView = (TextView) getActivity().findViewById(R.id.catalog_content_product_list_no_product_found_text);
        mNoProductFoundViewImage = getActivity().findViewById(R.id.catalog_content_product_list_no_product_found_image);
        mNoProductFoundDidYouMeanView = getActivity().findViewById(
                R.id.catalog_content_product_list_no_product_found_did_you_mean_view);
        mNoProductFoundDidYouMeanTextView = (TextView) getActivity().findViewById(
                R.id.catalog_content_product_list_no_product_found_did_you_mean);

        // On click on did you mean, we update the list with the suggested term
        mNoProductFoundDidYouMeanTextView.setOnClickListener(mDidYouMeanOnClickListener);

        // Search header
        mSearchHeader = getActivity().findViewById(R.id.catalog_content_product_list_header_search);
        mSearchHeaderSearched = (TextView) getActivity().findViewById(R.id.catalog_content_product_list_header_search_searched);
        mSearchHeaderDidYouMean = (TextView) getActivity().findViewById(
                R.id.catalog_content_product_list_header_search_did_you_mean);
        mSearchHeaderDidYouMeanView = getActivity().findViewById(R.id.catalog_content_product_list_header_search_did_you_mean_view);
        mSearchHeaderNbResults = (TextView) getActivity().findViewById(R.id.catalog_content_product_list_header_search_nb_results);

        // On click on did you mean, we update the list with the suggested term
        mSearchHeaderDidYouMean.setOnClickListener(mDidYouMeanOnClickListener);

        // Restore the current category or search query
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_INSTANCE_CATEGORY)) {
                mCurrentCategory = savedInstanceState.getParcelable(SAVED_INSTANCE_CATEGORY);
            }

            if (savedInstanceState.containsKey(SAVED_INSTANCE_SEARCH_TEXT)) {
                mCurrentSearchText = savedInstanceState.getString(SAVED_INSTANCE_SEARCH_TEXT);
            }

        }

        // Product list loader
        mProductListLoader = new ProductListLoader(getActivity(), B2BApplication.getContentServiceHelper(), mProductItemsAdapter, new QueryProducts(),
                new OnRequestListener() {

                    @Override
                    public void beforeRequest() {
                        showLoading(true);
                    }

                    @Override
                    public void afterRequest(boolean isDataSynced) {
                        updateUI(false, null, mProductItemsAdapter.getCount());
                        mInitialCall = false;

                        // TODO - offline enabling this will cause the content provider provider to ba called infinitly
                        showLoading(false);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();

        // Coming from a search request
        if (isSearchRequest()) {
            resetData();

            Intent intent = getActivity().getIntent();

            mCurrentSearchText = intent.getStringExtra(SearchManager.QUERY);
        }

        // Updating product list
        updateProductList();
    }

    /**
     * Return true if we come from a search request
     *
     * @return
     */
    private boolean isSearchRequest() {
        Intent intent = getActivity().getIntent();

        return (intent != null && StringUtils.equals(Intent.ACTION_SEARCH, intent.getAction()) && intent
                .hasExtra(SearchManager.QUERY)) || StringUtils.isNotBlank(mCurrentSearchText);
    }

    // Only for the search
    @Override
    public void onResponse(Response<ProductList> response) {
        mInitialCall = false;

        // TODO - offline -  when offline work with the local search
        // We create a cursor based on the results, to use the same adapter as the one used for the product list from a category
        if (response.getData().getProducts() != null) {

            List<DataBaseDataSimple> dataList = new ArrayList<DataBaseDataSimple>();

            for (Product product : response.getData().getProducts()) {
                try {
                    dataList.add(new DataBaseDataSimple(product.getCode(), B2BApplication.getContentServiceHelper().getDataConverter()
                            .convertTo(product), CatalogContract.SyncStatus.UPTODATE));
                } catch (DataConverterException e) {
                    Log.e(TAG, "Error converting the product " + product.getCode() + ". Details: " + e.getLocalizedMessage());
                }
            }

            // Merging old with new cursor - in case of pagination
            Cursor oldCursor = mProductItemsAdapter.getCursor();
            Cursor newCursor = CatalogDatabaseHelper.createCursor(dataList);
            MergeCursor mergeCursor;

            if (oldCursor != null && oldCursor.getCount() > 0) {
                mergeCursor = new MergeCursor(new Cursor[]
                        {oldCursor, newCursor});
            } else {
                mergeCursor = new MergeCursor(new Cursor[]
                        {newCursor});
            }

            mProductItemsAdapter.swapCursor(mergeCursor);
        }

        updateUI(StringUtils.equals(response.getRequestId(), mSearchRequestId), response.getData().getSpellingSuggestion(),
                response.getData().getPagination() != null ? response.getData().getPagination().getTotalResults() : 0);

        mOnCategorySelectedActivated = true;
    }

    @Override
    public void onError(Response<DataError> response) {
        UIUtils.showError(response, getActivity());
    }

    /**
     * Update the product list
     */
    private void updateProductList() {
        // We try first to use the search query
        if (StringUtils.isNotBlank(mCurrentSearchText)) {
            updateProductListFromSearch();
        }
        // Then the category
        else {
            updateProductListFromCategory();
        }
    }

    /**
     * Call to update the product list from a category
     */
    private void updateProductListFromCategory() {
        if (mCurrentCategory != null) {
            QueryProducts queryProducts = new QueryProducts();
            queryProducts.setIdCategory(mCurrentCategory.getId());
            queryProducts.setCurrentPage(mCurrentPage);
            queryProducts.setPageSize(B2BApplication.getConfiguration().getDefaultPageSize());

            mProductListLoader.setLimitFrom(0);
            mProductListLoader.setLimitTo((mCurrentPage + 1) * B2BApplication.getConfiguration().getDefaultPageSize());
            mProductListLoader.updateQuery(queryProducts);
            getLoaderManager().restartLoader(0, null, mProductListLoader);
        }
    }

    /**
     * Call to update the product list from a search query
     */
    private void updateProductListFromSearch() {

        if (StringUtils.isNotBlank(mCurrentSearchText)) {
            mOnCategorySelectedActivated = false;
            mActivity.onSearchRequest();

            QueryProducts queryProducts = new QueryProducts();
            queryProducts.setSearchText(mCurrentSearchText);
            queryProducts.setCurrentPage(mCurrentPage);
            queryProducts.setPageSize(B2BApplication.getConfiguration().getDefaultPageSize());

            B2BApplication.getContentServiceHelper().getProducts(this, mSearchRequestId, queryProducts, false, null,
                    new OnRequestListener() {

                        @Override
                        public void beforeRequest() {
                            showLoading(true);
                        }

                        @Override
                        public void afterRequest(boolean isDataSynced) {
                            showLoading(false);
                        }
                    });
        }
    }

    /**
     * Called when a category is selected
     *
     * @param category
     */
    public void onCategorySelected(Category category) {
        // Update the breadcrum
        updateBreadcrum(category);

        if (mOnCategorySelectedActivated) {
            // Reset the data
            resetData();

            // Clearing the infinite scroll parameter
            mInfiniteScrollListener.reset();

            // Updating category
            mCurrentCategory = category;

            // Resetting search
            mSearchHeader.setVisibility(View.GONE);

            // Updating the product list
            updateProductList();
        }
    }

    /**
     * Update the breadcrum
     */
    public void updateBreadcrum(Category category) {
        // Updating the breadcrum
        if (category != null) {
            mCategoryBreadcrumb.setText(getString(R.string.catalog_content_breadcrumb));

            // Category
            if (category.getParent() != null) {
                mCategoryBreadcrumb.setText(mCategoryBreadcrumb.getText() + category.getParent().getName() + " / ");
            }

            mCategoryBreadcrumb.setText(mCategoryBreadcrumb.getText() + category.getName());
        }
    }

    /**
     * Update the UI
     *
     * @param requestFromSearchQuery
     */
    public void updateUI(boolean requestFromSearchQuery, SpellingSuggestion spellingSuggestion, int totalResults) {

        // Updating the breadcrum
        updateBreadcrum(mCurrentCategory);

        // No results (On the initial call we don't display this page as the content provider return the results twice (local then remote), the first results might be empty)
        if (totalResults == 0 && mCurrentPage == 0 && !mInitialCall) {

            // Display the no product view and hide the product list
            showProductItemsView(false);

            // Hide the product items view
            mNoProductFoundView.setVisibility(View.VISIBLE);

            if (requestFromSearchQuery) {
                mSearchHeader.setVisibility(View.GONE);
                mNoProductFoundViewImage.setVisibility(View.VISIBLE);
                mNoProductFoundTextView.setText(getString(R.string.products_not_found_search_request, mCurrentSearchText));

                // Spelling suggestions
                if (spellingSuggestion != null && StringUtils.isNotBlank(spellingSuggestion.getSuggestion())) {
                    mNoProductFoundDidYouMeanView.setVisibility(View.VISIBLE);
                    mNoProductFoundDidYouMeanTextView.setText(spellingSuggestion.getSuggestion());
                } else {
                    mNoProductFoundDidYouMeanView.setVisibility(View.GONE);
                }

            } else {
                mNoProductFoundViewImage.setVisibility(View.GONE);
                mNoProductFoundTextView.setText(getString(R.string.products_not_found));
                mNoProductFoundDidYouMeanView.setVisibility(View.GONE);
            }

        }
        // Displaying the results
        else {
            // Hide the no product view and display the product list
            mNoProductFoundView.setVisibility(View.GONE);

            // Show the correct product items view
            showProductItemsView(true);

            // Display the search header with did you mean and number of results
            if (requestFromSearchQuery) {
                mSearchHeader.setVisibility(View.VISIBLE);
                mSearchHeaderSearched.setText(mCurrentSearchText);
                mSearchHeaderNbResults.setText(getString(R.string.products_search_header_nb_results, totalResults));

                // Spelling suggestions
                if (spellingSuggestion != null && StringUtils.isNotBlank(spellingSuggestion.getSuggestion())) {
                    mSearchHeaderDidYouMeanView.setVisibility(View.VISIBLE);
                    mSearchHeaderDidYouMean.setText(spellingSuggestion.getSuggestion());
                } else {
                    mSearchHeaderDidYouMeanView.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVED_INSTANCE_CATEGORY, mCurrentCategory);
        outState.putString(SAVED_INSTANCE_SEARCH_TEXT, mCurrentSearchText);
        super.onSaveInstanceState(outState);
    }

    /**
     * Show the loading spinner
     *
     * @param show
     */
    private void showLoading(boolean show) {
        // Adding the loading view for the list view
        if (mProductListView.getVisibility() == View.VISIBLE) {
            if (show) {
                if (mProductListView.getFooterViewsCount() == 0) {
                    mProductListView.addFooterView(mFooterLoadingListView, null, false);
                }
            } else {
                mProductListView.removeFooterView(mFooterLoadingListView);
            }
        }
        // Adding the loading view for the list view
        else if (mProductGridView.getVisibility() == View.VISIBLE) {
            UIUtils.showLoadingActionBar(getActivity(), show);
        }

    }

    /**
     * Select the view for products
     *
     * @param position
     */
    private void selectView(int position) {

        // Show the correct view
        // Product list not empty OR product list empty and the view no product found is not shown (first call of the page)
        showProductItemsView(mProductItemsAdapter != null
                && (!mProductItemsAdapter.isEmpty() || (mProductItemsAdapter.isEmpty() && !mNoProductFoundView.isShown())));

        switch (position) {
            case 0:
            default:
                mProductListView.setContentDescription(getString(R.string.catalog_content_header_list_view));
                mProductItemsAdapter = new ProductListAdapter(getActivity(), mProductItemsAdapter.getCursor(), 0,
                        B2BApplication.getContentServiceHelper());
                break;
            case 1:
                mProductGridView.setContentDescription(getString(R.string.catalog_content_header_grid_view));
                mProductItemsAdapter = new ProductGridAdapter(getActivity(), mProductItemsAdapter.getCursor(), 0,
                        B2BApplication.getContentServiceHelper());
                break;
        }

        // Listview
        if (position == 0) {
            // When changing the adapter we need to re-add the footer view
            if (mProductListView.getFooterViewsCount() == 0) {
                mProductListView.addFooterView(mFooterLoadingListView, null, false);
            }

            mProductListView.setAdapter(mProductItemsAdapter);
        } else {
            mProductGridView.setAdapter(mProductItemsAdapter);
        }

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mProductGridView.setNumColumns(getResources().getInteger(R.integer.grid_landscape_nb_items));
        } else {
            mProductGridView.setNumColumns(getResources().getInteger(R.integer.grid_portrait_nb_items));
        }

        // Updating the cursor adapter in the loader
        mProductListLoader.setCursorAdapter(mProductItemsAdapter);
    }

    /**
     * Onclick listener for did you mean text
     */
    private OnClickListener mDidYouMeanOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            resetData();
            mCurrentSearchText = ((TextView) v).getText().toString();

            // Updating product list
            updateProductList();
        }
    };

    /**
     * Reset the data
     */
    private void resetData() {
        mProductItemsAdapter.swapCursor(null);
        mProductItemsAdapter.notifyDataSetChanged();
        mCurrentPage = 0;
        mCurrentCategory = null;
        mCurrentSearchText = null;

        // When offline, there is just one call to the provider (No sync request are made on the provider side)
        // TODO - offline - base this value with the isDataSynced of the callabck response
        mInitialCall = B2BApplication.isOnline();
    }

    /**
     * Listener for handling changes on view type
     */
    public class ItemViewTypeSpinnerListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            SessionHelper.setCatalogContentViewType(position);
            selectView(SessionHelper.getCatalogContentViewType());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /**
     * Show/Hide the correct product items view
     *
     * @param show
     */
    private void showProductItemsView(boolean show) {
        switch (mItemViewTypeSpinner.getSelectedItemPosition()) {
            // List/Line view
            case 0:
            default:
                mProductListView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProductGridView.setVisibility(View.GONE);
                break;
            // Grid view
            case 1:
                mProductListView.setVisibility(View.GONE);
                mProductGridView.setVisibility(show ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        B2BApplication.getContentServiceHelper().cancel(mSearchRequestId);
    }
}
