package com.worldpay.service.payment.request;

import com.worldpay.data.*;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.*;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;


/**
 * Request factory for building request to Worldpay
 */
public interface WorldpayRequestFactory {

    /**
     * Builds a create token request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param cseAdditionalAuthInfo      contains cse form specific information for the request including encrypted payment information
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return Built {@link CreateTokenServiceRequest}
     * @throws WorldpayConfigurationException
     */
    CreateTokenServiceRequest buildTokenRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayConfigurationException;

    /**
     * Builds an update token request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cseAdditionalAuthInfo      contains cse form specific information for the request including encrypted payment information
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @param createTokenResponse        represents the response to the token creation request
     * @return Built {@link UpdateTokenServiceRequest}
     */
    UpdateTokenServiceRequest buildTokenUpdateRequest(final MerchantInfo merchantInfo, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                      final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse);

    /**
     * Builds an update token request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param creditCardPaymentInfoModel the creditCardPaymentInfoModel
     * @return Built {@link DeleteTokenServiceRequest}
     */
    DeleteTokenServiceRequest buildTokenDeleteRequest(final MerchantInfo merchantInfo, final CreditCardPaymentInfoModel creditCardPaymentInfoModel);

    /**
     * Builds an authorise request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return Built {@link DirectAuthoriseServiceRequest}
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseRequestWithTokenForCSE(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * * Builds an authorise request with a GooglePay request to send to Worldpay
     *
     * @param merchantInfo                the merchantInfo
     * @param cartModel                   the session cart
     * @param googlePayAdditionalAuthInfo object that contains the parameters returned by Google Pay
     * @return Built {@link DirectAuthoriseServiceRequest}
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseGooglePayRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo);

    /**
     * Builds a 3D secure direct request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param worldpayOrderCode          the worldpay order code
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @param paRes                      the payer Response required for 3D request
     * @param cookie                     the cookie from the authorise response
     * @return Built {@link DirectAuthoriseServiceRequest}
     */
    DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode,
                                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                final String paRes, final String cookie);

    /**
     * Builds a direct authorise request to send to Worldpay with Bank details
     *
     * @param merchantInfo
     * @param cartModel
     * @param bankTransferAdditionalAuthInfo
     * @param worldpayAdditionalInfoData
     * @return Built {@link DirectAuthoriseServiceRequest}
     * @throws WorldpayConfigurationException thrown when the URLs are not configured correctly
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseBankTransferRequest(final MerchantInfo merchantInfo,
                                                                          final CartModel cartModel,
                                                                          final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                          final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException;

    /**
     * Builds an authorise recurring payment request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param abstractOrderModel         the session cart or an order
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return Built {@link DirectAuthoriseServiceRequest}
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseRecurringPayment(MerchantInfo merchantInfo, AbstractOrderModel abstractOrderModel, WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Builds a direct authorise request to send to Worldpay with Bank details
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart or an order
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @param additionalAuthInfo
     * @return Built {@link DirectAuthoriseServiceRequest}
     * @throws WorldpayConfigurationException thrown when the URLs are not configured correctly
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseKlarnaRequest(final MerchantInfo merchantInfo,
                                                                    final CartModel cartModel,
                                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                    final AdditionalAuthInfo additionalAuthInfo) throws WorldpayConfigurationException;

    /**
     * Builds a direct authorise request using ApplePay
     *
     * @param merchantInfo
     * @param cartModel
     * @param applePayAdditionalInfo
     * @return a directAuthoriseServiceRequest
     */
    DirectAuthoriseServiceRequest buildApplePayDirectAuthorisationRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final ApplePayAdditionalAuthInfo applePayAdditionalInfo);

    /**
     * Builds the second 3D secure payment request to send to Worldpay
     *
     * @param merchantInfo      the merchantInfo
     * @param worldpayOrderCode the current checkout order code
     * @param sessionId         the session id
     * @param cookie            the cookie
     * @return Built {@link SecondThreeDSecurePaymentRequest}
     */
    SecondThreeDSecurePaymentRequest buildSecondThreeDSecurePaymentRequest(final MerchantInfo merchantInfo, final String worldpayOrderCode, final String sessionId, final String cookie);


    /**
     * Builds a tokenised authorisation direct request
     *
     * @param merchantInfo
     * @param cartModel
     * @return a directAuthoriseServiceRequest
     */
    DirectAuthoriseServiceRequest buildDirectTokenAndAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo);

    /**
     * Builds a redirect authorise request
     * @param merchantInfo
     * @param cartModel
     * @param additionalAuthInfo
     * @return
     */
    RedirectAuthoriseServiceRequest buildRedirectAuthoriseRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo);
}
