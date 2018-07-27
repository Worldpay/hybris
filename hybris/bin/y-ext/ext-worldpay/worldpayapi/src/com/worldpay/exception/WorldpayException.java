package com.worldpay.exception;

import com.worldpay.service.WorldpayServiceGateway;

/**
 * Superclass placeholder for all Worldpay exceptions. Thrown from the {@link WorldpayServiceGateway} methods
 */
public class WorldpayException extends Exception {

    private static final long serialVersionUID = 2076146272191401917L;

    /**
     * Default constructor setting a message and a cause
     *
     * @param message
     * @param throwable
     */
    public WorldpayException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Default constructor setting a message
     *
     * @param message
     */
    public WorldpayException(final String message) {
        super(message);
    }
}
