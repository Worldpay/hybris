package com.worldpay.exception;

/**
 * Exception thrown if there is a problem with the configuration.
 */
public class WorldpayConfigurationException extends WorldpayException {

    private static final long serialVersionUID = -1620350927954767400L;

    /**
     * Default constructor setting a message and a cause
     *
     * @param message
     * @param throwable
     */
    public WorldpayConfigurationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Default constructor setting a message
     *
     * @param message
     */
    public WorldpayConfigurationException(final String message) {
        super(message);
    }
}
