package com.worldpay.facades.payment.hosted;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;

import java.util.Map;


/**
 * Worldpay Hosted Order facade interface. Service is responsible for organising the calls and request/response from
 * Worldpay Hosted Order page implementation
 */
public interface WorldpayHostedOrderFacade {

    /**
     * Carries out the redirectAuthorise against the worldpayServiceGateway and then builds the data required to send to
     * Worldpay Hosted Order Page
     *
     * @param additionalAuthInfo
     * @return carry out the redirect to worldpay Hosted Order Page
     * @throws WorldpayException if there are any issues found communicating with Worldpay
     */
    PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException;

    /**
     * Complete the redirectAuthorise if successful authorise response received. Create payment details and return back
     * to UI
     *
     * @param redirectParameters The resultMap of parameters returned from Worldpay redirect
     */
    void completeRedirectAuthorise(final RedirectAuthoriseResult redirectParameters);

    /**
     * Checks that the response is authentic and sent from Worldpay
     *
     * @param worldpayResponse
     * @return
     */
    boolean validateRedirectResponse(final Map<String, String> worldpayResponse);

    /**
     * Inquiries the payment status of an order in Worldpay
     * @return String representing the Authorisation status of the order in Worldpay
     */
    RedirectAuthoriseResult inquiryPaymentStatus() throws WorldpayException;
}
