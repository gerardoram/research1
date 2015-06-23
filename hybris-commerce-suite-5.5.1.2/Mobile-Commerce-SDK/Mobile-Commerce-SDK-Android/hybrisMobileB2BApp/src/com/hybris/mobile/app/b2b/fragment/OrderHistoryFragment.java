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

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.adapter.OrderHistoryAdapter;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.order.Order;
import com.hybris.mobile.lib.b2b.query.QueryOrderHistory;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.http.utils.RequestUtils;
import com.hybris.mobile.lib.ui.listener.InfiniteScrollListener;
import com.hybris.mobile.lib.ui.view.Alert;


/**
 * Container that handle the details information for a specific order
 */
public class OrderHistoryFragment extends Fragment implements ResponseReceiver<List<Order>>
{
	private static final String TAG = OrderHistoryFragment.class.getCanonicalName();

	private String mOrderHistoryRequestId = RequestUtils.generateUniqueRequestId();
	private OrderHistoryAdapter mOrderHistoryAdapter;
	private ListView mOrderHistoryListView;
	private View mFooterLoadingListView;
	private LinearLayout mOrderHistoryHeader;

	private int mCurrentPage;
	private InfiniteScrollListener mInfiniteScrollListener;


	private List<Order> mOrderHistory;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_order_history, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);


		// Order History list
		mOrderHistoryListView = (ListView) getActivity().findViewById(R.id.order_history_list);
		mOrderHistoryHeader = (LinearLayout) getActivity().findViewById(R.id.order_history_header);

		mFooterLoadingListView = getActivity().getLayoutInflater().inflate(R.layout.loading_spinner, mOrderHistoryListView,
				false);
		mOrderHistoryListView.addFooterView(mFooterLoadingListView, null, false);

		mOrderHistoryAdapter = new OrderHistoryAdapter(getActivity(), new ArrayList<Order>());
		mOrderHistoryListView.setAdapter(mOrderHistoryAdapter);


		// Infinite scroll listener for the list view
		mInfiniteScrollListener = new InfiniteScrollListener()
		{

			@Override
			public void loadNextItems(int page)
			{
				mCurrentPage = page;
				updateOrderHistory();
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				super.onScrollStateChanged(view, scrollState);

				if (scrollState == SCROLL_STATE_IDLE)
				{
					B2BApplication.getContentServiceHelper().start();
				}
				else
				{
					B2BApplication.getContentServiceHelper().pause();
				}
			}
		};

		mOrderHistoryListView.setOnScrollListener(mInfiniteScrollListener);

	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (B2BApplication.isOnline())
		{
			updateOrderHistory();
			mOrderHistoryHeader.setVisibility(View.VISIBLE);
			mOrderHistoryListView.setVisibility(View.VISIBLE);
		}
		else
		{
			showLoading(false);
			mOrderHistoryHeader.setVisibility(View.INVISIBLE);
			mOrderHistoryListView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Call to update the product list from a category
	 */
	private void updateOrderHistory()
	{
		QueryOrderHistory queryOrderHistory = new QueryOrderHistory();
		queryOrderHistory.setCurrentPage(mCurrentPage);
		queryOrderHistory.setPageSize(B2BApplication.getConfiguration().getDefaultPageSize());

		// Getting the order
		B2BApplication.getContentServiceHelper().getOrderHistory(this, mOrderHistoryRequestId, queryOrderHistory, false, null,
				new OnRequestListener()
				{

					@Override
					public void beforeRequest()
					{
						showLoading(true);
					}

					@Override
					public void afterRequest(boolean isDataSynced)
					{
						showLoading(false);
					}
				});
	}

	@Override
	public void onResponse(Response<List<Order>> response)
	{
		if (response.getData() != null && !response.getData().isEmpty())
		{

			mOrderHistory = response.getData();
			updateOrderHistoryAdapter();
		}
	}

	@Override
	public void onError(Response<DataError> response)
	{
		Alert.showCritical(getActivity(), response.getData().getErrorMessage().getMessage());
	}

	@Override
	public void onStop()
	{
		super.onStop();
		B2BApplication.getContentServiceHelper().cancel(mOrderHistoryRequestId);
	}


	private void updateOrderHistoryAdapter()
	{

		mOrderHistoryAdapter.addAll(mOrderHistory);
		mOrderHistoryAdapter.notifyDataSetChanged();
	}

	/**
	 * Show the loading spinner
	 *
	 * @param show
	 */
	private void showLoading(boolean show)
	{
		// Adding the loading view for the list view

		if (show)
		{
			if (mOrderHistoryListView.getFooterViewsCount() == 0)
			{
				mOrderHistoryListView.addFooterView(mFooterLoadingListView, null, false);
			}
		}
		else
		{
			mOrderHistoryListView.removeFooterView(mFooterLoadingListView);
		}
	}
}