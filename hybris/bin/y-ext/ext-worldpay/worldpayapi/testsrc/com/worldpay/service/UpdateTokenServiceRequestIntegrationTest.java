package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerJUnit5BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class UpdateTokenServiceRequestIntegrationTest extends ServicelayerJUnit5BaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    private MerchantInfo merchantInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @BeforeEach
    void setUp() {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    void testUpdateTokenWithoutOrderWithShopperScope() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setMerchantToken(false);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setTokenReason(TOKEN_REASON);

        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createShopperToken(gateway, merchantInfo, tokenRequest, authenticatedShopperId);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = createCardDetailsWithExpirationDate6YearsFromNow();

        final UpdateTokenResponse updateTokenResponse = WPSGTestHelper.updateTokenWithShopperScope(gateway, merchantInfo, tokenRequest, paymentTokenId, cardDetails, authenticatedShopperId);

        assertNotNull(updateTokenResponse, "updateTokenResponse is null!");
        assertFalse(updateTokenResponse.isError(), "Errors returned from authorisation code request");
        assertNotNull(updateTokenResponse.getUpdateTokenReply(), "updateTokenResponse.getUpdateTokenReply is null!");
        assertNotNull(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId(), "tokenId is null");
        assertThat(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId()).isNotEmpty();
    }

    // This test may fail with the following error:
    // <paymentService version="1.4" merchantCode="MERCHANT1ECOM"><reply><error code="5">Maximum number of updates exceeded for this token</error></reply></paymentService>
    @Test
    @Disabled("ignored because its unstable")
    void testUpdateTokenWithoutOrderWithMerchantScope() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setMerchantToken(true);
        tokenRequest.setTokenEventReference(TOKEN_EVENT_REFERENCE);
        tokenRequest.setTokenReason(TOKEN_REASON);
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createMerchantToken(gateway, merchantInfo, tokenRequest);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = createCardDetailsWithExpirationDate6YearsFromNow();

        final UpdateTokenResponse updateTokenResponse = WPSGTestHelper.updateTokenWithMerchantScope(gateway, merchantInfo, tokenRequest, paymentTokenId, cardDetails);

        assertNotNull(updateTokenResponse, "updateTokenResponse is null!");
        assertFalse(updateTokenResponse.isError(), "Errors returned from authorisation code request");
        assertNotNull(updateTokenResponse.getUpdateTokenReply(), "updateTokenResponse.getUpdateTokenReply is null!");
        assertNotNull(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId(), "tokenId is null");
        assertThat(updateTokenResponse.getUpdateTokenReply().getPaymentTokenId()).isNotEmpty();
    }

    private CardDetails createCardDetailsWithExpirationDate6YearsFromNow() {
        final CardDetails cardDetails = new CardDetails();

        cardDetails.setExpiryDate(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now().plusYears(6)));
        cardDetails.setCardHolderName("new name");
        return cardDetails;
    }
}
