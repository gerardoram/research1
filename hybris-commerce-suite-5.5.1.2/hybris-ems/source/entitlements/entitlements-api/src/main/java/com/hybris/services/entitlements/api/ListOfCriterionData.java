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

import com.hybris.services.entitlements.condition.CriterionData;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

// TODO is it DTO? move to API? rename to  ExecuteArgumentsData?

/**
 * Class represents arguments for
 * {@link com.hybris.services.entitlements.api.EntitlementFacade#execute(String, String, String, java.util.List, boolean)}
 * method.
 */
@XmlRootElement(name = "listOfCriterion")
public class ListOfCriterionData
{
	@XmlElementWrapper(name="criterions")
	@XmlElement(name="criterion")
	private List<CriterionData> criterionDataList;

	/**
	 * the Constructor.
	 */
	public ListOfCriterionData()
	{

	}

	/**
	 * the Constructor.
	 *
	 * @param criterionDataList = the list of CriterionData.
	 */
	public ListOfCriterionData(final List<CriterionData> criterionDataList)
	{
		this.criterionDataList = criterionDataList;
	}

	public List<CriterionData> getCriterionDataList()
	{
		return criterionDataList;
	}
}
