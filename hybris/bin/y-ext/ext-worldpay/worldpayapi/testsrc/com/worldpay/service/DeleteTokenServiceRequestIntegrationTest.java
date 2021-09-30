package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DeleteTokenResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class DeleteTokenServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Test
    public void testDeleteCreatedToken() throws WorldpayException {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setTokenReason(TOKEN_REASON);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setMerchantToken(false);

        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createShopperToken(gateway, merchantInfo, tokenRequest, authenticatedShopperId);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final DeleteTokenResponse deleteTokenResponse = WPSGTestHelper.deleteToken(gateway, merchantInfo, tokenRequest, paymentTokenId, authenticatedShopperId);

        assertNotNull("deleteTokenResponse is null!", deleteTokenResponse);
        assertFalse("Errors returned from authorisation code request", deleteTokenResponse.isError());
        assertNotNull("deleteTokenResponse.getDeleteTokenReply is null!", deleteTokenResponse.getDeleteTokenReply());
        assertFalse("tokenId is null", deleteTokenResponse.getDeleteTokenReply().getPaymentTokenId() == null);
        assertFalse("tokenId is empty", deleteTokenResponse.getDeleteTokenReply().getPaymentTokenId().length() == 0);
    }

}
