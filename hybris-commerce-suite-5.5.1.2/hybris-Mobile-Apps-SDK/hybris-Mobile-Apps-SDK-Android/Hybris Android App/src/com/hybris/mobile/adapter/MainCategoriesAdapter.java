/*******************************************************************************
 * [y] hybris Platform
 *  
 *   Copyright (c) 2000-2013 hybris AG
 *   All rights reserved.
 *  
 *   This software is the confidential and proprietary information of hybris
 *   ("Confidential Information"). You shall not disclose such Confidential
 *   Information and shall use it only in accordance with the terms of the
 *   license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hybris.mobile.R;
import com.hybris.mobile.model.Category;


public class MainCategoriesAdapter extends ArrayAdapter<Category>
{
	private final Context context;
	private final List<Category> objects;

	public MainCategoriesAdapter(Context context, List<Category> values)
	{
		super(context, R.layout.product_row, values);
		this.context = context;
		this.objects = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View rowView = convertView;

		if (rowView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.row_singleline_with_icon, parent, false);
		}

		TextView textView = (TextView) rowView.findViewById(R.id.textViewSingleLine);

		Category category = (Category) objects.get(position);
		textView.setText(category.getName());

		return rowView;
	}

}
