package com.worldpay.service.payment.impl;

import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import de.hybris.platform.order.CartService;


import java.time.Instant;

/**
 * Creates Token event reference
 */
public class DefaultWorldpayTokenEventReferenceCreationStrategy implements WorldpayTokenEventReferenceCreationStrategy {

    public static final String UNDERSCORE = "_";
    protected final CartService cartService;

    public DefaultWorldpayTokenEventReferenceCreationStrategy(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Create Token event reference from cart code
     * @return
     */
    @Override
    public String createTokenEventReference() {
        final String cartCode = cartService.getSessionCart().getCode();
        return cartCode + UNDERSCORE + Instant.now().toEpochMilli();
    }

}
