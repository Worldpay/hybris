package com.worldpay.converters.internal.model.payment.impl;

import com.worldpay.converters.internal.model.payment.PaymentConverterStrategy;
import com.worldpay.data.payment.Payment;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of the {@link PaymentConverterStrategy}
 */
public class DefaultPaymentConverterStrategy implements PaymentConverterStrategy {

    private final Map<PaymentType, Converter<Payment, Object>> paymentConverters;

    public DefaultPaymentConverterStrategy(final Map<PaymentType, Converter<Payment, Object>> paymentConverters) {
        this.paymentConverters = paymentConverters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertPayment(final Payment payment) {
        validateParameterNotNull(payment, "Payment must not be null!");

        final PaymentType paymentType = PaymentType.getPaymentType(payment.getPaymentType());

        if (paymentConverters.containsKey(paymentType)) {
            return paymentConverters.get(paymentType).convert(payment);
        } else {
            throw new IllegalArgumentException("Payment converter with key [" + payment.getPaymentType() + "] not supported");
        }
    }
}
