package com.worldpay.service.payment.request;

import com.worldpay.data.*;
import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Request factory for building request to Worldpay
 */
public interface WorldpayRequestService {

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
     * @param worldpayOrderCode worldpay order code
     * @param paymentMethod     indicates which payment method for bank transfer is going to be used (IDEAL-SSL,...)
     * @param shopperBankCode   indicates the selected bank by the user
     * @return Payment object
     */
    Payment createBankPayment(final String worldpayOrderCode, final String paymentMethod, final String shopperBankCode) throws WorldpayConfigurationException;

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

    /**
     * Creates {@link CardDetails} from additional auth info and payment address
     *
     * @param cseAdditionalAuthInfo
     * @param paymentAddress
     * @return
     */
    CardDetails createCardDetails(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final AddressModel paymentAddress);

    /**
     * Get the Dynamic Interaction type for the worldpay additional data
     *
     * @param worldpayAdditionalInfoData the additional data
     * @return the {@link DynamicInteractionType}
     */
    DynamicInteractionType getDynamicInteractionType(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Create an {@link AlternativeShippingAddress} object
     * @return {@link AlternativeShippingAddress}
     *
     * Add your custom implementation of this method to create an {@link AlternativeShippingAddress} object
     */
    AlternativeShippingAddress createAlternativeShippingAddress();

    /**
     * Create an {@link AlternativeShippingAddress} object
     * @return {@link AlternativeShippingAddress}
     */
    boolean isMerchantTokenEnabled();

    /**
     * Creates a tokenRequest object
     * @param tokenEventReference unique identifier for the token transaction
     * @param tokenReason refers to the seller so they can be tracked to the site/web.
     * @param merchant indicates if the merchant token is enabled
     * @return {@link TokenRequest}
     */
    TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason, final boolean merchant);
}
