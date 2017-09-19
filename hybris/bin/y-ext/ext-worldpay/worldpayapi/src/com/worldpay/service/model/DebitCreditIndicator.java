package com.worldpay.service.model;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum of the Debit/Credit status associated to an {@link Amount}
 */
public enum DebitCreditIndicator implements Serializable {

    DEBIT("debit"),
    CREDIT("credit"),;

    private static final Map<String, DebitCreditIndicator> lookup = new HashMap<>();

    static {
        for (DebitCreditIndicator code : EnumSet.allOf(DebitCreditIndicator.class)) {
            lookup.put(code.getCode().toUpperCase(), code);
        }
    }

    private String code;

    DebitCreditIndicator(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Lookup the enum representation of a debit credit indicator code
     *
     * @param code to be looked up
     * @return DebitCreditIndicator representation of the supplied code, or null if it can't be found
     */
    public static DebitCreditIndicator getDebitCreditIndicator(String code) {
        return code == null ? null : lookup.get(code.toUpperCase());
    }
}
