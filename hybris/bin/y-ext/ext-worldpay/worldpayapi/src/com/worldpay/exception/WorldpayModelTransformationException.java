package com.worldpay.exception;

import com.worldpay.service.request.ServiceRequest;
import com.worldpay.service.response.ServiceResponse;

/**
 * Exception thrown if there is either a problem transforming the {@link ServiceRequest} into the internal model, or a problem transforming the reply from
 * Worldpay back into the {@link ServiceResponse} returned to the user
 */
public class WorldpayModelTransformationException extends WorldpayException {

    private static final long serialVersionUID = 2437496248341690077L;

    /**
     * Default constructor setting a message and a cause
     *
     * @param message
     * @param throwable
     */
    public WorldpayModelTransformationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Default constructor setting a message
     *
     * @param message
     */
    public WorldpayModelTransformationException(String message) {
        super(message);
    }
}
