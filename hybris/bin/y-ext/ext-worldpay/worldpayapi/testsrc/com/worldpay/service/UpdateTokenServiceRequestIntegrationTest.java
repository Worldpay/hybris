package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class UpdateTokenServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Test
    public void testUpdateTokenWithoutOrderWithShopperScope() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createShopperToken(gateway, merchantInfo, tokenRequest, authenticatedShopperId);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = createCardDetailsWithExpirationDate6YearsFromNow();

        final UpdateTokenResponse updateTokenResponse = WPSGTestHelper.updateTokenWithShopperScope(gateway, merchantInfo, tokenRequest, paymentTokenId, cardDetails, authenticatedShopperId);

        assertNotNull("updateTokenResponse is null!", updateTokenResponse);
        assertFalse("Errors returned from authorisation code request", updateTokenResponse.isError());
        assertNotNull("updateTokenResponse.getUpdateTokenReply is null!", updateTokenResponse.getUpdateTokenReply());
        assertNotNull("tokenId is null", updateTokenResponse.getUpdateTokenReply().getPaymentTokenId());
        assertThat(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId()).isNotEmpty();
    }

    // This test may fail with the following error:
    // <paymentService version="1.4" merchantCode="MERCHANT1ECOM"><reply><error code="5">Maximum number of updates exceeded for this token</error></reply></paymentService>
    @Test
    public void testUpdateTokenWithoutOrderWithMerchantScope() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON, true);
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createMerchantToken(gateway, merchantInfo, tokenRequest);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = createCardDetailsWithExpirationDate6YearsFromNow();

        final UpdateTokenResponse updateTokenResponse = WPSGTestHelper.updateTokenWithMerchantScope(gateway, merchantInfo, tokenRequest, paymentTokenId, cardDetails);

        assertNotNull("updateTokenResponse is null!", updateTokenResponse);
        assertFalse("Errors returned from authorisation code request", updateTokenResponse.isError());
        assertNotNull("updateTokenResponse.getUpdateTokenReply is null!", updateTokenResponse.getUpdateTokenReply());
        assertNotNull("tokenId is null", updateTokenResponse.getUpdateTokenReply().getPaymentTokenId());
        assertThat(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId()).isNotEmpty();
    }

    private CardDetails createCardDetailsWithExpirationDate6YearsFromNow() {
        final CardDetails cardDetails = new CardDetails();

        cardDetails.setExpiryDate(new Date(LocalDateTime.now().plusYears(6)));
        cardDetails.setCardHolderName("new name");
        return cardDetails;
    }
}
