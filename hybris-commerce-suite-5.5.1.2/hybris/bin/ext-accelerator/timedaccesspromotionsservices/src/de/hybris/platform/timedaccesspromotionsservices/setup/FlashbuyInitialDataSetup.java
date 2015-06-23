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
package de.hybris.platform.timedaccesspromotionsservices.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.timedaccesspromotionsservices.constants.TimedaccesspromotionsservicesConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = TimedaccesspromotionsservicesConstants.EXTENSIONNAME)
public class FlashbuyInitialDataSetup extends AbstractSystemSetup
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(FlashbuyInitialDataSetup.class);

	public static final String TIMED_ACCESS_PROMOTIONS_SERVICES = "timedaccesspromotionsservices";

	private static final String PROMOTION_IMPEX = "/timedaccesspromotionsservices/import/sampledata/stores/electronics-cn/promotions.impex";
	private static final String FLASHBUYPAGE_IMPEX = "/timedaccesspromotionsservices/import/sampledata/contentCatalogs/electronics-cnContentCatalog/FlashbuyPage.impex";

	private static final String IMPORT_CORE_DATA = "importCoreData";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";

	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));

		return params;
	}


	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{

		final List<ImportData> importData = new ArrayList<ImportData>();

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName(TIMED_ACCESS_PROMOTIONS_SERVICES);
		sampleImportData.setContentCatalogNames(Arrays.asList(TIMED_ACCESS_PROMOTIONS_SERVICES));
		sampleImportData.setStoreNames(Arrays.asList(TIMED_ACCESS_PROMOTIONS_SERVICES));
		importData.add(sampleImportData);

		flashBuyDataExecute(this, context, importData);

	}

	public void flashBuyDataExecute(final AbstractSystemSetup systemSetup, final SystemSetupContext context,
			final List<ImportData> importData)
	{
		final boolean importCoreData = systemSetup.getBooleanSystemSetupParameter(context, IMPORT_CORE_DATA);
		final boolean importSampleData = systemSetup.getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA);

		if (importCoreData)
		{
		}

		if (importSampleData)
		{
			for (final ImportData data : importData)
			{
				// the impex of promotion and flashbuy page
				importFlashBuyData(systemSetup, context, data, false, PROMOTION_IMPEX);
				importFlashBuyData(systemSetup, context, data, false, FLASHBUYPAGE_IMPEX);
			}
		}
	}

	void importFlashBuyData(final AbstractSystemSetup systemSetup, final SystemSetupContext context, final ImportData importData,
			final boolean syncCatalogs, final String dataLocation)
	{
		systemSetup.logInfo(context, String.format("Begin importing data for [%s]", context.getExtensionName()));
		getSetupImpexService().importImpexFile(String.format(dataLocation, context.getExtensionName()), false);

	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	@Required
	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	@Required
	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}
}
