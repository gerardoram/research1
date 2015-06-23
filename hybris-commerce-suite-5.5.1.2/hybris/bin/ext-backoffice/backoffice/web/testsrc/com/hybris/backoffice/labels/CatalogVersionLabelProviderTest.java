package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.CatalogVersionLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class CatalogVersionLabelProviderTest
{
    @Test
    public void blankTest()
    {
        final CatalogVersionLabelProvider provider=new CatalogVersionLabelProvider();
        Assert.isNull(provider.getIconPath(null));
    }
}
