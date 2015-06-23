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
package it.pkg.oms.formula;

import com.hybris.dataonboarding.framework.exceptions.ImportLineException;
import com.hybris.dataonboarding.framework.processor.AbstractTenantAwareEndpoint;
import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.domain.ats.AtsFormula;
import com.hybris.oms.domain.exception.EntityValidationException;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Endpoint adapter for formula insert routes.
 */
public class FormulaInsertEndpointAdapter extends AbstractTenantAwareEndpoint<AtsFormula>
{
	@Autowired
	private AtsFacade atsFacade;

	@Override
	protected void doInTenant(final AtsFormula atsFormula)
	{
		try
		{
			atsFacade.createFormula(atsFormula);
		}
		catch (final EntityValidationException e)
		{
			throw new ImportLineException(FormulaErrorCode._101, "Invalid request for formula insert.", e);
		}
	}

	public void setAtsFacade(final AtsFacade atsFacade)
	{
		this.atsFacade = atsFacade;
	}
}
