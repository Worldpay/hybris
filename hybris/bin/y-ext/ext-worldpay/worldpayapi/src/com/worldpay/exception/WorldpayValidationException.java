package com.worldpay.exception;

import com.worldpay.service.request.validation.WorldpayXMLValidator;

/**
 * Exception thrown if there is a problem validating the outgoing xml message against the schema. Thrown from {@link WorldpayXMLValidator}
 */
public class WorldpayValidationException extends WorldpayException {

    private static final long serialVersionUID = -1620350927954767400L;

    /**
     * Default constructor setting a message and a cause
     *
     * @param message
     * @param throwable
     */
    public WorldpayValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Default constructor setting a message
     *
     * @param message
     */
    public WorldpayValidationException(String message) {
        super(message);
    }
}
