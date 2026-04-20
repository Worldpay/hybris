package com.worldpay.worldpayocccommons.validator;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Spring validator for validating Boolean values
 */
public class BooleanValidator implements Validator {

    private static final String ERRORS_OBJECT_MUST_NOT_BE_NULL = "Errors object must not be null";
    private String booleanValue;
    private String fieldPath;
    private String errorMessageID;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Assert.notNull(errors, ERRORS_OBJECT_MUST_NOT_BE_NULL);
        Object fieldValue = getFieldPath() == null ? target : errors.getFieldValue(getFieldPath());
        if(fieldValue instanceof Boolean && Boolean.valueOf(getBooleanValue()).equals(fieldValue)) {
            return;
        }
        errors.rejectValue(getFieldPath(), getErrorMessageID(), new String[]{getFieldPath()}, null);
    }

    public String getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(String booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }
}
