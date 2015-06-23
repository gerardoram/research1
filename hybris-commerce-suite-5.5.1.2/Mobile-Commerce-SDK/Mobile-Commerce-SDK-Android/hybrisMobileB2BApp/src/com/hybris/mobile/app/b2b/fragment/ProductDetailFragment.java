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

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.IntentConstants;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.helper.CartHelper;
import com.hybris.mobile.app.b2b.helper.CartHelper.OnAddToCart;
import com.hybris.mobile.app.b2b.helper.ProductHelper;
import com.hybris.mobile.app.b2b.utils.ProductUtils;
import com.hybris.mobile.app.b2b.utils.UIUtils;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.cart.ProductAdded;
import com.hybris.mobile.lib.b2b.data.product.Price;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.data.product.Product.Image;
import com.hybris.mobile.lib.b2b.data.product.ProductVariant;
import com.hybris.mobile.lib.b2b.loader.ProductLoader;
import com.hybris.mobile.lib.b2b.query.QueryProductDetails;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.http.utils.RequestUtils;
import com.hybris.mobile.lib.ui.listener.SubmitListener;
import com.hybris.mobile.lib.ui.view.ZoomImageView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Container that handle_anchor the details information for a specific product
 */
public class ProductDetailFragment extends Fragment {
    private static final String TAG = ProductDetailFragment.class.getCanonicalName();

    private static final String SAVED_INSTANCE_PRODUCT_CODE = "SAVED_INSTANCE_PRODUCT_CODE";

    private String mProductDetailRequestId = RequestUtils.generateUniqueRequestId();

    private Response<Product> mProduct;

    private ImageButton mCloseProductDetailButton;
    private TextView mProductDetaiNameText;
    private ImageButton mOrderFormButton;

    private View mNoImage;

    private ViewPager mViewPager;
    private ImagePagerAdapter mImageAdapter;
    private LinearLayout mLayoutIndicator;

    private TextView mProductPrice;
    private Button mVolumePricingExpandableButton;
    private TableLayout mVolumePricingExpandableLayout;
    private TextView mProductShortDescription;

    private EditText mQuantityEditText;
    private TextView mStocklevelText;
    private TextView mTotalPriceText;

    private LinearLayout mAddToCartButton;
    private TextView mProductDetailAddToCartText;
    private Button mProductDetailExpandableButton;
    private TextView mProductDetailExpandableText;
    private Button mDeliveryExpandableButton;
    private TextView mDeliveryExpandableText;
    private LinearLayout mProductDetailDescriptionLayout;
    private Spinner mProductDetailVariantSpinner1;
    private Spinner mProductDetailVariantSpinner2;
    private Spinner mProductDetailVariantSpinner3;
    private List<Spinner> mSpinnersVariants;

    private ProgressBar mStocklevelTextLoading;

    // Zoomed UI
    private ScrollView mProductDetailScrollView;
    private LinearLayout mScrollViewLayout;
    private LinearLayout mImageLayout;
    private LinearLayout mMiddleSection;
    private LinearLayout mBottomSection;
    private View mViewDivider;

    private int mCurrentIndicator = 0;

    private ProductLoader mProductLoader;
    private QueryProductDetails mQueryProductDetails;

