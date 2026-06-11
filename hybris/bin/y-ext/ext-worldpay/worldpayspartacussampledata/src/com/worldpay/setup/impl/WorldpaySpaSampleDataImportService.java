package com.worldpay.setup.impl;

import de.hybris.platform.addonsupport.setup.impl.DefaultAddonSampleDataImportService;
import de.hybris.platform.core.initialization.SystemSetupContext;

/**
 * This class extends {@link DefaultAddonSampleDataImportService} and specifies how to import custom data spartacus
 */
public class WorldpaySpaSampleDataImportService extends DefaultAddonSampleDataImportService {

    private static final String ELECTRONICS = "electronics";
    private static final String POWERTOOLS = "powertools";
    private static final String APPAREL_UK = "apparel-uk";
    private static final String APPAREL_DE = "apparel-de";
    private static final String SPA = "-spa";

    @Override
    protected void importContentCatalog(final SystemSetupContext context, final String importRoot, final String catalogName) {

        super.importContentCatalog(context, importRoot, catalogName);

        if (catalogName.equals(ELECTRONICS) || catalogName.equals(POWERTOOLS) || catalogName.equals(APPAREL_UK) || catalogName.equals(APPAREL_DE)) {
            synchronizeContentCatalog(context, catalogName + SPA, true);
        }
    }
}
