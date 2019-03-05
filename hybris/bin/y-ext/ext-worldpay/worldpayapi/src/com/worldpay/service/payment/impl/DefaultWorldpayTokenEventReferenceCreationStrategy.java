package com.worldpay.service.payment.impl;

import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import de.hybris.platform.order.CartService;
import org.springframework.beans.factory.annotation.Required;

import java.time.Instant;

/**
 * Creates Token event reference
 */
public class DefaultWorldpayTokenEventReferenceCreationStrategy implements WorldpayTokenEventReferenceCreationStrategy {

    public static final String UNDERSCORE = "_";
    private CartService cartService;

    /**
     * Create Token event reference from cart code
     * @return
     */
    @Override
    public String createTokenEventReference() {
        final String cartCode = cartService.getSessionCart().getCode();
        return cartCode + UNDERSCORE + Instant.now().toEpochMilli();
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
