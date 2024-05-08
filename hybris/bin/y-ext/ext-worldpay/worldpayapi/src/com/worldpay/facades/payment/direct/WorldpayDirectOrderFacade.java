package com.worldpay.facades.payment.direct;

import com.worldpay.data.*;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;

/**
 * Interface that exposes the authorise operations that enables the Client Side Encryption with Worldpay
 */
public interface WorldpayDirectOrderFacade {

    /**
     * Performs a tokenize with Worldpay
     *
     * @param cartModel
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @throws WorldpayException - something went wrong
     */
    void tokenize(final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Performs a direct authorisation using Client Side Encryption with Worldpay.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authorise(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a request to Worldpay to retrieve the URL to redirect to when using authorisation using Bank transfer
     *
     * @param bankTransferAdditionalAuthInfo Object that contains additional authorisation information and the shopperBank
     * @param worldpayAdditionalInfoData     Object that contains information about the current session, browser used, and cookies.
     * @return String containing the redirect url
     * @throws WorldpayException - something went wrong
     */
    String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                         final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Performs authorisation of a transaction after it has been handled by 3dSecure issuer.
     *
     * @param paResponse                 The response from the 3dSecure issuer.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct authorisation using a saved card with Worldpay.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct authorisation using a saved order with Worldpay.
     *
     * @param abstractOrderModel         The abstractOrderModel to authorise.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authoriseRecurringPayment(final AbstractOrderModel abstractOrderModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a request to Worldpay to retrieve the URL to redirect to when using Klarna.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @param additionalAuthInfo         the additional auth info
     * @return String containing the redirect url
     * @throws WorldpayException - something went wrong
     */
    String authoriseKlarnaRedirect(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException;

    /**
     * Performs a direct authorisation using the data provided by ApplePay
     *
     * @param applePayAdditionalAuthInfo Object that contains information to authorise an order using ApplePay
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authoriseApplePayDirect(final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException;

    /**
     * Performs a request to Worldpay with the payment details of a GooglePay transaction.
     *
     * @param googlePayAdditionalAuthInfo Object that contains information to authorise an order using GooglePay.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay.
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authoriseGooglePayDirect(final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException;

    /**
     * Update the payment method.
     *
     * @param paymentMethodUpdateRequest - update request
     * @return the updated total and line items
     */
    ApplePayOrderUpdate updatePaymentMethod(final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest);

    /**
     * Execute the second payment request for 3d secure flow
     *
     * @return DirectResponseData
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData executeSecondPaymentAuthorisation3DSecure() throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct tokenize and authorisation at same time using Client Side Encryption with Worldpay.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - something went wrong
     */
    DirectResponseData authoriseAndTokenize(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException, InvalidCartException;

    /**
     * Performs a tokenize with Worldpay
     *
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @throws WorldpayException - something went wrong
     */
    void tokenize(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;


    /**
     * Performs the fist authorisation for 3d secure
     *
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return
     * @throws WorldpayException    - something went wrong
     * @throws InvalidCartException - Cart is not valid
     */
    DirectResponseData executeFirstPaymentAuthorisation3DSecure(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Execute the second payment request for 3d secure flow for given worldpay order code
     *
     * @param worldpayOrderCode
     * @return
     * @throws WorldpayException
     */
    DirectResponseData executeSecondPaymentAuthorisation3DSecure(final String worldpayOrderCode) throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct tokenize and authorisation at same time using Client Side Encryption with Worldpay.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @param cseAdditionalAuthInfo     Object that contains additional authorisation information and the cseToken
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException - something went wrong
     * @throws InvalidCartException - Cart is not valid
     */
    DirectResponseData internalTokenizeAndAuthorise(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException, InvalidCartException;

    /**
     * Performs a request to Worldpay with the payment details of a ACH Direct Debot transaction.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @param additionalAuthInfo         the additional auth info
     * @return Wrapper object containing information on the response from Worldpay
     * @throws WorldpayException - something went wrong
     */
    DirectResponseData authoriseACHDirectDebit(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final ACHDirectDebitAdditionalAuthInfo additionalAuthInfo) throws WorldpayException, InvalidCartException;
}
