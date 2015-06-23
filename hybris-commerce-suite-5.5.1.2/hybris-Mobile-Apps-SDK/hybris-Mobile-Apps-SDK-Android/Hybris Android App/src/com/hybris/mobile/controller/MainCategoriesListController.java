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
package com.hybris.mobile.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.app.Activity;

import com.hybris.mobile.Hybris;
import com.hybris.mobile.WebserviceMethodEnums;
import com.hybris.mobile.loader.RESTLoader;
import com.hybris.mobile.loader.RESTLoaderObserver;
import com.hybris.mobile.loader.RESTLoaderResponse;
import com.hybris.mobile.model.Category;
import com.hybris.mobile.model.GenericValue;
import com.hybris.mobile.query.QueryText;
import com.hybris.mobile.utility.JsonUtils;


public class MainCategoriesListController extends Controller implements RESTLoaderObserver
{

	private List<String> mSearchSuggestions = new ArrayList<String>();
	private List<Category> mModel = new ArrayList<Category>();

	private String mCurrentQueryString = "";

	public static final int MESSAGE_MODEL_UPDATED = 1;
	public static final int MESSAGE_SEARCH_SUGGESTIONS = 2;

	public List<Category> getModel()
	{
		return mModel;
	}

	public List<String> getSearchSuggestions()
	{
		return mSearchSuggestions;
	}

	public void getSpellingSuggestions(Activity context, String queryText)
	{
		mCurrentQueryString = queryText;
		QueryText query = new QueryText();
		query.setQueryText(mCurrentQueryString);
		RESTLoader.execute(context, WebserviceMethodEnums.METHOD_SPELLING_SUGGESTIONS, query, this, true, false);
	}

	public void getCategories(Category category)
	{
		if (category != null && category.getChildCategories() != null)
		{
			mModel.clear();
			mModel.addAll(category.getChildCategories());
		}
		notifyOutboxHandlers(MainCategoriesListController.MESSAGE_MODEL_UPDATED, 0, 0, null);
	}

	@Override
	public void onReceiveResult(RESTLoaderResponse restLoaderResponse, WebserviceMethodEnums webserviceEnumMethod)
	{
		if (restLoaderResponse.getCode() == RESTLoaderResponse.SUCCESS)
		{
			String jsonResult = restLoaderResponse.getData();

			switch (webserviceEnumMethod)
			{

				case METHOD_SPELLING_SUGGESTIONS:
					List<GenericValue> listGenericValue = JsonUtils.fromJsonList(jsonResult, "suggestions", GenericValue.class);

					mSearchSuggestions.clear();

					// Add previous searches that pass filter 
					mSearchSuggestions.addAll(filteredAndSortedPreviousSearches());

					if (listGenericValue != null)
					{
						for (GenericValue genericValue : listGenericValue)
						{
							if (!mSearchSuggestions.contains(genericValue.getValue()))
							{
								mSearchSuggestions.add(genericValue.getValue());
							}
						}
					}

					notifyOutboxHandlers(MainCategoriesListController.MESSAGE_SEARCH_SUGGESTIONS, 0, 0, null);

				default:
					break;

			}

		}
		else if (restLoaderResponse.getCode() == RESTLoaderResponse.ERROR)
		{
			switch (webserviceEnumMethod)
			{
				case METHOD_SPELLING_SUGGESTIONS:
					notifyOutboxHandlers(MainCategoriesListController.MESSAGE_MODEL_UPDATED, 0, 0, null);
					break;
				default:
					break;
			}
		}
	}



	/**
	 * Filters and sorts the previous searches Filtering done against the current search text.
	 * 
	 * @return list of previous searches to display
	 */
	private List<String> filteredAndSortedPreviousSearches()
	{
		Set<String> unfilteredStrings = Hybris.getPreviousSearches();

		List<String> filteredStrings = new ArrayList<String>();
		for (String s : unfilteredStrings)
		{
			if (s != null && s.startsWith(mCurrentQueryString))
			{
				filteredStrings.add(s);
			}
		}
		Collections.sort(filteredStrings);
		return filteredStrings;
	}

}
