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

    public static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    public static final String CARD_NUMBER = "cardNumber";

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenID() throws WorldpayModelTransformationException {
        final Token token = new Token(PAYMENT_TOKEN_ID);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertNull(result.getPaymentInstrument());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDAndPaymentInstrument() throws WorldpayModelTransformationException {
        final CardDetails paymentInstrument = new CardDetails();
        paymentInstrument.setCardNumber(CARD_NUMBER);

        final Token token = new Token(PAYMENT_TOKEN_ID, paymentInstrument);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        assertEquals(PAYMENT_TOKEN_ID, result.getPaymentTokenID());
        assertEquals(CARD_NUMBER,((com.worldpay.internal.model.CardDetails) result.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().get(0)).getDerived().getObfuscatedPAN());
    }
}
