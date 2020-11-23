package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayHOPNoReturnParamsStrategy;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.math.BigDecimal;

public class DefaultWorldpayHOPNoReturnParamsStrategy implements WorldpayHOPNoReturnParamsStrategy {
    protected final CartService cartService;

    public DefaultWorldpayHOPNoReturnParamsStrategy(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedirectAuthoriseResult authoriseCart() {
        final RedirectAuthoriseResult redirectAuthoriseResult = new RedirectAuthoriseResult();
        final CartModel cartModel = cartService.getSessionCart();

        redirectAuthoriseResult.setPaymentStatus(AuthorisedStatus.AUTHORISED);
        redirectAuthoriseResult.setPaymentAmount(BigDecimal.valueOf(cartModel.getTotalPrice()));
        redirectAuthoriseResult.setPending(true);

        return redirectAuthoriseResult;
    }
}
