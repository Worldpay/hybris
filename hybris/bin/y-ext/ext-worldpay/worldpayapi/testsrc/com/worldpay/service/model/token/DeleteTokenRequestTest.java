package com.worldpay.service.model.token;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentTokenDelete;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeleteTokenRequestTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    private DeleteTokenRequest testObj;

    @Mock
    private TokenRequest tokenRequestMock;

    @Before
    public void setUp(){
        testObj = new DeleteTokenRequest(PAYMENT_TOKEN_ID, AUTHENTICATED_SHOPPER_ID, tokenRequestMock);
        when(tokenRequestMock.getTokenReason()).thenReturn(TOKEN_REASON);
        when(tokenRequestMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
    }

    @Test
    public void shouldTransformToAPaymentTokenDeleteWithShopperScope() {
        final InternalModelObject result = testObj.transformToInternalModel();

        assertTrue(result instanceof PaymentTokenDelete);
        final PaymentTokenDelete paymentTokenDelete = (PaymentTokenDelete) result;
        assertEquals(PAYMENT_TOKEN_ID, paymentTokenDelete.getPaymentTokenID().getvalue());
        assertEquals(AUTHENTICATED_SHOPPER_ID, paymentTokenDelete.getAuthenticatedShopperID());
        assertEquals(TOKEN_REASON, paymentTokenDelete.getTokenReason().getvalue());
        assertEquals(TOKEN_EVENT_REFERENCE, paymentTokenDelete.getTokenEventReference());
        assertEquals("shopper", paymentTokenDelete.getTokenScope());
    }

    @Test
    public void shouldTransformToAPaymentTokenDeleteWithMerchantScope() {
        when(tokenRequestMock.isMerchantToken()).thenReturn(true);

        final InternalModelObject result = testObj.transformToInternalModel();

        assertTrue(result instanceof PaymentTokenDelete);
        final PaymentTokenDelete paymentTokenDelete = (PaymentTokenDelete) result;
        assertEquals(PAYMENT_TOKEN_ID, paymentTokenDelete.getPaymentTokenID().getvalue());
        assertEquals(AUTHENTICATED_SHOPPER_ID, paymentTokenDelete.getAuthenticatedShopperID());
        assertEquals(TOKEN_REASON, paymentTokenDelete.getTokenReason().getvalue());
        assertEquals(TOKEN_EVENT_REFERENCE, paymentTokenDelete.getTokenEventReference());
        assertEquals("merchant", paymentTokenDelete.getTokenScope());
    }
}
