package com.worldpay.worldpayextocc.exceptions;

/**
 * Thrown when an 3D secure did not succeed.
 */
public class ThreeDSecureException extends Exception {

    /**
     * @param message
     */
    public ThreeDSecureException(final String message) {
        super(message);
    }

}
