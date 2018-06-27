package com.worldpay.service;

import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateTokenServiceRequestTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShoperId";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @Mock
    private CardDetails cardDetailsMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Test
    public void shouldCreateUpdateTokenServiceRequestWithShopperScope() {
        final UpdateTokenServiceRequest result = UpdateTokenServiceRequest.updateTokenRequestWithShopperScope(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, PAYMENT_TOKEN_ID, tokenRequestMock, cardDetailsMock);

        assertEquals(tokenRequestMock, result.getUpdateTokenRequest().getTokenRequest());
        assertEquals(cardDetailsMock, result.getUpdateTokenRequest().getCardDetails());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getUpdateTokenRequest().getAuthenticatedShopperID());
        assertEquals(PAYMENT_TOKEN_ID, result.getUpdateTokenRequest().getPaymentTokenId());
        assertEquals(merchantInfoMock, result.getMerchantInfo());
        assertFalse(result.getUpdateTokenRequest().isMerchantToken());
    }

    @Test
    public void shouldCreateUpdateTokenServiceRequestWithMerchantScope() {
        final UpdateTokenServiceRequest result = UpdateTokenServiceRequest.updateTokenRequestWithMerchantScope(merchantInfoMock, null, PAYMENT_TOKEN_ID, tokenRequestMock, cardDetailsMock);

        assertEquals(tokenRequestMock, result.getUpdateTokenRequest().getTokenRequest());
        assertEquals(cardDetailsMock, result.getUpdateTokenRequest().getCardDetails());
        assertNull(result.getUpdateTokenRequest().getAuthenticatedShopperID());
        assertEquals(PAYMENT_TOKEN_ID, result.getUpdateTokenRequest().getPaymentTokenId());
        assertEquals(merchantInfoMock, result.getMerchantInfo());
        assertTrue(result.getUpdateTokenRequest().isMerchantToken());
    }
}
