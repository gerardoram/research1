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
import com.hybris.dataonboarding.framework.transform.exceptions.Map2PojoTransformationException;
import com.hybris.dataonboarding.framework.transform.map.Map2PojoTransformer;
import com.hybris.oms.domain.ats.AtsFormula;

import java.io.Serializable;
import java.util.Map;


/**
 * Custom transformer allowing custom transformation of formulas and then delegating to default transformer.
 */
public class Map2FormulaTransformer
{
	private Map2PojoTransformer map2PojoTransformer;

	/**
	 * Converts a map to an AtsFormula object.
	 * 
	 * @param map
	 *           the map to be converted to an AtsFormula object
	 * @return the AtsFormula object
	 * @throws ImportLineException if error occurs during transformations
	 */
	public AtsFormula transform(final Map<String, Serializable> map) throws ImportLineException
	{
		try
		{
			final AtsFormula result = map2PojoTransformer.transform(map, AtsFormula.class);

			if (result.getAtsId() == null || result.getAtsId().trim().isEmpty())
			{
				throw new ImportLineException(FormulaErrorCode._003, "Parameter ${symbol_escape}"atsId${symbol_escape}" cannot be null or empty.");
			}

			return result;
		}
		catch (final Map2PojoTransformationException e)
		{
			throw new ImportLineException(FormulaErrorCode._001, "Problem on transforming formula line", e);
		}
	}

	public void setMap2PojoTransformer(final Map2PojoTransformer map2PojoTransformer)
	{
		this.map2PojoTransformer = map2PojoTransformer;
	}
}
