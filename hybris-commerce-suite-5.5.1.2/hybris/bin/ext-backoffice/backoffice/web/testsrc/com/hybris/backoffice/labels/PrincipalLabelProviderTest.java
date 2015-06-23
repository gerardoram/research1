package com.hybris.backoffice.labels;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.backoffice.cockpitng.labels.impl.PrincipalLabelProvider;

import org.junit.Test;
import org.springframework.util.Assert;

@UnitTest
public class PrincipalLabelProviderTest {
    @Test
    public void blankTest()
    {
        final PrincipalLabelProvider provider = new PrincipalLabelProvider();
        Assert.isNull(provider.getIconPath(null));
    }
}
