package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class UpdateTokenServiceRequestIntegrationTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway gateway = WorldpayServiceGateway.getInstance();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Test
    public void testUpdateTokenWithoutOrder() throws WorldpayException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
        final String authenticatedShopperId = UUID.randomUUID().toString();
        final CreateTokenResponse createTokenResponse = WPSGTestHelper.createToken(gateway, WORLD_PAY_CONFIG, merchantInfo, tokenRequest, authenticatedShopperId);
        final String paymentTokenId = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = createCardDetails();
        final UpdateTokenResponse updateTokenResponse = WPSGTestHelper.updateToken(gateway, WORLD_PAY_CONFIG, merchantInfo, tokenRequest, paymentTokenId, cardDetails, authenticatedShopperId);

        assertNotNull("updateTokenResponse is null!", updateTokenResponse);
        assertFalse("Errors returned from authorisation code request", updateTokenResponse.isError());
        assertNotNull("updateTokenResponse.getUpdateTokenReply is null!", updateTokenResponse.getUpdateTokenReply());
        assertFalse("tokenId is null", updateTokenResponse.getUpdateTokenReply().getPaymentTokenId() == null);
        assertFalse("tokenId is empty", updateTokenResponse.getUpdateTokenReply().getPaymentTokenId().length() == 0);
    }

    private CardDetails createCardDetails() {
        final CardDetails cardDetails = new CardDetails();

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 6);
        final Date expiryDate = new Date(calendar);

        cardDetails.setExpiryDate(expiryDate);
        cardDetails.setCardHolderName("new name");
        return cardDetails;
    }
}
