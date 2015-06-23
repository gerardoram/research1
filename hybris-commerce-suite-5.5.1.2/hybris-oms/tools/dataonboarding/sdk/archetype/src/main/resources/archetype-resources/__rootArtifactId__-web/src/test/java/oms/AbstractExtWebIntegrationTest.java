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
package ${package}.oms;

import com.hybris.dataonboarding.framework.test.AbstractDomainDataonboardingTest;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/${parentArtifactId}-web-spring-test.xml"})
@Ignore
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.AbstractClassWithoutAnyMethod"})
public abstract class AbstractExtWebIntegrationTest extends AbstractDomainDataonboardingTest
{
	// nothing to declare
}
