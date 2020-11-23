package com.worldpay.klarna;

/**
 * Utils class for Klarna Payment methods
 */
public interface WorldpayKlarnaUtils {
    /**
     * Checkes wether the paymentCode passed is or not a Klarna valid payment method type code
     *
     * @param paymentCode
     * @return
     */
    boolean isKlarnaPaymentType(final String paymentCode);
}
