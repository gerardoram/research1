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
package com.hybris.services.entitlements.condition;

/**
 * A factory to provide action-specific execution handlers for given condition type.
 */
public interface ActionHandlerFactory
{
	/**
	 * Type of condition processed by the factory.
	 *
	 * @return condition type. See {@link com.hybris.services.entitlements.condition.ConditionData#getType()}.
	 */
	String getConditionType();

	/**
	 * Get action handler.
	 *
	 * @param action action
	 * @return corresponding action handler
	 */
	ActionHandler getActionHandler(String action);
}
