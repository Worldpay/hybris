package com.worldpay.service.payment;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

/**
 * Interface that expose methods to authorise payments encrypted with the Worldpay CSE-library using the direct xml integration.
 * <p>
 * In order to successfully complete a direct xml integration with CSE, the request must be authorised first, and with the response, update the transaction and order accordingly.
 * <p>
 * It is possible to authorise based on a cart or a recurring order.
 */
public interface WorldpayDirectOrderService {
    /**
     * Builds the directAuthoriseRequest containing the encrypted card details and the address-details.
     * The request is then sent to Worldpay for processing resulting in either an authorised, refused or error response.
     *
     * @param merchantInfo               Merchant configuration
     * @param cartModel                  {@link CartModel} used in the current checkout.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return the {@link DirectAuthoriseServiceResponse} from Worldpay.
     */
    DirectAuthoriseServiceResponse authorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Builds the directAuthoriseRequest containing the payment details for a Bank transfer.
     * The request is then sent to Worldpay for processing resulting in either an authorised, refused or error response.
     *
     * @param merchantInfo                   Merchant configuration
     * @param cartModel                      {@link CartModel} used in the current checkout.
     * @param bankTransferAdditionalAuthInfo Object that contains additional authorisation information and the shopper bank
     * @param worldpayAdditionalInfoData     Object that contains information about the current session, browser used, and cookies.
     * @return the {@link DirectAuthoriseServiceResponse} from Worldpay.
     */
    DirectAuthoriseServiceResponse authoriseBankTransfer(final MerchantInfo merchantInfo, final CartModel cartModel, final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                         final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Builds the directAuthoriseRequest containing the encrypted card details and the address-details.
     * The request is then sent to Worldpay for processing resulting in either an authorised, refused or error response.
     *
     * @param merchantInfo               Merchant configuration
     * @param cartModel                  {@link CartModel} used in the current checkout.
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     */
    void createToken(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayException;

    /**
     * Builds the deleteTokenRequest.
     * The request is then sent to Worldpay
     *
     * @param merchantInfo               Merchant configuration
     * @param creditCardPaymentInfoModel    {@link CreditCardPaymentInfoModel} saved payment information needed to be deleted
     */
    void deleteToken(final MerchantInfo merchantInfo, CreditCardPaymentInfoModel creditCardPaymentInfoModel)
            throws WorldpayException;

    /**
     * Recovers the original {@link DirectAuthoriseServiceRequest} and adds the paResponse from the 3dSecure issuer.
     * The request is then sent to Worldpay for processing.
     *
     * @param merchantInfo               Merchant configuration
     * @param worldpayOrderCode          used in the current checkout.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @param paResponse                 String containing the response from the 3DSecure issuer.
     * @return the {@link DirectAuthoriseServiceResponse} from Worldpay.
     */
    DirectAuthoriseServiceResponse authorise3DSecure(final MerchantInfo merchantInfo, final String worldpayOrderCode,
                                                     final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final String paResponse)
            throws WorldpayException;

    /**
     * Updates/Creates the PaymentTransaction associated to the authorisation received from Worldpay. Updates/Creates a non-pending paymentTransactionEntry of type AUTHORISATION.
     * Updates/Creates the paymentInfo associated to the transaction and the order.
     *
     * @param serviceResponse {@link DirectAuthoriseServiceResponse} contains the response information from Worldpay.
     * @param abstractOrderModel The cart or order used in the current checkout.
     * @param merchantCode    merchantCode used in the communication with Worldpay.
     */
    void completeAuthorise(final DirectAuthoriseServiceResponse serviceResponse, final AbstractOrderModel abstractOrderModel, final String merchantCode);

    /**
     * See {@see completeAuthorise}. Completes the authorization after being validated by the 3D Secure issuer.
     *
     * @param abstractOrderModel The cart or order to authorise
     * @param serviceResponse {@link DirectAuthoriseServiceResponse} contains the response information from Worldpay.
     * @param merchantInfo    Merchant configuration
     */
    void completeAuthorise3DSecure(final AbstractOrderModel abstractOrderModel, final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo);

    /**
     * Builds the directAuthoriseRequest containing the token identifier and the cvv.
     * The request is then sent to Worldpay for processing resulting in either an authorised, refused or error response.
     *
     * @param merchantInfo               Merchant configuration
     * @param abstractOrderModel  {@link CartModel} or {@link de.hybris.platform.core.model.order.OrderModel} used in the current checkout.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return the {@link DirectAuthoriseServiceResponse} from Worldpay.
     */
    DirectAuthoriseServiceResponse authoriseRecurringPayment(final MerchantInfo merchantInfo, final AbstractOrderModel abstractOrderModel,
                                                             final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;


    /**
     * Builds the directAuthoriseRequest containing the details for Klarna payment.
     * The request is then sent to Worldpay for processing resulting in either an authorised, refused or error response.
     *
     * @param merchantInfo                   Merchant configuration
     * @param cartModel                      {@link CartModel} used in the current checkout.
     * @param worldpayAdditionalInfoData     Object that contains information about the current session, browser used, and cookies.
     * @param additionalAuthInfo
     * @return the {@link DirectAuthoriseServiceResponse} from Worldpay.
     */
    DirectAuthoriseServiceResponse authoriseKlarna(final MerchantInfo merchantInfo, final CartModel cartModel,
                                                   final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                   final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException;
}
