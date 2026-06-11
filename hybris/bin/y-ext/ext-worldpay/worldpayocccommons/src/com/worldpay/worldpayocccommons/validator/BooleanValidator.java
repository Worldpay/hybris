package com.worldpay.worldpayocccommons.validator;


import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Spring validator for validating Boolean values
 */
public class BooleanValidator implements Validator {

    private static final String ERRORS_OBJECT_MUST_NOT_BE_NULL = "Errors object must not be null";

    protected final String booleanValue;
    protected final String fieldPath;
    protected final String errorMessageID;

    public BooleanValidator(final String booleanValue, final String fieldPath, final String errorMessageID) {
        this.booleanValue = booleanValue;
        this.fieldPath = fieldPath;
        this.errorMessageID = errorMessageID;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Assert.notNull(errors, ERRORS_OBJECT_MUST_NOT_BE_NULL);
        Object fieldValue = getFieldPath() == null ? target : errors.getFieldValue(getFieldPath());
        if (fieldValue instanceof Boolean && Boolean.valueOf(getBooleanValue()).equals(fieldValue)) {
            return;
        }
        errors.rejectValue(getFieldPath(), getErrorMessageID(), new String[]{getFieldPath()}, null);
    }

    public String getBooleanValue() {
        return booleanValue;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

}
