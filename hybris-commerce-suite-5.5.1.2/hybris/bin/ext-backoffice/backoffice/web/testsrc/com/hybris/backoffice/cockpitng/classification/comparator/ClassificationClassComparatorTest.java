package com.hybris.backoffice.cockpitng.classification.comparator;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class ClassificationClassComparatorTest
{
	private ClassificationClassComparator comparator;

	@Before
	public void setUp()
	{
		comparator = new ClassificationClassComparator();
	}

	@Test
	public void testEqualNames()
	{
		final ClassificationClassModel leftObject = Mockito.mock(ClassificationClassModel.class);
		final ClassificationClassModel rightObject = Mockito.mock(ClassificationClassModel.class);
		Mockito.doReturn("n1").when(leftObject).getName();
		Mockito.doReturn("n1").when(rightObject).getName();

		Assert.assertEquals(0, getComparator().compare(leftObject, rightObject));
	}

	@Test
	public void testNotEqualNames()
	{
		final ClassificationClassModel leftObject = Mockito.mock(ClassificationClassModel.class);
		final ClassificationClassModel rightObject = Mockito.mock(ClassificationClassModel.class);
		Mockito.doReturn("n1").when(leftObject).getName();
		Mockito.doReturn("n2").when(rightObject).getName();

		Assert.assertEquals(-1, getComparator().compare(leftObject, rightObject));
	}

	@Test
	public void testLeftObjectContainsRightObject()
	{
		final ClassificationClassModel leftObject = Mockito.mock(ClassificationClassModel.class);
		final ClassificationClassModel rightObject = Mockito.mock(ClassificationClassModel.class);

		final Collection<?> models = Arrays.asList(rightObject);
		Mockito.doReturn(models).when(leftObject).getAllSubcategories();

		Assert.assertEquals(-1, getComparator().compare(leftObject, rightObject));
	}

	@Test
	public void testRightObjectContainsLeftObject()
	{
		final ClassificationClassModel leftObject = Mockito.mock(ClassificationClassModel.class);
		final ClassificationClassModel rightObject = Mockito.mock(ClassificationClassModel.class);

		final Collection<?> models = Arrays.asList(leftObject);
		Mockito.doReturn(models).when(rightObject).getAllSubcategories();

		Assert.assertEquals(1, getComparator().compare(leftObject, rightObject));
	}


	public ClassificationClassComparator getComparator()
	{
		return comparator;
	}
}
