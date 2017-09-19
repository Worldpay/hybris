package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.CreateTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequest;
import static org.junit.Assert.assertEquals;

@UnitTest
public class CreateTokenServiceRequestTest {

    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final Address BILLING_ADDRESS = new Address("John", "Shopper", "Shopper Address1", "Shopper Address2", "Shopper Address3", "postalCode", "city", "GB");
    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo("MERCHANT1ECOM", "3l3ph4nt_&_c4st!3");
    private static final TokenRequest TOKEN_REQUEST = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final Payment PAYMENT = PaymentBuilder.createCSE(ENCRYPTED_DATA, BILLING_ADDRESS);


    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createTokenRequestWillRaiseIllegalArgumentExceptionWhenConfigIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        WorldpayConfig config = null;
        createTokenRequest(config, MERCHANT_INFO, AUTHENTICATED_SHOPPER_ID, PAYMENT, TOKEN_REQUEST);
    }

    @Test
    public void createTokenRequestWillRaiseIllegalArgumentExceptionWhenMerchantIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        MerchantInfo merchant = null;
        createTokenRequest(WORLD_PAY_CONFIG, merchant, AUTHENTICATED_SHOPPER_ID, PAYMENT, TOKEN_REQUEST);
    }

    @Test
    public void createTokenRequestWillRaiseIllegalArgumentExceptionWhenPaymentIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        Payment payment = null;
        createTokenRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, AUTHENTICATED_SHOPPER_ID, payment, TOKEN_REQUEST);
    }

    @Test
    public void createTokenRequestShouldBuildRequest() {
        final CreateTokenServiceRequest result = createTokenRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, AUTHENTICATED_SHOPPER_ID, PAYMENT, TOKEN_REQUEST);
        assertEquals(WORLD_PAY_CONFIG, result.getWorldpayConfig());
        assertEquals(MERCHANT_INFO, result.getMerchantInfo());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getCardTokenRequest().getAuthenticatedShopperId());
        assertEquals(PAYMENT, result.getCardTokenRequest().getPayment());
        assertEquals(TOKEN_REQUEST, result.getCardTokenRequest().getTokenRequest());
    }
}
