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
package de.hybris.platform.financialacceleratorstorefront.cockpit.labels.impl;

import de.hybris.platform.cockpit.services.label.AbstractModelLabelProvider;
import de.hybris.platform.financialservices.model.DisableRuleGeoAreaModel;


public class DisableRuleGeoAreaLabelProvider extends AbstractModelLabelProvider<DisableRuleGeoAreaModel>
{
	@Override
	protected String getItemLabel(final DisableRuleGeoAreaModel disableRule)
	{
		return disableRule.getAreaCode();
	}

	@Override
	protected String getItemLabel(final DisableRuleGeoAreaModel disableRule, final String languageIso)
	{
		return getItemLabel(disableRule);
	}

	@Override
	protected String getIconPath(final DisableRuleGeoAreaModel disableRule)
	{
		return null;
	}

	@Override
	protected String getIconPath(final DisableRuleGeoAreaModel disableRule, final String languageIso)
	{
		return null;
	}

	@Override
	protected String getItemDescription(final DisableRuleGeoAreaModel disableRule)
	{
		return disableRule.getAreaName();
	}

	@Override
	protected String getItemDescription(final DisableRuleGeoAreaModel disableRule, final String languageIso)
	{
		return getItemDescription(disableRule);
	}
}
