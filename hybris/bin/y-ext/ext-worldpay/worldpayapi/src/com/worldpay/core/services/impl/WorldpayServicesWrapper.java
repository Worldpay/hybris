package com.worldpay.core.services.impl;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

public class WorldpayServicesWrapper {

    protected final ModelService modelService;
    protected final SessionService sessionService;
    protected final EnumerationService enumerationService;
    protected final ConfigurationService configurationService;

    public WorldpayServicesWrapper(final ModelService modelService,
                                   final SessionService sessionService,
                                   final EnumerationService enumerationService,
                                   final ConfigurationService configurationService) {
        this.modelService = modelService;
        this.sessionService = sessionService;
        this.enumerationService = enumerationService;
        this.configurationService = configurationService;
    }
}
