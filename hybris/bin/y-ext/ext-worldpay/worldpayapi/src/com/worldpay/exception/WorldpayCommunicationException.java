package com.worldpay.exception;

import com.worldpay.service.http.WorldpayConnector;

/**
 * Exception thrown from the {@link WorldpayConnector} if there is some form of communication problem with Worldpay
 */
public class WorldpayCommunicationException extends WorldpayException {

    private static final long serialVersionUID = -7515733306810454317L;

    /**
     * Default constructor setting a message and a cause
     *
     * @param message
     * @param throwable
     */
    public WorldpayCommunicationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Default constructor setting a message
     *
     * @param message
     */
    public WorldpayCommunicationException(final String message) {
        super(message);
    }
}
