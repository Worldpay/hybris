package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.service.payment.WorldpayGuaranteedPaymentsStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;

import java.util.Optional;

/**
 * Worldpay checkout facade to ensure Worldpay details are included in correct place
 */
public class DefaultWorldpayPaymentCheckoutFacade implements WorldpayPaymentCheckoutFacade {

    protected final CheckoutFacade checkoutFacade;
    protected final WorldpayCheckoutService worldpayCheckoutService;
    protected final CartService cartService;
    protected final CustomerAccountService customerAccountService;
    protected final WorldpayFraudSightStrategy worldpayFraudSightStrategy;
    protected final WorldpayGuaranteedPaymentsStrategy worldpayGuaranteedPaymentsStrategy;
    protected final CustomerFacade customerFacade;

    public DefaultWorldpayPaymentCheckoutFacade(final CheckoutFacade checkoutFacade,
                                                final WorldpayCheckoutService worldpayCheckoutService,
                                                final CartService cartService,
                                                final CustomerAccountService customerAccountService,
                                                final WorldpayFraudSightStrategy worldpayFraudSightStrategy,
                                                final WorldpayGuaranteedPaymentsStrategy worldpayGuaranteedPaymentsStrategy,
                                                final CustomerFacade customerFacade) {
        this.checkoutFacade = checkoutFacade;
        this.worldpayCheckoutService = worldpayCheckoutService;
        this.cartService = cartService;
        this.customerAccountService = customerAccountService;
        this.worldpayFraudSightStrategy = worldpayFraudSightStrategy;
        this.worldpayGuaranteedPaymentsStrategy = worldpayGuaranteedPaymentsStrategy;
        this.customerFacade = customerFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingDetails(final AddressData addressData) {
        final CartModel cartModel = getCart();
        if (cartModel != null && addressData != null) {
            Optional.ofNullable(customerAccountService.getAddressForCode((CustomerModel) cartModel.getUser(), addressData.getId()))
                .ifPresent(addressModel -> worldpayCheckoutService.setPaymentAddress(cartModel, addressModel));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShippingAndBillingDetails(final AddressData addressData) {
        final CartModel cartModel = getCart();
        if (cartModel != null && addressData != null) {
            Optional.ofNullable(customerAccountService.getAddressForCode((CustomerModel) cartModel.getUser(), addressData.getId()))
                .ifPresent(addressModel -> worldpayCheckoutService.setShippingAndPaymentAddress(cartModel, addressModel));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBillingDetails() {
        final CartModel cartModel = getCart();
        return cartModel != null && cartModel.getPaymentAddress() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFSEnabled() {
        return worldpayFraudSightStrategy.isFraudSightEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGPEnabled() {
        return worldpayGuaranteedPaymentsStrategy.isGuaranteedPaymentsEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createCheckoutId() {
        final String userId = customerFacade.getCurrentCustomer().getCustomerId();
        if (cartService.hasSessionCart()) {
            return userId + "_" + cartService.getSessionCart().getCode();
        } else {
            return userId;
        }
    }

    protected CartModel getCart() {
        return checkoutFacade.hasCheckoutCart() ? cartService.getSessionCart() : null;
    }
}
