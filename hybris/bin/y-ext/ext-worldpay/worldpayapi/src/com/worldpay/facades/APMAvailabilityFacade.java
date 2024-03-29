package com.worldpay.facades;

import com.worldpay.model.WorldpayAPMConfigurationModel;

/**
 * Interface that exposes methods to check the availability of the configured APMs
 */
public interface APMAvailabilityFacade {

    /**
     * Method to check if the APM configuration is currently available
     * @param apmConfiguration the apm configuration
     * @return true or false depending on if the apm configuration is available
     */
    boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration);

    /**
     * Method to check if APM configuration is currently available given code of APM
     * @param paymentMethod code of APM
     * @return true or false depending on if the apm configuration is available
     */
    boolean isAvailable(String paymentMethod);
}
