package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeleteTokenServiceRequestTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShoperId";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Test
    public void shouldCreateDeleteTokenServiceRequest(){
        final DeleteTokenServiceRequest result = DeleteTokenServiceRequest.deleteTokenRequest(merchantInfoMock, AUTHENTICATED_SHOPPER_ID, PAYMENT_TOKEN_ID, tokenRequestMock);

        assertEquals(tokenRequestMock, result.getDeleteTokenRequest().getTokenRequest());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getDeleteTokenRequest().getAuthenticatedShopperId());
        assertEquals(PAYMENT_TOKEN_ID, result.getDeleteTokenRequest().getPaymentTokenId());
        assertEquals(merchantInfoMock, result.getMerchantInfo());
    }
}
