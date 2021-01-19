package com.worldpay.facades.impl;

import com.worldpay.core.address.services.WorldpayAddressService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.facades.WorldpayCartFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartFacade implements WorldpayCartFacade {

    protected final WorldpayCartService worldpayCartService;
    protected final CartService cartService;
    protected final WorldpayAddressService addressService;

    public DefaultWorldpayCartFacade(final WorldpayCartService worldpayCartService,
                                     final CartService cartService,
                                     final WorldpayAddressService addressService) {
        this.worldpayCartService = worldpayCartService;
        this.cartService = cartService;
        this.addressService = addressService;
    }

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
        if (cartService.hasSessionCart()) {
            final CartModel sessionCart = cartService.getSessionCart();

            validateParameterNotNull(sessionCart.getPaymentInfo(), "PaymentInfo cannot be null once selected an existing payment method.");
            final AddressModel clonedAddressFromPaymentInfo = addressService.cloneAddress(sessionCart.getPaymentInfo().getBillingAddress());
            addressService.setCartPaymentAddress(sessionCart, clonedAddressFromPaymentInfo);
        }
    }
}
