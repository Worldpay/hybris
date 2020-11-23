package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;

import static java.text.MessageFormat.format;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the hybris version
 */
public class WorldpayAddonVersionAppender extends WorldpaySupportEmailAppender {

    private static final String WORLDPAY_ADDON_VERSION_KEY = "worldpay.addon.version";
    protected final ConfigurationService configurationService;

    public WorldpayAddonVersionAppender(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String addonVersion = configurationService.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY);
        if (StringUtils.isBlank(addonVersion)) {
            return StringUtils.EMPTY;
        }

        return format("{0}Worldpay Plugin Version: {1}{0}", System.lineSeparator(), addonVersion);
    }
}
