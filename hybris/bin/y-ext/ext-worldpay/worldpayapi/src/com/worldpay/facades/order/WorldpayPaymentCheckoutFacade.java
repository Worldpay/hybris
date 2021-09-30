package com.worldpay.facades.order;

import de.hybris.platform.commercefacades.user.data.AddressData;

/**
 * Worldpay specific checkout facade interface. This is overridden from the standard to ensure the worldpay information
 * is included at the correct point. Adds convenience methods required for worldpay interaction
 */
public interface WorldpayPaymentCheckoutFacade {

    /**
     * Sets billing details to the session cart (payment address)
     *
     * @param addressData the address data
     */
    void setBillingDetails(AddressData addressData);

    /**
     * Sets shipping and billing details to the session cart
     *
     * @param addressData the address data
     */
    void setShippingAndBillingDetails(AddressData addressData);

    /**
     * Does the session cart have a billing details (payment address)
     *
     * @return the boolean
     */
    boolean hasBillingDetails();

    /**
     * Return true if Fraud Sight is enabled, false otherwise
     *
     * @return true if enabled, false otherwise
     */
    boolean isFSEnabled();
}
