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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hybris.mobile.app.b2b.B2BConstants;
import com.hybris.mobile.app.b2b.IntentConstants;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.activity.OrderDetailActivity;
import com.hybris.mobile.lib.b2b.data.order.Order;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Adapter for the products of the order
 */
public class OrderHistoryAdapter extends ArrayAdapter<Order>
{
	private static final String TAG = OrderHistoryAdapter.class.getCanonicalName();

	public OrderHistoryAdapter(Activity context, List<Order> values)
	{
		super(context, R.layout.item_order_history, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View rowView;

		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.item_order_history, parent, false);
			rowView.setTag(new OrderHistoryViewHolder(rowView, position));
		}
		else
		{
			rowView = convertView;
		}

		OrderHistoryViewHolder orderHistoryViewHolder = (OrderHistoryViewHolder) rowView.getTag();

		final Order order = getItem(position);

		if (order != null)
		{
			// Order Code
			if (StringUtils.isNotBlank(order.getCode()))
			{
				orderHistoryViewHolder.orderNumberTextView.setText(order.getCode());
			}

			// Date placed / created
			try
			{
				String date = StringUtils.isBlank(order.getPlaced()) ? order.getCreated() : order.getPlaced();

				Date formattedDate = DateUtils
						.parseDate(date.replace("+", "-"), B2BConstants.DATE_FORMAT_FROM_CONTENT_SERVICE_HELPER);
				SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(B2BConstants.DATE_FORMAT_COMPLETE,
						SimpleDateFormat.getAvailableLocales()[0]);

				orderHistoryViewHolder.orderDatePlacedTextView.setText(mSimpleDateFormat.format(formattedDate));

			}
			catch (ParseException e)
			{
				Log.e(TAG, "Error parsing date. Details: " + e.getLocalizedMessage());
			}

			// Status

			if (StringUtils.isNotBlank(order.getStatusDisplay()))
			{
				orderHistoryViewHolder.orderStatusTextView.setText(order.getStatusDisplay());
			}

			// Total price
			if (order.getTotal() != null)
			{
				orderHistoryViewHolder.orderTotalTextView.setText(order.getTotal().getFormattedValue());
			}

			// Redirecting to the order detail page when clicking on the item
			orderHistoryViewHolder.orderHistoryLayout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intentOrderDetail = new Intent(getContext(), OrderDetailActivity.class);
					intentOrderDetail.putExtra(IntentConstants.ORDER_DETAIL_CODE, order.getCode());
					getContext().startActivity(intentOrderDetail);
				}
			});

		}
		return rowView;
	}


	/**
	 * Contains all UI elements for Order to improve view display while scrolling
	 */
	static class OrderHistoryViewHolder
	{
		private LinearLayout orderHistoryLayout;
		private TextView orderNumberTextView;
		private TextView orderDatePlacedTextView;
		private TextView orderStatusTextView;
		private TextView orderTotalTextView;

		public OrderHistoryViewHolder(View view, int position)
		{

			orderHistoryLayout = ((LinearLayout) view.findViewById(R.id.order_history_item));
			orderNumberTextView = ((TextView) view.findViewById(R.id.order_number));
			orderDatePlacedTextView = (TextView) view.findViewById(R.id.order_date_placed);
			orderStatusTextView = (TextView) view.findViewById(R.id.order_status);
			orderTotalTextView = ((TextView) view.findViewById(R.id.order_total));

			orderHistoryLayout.setContentDescription("order_history_item" + position);
			orderNumberTextView.setContentDescription("order_number" + position);
			orderDatePlacedTextView.setContentDescription("order_date_placed" + position);
			orderStatusTextView.setContentDescription("order_status" + position);
			orderTotalTextView.setContentDescription("order_total" + position);

		}
	}
}
