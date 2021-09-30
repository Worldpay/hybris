package com.worldpay.facades;

/**
 * Provides specific methods to handle Worldpay functionality handled in the cart.
 */
public interface WorldpayCartFacade {

    /**
     * Sets shopper bank code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param shopperBankCode the shopper bank code
     */
    void resetDeclineCodeAndShopperBankOnCart(final String shopperBankCode);

    /**
     * Sets the payment address from the paymentInfo
     */
    void setBillingAddressFromPaymentInfo();
}
