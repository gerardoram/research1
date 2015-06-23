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
package com.hybris.mobile.app.b2b.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.helper.CartHelper;
import com.hybris.mobile.app.b2b.helper.ProductHelper;
import com.hybris.mobile.app.b2b.utils.ProductUtils;
import com.hybris.mobile.app.b2b.utils.UIUtils;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.product.Product;
import com.hybris.mobile.lib.b2b.data.product.ProductVariant;
import com.hybris.mobile.lib.b2b.loader.ProductLoader;
import com.hybris.mobile.lib.b2b.query.QueryProductDetails;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.b2b.service.ContentServiceHelper;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.ui.listener.SubmitListener;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the products of the catalog in a listview
 */
public class ProductListAdapter extends ProductItemsAdapter
{
	public static final String TAG = ProductListAdapter.class.getCanonicalName();

	private int currentSelectedPosition = -1;
	private Product currentSelectedProduct;
	private boolean mTriggerSpinnerOnChange = false;
	private int mNbVariantLevels = 0;
	private int mNbVariantLevelsInstantiated = 0;
	private List<Spinner> mSpinnersVariants;

	public ProductListAdapter(Context context, Cursor c, int flags, ContentServiceHelper contentServiceHelper)
	{
		super(context, c, flags, contentServiceHelper);
	}


