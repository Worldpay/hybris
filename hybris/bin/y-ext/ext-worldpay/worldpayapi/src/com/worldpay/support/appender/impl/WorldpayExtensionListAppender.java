package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.core.Registry;

import java.util.List;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the list of installed extensions
 */
public class WorldpayExtensionListAppender extends WorldpaySupportEmailAppender {

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final StringBuilder extensions = new StringBuilder();
        final List<String> tenantSpecificExtensionNames = getTenantSpecificExtensionNames();
        extensions.append(System.lineSeparator()).append("Extensions:").append(System.lineSeparator());
        for (final String tenantSpecificExtensionName : tenantSpecificExtensionNames) {
            extensions.append(ONE_TAB).append(tenantSpecificExtensionName).append(System.lineSeparator());
        }
        return extensions.toString();
    }

    protected List<String> getTenantSpecificExtensionNames() {
        return Registry.getCurrentTenantNoFallback().getTenantSpecificExtensionNames();
    }
}
