package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PaymentDetails;
import com.worldpay.data.PaymentMethodAttribute;
import com.worldpay.data.PaymentMethodMask;
import com.worldpay.data.payment.PayAsOrder;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class PaymentOrderConvertersWrapper {

    protected final Converter<PaymentMethodMask, com.worldpay.internal.model.PaymentMethodMask> internalPaymentMethodMaskConverter;
    protected final Converter<PaymentDetails, com.worldpay.internal.model.PaymentDetails> internalPaymentDetailsConverter;
    protected final Converter<PayAsOrder, com.worldpay.internal.model.PayAsOrder> internalPayAsOrderConverter;
    protected final Converter<PaymentMethodAttribute, com.worldpay.internal.model.PaymentMethodAttribute> internalPaymentMethodAttributeConverter;

    public PaymentOrderConvertersWrapper(final Converter<PaymentMethodMask, com.worldpay.internal.model.PaymentMethodMask> internalPaymentMethodMaskConverter,
                                         final Converter<PaymentDetails, com.worldpay.internal.model.PaymentDetails> internalPaymentDetailsConverter,
                                         final Converter<PayAsOrder, com.worldpay.internal.model.PayAsOrder> internalPayAsOrderConverter,
                                         final Converter<PaymentMethodAttribute, com.worldpay.internal.model.PaymentMethodAttribute> internalPaymentMethodAttributeConverter) {
        this.internalPaymentMethodMaskConverter = internalPaymentMethodMaskConverter;
        this.internalPaymentDetailsConverter = internalPaymentDetailsConverter;
        this.internalPayAsOrderConverter = internalPayAsOrderConverter;
        this.internalPaymentMethodAttributeConverter = internalPaymentMethodAttributeConverter;
    }
}
