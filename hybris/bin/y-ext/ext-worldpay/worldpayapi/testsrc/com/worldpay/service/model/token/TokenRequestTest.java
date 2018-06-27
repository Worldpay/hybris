package com.worldpay.service.model.token;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.CreateToken;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class TokenRequestTest {

    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String SHOPPER = "shopper";

    @Test
    public void shouldTransformToInternalCreateTokenWithScopeMerchant() throws WorldpayModelTransformationException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON, true);

        final CreateToken result = (CreateToken) tokenRequest.transformToInternalModel();

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason().getvalue());
        assertEquals("merchant", result.getTokenScope());
    }

    @Test
    public void shouldTransformToInternalCreateTokenWithScopeShopper() throws WorldpayModelTransformationException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON, false);

        final CreateToken result = (CreateToken) tokenRequest.transformToInternalModel();

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason().getvalue());
        assertEquals(SHOPPER, result.getTokenScope());
    }

    @Test
    public void shouldTransformToInternalCreateTokenWithoutTokenReason() throws WorldpayModelTransformationException {
        final TokenRequest tokenRequest = new TokenRequest(TOKEN_EVENT_REFERENCE, null, false);

        final CreateToken result = (CreateToken) tokenRequest.transformToInternalModel();

        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertNull(result.getTokenReason());
        assertEquals(SHOPPER, result.getTokenScope());
    }
}
