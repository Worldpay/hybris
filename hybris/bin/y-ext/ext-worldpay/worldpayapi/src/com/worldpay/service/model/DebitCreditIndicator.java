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
    CREDIT("credit"),
    ;

    private static final Map<String, DebitCreditIndicator> lookup = new HashMap<>();

    static {
        for (final DebitCreditIndicator code : EnumSet.allOf(DebitCreditIndicator.class)) {
            lookup.put(code.getCode().toUpperCase(), code);
        }
    }

    protected final String code;

    DebitCreditIndicator(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Lookup the enum representation of a debit credit indicator code
     *
     * @param code to be looked up
     * @return DebitCreditIndicator representation of the supplied code, or null if it can't be found
     */
    public static DebitCreditIndicator getDebitCreditIndicator(final String code) {
        return code == null ? null : lookup.get(code.toUpperCase());
    }
}
