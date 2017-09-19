package com.worldpay.service.model;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum of the last event statusses returned by Worldpay
 */
public enum AuthorisedStatus implements Serializable {

    SIGNED_FORM_RECEIVED("SIGNED_FORM_RECEIVED"),
    SENT_FOR_AUTHORISATION("SENT_FOR_AUTHORISATION"),
    AUTHORISED("AUTHORISED"),
    REFUSED("REFUSED"),
    ERROR("ERROR"),
    CANCELLED("CANCELLED"),
    EXPIRED("EXPIRED"),
    CAPTURED("CAPTURED"),
    SENT_FOR_REFUND("SENT_FOR_REFUND"),
    SETTLED("SETTLED"),
    CHARGED_BACK("CHARGED_BACK"),
    REFUNDED("REFUNDED"),
    OPEN("OPEN"),
    REFUND_WEBFORM_ISSUED("REFUND_WEBFORM_ISSUED"),
    SHOPPER_REDIRECTED("SHOPPER_REDIRECTED"),
    FAILURE("FAILURE");

    private static final Map<String, AuthorisedStatus> lookup = new HashMap<>();

    static {
        for (AuthorisedStatus code : EnumSet.allOf(AuthorisedStatus.class)) {
            lookup.put(code.getCode().toUpperCase(), code);
        }
    }

    private String code;

    AuthorisedStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Lookup the enum representation of an authorisation code
     *
     * @param code to be looked up
     * @return AuthorisedStatus representation of the supplied code, or null if it can't be found
     */
    public static AuthorisedStatus getAuthorisedStatus(String code) {
        return code == null ? null : lookup.get(code.toUpperCase());
    }

}
