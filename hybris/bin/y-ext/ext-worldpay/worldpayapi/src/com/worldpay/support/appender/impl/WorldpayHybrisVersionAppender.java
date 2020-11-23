package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the hybris version
 */
public class WorldpayHybrisVersionAppender extends WorldpaySupportEmailAppender {

    protected static final String HYBRIS_BUILD_VERSION_KEY = "build.version";

    protected final ConfigurationService configurationService;

    public WorldpayHybrisVersionAppender(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String hybrisVersion = configurationService.getConfiguration().getString(HYBRIS_BUILD_VERSION_KEY);
        return "Hybris version: " + hybrisVersion + System.lineSeparator();
    }
}
