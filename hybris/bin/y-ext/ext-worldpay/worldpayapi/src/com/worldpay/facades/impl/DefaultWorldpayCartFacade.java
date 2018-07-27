package com.worldpay.facades.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.facades.WorldpayCartFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartFacade implements WorldpayCartFacade {

    private WorldpayCartService worldpayCartService;
    private CartService cartService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetDeclineCodeAndShopperBankOnCart(final String shopperBankCode) {
        worldpayCartService.resetDeclineCodeAndShopperBankOnCart(shopperBankCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingAddressFromPaymentInfo() {
        final CartModel sessionCart = cartService.getSessionCart();
        if (sessionCart.getPaymentAddress() == null) {
            sessionCart.setPaymentAddress(sessionCart.getPaymentInfo().getBillingAddress());
            cartService.saveOrder(sessionCart);
        }
    }

    @Required
    public void setWorldpayCartService(final WorldpayCartService worldpayCartService) {
        this.worldpayCartService = worldpayCartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }
}
