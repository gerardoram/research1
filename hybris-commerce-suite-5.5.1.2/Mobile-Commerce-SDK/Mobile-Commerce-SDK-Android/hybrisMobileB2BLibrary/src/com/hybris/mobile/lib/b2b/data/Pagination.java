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
package com.hybris.mobile.lib.b2b.data;

public class Pagination
{

	private int currentPage;
	private int pageSize;
	private int totalResults;
	private int totalPages;

	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getTotalResults()
	{
		return totalResults;
	}

	public void setTotalResults(int totalResults)
	{
		this.totalResults = totalResults;
	}

	public int getTotalPages()
	{
		return totalPages;
	}

	public void setTotalPages(int totalPages)
	{
		this.totalPages = totalPages;
	}

}
