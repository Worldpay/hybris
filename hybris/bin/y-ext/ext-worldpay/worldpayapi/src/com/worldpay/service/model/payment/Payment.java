package com.worldpay.service.model.payment;

import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * Interface marker for a Payment object.
 */
public interface Payment extends InternalModelTransformer, Serializable {

    PaymentType getPaymentType();

    void setPaymentType(final PaymentType paymentType);
}
