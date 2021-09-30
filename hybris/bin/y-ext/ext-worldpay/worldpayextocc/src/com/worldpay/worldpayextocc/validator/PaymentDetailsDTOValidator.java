package com.worldpay.worldpayextocc.validator;

import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Calendar;


/**
 * Validates instances of {@link PaymentDetailsWsDTO}.
 */
public class PaymentDetailsDTOValidator implements Validator {
    private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";
    private Validator paymentAddressValidator;

    @SuppressWarnings("squid:S3740")
    @Override
    public boolean supports(final Class clazz) {
        return PaymentDetailsWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final PaymentDetailsWsDTO paymentDetails = (PaymentDetailsWsDTO) target;
        if (Boolean.FALSE.equals(paymentDetails.getAcceptedTermsAndConditions())) {
            errors.rejectValue("acceptedTermsAndConditions", "field.payment.acceptedTermsAndConditions.false");
        }

        if (StringUtils.isNotBlank(paymentDetails.getStartMonth()) && StringUtils.isNotBlank(paymentDetails.getStartYear())
                && StringUtils.isNotBlank(paymentDetails.getExpiryMonth()) && StringUtils.isNotBlank(paymentDetails.getExpiryYear())) {
            final Calendar start = Calendar.getInstance();
            start.set(Calendar.DAY_OF_MONTH, 0);
            start.set(Calendar.MONTH, Integer.parseInt(paymentDetails.getStartMonth()) - 1);
            start.set(Calendar.YEAR, Integer.parseInt(paymentDetails.getStartYear()) - 1);

            final Calendar expiration = Calendar.getInstance();
            expiration.set(Calendar.DAY_OF_MONTH, 0);
            expiration.set(Calendar.MONTH, Integer.parseInt(paymentDetails.getExpiryMonth()) - 1);
            expiration.set(Calendar.YEAR, Integer.parseInt(paymentDetails.getExpiryYear()) - 1);

            if (start.after(expiration)) {
                errors.rejectValue("startMonth", "payment.startDate.invalid");
            }
        }

        boolean savedPayment = Boolean.TRUE.equals(paymentDetails.getSaved());
        if ((savedPayment && StringUtils.isEmpty(paymentDetails.getCseToken()) && StringUtils.isEmpty(paymentDetails.getSubscriptionId()))
        || !savedPayment && StringUtils.isEmpty(paymentDetails.getCseToken())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cseToken", FIELD_REQUIRED_MESSAGE_ID);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountHolderName", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardType.code", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryMonth", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryYear", FIELD_REQUIRED_MESSAGE_ID);

        paymentAddressValidator.validate(paymentDetails, errors);
    }

    @Required
    public void setPaymentAddressValidator(final Validator paymentAddressValidator) {
        this.paymentAddressValidator = paymentAddressValidator;
    }
}
