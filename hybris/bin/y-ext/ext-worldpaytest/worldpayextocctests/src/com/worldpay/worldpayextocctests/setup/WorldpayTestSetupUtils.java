package com.worldpay.worldpayextocctests.setup;

import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils;
import de.hybris.platform.core.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class WorldpayTestSetupUtils extends TestSetupUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayTestSetupUtils.class);

    private static SetupImpexService getSetupImpexService() {
        return Registry.getApplicationContext().getBean("setupImpexService", SetupImpexService.class);
    }

    public static void loadExtensionDataInJunit() {
        Registry.setCurrentTenantByID("junit");
        LOG.info("Importing worldpay occ test data");
        getSetupImpexService().importImpexFile("/worldpayextocctests/import/coredata/common/store.impex", true, false);
        getSetupImpexService().importImpexFile("/worldpayextocctests/import/coredata/common/essential-data.impex", true, false);
        getSetupImpexService().importImpexFile("/worldpayextocctests/import/sampledata/cms-content.impex", true, false);
    }
}
