package com.worldpay.service;

import com.worldpay.model.IntegrationVersionModel;

public interface WorldpayIntegrationVersionService {

    /**
     * Retrieves the last tree recorded integration versions
     *
     * @return String of integration versions comma separated
     */

    String getPreviousThreeIntegrationVersions();

    /**
     * Retrieves an integration version by its version number
     *
     * @param versionNumber the version number
     * @return {@link IntegrationVersionModel}
     */
    IntegrationVersionModel getIntegrationVersionByNumber(final String versionNumber);

    /**
     * Retrieves the current integration version value from configuration
     *
     * @return the current integration version value
     */
    String getCurrentIntegrationVersionValue();
}
