package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class DeleteTokenServiceRequestIntegrationTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway gateway = WorldpayServiceGateway.getInstance();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Test
    public void testDeleteCreatedToken() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createToken(gateway, WORLD_PAY_CONFIG, merchantInfo, tokenRequest, authenticatedShopperId);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final DeleteTokenResponse deleteTokenResponse = WPSGTestHelper.deleteToken(gateway, WORLD_PAY_CONFIG, merchantInfo, tokenRequest, paymentTokenId, authenticatedShopperId);

        assertNotNull("deleteTokenResponse is null!", deleteTokenResponse);
        assertFalse("Errors returned from authorisation code request", deleteTokenResponse.isError());
        assertNotNull("deleteTokenResponse.getDeleteTokenReply is null!", deleteTokenResponse.getDeleteTokenReply());
        assertFalse("tokenId is null", deleteTokenResponse.getDeleteTokenReply().getPaymentTokenId() == null);
        assertFalse("tokenId is empty", deleteTokenResponse.getDeleteTokenReply().getPaymentTokenId().length() == 0);
    }

}
