package com.worldpay.service;

import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequestForMerchantToken;
import static com.worldpay.service.request.CreateTokenServiceRequest.createTokenRequestForShopperToken;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.worldpay.data.Address;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.payment.Cse;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.request.CreateTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class CreateTokenServiceRequestTest {

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

    @BeforeEach
    public void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);

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
        assertThatThrownBy(() -> createTokenRequestForShopperToken(null, AUTHENTICATED_SHOPPER_ID, payment, tokenRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
    }

    @Test
    public void createTokenRequestForShopperWillRaiseIllegalArgumentExceptionWhenPaymentIsNull() {
        assertThatThrownBy(() -> createTokenRequestForShopperToken(merchantInfo, AUTHENTICATED_SHOPPER_ID, null, tokenRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
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
        assertThatThrownBy(() -> createTokenRequestForMerchantToken(null, payment, tokenRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
    }

    @Test
    public void createTokenRequestForMerchantWillRaiseIllegalArgumentExceptionWhenPaymentIsNull() {
        createTokenRequestForMerchantToken(merchantInfo, payment, tokenRequest);
        assertThatThrownBy(() -> createTokenRequestForMerchantToken(merchantInfo, null, tokenRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Required parameter to create CreateTokenServiceRequest cannot be null");
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
