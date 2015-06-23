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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractLocalizedValueResolverTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@UnitTest
public class ProductClassificationAttributesValueResolverTest extends AbstractLocalizedValueResolverTest
{
	@Mock
	private ClassificationService classificationService;

	@Mock
	private ProductModel product;

	@Mock
	private ClassAttributeAssignmentModel indexedPropertyAssignment;

	@Mock
	private ClassAttributeAssignmentModel localizedIndexedPropertyAssignment;

	@Mock
	private FeatureList featureList;

	@Mock
	private Feature feature;

	@Mock
	private LocalizedFeature localizedFeature;

	@Mock
	private FeatureValue featureValue1;

	@Mock
	private FeatureValue featureValue2;

	private ProductClassificationAttributesValueResolver valueResolver;

	@Before
	public void setUp()
	{
		getIndexedProperty().setClassAttributeAssignment(indexedPropertyAssignment);
		getLocalizedIndexedProperty().setClassAttributeAssignment(localizedIndexedPropertyAssignment);

		valueResolver = new ProductClassificationAttributesValueResolver();
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
		valueResolver.setClassificationService(classificationService);
	}

	@Test
	public void resolveAttributeWithNoValue() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = null;

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
	}

	@Test
	public void resolveNonLocalizedAttribute() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = new Object();

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, attributeValue, null);
	}

	@Test
	public void resolveLocalizedAttribute() throws Exception
	{
		// given
		final IndexedProperty localizedIndexedProperty = getLocalizedIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(localizedIndexedProperty);
		final Object localizedAttributeValue = new Object();

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(localizedIndexedProperty.getClassAttributeAssignment())).thenReturn(
				localizedFeature);
		when(localizedFeature.getValues(Locale.ENGLISH)).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(localizedAttributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(localizedIndexedProperty, localizedAttributeValue, EN_LANGUAGE_QUALIFIER);
	}

	@Test
	public void resolveLocalizedAttributeWithMultipleLanguages() throws Exception
	{
		// given
		final IndexedProperty localizedIndexedProperty = getLocalizedIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(localizedIndexedProperty);
		final Object enAttributeValue = new Object();
		final Object deAttributeValue = new Object();

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier(), getDeQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(localizedIndexedProperty.getClassAttributeAssignment())).thenReturn(
				localizedFeature);
		when(localizedFeature.getValues(Locale.ENGLISH)).thenReturn(Collections.singletonList(featureValue1));
		when(localizedFeature.getValues(Locale.GERMAN)).thenReturn(Collections.singletonList(featureValue2));
		when(featureValue1.getValue()).thenReturn(enAttributeValue);
		when(featureValue2.getValue()).thenReturn(deAttributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(localizedIndexedProperty, enAttributeValue, EN_LANGUAGE_QUALIFIER);
		verify(getInputDocument()).addField(localizedIndexedProperty, deAttributeValue, DE_LANGUAGE_QUALIFIER);
	}

	@Test
	public void resolveWithMultipleIndexedProperties() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final IndexedProperty localizedIndexedProperty = getLocalizedIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Arrays.asList(indexedProperty, localizedIndexedProperty);
		final Object attributeValue = new Object();
		final Object localizedAttributeValue = new Object();

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);

		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		when(featureList.getFeatureByAssignment(localizedIndexedProperty.getClassAttributeAssignment())).thenReturn(
				localizedFeature);
		when(localizedFeature.getValues(Locale.ENGLISH)).thenReturn(Collections.singletonList(featureValue2));
		when(featureValue2.getValue()).thenReturn(localizedAttributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, attributeValue, null);
		verify(getInputDocument()).addField(localizedIndexedProperty, localizedAttributeValue, EN_LANGUAGE_QUALIFIER);
	}

	@Test
	public void resolveNonOptionalAttributeWithValue() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = new Object();

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.OPTIONAL_PARAM,
				Boolean.FALSE.toString());

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(localizedFeature);
		when(localizedFeature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, attributeValue, null);
	}

	@Test
	public void resolveNonOptionalAttributeWithEmtyStringValue() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final String attributeValue = "";

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.OPTIONAL_PARAM,
				Boolean.FALSE.toString());

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(localizedFeature);
		when(localizedFeature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// expect
		expectedException.expect(FieldValueProviderException.class);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);
	}

	@Test
	public void resolveNonOptionalAttributeWithNoValue() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final String attributeValue = null;

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.OPTIONAL_PARAM,
				Boolean.FALSE.toString());

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(localizedFeature);
		when(localizedFeature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// expect
		expectedException.expect(FieldValueProviderException.class);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);
	}

	@Test
	public void resolveWithSplit() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = "a b";

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.SPLIT_PARAM,
				Boolean.TRUE.toString());

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, "a", null);
		verify(getInputDocument()).addField(indexedProperty, "b", null);
	}

	@Test
	public void resolveWithSplitRegex() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = "a/b";

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.SPLIT_PARAM,
				Boolean.TRUE.toString());
		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.SPLIT_REGEX_PARAM, "/");

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, "a", null);
		verify(getInputDocument()).addField(indexedProperty, "b", null);
	}

	@Test
	public void resolveNonStringAttributeWithSplit() throws Exception
	{
		// given
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);
		final Object attributeValue = new Object();

		indexedProperty.getValueProviderParameters().put(ProductClassificationAttributesValueResolver.SPLIT_PARAM,
				Boolean.TRUE.toString());

		when(getQualifierProvider().getAvailableQualifiers(getFacetSearchConfig(), getIndexedType())).thenReturn(
				Arrays.asList(getEnQualifier()));
		when(classificationService.getFeatures(eq(product), anyListOf(ClassAttributeAssignmentModel.class)))
				.thenReturn(featureList);
		when(featureList.getFeatureByAssignment(indexedProperty.getClassAttributeAssignment())).thenReturn(feature);
		when(feature.getValues()).thenReturn(Collections.singletonList(featureValue1));
		when(featureValue1.getValue()).thenReturn(attributeValue);

		// when
		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument()).addField(indexedProperty, attributeValue, null);
	}
}
