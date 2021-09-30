package com.worldpay.core.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Prime Routing service interface. This service is responsible for verifying if prime routing is enabled and other prime routing functionalities
 */
public interface WorldpayPrimeRoutingService {

    /**
     * Verifies if prime routing is enable for the given cart.
     * For it to be enabled feature must be enabled on the base site and billing address must be a US address
     *
     * @param cartModel the cart
     * @return true if prime routing is enabled, false otherwise
     */
    boolean isPrimeRoutingEnabled(AbstractOrderModel cartModel);

    /**
     * Sets the flag authorised with prime routing to true
     *
     * @param cart the cart
     */
    void setAuthorisedWithPrimeRoutingOnCart(AbstractOrderModel cart);

    /**
     * Returns true if order with the given code is authorised with prime routing
     *
     * @param worldpayOrderCode teh worldpay order code
     * @return true if it's authorised with Prime Routing, false otherwise
     */
    boolean isOrderAuthorisedWithPrimeRouting(String worldpayOrderCode);

}
