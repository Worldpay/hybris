package com.worldpay.service.model.payment;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.TOKENSSL;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class TokenTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String SHOPPER = "shopper";
    private static final String MERCHANT = "merchant";

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDWithMerchantScope() throws WorldpayModelTransformationException {
        final Token token = new Token(PAYMENT_TOKEN_ID, true);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertEquals(MERCHANT, result.getTokenScope());
        assertNull(result.getPaymentInstrument());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDWithShopperScope() throws WorldpayModelTransformationException {
        final Token token = new Token(PAYMENT_TOKEN_ID, false);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertEquals(SHOPPER, result.getTokenScope());
        assertNull(result.getPaymentInstrument());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDAndPaymentInstrumentWithMerchantScope() throws WorldpayModelTransformationException {
        final CardDetails paymentInstrument = new CardDetails();
        paymentInstrument.setCardNumber(CARD_NUMBER);

        final Token token = new Token(PAYMENT_TOKEN_ID, paymentInstrument, true);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertEquals(MERCHANT, result.getTokenScope());
        assertEquals(CARD_NUMBER, ((com.worldpay.internal.model.CardDetails) result.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().get(0)).getDerived().getObfuscatedPAN());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDAndPaymentInstrumentWithShopperScope() throws WorldpayModelTransformationException {
        final CardDetails paymentInstrument = new CardDetails();
        paymentInstrument.setCardNumber(CARD_NUMBER);

        final Token token = new Token(PAYMENT_TOKEN_ID, paymentInstrument, false);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertEquals(SHOPPER, result.getTokenScope());
        assertEquals(CARD_NUMBER, ((com.worldpay.internal.model.CardDetails) result.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().get(0)).getDerived().getObfuscatedPAN());
    }
}
