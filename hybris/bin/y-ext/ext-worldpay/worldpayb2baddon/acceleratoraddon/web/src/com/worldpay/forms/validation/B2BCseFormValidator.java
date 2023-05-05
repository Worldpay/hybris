package com.worldpay.forms.validation;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Validate CSE form
 */
@Component("cseFormValidator")
public class B2BCseFormValidator implements Validator {

    protected static final String GLOBAL_MISSING_CSE_TOKEN = "checkout.multi.paymentMethod.cse.invalid";
    protected static final String CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY = "checkout.error.fraudSight.dob.mandatory";

    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;

    @Override
    public boolean supports(final Class<?> aClass) {
        return B2BCSEPaymentForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {
        final B2BCSEPaymentForm form = (B2BCSEPaymentForm) object;

        if (StringUtils.isBlank(form.getCseToken())) {
            errors.reject(GLOBAL_MISSING_CSE_TOKEN);
        }

        if (worldpayPaymentCheckoutFacade.isFSEnabled() && form.isDobRequired() && !isValidDate(form.getDateOfBirth())) {
            errors.reject(CHECKOUT_ERROR_FRAUDSIGHT_DOB_MANDATORY);
        }
    }

    /**
     * Checks if the birthday date is in the past
     *
     * @param birthdayDate the birthday date
     * @return true if valid, false otherwise
     */
    private boolean isValidDate(final Date birthdayDate) {
        return birthdayDate != null && birthdayDate.before(new Date());
    }
}