    private boolean mVariantPopulated = false;
    private int mNbVariantLevels = 0;
    private int mNbVariantLevelsInstantiated = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize View by linking and giving instance to xml element in the
        // layout
        mCloseProductDetailButton = (ImageButton) getView().findViewById(R.id.product_detail_exit);
        mProductDetaiNameText = (TextView) getView().findViewById(R.id.product_detail_name);
        mOrderFormButton = (ImageButton) getView().findViewById(R.id.product_detail_order_form);
        mViewPager = (ViewPager) getView().findViewById(R.id.product_detail_view_pager);
        mImageAdapter = new ImagePagerAdapter(new ArrayList<Image>());
        mLayoutIndicator = (LinearLayout) getView().findViewById(R.id.product_detail_view_pager_indicator_bar);
        mProductPrice = (TextView) getView().findViewById(R.id.product_detail_volume_price);
        mVolumePricingExpandableButton = (Button) getView().findViewById(R.id.product_detail_volume_pricing_button);
        mVolumePricingExpandableLayout = (TableLayout) getView().findViewById(R.id.product_detail_volume_pricing_table_layout);
        mProductDetailDescriptionLayout = (LinearLayout) getView().findViewById(R.id.product_detail_description_layout);
        mProductShortDescription = (TextView) getView().findViewById(R.id.product_detail_short_description);
        mStocklevelText = (TextView) getView().findViewById(R.id.product_detail_stocklevel_text);
        mTotalPriceText = (TextView) getView().findViewById(R.id.product_detail_total_price_text);
        mQuantityEditText = (EditText) getView().findViewById(R.id.product_detail_quantity_editText);
        mAddToCartButton = (LinearLayout) getView().findViewById(R.id.product_detail_add_to_cart_layout_button);
        mProductDetailAddToCartText = (TextView) getView().findViewById(R.id.product_detail_add_to_cart_text);
        mProductDetailExpandableButton = (Button) getView().findViewById(R.id.product_detail_expandable_button);
        mProductDetailExpandableText = (TextView) getView().findViewById(R.id.product_detail_expandable_text);
        mDeliveryExpandableButton = (Button) getView().findViewById(R.id.product_detail_delivery_expandable_button);
        mDeliveryExpandableText = (TextView) getView().findViewById(R.id.product_detail_delivery_expandable_text);
        mProductDetailVariantSpinner1 = (Spinner) getView().findViewById(R.id.product_detail_variant_spinner_1);
        mProductDetailVariantSpinner2 = (Spinner) getView().findViewById(R.id.product_detail_variant_spinner_2);
        mProductDetailVariantSpinner3 = (Spinner) getView().findViewById(R.id.product_detail_variant_spinner_3);
        mStocklevelTextLoading = (ProgressBar) getView().findViewById(R.id.product_detail_stocklevel_text_loading);
        mNoImage = getView().findViewById(R.id.product_item_no_image);

        // Zoom UI
        mProductDetailScrollView = (ScrollView) getView().findViewById(R.id.product_detail_scrollView);
        mScrollViewLayout = (LinearLayout) getView().findViewById(R.id.product_detail_scrollView_layout);
        mImageLayout = (LinearLayout) getView().findViewById(R.id.product_detail_image_layout);
        mMiddleSection = (LinearLayout) getView().findViewById(R.id.product_detail_middle_section);
        mBottomSection = (LinearLayout) getView().findViewById(R.id.product_detail_bottom_section);
        mViewDivider = (View) getView().findViewById(R.id.product_detail_middle_divider);

        // Only show description when not blank
        mProductDetailDescriptionLayout.setVisibility(View.GONE);

        // Listener
        mCloseProductDetailButton.setOnClickListener(exitProductDetailButtonListener);
        mOrderFormButton.setOnClickListener(orderFormButtonListener);
        mViewPager.setOnTouchListener(imageViewPagerOnTouchListener);
        mViewPager.setOnPageChangeListener(imageViewPagerOnPageChangeListener);
        mVolumePricingExpandableButton.setOnClickListener(expandableLayoutListener);
        mProductDetailExpandableButton.setOnClickListener(new ExpandablePanelListener(mProductDetailExpandableButton,
                mProductDetailExpandableText));
        mDeliveryExpandableButton
                .setOnClickListener(new ExpandablePanelListener(mDeliveryExpandableButton, mDeliveryExpandableText));
        mQuantityEditText.setOnFocusChangeListener(quantityEditTextListener);
        mAddToCartButton.setOnClickListener(addToCartButtonListener);

        mProductDetailVariantSpinner1.setOnItemSelectedListener(productDetailVariantSpinnerListener);
        mProductDetailVariantSpinner2.setOnItemSelectedListener(productDetailVariantSpinnerListener);
        mProductDetailVariantSpinner3.setOnItemSelectedListener(productDetailVariantSpinnerListener);

        mSpinnersVariants = new ArrayList<Spinner>();
        mSpinnersVariants.add(mProductDetailVariantSpinner1);
        mSpinnersVariants.add(mProductDetailVariantSpinner2);
        mSpinnersVariants.add(mProductDetailVariantSpinner3);

        mQuantityEditText.addTextChangedListener(quantityEditTextTextWatcher);
        mQuantityEditText.setOnEditorActionListener(new SubmitListener() {
            @Override
            public void onSubmitAction() {
                addToCart();
            }
        });

