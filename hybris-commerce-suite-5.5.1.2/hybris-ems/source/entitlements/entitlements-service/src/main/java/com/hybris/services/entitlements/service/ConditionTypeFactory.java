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
package com.hybris.services.entitlements.service;

import com.hybris.services.entitlements.condition.ActionHandlerFactory;
import com.hybris.services.entitlements.condition.GrantValidator;

import java.util.Set;

/**
 * Provide type-specific implementations.
 */
public interface ConditionTypeFactory
{
	/**
	 * Executor.
	 *
	 * @param conditionType type name
	 * @return object that performs check for given type
	 */
	ActionHandlerFactory getExecutor(String conditionType);

	/**
	 * Validator.
	 *
	 * @param conditionType type name
	 * @return object that can validate corresponding conditions and criteria
	 */
	GrantValidator getValidator(String conditionType);

	/**
	 * Available types.
	 *
	 * @return set of known types
	 */
	Set<String> getKnownTypes();
}
