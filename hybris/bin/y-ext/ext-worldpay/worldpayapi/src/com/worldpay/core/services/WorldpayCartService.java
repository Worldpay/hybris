package com.worldpay.core.services;

import de.hybris.platform.core.model.order.CartModel;

/**
 * Service providing extended Worldpay cart service functionality.
 *
 * @spring.bean worldpayCartService
 */
public interface WorldpayCartService {

    /**
     * Sets shopper bank code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param shopperBankCode the shopper bank code
     */
    void resetDeclineCodeAndShopperBankOnCart(final String shopperBankCode);

    /**
     * Sets worldpay decline code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param worldpayOrderCode the worldpay order code
     * @param declineCode       the decline code
     */
    void setWorldpayDeclineCodeOnCart(final String worldpayOrderCode, final String declineCode);

    /**
     * Returns the cart for the given WorldpayOrderCode
     *
     * @param worldpayOrderCode
     * @return cart for the given worldpayCode
     */
    CartModel findCartByWorldpayOrderCode(final String worldpayOrderCode);

    /**
     * Store the session id from the initial payment request on the cart
     *
     * @param sessionId
     */
    void setSessionId(final String sessionId);
}
