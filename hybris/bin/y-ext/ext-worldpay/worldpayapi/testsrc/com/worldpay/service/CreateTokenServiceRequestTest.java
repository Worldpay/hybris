package com.worldpay.service;

import com.worldpay.data.Address;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.CreateTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequestForMerchantToken;
import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequestForShopperToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class CreateTokenServiceRequestTest {

    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ENCRYPTED_DATA = "encryptedData";
    private static final String CITY = "city";
    private static final String GB = "GB";
    private static final String NAME = "John";
    private static final String SHOPPER = "Shopper";
    private static final String SHOPPER_ADDRESS_1 = "Shopper Address1";
    private static final String SHOPPER_ADDRESS_2 = "Shopper Address2";
    private static final String SHOPPER_ADDRESS_3 = "Shopper Address3";
    private static final String POSTAL_CODE = "Postal code";

    private Payment payment;
    private MerchantInfo merchantInfo;
    private TokenRequest tokenRequest;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);
        this.tokenRequest = tokenRequest;

        final Address billingAddress = new Address();
        billingAddress.setFirstName(NAME);
        billingAddress.setLastName(SHOPPER);
        billingAddress.setAddress1(SHOPPER_ADDRESS_1);
        billingAddress.setAddress2(SHOPPER_ADDRESS_2);
        billingAddress.setAddress3(SHOPPER_ADDRESS_3);
        billingAddress.setPostalCode(POSTAL_CODE);
        billingAddress.setCity(CITY);
        billingAddress.setCountryCode(GB);

        final Cse cse = new Cse();
        cse.setEncryptedData(ENCRYPTED_DATA);
        cse.setAddress(billingAddress);
        cse.setPaymentType(PaymentType.CSEDATA.getMethodCode());
        this.payment = cse;
    }

    @Test
    public void createTokenRequestForShopperWillRaiseIllegalArgumentExceptionWhenMerchantIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        final MerchantInfo merchant = null;
        createTokenRequestForShopperToken(merchant, AUTHENTICATED_SHOPPER_ID, payment, tokenRequest);
    }

    @Test
    public void createTokenRequestForShopperWillRaiseIllegalArgumentExceptionWhenPaymentIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        final Payment payment = null;
        createTokenRequestForShopperToken(merchantInfo, AUTHENTICATED_SHOPPER_ID, payment, tokenRequest);
    }

    @Test
    public void createTokenRequestForShopperShouldBuildRequest() {
        final CreateTokenServiceRequest result = createTokenRequestForShopperToken(merchantInfo, AUTHENTICATED_SHOPPER_ID, payment, tokenRequest);
        assertEquals(merchantInfo, result.getMerchantInfo());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getCardTokenRequest().getAuthenticatedShopperId());
        assertEquals(payment, result.getCardTokenRequest().getPayment());
        assertEquals(tokenRequest, result.getCardTokenRequest().getTokenRequest());
    }

    @Test
    public void createTokenRequestForMerchantWillRaiseIllegalArgumentExceptionWhenMerchantIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        final MerchantInfo merchant = null;
        createTokenRequestForMerchantToken(merchant, payment, tokenRequest);
    }

    @Test
    public void createTokenRequestForMerchantWillRaiseIllegalArgumentExceptionWhenPaymentIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
        final Payment payment = null;
        createTokenRequestForMerchantToken(merchantInfo, payment, tokenRequest);
    }

    @Test
    public void createTokenRequestForMerchantShouldBuildRequest() {
        final CreateTokenServiceRequest result = createTokenRequestForMerchantToken(merchantInfo, payment, tokenRequest);
        assertEquals(merchantInfo, result.getMerchantInfo());
        assertNull(result.getCardTokenRequest().getAuthenticatedShopperId());
        assertEquals(payment, result.getCardTokenRequest().getPayment());
        assertEquals(tokenRequest, result.getCardTokenRequest().getTokenRequest());
    }
}
