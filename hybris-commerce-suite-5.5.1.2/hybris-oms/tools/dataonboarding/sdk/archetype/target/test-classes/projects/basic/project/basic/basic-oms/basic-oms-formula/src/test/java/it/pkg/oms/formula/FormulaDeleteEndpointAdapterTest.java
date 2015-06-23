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

import com.hybris.commons.context.impl.DefaultContext;
import com.hybris.commons.tenant.MultiTenantContextService;
import com.hybris.dataonboarding.framework.exceptions.AbstractImportException;
import com.hybris.dataonboarding.framework.exceptions.ImportLineException;
import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.domain.exception.EntityNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class FormulaDeleteEndpointAdapterTest
{
	private FormulaDeleteEndpointAdapter adapter;
	@Mock
	private AtsFacade atsFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		adapter = new FormulaDeleteEndpointAdapter();
		adapter.setTenantContextService(new MultiTenantContextService(new DefaultContext()));
		adapter.setAtsFacade(atsFacade);
	}

	@Test
	public void shouldExecuteEndpointAdapter()
	{
		adapter.process(FormulaTestUtils.buildImportLineMessageHeader(), FormulaTestUtils.buildFormula());
		Mockito.verify(atsFacade).deleteFormula(FormulaTestUtils.ATS_ID);
	}

	@Test
	public void testReturnCorrectErrorCodeWhenEntityNotFound()
	{
		Mockito.doThrow(EntityNotFoundException.class).when(atsFacade).deleteFormula(Mockito.anyString());

		try
		{
			adapter.process(FormulaTestUtils.buildImportLineMessageHeader(), FormulaTestUtils.buildFormula());
			Assert.fail();
		}
		catch (final ImportLineException e)
		{
			Assert.assertEquals(FormulaErrorCode._402.getCode(), AbstractImportException.getErrorCode(e));
		}
	}
}
