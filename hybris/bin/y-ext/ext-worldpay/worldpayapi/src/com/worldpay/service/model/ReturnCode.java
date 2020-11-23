package com.worldpay.service.model;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representation of the ISO8583 response codes
 */
public enum ReturnCode implements Serializable {

    AUTHORISED("0", "AUTHORISED"),
    REFERRED("2", "REFERRED"),
    HOLD_CARD("4", "HOLD CARD"),
    REFUSED("5", "REFUSED"),
    APPROVE_AFTER_IDENTIFICATION("8", "APPROVE AFTER IDENTIFICATION"),
    INVALID_AMOUNT("13", "INVALID AMOUNT"),
    INVALID_CARD_ISSUER("15", "INVALID CARD ISSUER"),
    ANNULATION_BY_CLIENT("17", "ANNULATION BY CLIENT"),
    ACCESS_DENIED("28", "ACCESS DENIED"),
    IMPOSSIBLE_REFERENCE_NUMBER("29", "IMPOSSIBLE REFERENCE NUMBER"),
    CARD_EXPIRED("33", "CARD EXPIRED"),
    FRAUD_SUSPICION("34", "FRAUD SUSPICION"),
    SECURITY_CODE_EXPIRED("38", "SECURITY CODE EXPIRED"),
    LOST_CARD("41", "LOST CARD"),
    STOLEN("43", "STOLEN"),
    REJECTED_BY_CARD_ISSUER("85", "REJECTED BY CARD ISSUER"),
    CREDITCARD_ISSUER_TEMPORARILY_NOT_REACHABLE("91", "CREDITCARD ISSUER TEMPORARILY NOT REACHABLE"),
    SECURITY_BREACH("97", "SECURITY BREACH"),
    INVALID_ACCEPTOR("3", "INVALID ACCEPTOR"),
    INVALID_TRANSACTION("12", "INVALID TRANSACTION"),
    INVALID_ACCOUNT("14", "INVALID ACCOUNT"),
    REPEAT_OF_LAST_TRANSACTION("19", "REPEAT OF LAST TRANSACTION"),
    ACQUIRER_ERROR("20", "ACQUIRER ERROR"),
    REVERSAL_NOT_PROCESSED_MISSING_AUTHORITY("21", "REVERSAL NOT PROCESSED, MISSING AUTHORISATION"),
    UPDATE_OF_FILE_IMPOSSIBLE("24", "UPDATE OF FILE IMPOSSIBLE"),
    REFERENCE_NUMBER_CANNOT_BE_FOUND("25", "REFERENCE NUMBER CANNOT BE FOUND"),
    DUPLICATE_REEFERENCE_NUMBER("26", "DUPLICATE REFERENCE NUMBER"),
    ERROR_IN_REFERENCE_NUMBER_FIELD("27", "ERROR IN REFERENCE NUMBER FIELD"),
    FORMAT_ERROR("30", "FORMAT ERROR"),
    UNKNOWN_ACQUIRER_ACCOUNT_CODE("31", "UNKNOWN ACQUIRER ACCOUNT CODE"),
    REQUESTED_FUNCTION_NOT_SUPPORTED("40", "REQUESTED FUNCTION NOT SUPPORTED"),
    TRANSACTION_NOT_PERMITTED("58", "TRANSACTION NOT PERMITTED"),
    AMOUNT_HIGHER_THAN_PREVIOUS_TRANSACTION_AMOUNT("64", "AMOUNT HIGHER THAN PREVIOUS TRANSACTION AMOUNT"),
    TRANSACTION_TIMED_OUT("68", "TRANSACTION TIMED OUT"),
    AMOUNT_NO_LONGER_AVAILABLE_AUTHORISATION_EXPIRED("80", "AMOUNT NO LONGER AVAILABLE, AUTHORISATION EXPIRED"),
    CREDITCARD_TYPE_NOT_PROCESSED_BY_ACQUIRER("92", "CREDITCARD TYPE NOT PROCESSED BY ACQUIRER"),
    DUPLICATE_REQUEST_ERROR("94", "DUPLICATE REQUEST ERROR"),
    ;

    private static final Map<String, ReturnCode> lookup = new HashMap<>();

    static {
        for (final ReturnCode code : EnumSet.allOf(ReturnCode.class)) {
            lookup.put(code.getCode(), code);
        }
    }

    protected final String code;
    protected final String description;

    ReturnCode(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Lookup the enum representation of a return code
     *
     * @param code to be looked up
     * @return ReturnCode representation of the supplied code, or null if it can't be found
     */
    public static ReturnCode getReturnCode(final String code) {
        return code == null ? null : lookup.get(code);
    }

}
