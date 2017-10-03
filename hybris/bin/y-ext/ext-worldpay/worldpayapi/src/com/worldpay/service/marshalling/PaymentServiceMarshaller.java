package com.worldpay.service.marshalling;

import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;

import java.io.InputStream;

/**
 * Interface that expose the methods to transform the PaymentService {@link PaymentService} into an XML that represents it and vice-versa.
 */
public interface PaymentServiceMarshaller {

    /**
     * Transforms the xml that represents a PaymentService object into an instance of PaymentService.
     * @param in
     * @return
     */
    PaymentService unmarshal(final InputStream in) throws WorldpayModelTransformationException;


    /**
     * Transforms the PaymentService object into an XML that represents it.
     * @param paymentService
     * @return
     */
    String marshal(final PaymentService paymentService) throws WorldpayException;
}
