package com.worldpay.facades.order;

import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;

/**
 * Klarna APM specific checkout facade interface. Adds convenience methods required for Klarna interaction
 */
public interface WorldpayKlarnaPaymentCheckoutFacade {

    /**
     * Retrieves the Klarna Confirmation page using the inquiry services
     *
     * @return a KlarnaRedirectAuthoriseResult containing the HTML content to show to the customer after placing an order and the result of the authorisation
     */
    KlarnaRedirectAuthoriseResult checkKlarnaOrderStatus() throws WorldpayException;

}
