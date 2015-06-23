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
package de.hybris.platform.storefront.yforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.platform.xyformsservices.enums.YFormDataTypeEnum;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Tests for the EmbeddedFormXml class to check commit status and correct values returned by xpath queries
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ XPathFactory.class })
public class EmbeddedFormXmlTest
{
	@InjectMocks
	private EmbeddedFormXml formXml;

	@Mock
	private XPath xpath;

	@Mock
	private XPathExpression xpathExpression;

	@Mock
	private Document domDocument;

	@Mock
	private NodeList nodeList;

	@Mock
	private Node nodeZero;
	@Mock
	private Node nodeZeroChild;
	@Mock
	private Node nodeOne;
	@Mock
	private Node nodeOneChild;
	@Mock
	private Node nodeTwo;
	@Mock
	private Node nodeTwoChild;

	@Mock
	XPathFactory xPathFactory;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(XPathFactory.class);
		PowerMockito.when(XPathFactory.newInstance()).thenReturn(xPathFactory);
		when(xPathFactory.newXPath()).thenReturn(xpath);

		formXml = new EmbeddedFormXml("appId", "formId", "dataId", domDocument, YFormDataTypeEnum.DRAFT);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals(formXml.getApplicationId(), "appId");
		assertEquals(formXml.getFormId(), "formId");
		assertEquals(formXml.getDataId(), "dataId");
		assertEquals(formXml.getDocument(), domDocument);
		assertEquals(formXml.getDataType(), YFormDataTypeEnum.DRAFT);
	}

	@Test
	public void testEmptyFormIsNotCommitted() throws Exception
	{
		assertTrue(formXml.isCommitted());
		formXml.setDocument(null);
		assertFalse(formXml.isCommitted());
	}

	@Test
	public void testSingleFormMock() throws Exception
	{
		when(xpath.compile("/form/test")).thenReturn(xpathExpression);
		when(xpathExpression.evaluate(domDocument)).thenReturn("test123");

		final String singleNode = formXml.safelyEvaluateSingle("/form/test");
		assertEquals(singleNode, "test123");
	}

	@Test
	public void testMultipleFormMock() throws Exception
	{
		when(xpath.compile("/form/test2")).thenReturn(xpathExpression);
		when(xpathExpression.evaluate(domDocument, XPathConstants.NODESET)).thenReturn(nodeList);
		when(nodeList.getLength()).thenReturn(3);

		when(nodeList.item(0)).thenReturn(nodeZero);
		when(nodeZero.getFirstChild()).thenReturn(nodeZeroChild);
		when(nodeZeroChild.getNodeValue()).thenReturn("test1");

		when(nodeList.item(1)).thenReturn(nodeOne);
		when(nodeOne.getFirstChild()).thenReturn(nodeOneChild);
		when(nodeOneChild.getNodeValue()).thenReturn("test2");

		when(nodeList.item(2)).thenReturn(nodeTwo);
		when(nodeTwo.getFirstChild()).thenReturn(nodeTwoChild);
		when(nodeTwoChild.getNodeValue()).thenReturn("test3");

		final List<String> multiNodes = formXml.safelyEvaluateMultiple("/form/test2");

		assertEquals(multiNodes.size(), 3);
		assertEquals(multiNodes.get(0), "test1");
		assertEquals(multiNodes.get(1), "test2");
		assertEquals(multiNodes.get(2), "test3");
	}

}
