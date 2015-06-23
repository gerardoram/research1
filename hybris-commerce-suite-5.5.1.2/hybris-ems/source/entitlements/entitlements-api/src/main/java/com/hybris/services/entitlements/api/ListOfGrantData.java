/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.services.entitlements.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represent arguments for
 * {@link EntitlementFacade#getGrants(String, String, String, String)}
 * method.
 */
@XmlRootElement(name = "listOfGrants")
public class ListOfGrantData
{
	@XmlElementWrapper(name="grants")
	@XmlElement(name="grant")
	private List<GrantData> grantDataList;

	/**
	 * the Constructor.
	 */
	public ListOfGrantData()
	{

	}

	/**
	 * the Constructor.
	 *
	 * @param grantDataList = the list of GrantData.
	 */
	public ListOfGrantData(final List<GrantData> grantDataList)
	{
		this.grantDataList = grantDataList;
	}

	public List<GrantData> getGrantDataList()
	{
		return grantDataList;
	}
}
