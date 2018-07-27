package com.worldpay.service.model.payment;

import java.io.Serializable;

/**
 * Enum representation of the ACH Payment types
 */
public enum AchType implements Serializable {

    AUTHENTICATION("authentication"),
    DEPOSIT("deposit"),
    VALIDATION("validation"),
    VERIFICATION("verification"),;

    private String typeName;

    private AchType(final String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
