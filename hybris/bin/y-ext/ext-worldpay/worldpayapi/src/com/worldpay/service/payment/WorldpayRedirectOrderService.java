package com.worldpay.service.payment;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.MerchantInfo;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;

import java.math.BigDecimal;

/**
 * worldpay Hosted Order service interface.
 * <p>
 * Service implementation is responsible for organising the calls and request/response from
 * worldpay Hosted Order page
 */
public interface WorldpayRedirectOrderService {

    /**
     * Carries out the redirectAuthorise against the worldpayServiceGateway and then builds the data required to send to
     * worldpay Hosted Order Page.
     *
     * @param merchantInfo               The {@link MerchantInfo} object to be used with this call to worldpay
     * @param cartModel                  The cart for the order
     * @param additionalAuthInfo         The {@link AdditionalAuthInfo} to be supplied to worldpay. Includes installation id, order
     *                                   content, included and excluded payment types etc.
     * @param worldpayAdditionalInfoData The {@link WorldpayAdditionalInfoData} to be supplied to worldpay.
     * @return a {@link PaymentData} object which contains all data required to carry out the redirect to worldpay Hosted Order Page.
     * @throws WorldpayException The {@link WorldpayException} if there are any issues found communicating with worldpay.
     */
    PaymentData redirectAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Completes the redirectAuthorise if successful authorise response received. Creates payment details and returns back
     * to UI
     *
     * @param authoriseResult The {@link RedirectAuthoriseResult} containing the processed parameters returned from worldpay redirect
     * @param merchantCode    The merchantCode used in the authorise with worldpay
     * @param cartModel       The {@link CartModel} cart for the order
     */
    void completePendingRedirectAuthorise(final RedirectAuthoriseResult authoriseResult, final String merchantCode, final CartModel cartModel);

    /**
     * Completes the redirectAuthorise if successful authorise response received. Creates payment details with a payment transaction entry in a non-pending status
     *
     * @param paymentAmount The {@link BigDecimal} containing the amount for this paymentTransactionEntry
     * @param merchantCode  The merchantCode used in the authorise with worldpay
     * @param cartModel     The {@link CartModel} cart for the order
     */
    void completeConfirmedRedirectAuthorise(final BigDecimal paymentAmount, final String merchantCode, final CartModel cartModel);
}
