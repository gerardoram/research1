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

import com.hybris.dataonboarding.framework.processor.ImportLineMessageHeader;
import com.hybris.dataonboarding.framework.processor.ImportRunMessageHeader;
import com.hybris.oms.domain.ats.AtsFormula;

import java.io.File;


/**
 * Base class for all formula endpoint adapter unit tests.
 */
public final class FormulaTestUtils
{
	public static final String ATS_ID = "FORMULA_1";
	public static final String DESCRIPTION = "A very fast formula";
	public static final String NAME = "Formula1";
	public static final String FORMULA = "I[ON_HAND]";

	private FormulaTestUtils()
	{
		// avoid instantiation
	}

	public static AtsFormula buildFormula()
	{
		final AtsFormula atsFormula = new AtsFormula();
		atsFormula.setAtsId(ATS_ID);
		atsFormula.setDescription(DESCRIPTION);
		atsFormula.setName(NAME);
		atsFormula.setFormula(FORMULA);
		return atsFormula;
	}

	public static ImportLineMessageHeader buildImportLineMessageHeader()
	{
		final ImportRunMessageHeader importRunMessageHeader = new ImportRunMessageHeader("exchangeId", String.format(
				"%stenantId%sroute%sfilename.csv", File.separator, File.separator, File.separator));

		return new ImportLineMessageHeader(importRunMessageHeader, "rawLine", 0);
	}
}
