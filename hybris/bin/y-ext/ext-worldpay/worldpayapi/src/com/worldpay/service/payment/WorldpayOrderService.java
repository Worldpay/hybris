package com.worldpay.service.payment;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.PayWithGoogleSSL;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.StoredCredentials;
import com.worldpay.service.model.threeds2.Additional3DSData;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Exposes utility methods to create objects used in the request creation.
 */
public interface WorldpayOrderService {

    /**
     * Creates a Worldpay {@link Amount} object using the  currencyIsoCode and the amount.
     *
     * @param currency currency to be used
     * @param amount   total amount of the order
     * @return Amount object
     */
    Amount createAmount(final Currency currency, final int amount);

    /**
     * Creates a Worldpay {@link Amount} object using the {@link CurrencyModel} and the amount.
     *
     * @param currencyModel currency to be used
     * @param amount        total amount of the order
     * @return Amount object
     */
    Amount createAmount(final CurrencyModel currencyModel, final double amount);

    /**
     * Creates a Worldpay {@link Amount} object using the {@link Currency} and the amount.
     *
     * @param currency
     * @param amount
     * @return Amount object required by DirectAuthoriseServiceRequest
     */
    Amount createAmount(final Currency currency, final double amount);

    /**
     * Converts Amount Object into a BigDecimal value taking into account the currency
     *
     * @param amount
     * @return amount as a BigDecimal
     */
    BigDecimal convertAmount(final Amount amount);

    /**
     * Creates a Worldpay {@link Session} object
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return Session object
     */
    Session createSession(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates a Worldpay {@link Browser} object
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return Browser object
     */
    Browser createBrowser(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates a Worldpay {@link BasicOrderInfo} object
     *
     * @param worldpayOrderCode Identifier of the order in Worldpay
     * @param description       Description of the order
     * @param amount            Payable amount {@link Amount}
     * @return Basic order information object
     */
    BasicOrderInfo createBasicOrderInfo(final String worldpayOrderCode, final String description, final Amount amount);

    /**
     * Creates a {@link Shopper} object to be used in the Requests
     *
     * @param customerEmail customer email
     * @param session       session information
     * @param browser       browser information
     * @return Shopper object
     */
    Shopper createShopper(String customerEmail, final Session session, final Browser browser);

    /**
     * @param customerEmail          customer email
     * @param authenticatedShopperID unique identifier for the shopper
     * @param session                session information
     * @param browser                browser information
     * @return authenticated Shopper
     */
    Shopper createAuthenticatedShopper(final String customerEmail, final String authenticatedShopperID, final Session session, final Browser browser);

    /**
     * Creates a tokenRequest object with scope merchant or shopper depending on the configured property
     *
     * @param tokenEventReference unique identifier for the token transaction
     * @param tokenReason         refers to the seller so they can be tracked to the site/web.
     * @return Token Request with the appropiate scope
     */
    TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason);

    /**
     * Creates a tokenRequest object for token deletion with scope merchant or shopper depending on the authenticatedShopperId being null or not.
     *
     * @param tokenEventReference
     * @param tokenReason
     * @param authenticatedShopperId
     * @return
     */
    TokenRequest createTokenRequestForDeletion(final String tokenEventReference, final String tokenReason, final String authenticatedShopperId);

    /**
     * Creates a CreateTokenServiceRequest. If merchant token is enabled, authenticatedShopperId is ignored and the create token request uses a null.
     *
     * @param merchantInfo
     * @param authenticatedShopperId
     * @param csePayment
     * @param tokenRequest
     * @return {@link CreateTokenServiceRequest}
     */
    CreateTokenServiceRequest createTokenServiceRequest(final MerchantInfo merchantInfo, final String authenticatedShopperId,
                                                        final Payment csePayment, final TokenRequest tokenRequest);

    /**
     * Creates a payment element to be used in bank transfers
     *
     * @param paymentMethod   indicates which payment method for bank transfer is going to be used (IDEAL-SSL,...)
     * @param shopperBankCode indicates the selected bank by the user
     * @return Payment object
     */
    Payment createBankPayment(final String paymentMethod, final String shopperBankCode) throws WorldpayConfigurationException;

    /**
     * Creates a payment element to be used in klarna
     *
     * @param countryCode       indicates the shopper country code
     * @param languageCode      indicates the session language code of the user
     * @param extraMerchantData extra data to be filled by the merchant
     * @return Payment object
     */
    Payment createKlarnaPayment(final String countryCode, final String languageCode, final String extraMerchantData) throws WorldpayConfigurationException;

    /**
     * Creates token
     *
     * @param subscriptionId
     * @param securityCode
     * @return Token object
     */
    Token createToken(final String subscriptionId, final String securityCode);

    /**
     * Creates an UpdateTokenServiceRequest with merchant or shopper scope depending on the site configuration
     *
     * @param merchantInfo
     * @param worldpayAdditionalInfoData
     * @param tokenRequest
     * @param paymentTokenID
     * @param cardDetails
     * @return
     */
    UpdateTokenServiceRequest createUpdateTokenServiceRequest(final MerchantInfo merchantInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                              final TokenRequest tokenRequest, final String paymentTokenID,
                                                              final CardDetails cardDetails);

    /**
     * Creates an Payment of type ApplePay with the requested details
     *
     * @param worldpayAdditionalInfoApplePayData
     * @return
     */
    Payment createApplePayPayment(final ApplePayAdditionalAuthInfo worldpayAdditionalInfoApplePayData);

    /**
     * Creates an Payment of type CSE with the requested details
     *
     * @param cseAdditionalAuthInfo
     * @param billingAddress
     * @return
     */
    Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress);

    /**
     * Creates an Payment of type GooglePay with the requested details
     *
     * @param protocolVersion
     * @param signature
     * @param signedMessage
     * @return
     */
    PayWithGoogleSSL createGooglePayPayment(final String protocolVersion, final String signature, final String signedMessage);

    /**
     * Creates a additional 3DS Data element to be used in request
     *
     * @param worldpayAdditionalInfoData The additional info to build the object
     * @return Additional3DSData object
     */
    Additional3DSData createAdditional3DSData(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates stored credentials element to be used in request.
     *
     * @param usage
     * @param merchantInitiatedReason
     * @param transactionIdentifier
     * @return
     */
    StoredCredentials createStoredCredentials(final Usage usage, final MerchantInitiatedReason merchantInitiatedReason, final String transactionIdentifier);
}
