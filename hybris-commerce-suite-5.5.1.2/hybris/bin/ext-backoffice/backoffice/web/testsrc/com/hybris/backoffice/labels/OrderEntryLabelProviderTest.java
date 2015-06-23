package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.OrderEntryLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class OrderEntryLabelProviderTest
{
	@Test
	public void blankTest()
	{
		final OrderEntryLabelProvider provider = new OrderEntryLabelProvider();
		Assert.isNull(provider.getIconPath(null));
	}
}
