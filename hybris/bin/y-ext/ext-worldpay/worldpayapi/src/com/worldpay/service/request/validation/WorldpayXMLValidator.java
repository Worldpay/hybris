package com.worldpay.service.request.validation;

import com.worldpay.exception.WorldpayValidationException;
import com.worldpay.internal.model.PaymentService;

/**
 * Interface defining the methods that must be implemented by WorldpayXMLValidator
 * <p/>
 * <p>WorldpayXMLValidators must be able to validate a {@link PaymentService} against the xsd, and throw any exceptions if validation exceptions are found</p>
 */
public interface WorldpayXMLValidator {

    /**
     * Validate the provided {@link PaymentService} and ensure it meets the requirements defined in the xsd/dtd file
     *
     * @param paymentService paymentService object to be validated
     * @throws WorldpayValidationException if there are any issues in the validation or if validation fails
     */
    void validate(PaymentService paymentService) throws WorldpayValidationException;
}
