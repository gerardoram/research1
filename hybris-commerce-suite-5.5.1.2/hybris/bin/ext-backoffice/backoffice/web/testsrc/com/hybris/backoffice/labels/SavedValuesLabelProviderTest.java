package com.hybris.backoffice.labels;

import com.hybris.backoffice.cockpitng.labels.impl.PrincipalLabelProvider;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class SavedValuesLabelProviderTest
{
	@Test
	public void blankTest()
	{
		final PrincipalLabelProvider provider = new PrincipalLabelProvider();
		Assert.isNull(provider.getIconPath(null));
	}
}
