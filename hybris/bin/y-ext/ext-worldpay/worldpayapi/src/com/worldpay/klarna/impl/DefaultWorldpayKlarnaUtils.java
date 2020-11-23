package com.worldpay.klarna.impl;

import com.worldpay.klarna.WorldpayKlarnaUtils;

import java.util.List;
import java.util.Objects;

public class DefaultWorldpayKlarnaUtils implements WorldpayKlarnaUtils {
    private final List<String> klarnaPayments;

    public DefaultWorldpayKlarnaUtils(final List<String> klarnaPayments) {
        this.klarnaPayments = klarnaPayments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKlarnaPaymentType(final String paymentCode) {
        return Objects.nonNull(paymentCode) && klarnaPayments.stream().anyMatch(paymentCode::equals);
    }
}
