package com.worldpay.service.model.token;

import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.payment.StoredCredentials;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.*;

@UnitTest
public class CardTokenRequestTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final Cse PAYMENT = PaymentBuilder.createCSE("encryptedData", BILLING_ADDRESS);
    private static final String SCHEME_TRANSACTION_IDENTIFIER = "schemeTransactionIdentifier";

    @Test
    public void paymentTokenCreateShouldContainAnAuthenticatedShopperIdIfProvided() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(TOKEN_REQUEST, AUTHENTICATED_SHOPPER_ID, PAYMENT);
        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getAuthenticatedShopperID());
    }

    @Test
    public void paymentTokenCreateShouldNotContainAnAuthenticatedShopperIdIfNotProvided() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(TOKEN_REQUEST, null, PAYMENT);
        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        assertNull(result.getAuthenticatedShopperID());
    }

    @Test
    public void paymentTokenCreateShouldContainTokenRequestIfProvided() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(TOKEN_REQUEST, AUTHENTICATED_SHOPPER_ID, PAYMENT);
        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        assertEquals(TOKEN_REASON, result.getCreateToken().getTokenReason().getvalue());
        assertEquals(TOKEN_EVENT_REFERENCE, result.getCreateToken().getTokenEventReference());
    }

    @Test
    public void paymentTokenCreateShouldNotContainTokenRequestIfNotProvided() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(null, AUTHENTICATED_SHOPPER_ID, PAYMENT);
        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        assertNull(result.getCreateToken());
    }

    @Test
    public void paymentTokenCreateShouldContainPayment() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(TOKEN_REQUEST, AUTHENTICATED_SHOPPER_ID, PAYMENT);
        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        final CSEDATA csePayment = (CSEDATA) result.getPaymentInstrumentOrCSEDATA().get(0);
        final com.worldpay.internal.model.Address address = csePayment.getCardAddress().getAddress();

        assertEquals(PAYMENT.getEncryptedData(), csePayment.getEncryptedData());
        assertEquals(BILLING_ADDRESS.getFirstName(), address.getFirstName());
        assertEquals(BILLING_ADDRESS.getLastName(), address.getLastName());
        for (final Object addressLine : address.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3()) {
            if (addressLine instanceof Address1) {
                assertEquals(BILLING_ADDRESS.getAddress1(), ((Address1) addressLine).getvalue());
            } else if (addressLine instanceof Address2) {
                assertEquals(BILLING_ADDRESS.getAddress2(), ((Address2) addressLine).getvalue());
            } else {
                assertEquals(BILLING_ADDRESS.getAddress3(), ((Address3) addressLine).getvalue());
            }
        }
        assertEquals(BILLING_ADDRESS.getPostalCode(), address.getPostalCode());
        assertEquals(BILLING_ADDRESS.getCity(), address.getCity());
        assertEquals(BILLING_ADDRESS.getCountryCode(), address.getCountryCode());
    }

    @Test
    public void transformToInternalModel_ShouldSetStoredCredentialAttribute_WhenCardTokenRequestHasStoredCredentialsNotNull() throws WorldpayModelTransformationException {
        final StoredCredentials storedCredentials = new StoredCredentials(MerchantInitiatedReason.UNSCHEDULED, SCHEME_TRANSACTION_IDENTIFIER, Usage.FIRST);
        final CardTokenRequest cardTokenRequest = new CardTokenRequest(TOKEN_REQUEST, AUTHENTICATED_SHOPPER_ID, PAYMENT, storedCredentials);

        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        final com.worldpay.internal.model.StoredCredentials intStoredCredentials = result.getStoredCredentials();

        assertNotNull(intStoredCredentials);
    }

    @Test
    public void transformToInternalModel_ShouldNotSetStoredCredentialAttribute_WhenCardTokenRequestHasNotStoredCredentialsNotNull() throws WorldpayModelTransformationException {
        final CardTokenRequest cardTokenRequest = new CardTokenRequest( TOKEN_REQUEST, AUTHENTICATED_SHOPPER_ID, PAYMENT);

        final PaymentTokenCreate result = (PaymentTokenCreate) cardTokenRequest.transformToInternalModel();
        final com.worldpay.internal.model.StoredCredentials intStoredCredentials = result.getStoredCredentials();

        assertNull(intStoredCredentials);
    }
}
