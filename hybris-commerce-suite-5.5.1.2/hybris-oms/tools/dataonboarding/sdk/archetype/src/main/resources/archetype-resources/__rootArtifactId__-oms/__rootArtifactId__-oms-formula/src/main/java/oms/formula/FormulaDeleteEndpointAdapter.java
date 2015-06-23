#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.oms.formula;

import com.hybris.dataonboarding.framework.exceptions.ImportLineException;
import com.hybris.dataonboarding.framework.processor.AbstractTenantAwareEndpoint;
import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.domain.ats.AtsFormula;
import com.hybris.oms.domain.exception.EntityNotFoundException;
import com.hybris.oms.domain.exception.EntityValidationException;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Endpoint adapter for formula delete routes.
 */
public class FormulaDeleteEndpointAdapter extends AbstractTenantAwareEndpoint<AtsFormula>
{
	@Autowired
	private AtsFacade atsFacade;

	@Override
	protected void doInTenant(final AtsFormula atsFormula)
	{
		try
		{
			atsFacade.deleteFormula(atsFormula.getAtsId());
		}
		catch (final EntityValidationException e)
		{
			throw new ImportLineException(FormulaErrorCode._401, "Invalid request for formula deletion.", e);
		}
		catch (final EntityNotFoundException e)
		{
			throw new ImportLineException(FormulaErrorCode._402, "Formula not found for formula deletion", e);
		}
	}

	public void setAtsFacade(final AtsFacade atsFacade)
	{
		this.atsFacade = atsFacade;
	}
}
