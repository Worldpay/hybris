package com.worldpay.validator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Spring validator for validating Boolean values
 */
public class BooleanValidator implements Validator {

    private String booleanValue;
    private String fieldPath;
    private String errorMessageID;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Assert.notNull(errors, "Errors object must not be null");
        Object fieldValue = getFieldPath() == null ? target : errors.getFieldValue(getFieldPath());
        if(fieldValue instanceof Boolean && Boolean.valueOf(getBooleanValue()).equals(fieldValue)) {
            return;
        }
        errors.rejectValue(getFieldPath(), getErrorMessageID(), new String[]{getFieldPath()}, null);
    }

    public String getBooleanValue() {
        return booleanValue;
    }

    @Required
    public void setBooleanValue(String booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    @Required
    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    @Required
    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }
}
