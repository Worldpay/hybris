package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class CreateTokenServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Test
    public void testCreateTokenWithoutOrder() throws WorldpayException {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);
        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createShopperToken(gateway, merchantInfo, tokenRequest, authenticatedShopperId);

        assertNotNull("createTokenResponse is null!", createTokenResponse);
        assertFalse("Errors returned from authorisation code request", createTokenResponse.isError());
        assertNotNull("createTokenResponse.getToken is null!", createTokenResponse.getToken());
        assertFalse("tokenId is empty or null", createTokenResponse.getToken().getTokenDetails().getPaymentTokenID().isEmpty());
    }
}
