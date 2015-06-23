package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.ProductLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;

@UnitTest
public class ProductLabelProviderTest {
    @Test
    public void blankTest()
    {
        final ProductLabelProvider provider = new ProductLabelProvider();
        Assert.isNull(provider.getIconPath(null));
    }
}
