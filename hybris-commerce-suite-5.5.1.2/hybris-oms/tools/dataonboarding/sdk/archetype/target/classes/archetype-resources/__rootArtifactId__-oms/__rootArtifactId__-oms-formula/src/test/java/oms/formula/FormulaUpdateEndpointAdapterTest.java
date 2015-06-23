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

import com.hybris.commons.context.impl.DefaultContext;
import com.hybris.commons.tenant.MultiTenantContextService;
import com.hybris.dataonboarding.framework.exceptions.AbstractImportException;
import com.hybris.dataonboarding.framework.exceptions.ImportLineException;
import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.domain.ats.AtsFormula;
import com.hybris.oms.domain.exception.EntityNotFoundException;
import com.hybris.oms.domain.exception.EntityValidationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class FormulaUpdateEndpointAdapterTest
{
	private FormulaUpdateEndpointAdapter adapter;
	@Mock
	private AtsFacade atsFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		adapter = new FormulaUpdateEndpointAdapter();
		adapter.setTenantContextService(new MultiTenantContextService(new DefaultContext()));
		adapter.setAtsFacade(atsFacade);
	}

	@Test
	public void testExecuteEndpointAdapter()
	{
		adapter.process(FormulaTestUtils.buildImportLineMessageHeader(), FormulaTestUtils.buildFormula());
		Mockito.verify(atsFacade).updateFormula(Mockito.anyString(), Mockito.any(AtsFormula.class));
	}

	@Test
	public void testReturnCorrectErrorCodeWhenEntityNotFound()
	{
		Mockito.doThrow(EntityNotFoundException.class).when(atsFacade)
				.updateFormula(Mockito.anyString(), Mockito.any(AtsFormula.class));

		try
		{
			adapter.process(FormulaTestUtils.buildImportLineMessageHeader(), FormulaTestUtils.buildFormula());
			Assert.fail();
		}
		catch (final ImportLineException e)
		{
			Assert.assertEquals(FormulaErrorCode._302.getCode(), AbstractImportException.getErrorCode(e));
		}
	}

	@Test
	public void testReturnCorrectErrorCodeWhenEntityValidationError()
	{
		Mockito.doThrow(EntityValidationException.class).when(atsFacade)
				.updateFormula(Mockito.anyString(), Mockito.any(AtsFormula.class));

		try
		{
			adapter.process(FormulaTestUtils.buildImportLineMessageHeader(), FormulaTestUtils.buildFormula());
			Assert.fail();
		}
		catch (final ImportLineException e)
		{
			Assert.assertEquals(FormulaErrorCode._301.getCode(), AbstractImportException.getErrorCode(e));
		}
	}
}
