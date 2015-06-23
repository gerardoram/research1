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

import com.hybris.dataonboarding.framework.extract.SchemalessAttributesExtractor;
import com.hybris.dataonboarding.framework.transform.map.impl.DefaultMap2PojoTransformer;
import com.hybris.dataonboarding.framework.transform.map.impl.SchemalessMap2PojoTransformer;
import com.hybris.oms.domain.ats.AtsFormula;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test covering high level {@link Map2FormulaTransformer} logic.
 */
public class Map2FormulaTransformerTest
{

	private static final String FORMULA_ATS_ID_VALUE = "binCode_X93G";
	private static final String FORMULA_ATS_ID_KEY = "atsId";
	private static final String FORMULA_DESCRIPTION_VALUE = "location_A";
	private static final String FORMULA_DESCRIPTION_KEY = "description";
	private static final String FORMULA_FORMULA_VALUE = "dummy";
	private static final String FORMULA_FORMULA_KEY = "formula";
	private static final String FORMULA_NAME_VALUE = "binCode_X93G";
	private static final String FORMULA_NAME_KEY = "name";

	@Autowired
	private Map2FormulaTransformer transformer;

	@Before
	public void setUp()
	{
		final SchemalessMap2PojoTransformer schemalessMap2PojoTransformer = new SchemalessMap2PojoTransformer();
		schemalessMap2PojoTransformer.setSchemalessAttributesExtractor(new SchemalessAttributesExtractor());
		schemalessMap2PojoTransformer.setMap2PojoTransformer(new DefaultMap2PojoTransformer());

		transformer = new Map2FormulaTransformer();
		transformer.setMap2PojoTransformer(schemalessMap2PojoTransformer);
	}

	@Test
	public void shouldMap2Bin()
	{
		final Map<String, Serializable> map = new HashMap<>();
		map.put(FORMULA_ATS_ID_KEY, FORMULA_ATS_ID_VALUE);
		map.put(FORMULA_DESCRIPTION_KEY, FORMULA_DESCRIPTION_VALUE);
		map.put(FORMULA_FORMULA_KEY, FORMULA_FORMULA_VALUE);
		map.put(FORMULA_NAME_KEY, FORMULA_NAME_VALUE);

		final AtsFormula formula = this.transformer.transform(map);
		Assert.assertEquals(FORMULA_ATS_ID_KEY, FORMULA_ATS_ID_VALUE, formula.getAtsId());
		Assert.assertEquals(FORMULA_DESCRIPTION_KEY, FORMULA_DESCRIPTION_VALUE, formula.getDescription());
		Assert.assertEquals(FORMULA_FORMULA_KEY, FORMULA_FORMULA_VALUE, formula.getFormula());
		Assert.assertEquals(FORMULA_NAME_KEY, FORMULA_NAME_VALUE, formula.getName());
	}
}
