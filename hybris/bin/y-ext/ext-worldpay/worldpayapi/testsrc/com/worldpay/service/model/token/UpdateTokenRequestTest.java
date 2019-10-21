package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentTokenUpdate;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateTokenRequestTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    private UpdateTokenRequest testObj;

    @Mock
    private CardDetails cardDetailsMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private com.worldpay.internal.model.CardDetails intCardDetailsMock;

    @Before
    public void setUp() {
        when(cardDetailsMock.transformToInternalModel()).thenReturn(intCardDetailsMock);
        when(tokenRequestMock.getTokenReason()).thenReturn(TOKEN_REASON);
        when(tokenRequestMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
    }

    @Test
    public void shouldTransformToAPaymentTokenUpdateWithMerchantScope() {
        testObj = new UpdateTokenRequest(PAYMENT_TOKEN_ID, AUTHENTICATED_SHOPPER_ID, cardDetailsMock, tokenRequestMock, true);

        final InternalModelObject result = testObj.transformToInternalModel();

        assertTrue(result instanceof PaymentTokenUpdate);
        final PaymentTokenUpdate paymentTokenUpdate = (PaymentTokenUpdate) result;
        assertEquals(PAYMENT_TOKEN_ID, paymentTokenUpdate.getPaymentTokenID().getvalue());
        assertNull(paymentTokenUpdate.getAuthenticatedShopperID());
        assertEquals(intCardDetailsMock, paymentTokenUpdate.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().get(0));
        assertEquals(TOKEN_REASON, paymentTokenUpdate.getTokenReason().getvalue());
        assertEquals(TOKEN_EVENT_REFERENCE, paymentTokenUpdate.getTokenEventReference());
        assertEquals("merchant", paymentTokenUpdate.getTokenScope());
    }

    @Test
    public void shouldTransformToAPaymentTokenUpdateWithShopperScope() {
        testObj = new UpdateTokenRequest(PAYMENT_TOKEN_ID, AUTHENTICATED_SHOPPER_ID, cardDetailsMock, tokenRequestMock, false);

        final InternalModelObject result = testObj.transformToInternalModel();

        assertTrue(result instanceof PaymentTokenUpdate);
        final PaymentTokenUpdate paymentTokenUpdate = (PaymentTokenUpdate) result;
        assertEquals(PAYMENT_TOKEN_ID, paymentTokenUpdate.getPaymentTokenID().getvalue());
        assertEquals(AUTHENTICATED_SHOPPER_ID, paymentTokenUpdate.getAuthenticatedShopperID());
        assertEquals(intCardDetailsMock, paymentTokenUpdate.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().get(0));
        assertEquals(TOKEN_REASON, paymentTokenUpdate.getTokenReason().getvalue());
        assertEquals(TOKEN_EVENT_REFERENCE, paymentTokenUpdate.getTokenEventReference());
        assertEquals("shopper", paymentTokenUpdate.getTokenScope());
    }
}
