package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.AddressLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class AddressLabelProviderTest
{
	@Test
	public void blankTest()
	{
        final AddressLabelProvider provider=new AddressLabelProvider();
        Assert.isNull(provider.getIconPath(null));
    }
}
