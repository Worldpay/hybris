package com.worldpay.converters.internal.model.payment;

import com.worldpay.data.payment.Payment;

/**
 * Converts a payment into a specific apm payment internal model based on the payment type
 */
public interface PaymentConverterStrategy {

    /**
     * Converts the given payment data into a specific payment internal model based on the payment type.
     * If there is no converter for the payment type, an {@link IllegalArgumentException} will be throw.
     *
     * @param payment the payment
     * @return {@link Object} for the paymentType
     */
    Object convertPayment(final Payment payment);
}
