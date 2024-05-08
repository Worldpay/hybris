package com.worldpay.worldpayextocc.validator;

import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link PaymentDetailsWsDTO}.
 */
public class ACHDirectDebitPaymentDetailsDTOValidator implements Validator {


    @SuppressWarnings("squid:S3740")
    @Override
    public boolean supports(final Class clazz) {
        return AchDirectDebitPaymentWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final AchDirectDebitPaymentWsDTO paymentDetails = (AchDirectDebitPaymentWsDTO) target;

        validateAccountType(paymentDetails, errors);
        validateAccountNumber(paymentDetails, errors);
        validateRoutingNumber(paymentDetails, errors);
        validateCheckNumber(paymentDetails, errors);
        validateCustomIdentifier(paymentDetails, errors);
    }

    private void validateAccountType(final AchDirectDebitPaymentWsDTO paymentDetails, final Errors errors) {
        if (StringUtils.isBlank(paymentDetails.getAccountType())) {
            errors.rejectValue("accountType", "worldpay.achDirectDebit.accountType.invalid");
        }
    }

    private void validateAccountNumber(final AchDirectDebitPaymentWsDTO paymentDetails, final Errors errors) {
        if (StringUtils.isBlank(paymentDetails.getAccountNumber()) ||
                !StringUtils.isNumeric(paymentDetails.getAccountNumber()) ||
                paymentDetails.getAccountNumber().length() > 17) {
            errors.rejectValue("accountNumber", "worldpay.achDirectDebit.accountNumber.invalid");
        }
    }

    private void validateRoutingNumber(final AchDirectDebitPaymentWsDTO paymentDetails, final Errors errors) {
        if (StringUtils.isBlank(paymentDetails.getRoutingNumber()) ||
                !StringUtils.isNumeric(paymentDetails.getRoutingNumber()) ||
                paymentDetails.getRoutingNumber().length() < 8 ||
                paymentDetails.getRoutingNumber().length() > 9) {
            errors.rejectValue("routingNumber", "worldpay.achDirectDebit.routingNumber.invalid");
        }
    }

    private void validateCheckNumber(final AchDirectDebitPaymentWsDTO paymentDetails, final Errors errors) {
        if (!StringUtils.isNumeric(paymentDetails.getCheckNumber()) ||
                paymentDetails.getCheckNumber().length() > 15) {
            errors.rejectValue("checkNumber", "worldpay.achDirectDebit.checkNumber.invalid");
        }
    }

    private void validateCustomIdentifier(final AchDirectDebitPaymentWsDTO paymentDetails, final Errors errors) {
        if (StringUtils.isNotBlank(paymentDetails.getCustomIdentifier()) && paymentDetails.getCustomIdentifier().length() > 15) {
            errors.rejectValue("customIdentifier", "worldpay.achDirectDebit.customIdentifier.invalid");
        }
    }

}