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
package de.hybris.platform.timedaccesspromotionsservices.alipay.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.chinaaccelerator.alipay.service.impl.AlipayNotifyServiceImpl;
import de.hybris.platform.timedaccesspromotionsservices.service.FlashbuyPromotionService;

import org.springframework.beans.factory.annotation.Required;


public class ExtendedAlipayNotifyServiceImpl extends AlipayNotifyServiceImpl
{
	private FlashbuyPromotionService flashbuyPromotionService;

	@Override
	public void executeAction(final OrderModel order)
	{
		super.executeAction(order);
		final String result = checkPaymentTransaction(order);

		if ("OK".equals(result))
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				final String promotionMatcher = entry.getPromotionMatcher();
				final String productCode = entry.getProduct().getCode();
				final String promotionCode = entry.getPromotionCode();
				
				if(promotionCode != null && promotionMatcher != null){
					flashbuyPromotionService.allocate(promotionCode, productCode, promotionMatcher);
				}
			}

		}
	}

	public FlashbuyPromotionService getFlashbuyPromotionService()
	{
		return flashbuyPromotionService;
	}

	@Required
	public void setFlashbuyPromotionService(final FlashbuyPromotionService flashbuyPromotionService)
	{
		this.flashbuyPromotionService = flashbuyPromotionService;
	}

}
