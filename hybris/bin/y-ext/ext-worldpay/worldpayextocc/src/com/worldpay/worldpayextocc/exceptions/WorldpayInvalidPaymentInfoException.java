package com.worldpay.worldpayextocc.exceptions;

/**
 * Specific exception that is thrown when the given payment info could not be associated with the checkout cart.
 */
public class WorldpayInvalidPaymentInfoException extends Exception {

    private final String paymentInfoId;

    /**
     * @param id
     */
    public WorldpayInvalidPaymentInfoException(final String id) {
        super("PaymentInfo [" + id + "] is invalid for the current cart");
        this.paymentInfoId = id;
    }

    /**
     * @return the paymentInfoId
     */
    public String getPaymentInfoId() {
        return paymentInfoId;
    }

}
