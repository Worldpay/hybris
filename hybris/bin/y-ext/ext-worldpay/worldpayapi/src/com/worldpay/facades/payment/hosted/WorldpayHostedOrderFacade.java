package com.worldpay.facades.payment.hosted;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;


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
     * @param worldpayAdditionalInfoData
     * @return carry out the redirect to worldpay Hosted Order Page
     * @throws WorldpayException if there are any issues found communicating with Worldpay
     */
    PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo, WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Complete the redirectAuthorise if successful authorise response received. Create payment details and return back
     * to UI
     *
     * @param redirectParameters The resultMap of parameters returned from Worldpay redirect
     */
    void completeRedirectAuthorise(final RedirectAuthoriseResult redirectParameters);

    /**
     * Create a paymentInfoModel for the current cartModel, setting the isSaved attribute on the paymentInfoModel set to true
     *
     * @return
     */
    void createPaymentInfoModelOnCart(final boolean isSaved);
}
