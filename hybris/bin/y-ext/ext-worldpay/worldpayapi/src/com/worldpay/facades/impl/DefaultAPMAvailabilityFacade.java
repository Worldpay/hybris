package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.platform.order.CartService;


/**
 * {@inheritDoc}
 */
public class DefaultAPMAvailabilityFacade implements APMAvailabilityFacade {

    private final APMAvailabilityService apmAvailabilityService;
    private final CartService cartService;
    private final APMConfigurationLookupService apmConfigurationLookupService;

    public DefaultAPMAvailabilityFacade(final APMAvailabilityService apmAvailabilityService, final CartService cartService, final APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmAvailabilityService = apmAvailabilityService;
        this.cartService = cartService;
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration) {
        return apmAvailabilityService.isAvailable(apmConfiguration, cartService.getSessionCart());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable(final String paymentMethod) {
        final WorldpayAPMConfigurationModel apmConfiguration = apmConfigurationLookupService.getAPMConfigurationForCode(paymentMethod);
        if (apmConfiguration != null) {
            return this.isAvailable(apmConfiguration);
        }
        return false;
    }
}
