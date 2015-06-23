package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.CategoryLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class CategoryLabelProviderTest
{
	@Test
	public void blankTest()
	{
		final CategoryLabelProvider provider = new CategoryLabelProvider();
		Assert.isNull(provider.getIconPath(null));
	}
}
