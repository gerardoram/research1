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
 *
 *  
 */
package de.hybris.platform.b2bacceleratorfacades.api.cart;

import de.hybris.platform.b2bacceleratorservices.enums.B2BCheckoutFlowEnum;
import de.hybris.platform.b2bacceleratorservices.enums.B2BCheckoutPciOptionEnum;


/**
 * The CheckoutFlowFacade supports resolving the {@link B2BCheckoutFlowEnum} for the current request.
 */
public interface CheckoutFlowFacade
{
	B2BCheckoutFlowEnum getCheckoutFlow();

	B2BCheckoutPciOptionEnum getSubscriptionPciOption();
}