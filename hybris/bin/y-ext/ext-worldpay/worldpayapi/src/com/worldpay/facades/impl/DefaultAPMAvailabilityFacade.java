package com.worldpay.facades.impl;

import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.platform.order.CartService;
import org.springframework.beans.factory.annotation.Required;


/**
 * {@inheritDoc}
 */
public class DefaultAPMAvailabilityFacade implements APMAvailabilityFacade {

    private APMAvailabilityService apmAvailabilityService;
    private CartService cartService;

    /**
     * {@inheritDoc}
     */
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration) {
        return apmAvailabilityService.isAvailable(apmConfiguration, cartService.getSessionCart());
    }

    @Required
    public void setApmAvailabilityService(final APMAvailabilityService apmAvailabilityService) {
        this.apmAvailabilityService = apmAvailabilityService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