	@Override
	public void bindView(View rowView, final Context context, final Cursor cursor)
	{

		// When clicking outside a EditText, hide keyboard, remove focus and
		// reset to the default value
		// Clicking on the main view
		rowView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				UIUtils.hideKeyboard(getContext());
				v.performClick();
				return false;
			}
		});

		final ProductViewHolder mProductViewHolder = (ProductViewHolder) rowView.getTag();
		final Product product = getData();

		if (currentSelectedPosition == cursor.getPosition())
		{
			//TODO when item is in scrapview, index changed and wrong item is opened
			//createExpandedView(mProductViewHolder, cursor.getPosition());
		}
		else
		{

			// Populate name and code for a product when row collapsed
			mProductViewHolder.productName.setText(product.getName());
			mProductViewHolder.productNo.setText(product.getCode());
			mProductViewHolder.quantityEditText.setText(getContext().getString(R.string.default_qty));
			String rangeFormattedPrice = product.getPriceRangeFormattedValue();

			if (product.getPriceRange().getMaxPrice() != null)
			{
				rangeFormattedPrice =
						StringUtils.isNotBlank(product.getPriceRange().getMaxPrice().getFormattedValue()) && StringUtils.isNotBlank(
								product.getPriceRange().getMinPrice().getFormattedValue()) ? product.getPriceRangeFormattedValue() : "";

				if (StringUtils.isBlank(rangeFormattedPrice))
				{
					if (StringUtils.isNotBlank(product.getPriceRange().getMaxPrice().getValue()) && StringUtils.isNotBlank(
							product.getPriceRange().getMinPrice().getValue()))
					{

						rangeFormattedPrice =
								"$" + product.getPriceRange().getMinPrice().getValue() + ".00 - " + "$" + product.getPriceRange()
										.getMaxPrice().getValue() + ".00";
					}
				}
			}
			mProductViewHolder.productPrice.setText(rangeFormattedPrice);


			// Loading the product image
			loadProductImage(product.getImageThumbnailUrl(), mProductViewHolder.productImage,
					mProductViewHolder.productImageLoading, product.getCode());
			mProductViewHolder.collapse();

			if (product.isMultidimensional())
			{
				// Show arrow down with variants
				mProductViewHolder.productPriceTotal.setVisibility(View.GONE);
				mProductViewHolder.productImageViewCartIcon.setVisibility(View.GONE);
				mProductViewHolder.productImageViewExpandIcon.setVisibility(View.VISIBLE);
				mProductViewHolder.productItemAddQuantityLayout.setVisibility(View.GONE);
				mProductViewHolder.quantityEditText.setVisibility(View.GONE);
				mProductViewHolder.productAvailability.setVisibility(View.GONE);
				mProductViewHolder.productItemInStock.setVisibility(View.GONE);

				mProductViewHolder.productImageLoadingExpanded.setVisibility(View.VISIBLE);
				mProductViewHolder.productItemStockLevelLoadingExpanded.setVisibility(View.VISIBLE);
				mProductViewHolder.productImageExpanded.setVisibility(View.GONE);
				mProductViewHolder.productAvailabilityExpanded.setVisibility(View.GONE);

				/**
				 * Gray out button
				 */
				mProductViewHolder.setAddCartButton();
			}
			else
			{
				// Show cart icon without variants
				mProductViewHolder.productItemAddQuantityLayout.setVisibility(View.VISIBLE);
				mProductViewHolder.productPriceTotal.setVisibility(View.VISIBLE);
				mProductViewHolder.productPriceTotal.setText(mProductViewHolder.setTotalPrice(product.getPrice(),
						mProductViewHolder.quantityEditText.getText().toString()));
				mProductViewHolder.productImageViewCartIcon.setVisibility(View.VISIBLE);
				mProductViewHolder.productImageViewExpandIcon.setVisibility(View.GONE);
				mProductViewHolder.quantityEditText.setEnabled(true);
				mProductViewHolder.quantityEditText.setVisibility(View.VISIBLE);
				mProductViewHolder.productAvailability.setText(product.getStock().getStockLevel() + "");
				mProductViewHolder.productItemInStock.setVisibility(View.VISIBLE);

				mProductViewHolder.setAddCartButton();

				if (product.isLowStock() || product.isOutOfStock())
				{
					mProductViewHolder.productAvailability.setTextColor(getContext().getResources().getColor(
							R.color.product_item_low_stock));
					mProductViewHolder.productItemInStock.setTextColor(getContext().getResources().getColor(
							R.color.product_item_low_stock));
					mProductViewHolder.productAvailability.setContentDescription(getContext().getString(
							R.string.product_item_low_stock));

					if (product.isOutOfStock())
					{
						mProductViewHolder.quantityEditText.setEnabled(false);
						mProductViewHolder.quantityEditText.setText("");

					}

				}

				if (product.isInStock())
				{
					mProductViewHolder.productAvailability.setText("");
					mProductViewHolder.productItemInStock.setTextColor(getContext().getResources().getColor(
							R.color.product_item_in_stock));
				}
			}

		}

		/**
		 * Product item row is collapsed and user click the arrow down icon to expand
		 */
		mProductViewHolder.productImageViewExpandIcon.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Expanded
				QueryProductDetails queryProductDetails = new QueryProductDetails();
				queryProductDetails.setCode(StringUtils.isNotBlank(product.getFirstVariantCode()) ?
						product.getFirstVariantCode()
						:(product.getVariantMatrix() != null && product.getVariantMatrix().get(0).getVariantOption() != null) ?
						product.getVariantMatrix().get(0).getVariantOption().getCode() :
						product.getCode());   //Get the first variant by default if first variant is not available

				getContext().getLoaderManager().restartLoader(0, null,
						new ProductLoader(getContext(), B2BApplication.getContentServiceHelper(), new ResponseReceiver<Product>()
						{

							@Override
							public void onResponse(Response<Product> response)
							{

								currentSelectedProduct = response.getData();

								if (mCurrentSelectedViewHolder != null)
								{
									mCurrentSelectedViewHolder.collapse();
								}

								if (currentSelectedProduct != null)
								{
									currentSelectedPosition = cursor.getPosition();
									mCurrentSelectedViewHolder = mProductViewHolder;

									createExpandedView(mCurrentSelectedViewHolder, currentSelectedProduct);

									if (!mTriggerSpinnerOnChange)
									{
										mNbVariantLevels = ProductHelper.populateVariant(getContext(), mSpinnersVariants,
												currentSelectedProduct);

										ProductVariant firstVariant = (ProductVariant) mCurrentSelectedViewHolder.productItemVariantSpinner1.getItemAtPosition(0);

										if (firstVariant != null) {
											selectVariant(firstVariant);
										}
									}


								}

							}

							@Override
							public void onError(Response<DataError> response)
							{
								UIUtils.showError(response, getContext());
							}
						}, queryProductDetails, new OnRequestListener()
						{


							@Override
							public void beforeRequest()
							{
								// Expanded

								mProductViewHolder.productImageLoadingExpanded.setVisibility(View.VISIBLE);
								mProductViewHolder.productItemStockLevelLoadingExpanded.setVisibility(View.VISIBLE);
								mProductViewHolder.productImageExpanded.setVisibility(View.GONE);
								mProductViewHolder.productAvailabilityExpanded.setVisibility(View.GONE);
									UIUtils.showLoadingActionBar(getContext(), true);

							}

							@Override
							public void afterRequest(boolean isDataSynced)
							{

								mProductViewHolder.productImageLoadingExpanded.setVisibility(View.GONE);
								mProductViewHolder.productItemStockLevelLoadingExpanded.setVisibility(View.GONE);
								mProductViewHolder.productImageExpanded.setVisibility(View.VISIBLE);
								mProductViewHolder.productAvailabilityExpanded.setVisibility(View.VISIBLE);
								mProductViewHolder.productImageLoading.setVisibility(View.GONE);
									UIUtils.showLoadingActionBar(getContext(), false);

							}
						}));
			}
		});

		/**
		 * Detect when text is changed
		 */
		mProductViewHolder.quantityEditText.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

				try
				{

					if (cursor.getCount() > cursor.getPosition() && product != null)
					{
						if (product.getPrice() != null)
						{
							mProductViewHolder.productPriceTotal.setText(mProductViewHolder.setTotalPrice(product.getPrice(),
									mProductViewHolder.quantityEditText.getText().toString()));
						}
					}
					mProductViewHolder.setAddCartButton();
				}
				catch (NumberFormatException e)
				{
					Log.e(TAG, e.getLocalizedMessage());
				}

			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});

		/**
		 * Detect when text is changed
		 */
		mProductViewHolder.quantityEditTextExpanded.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

				try
				{
					if (cursor.getCount() > cursor.getPosition() && product.getPrice() != null)
					{
						mProductViewHolder.productPriceTotalExpanded.setText(mProductViewHolder.setTotalPrice(
								currentSelectedProduct.getPrice(), mProductViewHolder.quantityEditTextExpanded.getText().toString()));


					}
					mProductViewHolder.setAddCartButton();
				}
				catch (NumberFormatException e)
				{
					Log.e(TAG, e.getLocalizedMessage());
				}

			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});

		/**
		 * Add to cart when user click on cartIcon in Product item collapsed row
		 */
		mProductViewHolder.productImageViewCartIcon.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				addToCart(product.getCode(), mProductViewHolder.quantityEditText.getText().toString());
				mProductViewHolder.quantityEditText.setText(getContext().getString(R.string.default_qty));
			}
		});

		mProductViewHolder.quantityEditText.setOnEditorActionListener(new SubmitListener()
		{

			@Override
			public void onSubmitAction()
			{
				addToCart(product.getCode(), mProductViewHolder.quantityEditText.getText().toString());
				mProductViewHolder.quantityEditText.setText(getContext().getString(R.string.default_qty));
			}
		});


		/**
		 * Product item row is expanded and user click the arrow up icon to collapse
		 */
		mProductViewHolder.productItemButtonCollpaseLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// collapsed
				mProductViewHolder.collapse();
				mCurrentSelectedViewHolder.collapse();

			}
		});

		/**
		 * Product item row is collapsed and user click on the main part of the row to navigate to the product detail page
		 */
		mProductViewHolder.productItemClickableLayoutCollapsed.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ProductHelper.redirectToProductDetail(getContext(),
						StringUtils.isNotBlank(product.getFirstVariantCode()) ? product.getFirstVariantCode() : product.getCode());
			}
		});

		/**
		 * Product item row is collapsed and user click on the main part of the row to navigate to the product detail page
		 */
		mProductViewHolder.productItemClickableLayoutExpanded.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ProductHelper.redirectToProductDetail(getContext(),
						StringUtils.isNotBlank(product.getFirstVariantCode()) ? product.getFirstVariantCode() : product.getCode());
			}
		});

	}

	/**
	 * Show image Product from URL
	 *
	 * @param imageUrl    image url
	 * @param imageView
	 * @param progressBar
	 * @param productCode product code to find
	 */
	private void loadProductImage(String imageUrl, final ImageView imageView, final ProgressBar progressBar, String productCode)
	{

		// Loading the product image
		if (B2BApplication.isOnline())
		{
			if (StringUtils.isNotBlank(imageUrl))
			{
				B2BApplication.getContentServiceHelper().loadImage(imageUrl, "product_list_image_" + productCode, imageView, 0, 0,
						true, new OnRequestListener()
						{

							@Override
							public void beforeRequest()
							{
								imageView.setImageResource(android.R.color.transparent);
								imageView.setVisibility(View.GONE);
								progressBar.setVisibility(View.VISIBLE);
							}

							@Override
							public void afterRequest(boolean isDataSynced)
							{
								imageView.setVisibility(View.VISIBLE);
							}
						}, true);
			}
		}
		else
		{
			Log.i(TAG, "Application offline, displaying no image for product " + productCode);
			imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image_product));
			imageView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @param viewHolder
	 * @param currentExpandedProduct
	 */
	private void createExpandedView(ProductViewHolder viewHolder, Product currentExpandedProduct)
	{
		// By default the on change is not triggered on the variants spinner EXCEPT for the first one (prevent the call of onchange on each spinner)
		mTriggerSpinnerOnChange = false;
		mNbVariantLevelsInstantiated = 0;

		viewHolder.productNameExpanded.setText(currentExpandedProduct.getName());
		viewHolder.productNoExpanded.setText(currentExpandedProduct.getCode());

		viewHolder.productPriceExpanded.setText(currentExpandedProduct.getPriceRangeFormattedValue());

		viewHolder.productPriceTotalExpanded.setText(viewHolder.setTotalPrice(currentExpandedProduct.getPrice(),
				viewHolder.quantityEditTextExpanded.getText().toString()));

		if (currentExpandedProduct.isOutOfStock())
		{

			viewHolder.quantityEditTextExpanded.setEnabled(false);
			viewHolder.quantityEditTextExpanded.setText("");
			viewHolder.productAvailabilityExpanded.setText(currentExpandedProduct.getStock().getStockLevel() + "");
		}
		else
		{
			viewHolder.quantityEditTextExpanded.setEnabled(true);
			viewHolder.productAvailabilityExpanded.setText(currentExpandedProduct.getStock().getStockLevel() + "");
		}

		// Loading the product image for expanded view
		loadProductImage(currentExpandedProduct.getImageThumbnailUrl(), viewHolder.productImageExpanded,
				viewHolder.productImageLoadingExpanded, currentExpandedProduct.getCode());

		// Populate the spinner
		mSpinnersVariants = new ArrayList<Spinner>();
		mSpinnersVariants.add(viewHolder.productItemVariantSpinner1);
		mSpinnersVariants.add(viewHolder.productItemVariantSpinner2);
		mSpinnersVariants.add(viewHolder.productItemVariantSpinner3);
		mNbVariantLevels = ProductHelper.populateVariant(getContext(), mSpinnersVariants, currentExpandedProduct);

		viewHolder.productItemVariantSpinner2.setOnItemSelectedListener(productDetailVariantSpinnerListener);
		viewHolder.productItemVariantSpinner3.setOnItemSelectedListener(productDetailVariantSpinnerListener);
		viewHolder.productItemVariantSpinner1.setOnItemSelectedListener(productDetailVariantSpinnerListener);

		viewHolder.setAddCartButton();
		populateProductExpanded(currentExpandedProduct);
		viewHolder.expand();
	}

	/**
	 * Add to cart
	 *
	 * @param code : product code
	 * @param qty  : quantity to added
	 */
	private void addToCart(String code, String qty)
	{
		try
		{
			CartHelper.addToCart(getContext(), null, null, code, Integer.parseInt(qty), null, null);
		}
		catch (NumberFormatException e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/**
	 * Populate the product
	 *
	 * @param product
	 */
	private void populateProductExpanded(final Product product)
	{
		if (product != null)
		{
			/**
			 * Populate the view with data from response and associate it to the right element in the view
			 */
			mCurrentSelectedViewHolder.productNameExpanded.setText(product.getName());
			mCurrentSelectedViewHolder.productNoExpanded.setText(product.getCode());

			if (product.getStock() != null)
			{
				mCurrentSelectedViewHolder.productAvailabilityExpanded.setVisibility(View.VISIBLE);
				if (product.isLowStock() || product.isOutOfStock())
				{
					mCurrentSelectedViewHolder.productAvailabilityExpanded.setTextColor(getContext().getResources().getColor(
							R.color.product_item_low_stock));
					mCurrentSelectedViewHolder.productAvailabilityExpanded.setContentDescription(getContext().getString(
							R.string.product_item_low_stock));

					if (product.isOutOfStock())
					{
						mCurrentSelectedViewHolder.quantityEditTextExpanded.setEnabled(false);
						mCurrentSelectedViewHolder.quantityEditTextExpanded.setText("");
						mCurrentSelectedViewHolder.productAvailabilityExpanded.setText(product.getStock().getStockLevel() + "\n"
								+ getContext().getResources().getString(R.string.product_detail_in_stock));

					}

				}

				if (product.isInStock())
				{
					mCurrentSelectedViewHolder.productAvailabilityExpanded.setText(product.getStock().getStockLevel() + "\n"
							+ getContext().getResources().getString(R.string.product_detail_in_stock));
					mCurrentSelectedViewHolder.productAvailabilityExpanded.setTextColor(getContext().getResources().getColor(
							R.color.product_item_in_stock));
					mCurrentSelectedViewHolder.quantityEditTextExpanded.setEnabled(true);

				}


			}
			else
			{
				Log.d(TAG, "Stock is null");
			}


			mCurrentSelectedViewHolder.productPriceExpanded.setText((product.getVolumePrices() != null) ? product
					.getPriceRangeFormattedValue() + " | " : product.getPriceRangeFormattedValue());

			// Set the price with the default total value
			mCurrentSelectedViewHolder.productPriceTotalExpanded.setText((StringUtils.substring(product.getPrice()
					.getFormattedValue(), 0, 1) + ProductUtils.calculateQuantityPrice(
					mCurrentSelectedViewHolder.quantityEditTextExpanded.getText().toString(),
					(product.getVolumePrices() != null) ? ProductUtils.findVolumePrice(
							mCurrentSelectedViewHolder.quantityEditTextExpanded.getText().toString(), product.getVolumePrices())
							: product.getPrice())));

			// Loading the product image for expanded view
			loadProductImage(product.getImageThumbnailUrl(), mCurrentSelectedViewHolder.productImageExpanded,
					mCurrentSelectedViewHolder.productImageLoadingExpanded, product.getCode());

			/**
			 * Add to cart when user click on cartIcon in Product item expanded row
			 */
			mCurrentSelectedViewHolder.productImageViewCartIconExpanded.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					addToCart(product.getCode(), mCurrentSelectedViewHolder.quantityEditTextExpanded.getText().toString());
					mCurrentSelectedViewHolder.quantityEditTextExpanded.setText(getContext().getString(R.string.default_qty));
				}
			});

			mCurrentSelectedViewHolder.quantityEditTextExpanded.setOnEditorActionListener(new SubmitListener()
			{

				@Override
				public void onSubmitAction()
				{
					// Perform action on key press
					addToCart(product.getCode(), mCurrentSelectedViewHolder.quantityEditTextExpanded.getText().toString());
					mCurrentSelectedViewHolder.quantityEditTextExpanded.setText(getContext().getString(R.string.default_qty));
				}
			});
		}
	}

	/**
	 * Get variant from dropdown and display info in product details page
	 *
	 * @param pSelectedVariant
	 */
	private void selectVariant(ProductVariant pSelectedVariant)
	{
		QueryProductDetails queryProductDetails = new QueryProductDetails();
		queryProductDetails.setCode(pSelectedVariant.getVariantOption().getCode());

		getContext().getLoaderManager().restartLoader(0, null,
				new ProductLoader(getContext(), B2BApplication.getContentServiceHelper(), new ResponseReceiver<Product>()
				{

					@Override
					public void onResponse(Response<Product> response)
					{
						currentSelectedProduct = response.getData();
						if (currentSelectedProduct != null)
						{
							populateProductExpanded(currentSelectedProduct);
						}
					}

					@Override
					public void onError(Response<DataError> response)
					{
						UIUtils.showError(response, getContext());
					}
				}, queryProductDetails, new OnRequestListener()
				{

					@Override
					public void beforeRequest()
					{
						// Expanded
						mCurrentSelectedViewHolder.productImageLoadingExpanded.setVisibility(View.VISIBLE);
						mCurrentSelectedViewHolder.productItemStockLevelLoadingExpanded.setVisibility(View.VISIBLE);
						mCurrentSelectedViewHolder.productImageExpanded.setVisibility(View.GONE);
						mCurrentSelectedViewHolder.productAvailabilityExpanded.setVisibility(View.GONE);
						UIUtils.showLoadingActionBar(getContext(), true);
					}

					@Override
					public void afterRequest(boolean isDataSynced)
					{
						mCurrentSelectedViewHolder.productImageLoadingExpanded.setVisibility(View.GONE);
						mCurrentSelectedViewHolder.productItemStockLevelLoadingExpanded.setVisibility(View.GONE);
						mCurrentSelectedViewHolder.productImageExpanded.setVisibility(View.VISIBLE);
						mCurrentSelectedViewHolder.productAvailabilityExpanded.setVisibility(View.VISIBLE);
						mCurrentSelectedViewHolder.productImageLoading.setVisibility(View.GONE);
						UIUtils.showLoadingActionBar(getContext(), false);
					}
				}));
	}

	/**
	 * Class to handle_anchor User interaction with multi-dimensional spinner
	 */
	public OnItemSelectedListener productDetailVariantSpinnerListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{

			if (parent.getItemAtPosition(position) != null && mTriggerSpinnerOnChange)
			{
				ProductVariant mSelectedVariant = (ProductVariant) parent.getItemAtPosition(position);
				selectVariant(mSelectedVariant);

				Spinner spinnerToUpdate = null;

				switch (parent.getId())
				{
					case R.id.product_item_variant_spinner_1:
						spinnerToUpdate = mCurrentSelectedViewHolder.productItemVariantSpinner2;
						break;

					case R.id.product_item_variant_spinner_2:
						spinnerToUpdate = mCurrentSelectedViewHolder.productItemVariantSpinner3;
						break;

					default:
						break;
				}

				ProductHelper.populateSpinner(getContext(), spinnerToUpdate, mSelectedVariant.getElements(), 0);
			}

			// Workaround to activate the onchange listener only after having instantiated the latest spinner
			mNbVariantLevelsInstantiated++;
			if (mNbVariantLevelsInstantiated == mNbVariantLevels)
			{
				mTriggerSpinnerOnChange = true;
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{
		}
	};

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2)
	{
		View view = mInflater.inflate(R.layout.item_product_listview, arg2, false);
		view.setTag(new ProductViewHolder(view));
		return view;
	}
}
