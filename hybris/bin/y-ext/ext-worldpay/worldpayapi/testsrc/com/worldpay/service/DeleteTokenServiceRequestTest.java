package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.request.DeleteTokenServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DeleteTokenServiceRequestTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShoperId";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;

    @Test
    public void shouldCreateDeleteTokenServiceRequest(){
        final DeleteTokenServiceRequest result = DeleteTokenServiceRequest.deleteTokenRequest(worldpayConfigMock, merchantInfoMock, AUTHENTICATED_SHOPPER_ID, PAYMENT_TOKEN_ID, tokenRequestMock);

        assertEquals(tokenRequestMock, result.getDeleteTokenRequest().getTokenRequest());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getDeleteTokenRequest().getAuthenticatedShopperID());
        assertEquals(PAYMENT_TOKEN_ID, result.getDeleteTokenRequest().getPaymentTokenID());
        assertEquals(worldpayConfigMock, result.getWorldpayConfig());
        assertEquals(merchantInfoMock, result.getMerchantInfo());
    }
}
