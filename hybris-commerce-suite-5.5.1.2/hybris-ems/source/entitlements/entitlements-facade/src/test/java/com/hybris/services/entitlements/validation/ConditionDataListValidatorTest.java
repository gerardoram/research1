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
package com.hybris.services.entitlements.validation;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.hybris.services.entitlements.condition.ConditionData;
import com.hybris.services.entitlements.conversion.DateTimeConverter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/entitlement-service-test-spring.xml")
public class ConditionDataListValidatorTest
{
	public static final String ENTITLEMENT_TYPE = "Simple entitlement";

	@Autowired
	DateTimeConverter dateTimeConverter;

	@Autowired
//	@Qualifier("criterionDataValidator")
	private ConditionDataListValidator conditionDataListValidator;
	@Autowired
	private ApplicationContext context;

	@Test
	public void testGrantValidatorList()
	{
		final ConditionData condition = new ConditionData();
		condition.setType("string");

		List<ConditionData> conditionDataList = new ArrayList<>();
		conditionDataList.add(condition);
		conditionDataList.add(condition);
		conditionDataList.add(null);
		ValidationViolations validationViolations = new ValidationViolations();
		conditionDataListValidator.validate(conditionDataList, validationViolations);
		assertTrue(validationViolations.hasErrors());
	}

	@Test
    @Ignore("NULL in conditions is OK")
	public void testValidateRightCityGeoLocationForSingleCityCondition() throws IllegalAccessException
	{
		ValidationViolations validationViolations = new ValidationViolations();
		conditionDataListValidator.validate(null, validationViolations);
		assertTrue(validationViolations.hasErrors());
	}

	@Test
	public void testGrantValidatorList2()
	{
		final ConditionData condition = new ConditionData();
		condition.setType("non-exist");

		List<ConditionData> conditionDataList = new ArrayList<>();
		conditionDataList.add(condition);
		ValidationViolations validationViolations = new ValidationViolations();
		conditionDataListValidator.validate(conditionDataList, validationViolations);
		assertFalse(validationViolations.hasErrors());
	}
}