        // When clicking outside a EditText, hide keyboard, remove focus and
        // reset to the default value
        // Clicking on the main view
        getView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UIUtils.hideKeyboard(getActivity());
                mQuantityEditText.clearFocus();
                v.performClick();
                return false;
            }
        });

        // Query object to get the products details
        mQueryProductDetails = new QueryProductDetails();
        mQueryProductDetails.setCode(getActivity().getIntent().getStringExtra(IntentConstants.PRODUCT_CODE));

        // Restore the current spinner selection
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_INSTANCE_PRODUCT_CODE)) {
                mQueryProductDetails.setCode(savedInstanceState.getString(SAVED_INSTANCE_PRODUCT_CODE, null));
            }
        }

        mProductLoader = new ProductLoader(getActivity(), B2BApplication.getContentServiceHelper(), new ResponseReceiver<Product>() {

            @Override
            public void onResponse(Response<Product> response) {
                mProduct = response;

                /**
                 * Cases:
                 * 1 - Product with variants
                 * 2 - Product without any variants
                 */
                if (mVariantPopulated || ((mProduct.getData().getVariantOptions() == null || mProduct.getData().getVariantOptions().isEmpty()) && StringUtils.isBlank(mProduct.getData().getBaseProduct()))) {
                    populateProduct(mProduct.getData());
                }
                // First call for the base product, we populate the variants and select the first one to reload the product on the first variant
                else {
                    mNbVariantLevels = ProductHelper.populateVariant(getActivity(), mSpinnersVariants, mProduct.getData());
                    ProductVariant selectedVariant = (ProductVariant) mProductDetailVariantSpinner1.getItemAtPosition(0);

                    if (selectedVariant != null) {
                        selectVariant(selectedVariant.getVariantOption().getCode());
                    }
                }
            }

            @Override
            public void onError(Response<DataError> response) {
                UIUtils.showError(response, getActivity());
            }
        }, mQueryProductDetails, new OnRequestListener() {

            @Override
            public void beforeRequest() {
                afterBeforeRequest(true);
            }

            @Override
            public void afterRequest(boolean isDataSynced) {
                afterBeforeRequest(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // Start the loader
        getLoaderManager().restartLoader(0, null, mProductLoader);

        if (B2BApplication.isOnline()) {
            mNoImage.setVisibility(View.GONE);
            mImageLayout.setVisibility(View.VISIBLE);
        } else {
            mNoImage.setVisibility(View.VISIBLE);
            mImageLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_PRODUCT_CODE, mProduct.getData().getCode());
        super.onSaveInstanceState(outState);
    }

    /**
     * Before/After requests actions
     *
     * @param isBefore
     */
    private void afterBeforeRequest(boolean isBefore) {
        if (mProduct != null && mProduct.getData().isMultidimensional()) {
            mStocklevelTextLoading.setVisibility(isBefore ? View.VISIBLE : View.INVISIBLE);
            mStocklevelText.setVisibility(isBefore ? View.INVISIBLE : View.VISIBLE);
        }

        // Before the request we disable the edit text for the quantity and the add to cart button
        if (isBefore) {
            mQuantityEditText.setText("");
            setClickableAddToCartButton();
        }

        UIUtils.showLoadingActionBar(getActivity(), isBefore);
    }

    /**
     * Get variant from dropdown and display info in product details page
     *
     * @param productCode
     */
    private void selectVariant(String productCode) {
        QueryProductDetails queryProductDetails = new QueryProductDetails();
        queryProductDetails.setCode(productCode);

        mProductLoader.updateQuery(queryProductDetails);
        getLoaderManager().restartLoader(0, null, mProductLoader);
    }

    /**
     * Populate the product
     *
     * @param product
     */
    private void populateProduct(Product product) {
        if (product != null) {
            /**
             * Populate the view with data from response and associate it to the right element in the view
             */
            mProductDetaiNameText.setText(product.getName() + " (" + product.getCode() + ")");

            if (StringUtils.isNotBlank(product.getDescription())) {
                mProductDetailExpandableText.setText(product.getDescription());
            }

            if (StringUtils.isNotBlank(product.getSummary())) {
                mProductDetailDescriptionLayout.setVisibility(View.VISIBLE);
                mProductShortDescription.setText(product.getSummary());
            }

            if (product.getStock() != null) {

                boolean resetAddToCart = true;

                mStocklevelText.setVisibility(View.VISIBLE);
                if (product.isLowStock() || product.isOutOfStock()) {
                    mStocklevelText.setTextColor(getResources().getColor(R.color.product_item_low_stock));
                    mStocklevelText.setContentDescription(getString(R.string.product_item_low_stock));

                    if (product.isOutOfStock()) {
                        resetAddToCart = false;
                        mQuantityEditText.setEnabled(false);
                        mQuantityEditText.setText("");
                        mStocklevelText
                                .setText(product.getStock().getStockLevel() + "\n" + getString(R.string.product_detail_in_stock));
                    }
                }

                if (product.isInStock()) {
                    mStocklevelText.setText(product.getStock().getStockLevel() + "\n" + getString(R.string.product_detail_in_stock));
                    mStocklevelText.setTextColor(getResources().getColor(R.color.product_item_in_stock));
                }

                if (resetAddToCart) {
                    mQuantityEditText.setEnabled(true);
                    mQuantityEditText.setText(getString(R.string.default_qty));
                }

            } else {
                Log.d(TAG, "Stock is null");
            }

            if (product.getPrice() != null) {
                // to show pipe
                mProductPrice.setText((product.getVolumePrices() != null) ? product.getPriceRangeFormattedValue() + " | " : product
                        .getPriceRangeFormattedValue());

                // Set the price with the default total value with currency sign
                mTotalPriceText.setText((StringUtils.substring(product.getPrice().getFormattedValue(), 0, 1) + ProductUtils
                        .calculateQuantityPrice(
                                mQuantityEditText.getText().toString(),
                                (product.getVolumePrices() != null) ? ProductUtils.findVolumePrice(
                                        mQuantityEditText.getText().toString(), product.getVolumePrices()) : product.getPrice())));
            } else {
                Log.d(TAG, "Price is null");
            }

            if (product.getVolumePrices() != null) {
                mVolumePricingExpandableButton.setVisibility(View.VISIBLE);

            }

            setClickableAddToCartButton();

            // Updating images
            updateImageViewPagerIndicator(product.getImagesGallery());

        }
    }

    /**
     * Update the view pager for the images
     */
    private void updateImageViewPagerIndicator(List<Image> images) {
        mImageAdapter.clear();
        mImageAdapter.addAll(images);
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.setCurrentItem(mCurrentIndicator);
        mImageAdapter.notifyDataSetChanged();

        // for each image we create a matched indicator button with its listener
        for (int i = 0; i < mImageAdapter.getCount(); i++) {
            Button indicatorBtn = (Button) LayoutInflater.from(this.getActivity()).inflate(R.layout.viewpager_indicator_button,
                    mLayoutIndicator, false);
            if (mLayoutIndicator.findViewById(i) == null) {

                indicatorBtn.setId(i);

                indicatorBtn.setContentDescription("product_detail_image_indicator" + i);
                mLayoutIndicator.addView(indicatorBtn);

                mLayoutIndicator.findViewById(i).setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mViewPager.setCurrentItem(v.getId());
                        v.performClick();
                        return false;
                    }
                });
            }
        }


        if (mLayoutIndicator.findViewById(mViewPager.getCurrentItem()) != null) {
            mLayoutIndicator.findViewById(mViewPager.getCurrentItem()).setEnabled(false);
        }

    }

    /**
     * Define Action when cross button is clicked and quit the current product detail activity
     */
    private OnClickListener exitProductDetailButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };

    /**
     * Define Action when cross button is clicked and go back to product detail of the current zoomed product
     */
    private OnClickListener quitZoomButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            zoomImage(false);
            updateImageViewPagerIndicator(mProduct.getData().getImagesGallery());
            mCloseProductDetailButton.setOnClickListener(exitProductDetailButtonListener);
        }
    };

    /**
     * Display the order form
     */
    private OnClickListener orderFormButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "Order Form", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Define action when image view pager is touched
     */
    private OnTouchListener imageViewPagerOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.performClick();
            return false;
        }
    };

    /**
     * Prepare and create user interaction element from view
     */
    private OnPageChangeListener imageViewPagerOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {

            mCurrentIndicator = position;

            for (int i = 0; i < mImageAdapter.getCount(); i++) {

                if (mCurrentIndicator == i) {
                    mLayoutIndicator.findViewById(i).setEnabled(false);
                } else {
                    if (mLayoutIndicator.findViewById(i) != null) {
                        mLayoutIndicator.findViewById(i).setEnabled(true);
                    }
                }
            }
        }

    };

    /**
     * Define action when Volume Pricing is clicked
     */
    private OnClickListener expandableLayoutListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mVolumePricingExpandableLayout.getVisibility() == View.GONE) {
                UIUtils.expandLayout(getActivity(), mVolumePricingExpandableLayout);

                createVolumePricingTable(mVolumePricingExpandableLayout);

                mVolumePricingExpandableLayout.setVisibility(View.VISIBLE);
                mVolumePricingExpandableButton.setBackgroundColor(getResources().getColor(
                        R.color.product_detail_expandable_background));
                mVolumePricingExpandableLayout.setBackgroundColor(getResources().getColor(
                        R.color.product_detail_expandable_background));
                mProductPrice.setBackgroundColor(getResources().getColor(R.color.product_detail_expandable_background));
            } else {
                mVolumePricingExpandableLayout.removeAllViews();
                UIUtils.collapseLayout(getActivity(), mVolumePricingExpandableLayout);
                mVolumePricingExpandableButton.setBackgroundColor(Color.WHITE);
                mVolumePricingExpandableLayout.setBackgroundColor(Color.WHITE);

                mProductPrice.setBackgroundColor(Color.WHITE);
                mVolumePricingExpandableLayout.setVisibility(View.GONE);
            }
        }
    };

    /**
     * Create table to show data in a table
     *
     * @param volumePricingTable : Table which contains from volume pricing for the current product
     */
    private void createVolumePricingTable(TableLayout volumePricingTable) {
        if (mProduct.getData().getVolumePrices() != null) {
            TableRow headerRow = new TableRow(getActivity());

            TextView header1 = new TextView(getActivity());
            header1.setText(getString(R.string.product_detail_volume_qty));
            header1.setTypeface(null, Typeface.BOLD);
            headerRow.addView(header1);

            TextView header2 = new TextView(getActivity());
            header2.setText(getString(R.string.product_detail_volume_price));
            header2.setTypeface(null, Typeface.BOLD);
            headerRow.addView(header2);

            if (mProduct.getData().getVolumePrices().size() > 5) {
                TextView header3 = new TextView(getActivity());
                header3.setText(getString(R.string.product_detail_volume_qty));
                header3.setTypeface(null, Typeface.BOLD);
                headerRow.addView(header3);

                TextView header4 = new TextView(getActivity());
                header4.setText(getString(R.string.product_detail_volume_price));
                header4.setTypeface(null, Typeface.BOLD);
                headerRow.addView(header4);
            }
            volumePricingTable.addView(headerRow);

            for (Price volumePrice : mProduct.getData().getVolumePrices()) {
                TableRow contentRow = new TableRow(getActivity());

                if (volumePrice != null) {
                    TextView content1 = new TextView(getActivity());
                    content1.setText(volumePrice.getMinQuantity() + " - " + volumePrice.getMaxQuantity());
                    contentRow.addView(content1);

                    TextView content2 = new TextView(getActivity());
                    content2.setText(volumePrice.getFormattedValue());
                    contentRow.addView(content2);

                    if (mProduct.getData().getVolumePrices().size() > 5) {
                        TextView content3 = new TextView(getActivity());
                        content3.setText(volumePrice.getMinQuantity() + " - " + volumePrice.getMaxQuantity());
                        contentRow.addView(content3);

                        TextView content4 = new TextView(getActivity());
                        content4.setText(volumePrice.getFormattedValue());
                        contentRow.addView(content4);
                    }
                } else {
                    Log.d(TAG, "Price or Stock is null");
                }

                volumePricingTable.addView(contentRow);
            }
        }

    }

    /**
     * Define action when add to cart button is focused then set quantity to add to cart and update current availability
     * and closed the keyboard
     */
    private OnFocusChangeListener quantityEditTextListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!mQuantityEditText.hasFocus()) {
                UIUtils.hideKeyboard(getActivity());
            }
        }
    };

    /**
     * Define action when add to cart button is clicked then put the current product in the cart with price, quantity and
     * update the availability
     */
    public OnClickListener addToCartButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            addToCart();

        }
    };

    /**
     * Enable Add to cart button when text is entered in quantity field
     */
    private void setClickableAddToCartButton() {
        boolean enable = false;

        if (B2BApplication.isOnline()) {
            try {
                enable = (mQuantityEditText.getText() != null && !mQuantityEditText.getText().toString().isEmpty()
                        && StringUtils.isNotBlank(mQuantityEditText.getText().toString()) && Integer.parseInt(mQuantityEditText.getText()
                        .toString()) > 0) && mProduct != null && !mProduct.getData().isOutOfStock();
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        // Offline we disable the add to cart and the quantity
        else {
            mQuantityEditText.setEnabled(false);
            enable = false;
        }

        enableAddToCartButton(enable);
    }

    /**
     * Enable / disable the add to cart button
     *
     * @param enable
     */
    private void enableAddToCartButton(boolean enable) {
        mAddToCartButton.setEnabled(enable);
        mAddToCartButton.setClickable(enable);
        mProductDetailAddToCartText.setEnabled(enable);
    }

    /**
     * Display an image in a view pager style and show tab indicator for this selected image
     */
    private class ImagePagerAdapter extends PagerAdapter {
        private List<Image> imagesUrl;

        public void clear() {
            imagesUrl.clear();
        }

        public void addAll(List<Image> listToAdd) {
            imagesUrl.addAll(listToAdd);
        }

        public ImagePagerAdapter(List<Image> imagesUrl) {
            this.imagesUrl = imagesUrl;
        }

        @Override
        public int getCount() {
            return imagesUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            if (!imagesUrl.isEmpty() && position < imagesUrl.size() && StringUtils.isNotBlank(imagesUrl.get(position).getUrl())) {
                B2BApplication.getContentServiceHelper().loadImage(imagesUrl.get(position).getUrl(), null, imageView, 0, 0, true,
                        null, false);
                imageView.setContentDescription("product_detail_image_viewer" + position);
            }

            mViewPager.addView(imageView, 0);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomImage(true);
                    mCloseProductDetailButton.setOnClickListener(quitZoomButtonListener);
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mViewPager.removeView((ImageView) object);
        }
    }

    /**
     * Detect when text is changed
     */
    private TextWatcher quantityEditTextTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mProduct != null) {
                mTotalPriceText
                        .setText((StringUtils.substring(mProduct.getData().getPrice().getFormattedValue(), 0, 1) + ProductUtils
                                .calculateQuantityPrice(
                                        mQuantityEditText.getText().toString(),
                                        (mProduct.getData().getVolumePrices() != null) ? ProductUtils.findVolumePrice(mQuantityEditText
                                                .getText().toString(), mProduct.getData().getVolumePrices()) : mProduct.getData().getPrice())));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            setClickableAddToCartButton();
            mQuantityEditText.setBackgroundResource(R.drawable.quantity_editext_selector);
        }

    };

    /**
     * Handle user interaction to collapse or handle_anchor expandable panel
     */
    private class ExpandablePanelListener implements OnClickListener {
        Button expandableButton;
        TextView expandableTextView;

        public ExpandablePanelListener(Button expandableButton, TextView expandableTextView) {
            this.expandableButton = expandableButton;
            this.expandableTextView = expandableTextView;
        }

        @Override
        public void onClick(View v) {
            if (mProduct != null) {
                if (this.expandableTextView.getVisibility() == View.GONE) {
                    UIUtils.expandLayout(getActivity(), expandableTextView);
                    this.expandableButton.setBackgroundColor(Color.WHITE);
                    Drawable icon = getActivity().getResources().getDrawable(R.drawable.minus_icon);
                    this.expandableButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                    this.expandableTextView.setVisibility(View.VISIBLE);
                } else {
                    UIUtils.collapseLayout(getActivity(), expandableTextView);

                    this.expandableButton.setBackgroundColor(getResources().getColor(R.color.product_detail_expandable_background));
                    Drawable icon = getActivity().getResources().getDrawable(R.drawable.plus_icon);
                    this.expandableButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                    this.expandableTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Class to handle_anchor User interaction with multi-dimensional spinner
     */
    private OnItemSelectedListener productDetailVariantSpinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getItemAtPosition(position) != null && mVariantPopulated) {
                ProductVariant selectedVariant = (ProductVariant) parent.getItemAtPosition(position);
                selectVariant(selectedVariant.getVariantOption().getCode());

                Spinner spinnerToUpdate = null;

                switch (parent.getId()) {
                    case R.id.product_detail_variant_spinner_1:
                        spinnerToUpdate = mProductDetailVariantSpinner2;
                        break;

                    case R.id.product_detail_variant_spinner_2:
                        spinnerToUpdate = mProductDetailVariantSpinner3;
                        break;

                    default:
                        break;
                }

                ProductHelper.populateSpinner(getActivity(), spinnerToUpdate, selectedVariant.getElements(), 0);
            }

            // Workaround to activate the onchange listener only after having instantiated all the  spinners
            mNbVariantLevelsInstantiated++;
            if (mNbVariantLevelsInstantiated == mNbVariantLevels) {
                mVariantPopulated = true;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Hide UI element to only display Image ViewPager to allow interaction with the ImageView
     *
     * @param isZoomed Zoom to ImageView other wise reset image
     */
    private void zoomImage(boolean isZoomed) {
        final ZoomImageView zoomImageView = new ZoomImageView(getActivity());
        zoomImageView.setScaleType(ZoomImageView.ScaleType.CENTER_INSIDE);
        zoomImageView.setClickable(true);
        zoomImageView.setFocusableInTouchMode(true);
        zoomImageView.setVisibility(View.GONE);
        zoomImageView.setContentDescription("product_detail_zoom_image_view");

        if (!mProduct.getData().getImagesGallery().isEmpty() && mCurrentIndicator < mProduct.getData().getImagesGallery().size()
                && StringUtils.isNotBlank(mProduct.getData().getImagesGallery().get(mCurrentIndicator).getUrl())) {
            B2BApplication.getContentServiceHelper().loadImage(
                    mProduct.getData().getImagesGallery().get(mCurrentIndicator).getUrl(), null, zoomImageView, 0, 0, true, null,
                    false);
        }

        if (isZoomed) {
            getActivity().getActionBar().hide();

            mProductDetailScrollView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mScrollViewLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mImageLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mImageLayout.setGravity(Gravity.CENTER);

            zoomImageView.setVisibility(View.VISIBLE);
            mLayoutIndicator.setVisibility(View.GONE);
            mImageLayout.addView(zoomImageView);
            mViewPager.setVisibility(View.GONE);
            mMiddleSection.setVisibility(View.GONE);
            mBottomSection.setVisibility(View.GONE);
            mViewDivider.setVisibility(View.GONE);
        } else {
            getActivity().getActionBar().show();

            mProductDetailScrollView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mScrollViewLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mImageLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.product_detail_viewpager_height);

            mImageLayout.removeAllViewsInLayout();
            mImageLayout.addView(mViewPager);
            mImageLayout.addView(mLayoutIndicator);
            mLayoutIndicator.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mMiddleSection.setVisibility(View.VISIBLE);
            mBottomSection.setVisibility(View.VISIBLE);
            mViewDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        B2BApplication.getContentServiceHelper().cancel(mProductDetailRequestId);
    }

    private void addToCart() {
        try {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString());

            CartHelper.addToCart(getActivity(), mProductDetailRequestId, new OnAddToCart() {

                @Override
                public void onAddToCart(ProductAdded productAdded) {

                    if (productAdded.isOutOfStock()) {
                        enableAddToCartButton(false);
                    } else if (productAdded.isQuantityAddedNotFulfilled()) {
                        mQuantityEditText.setText(productAdded.getQuantityAdded() + "");
                    } else {
                        mQuantityEditText.setText(getString(R.string.default_qty));
                    }

                    UIUtils.hideKeyboard(getActivity());

                }

                @Override
                public void onAddToCartError(boolean isOutOfStock) {
                    mQuantityEditText.setBackgroundResource(R.drawable.quantity_editext_invalid);
                    enableAddToCartButton(!isOutOfStock);
                    UIUtils.hideKeyboard(getActivity());
                }

            }, mProduct.getData().getCode(), quantity, Arrays.asList((View) mAddToCartButton, mProductDetailAddToCartText), null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

}
