package com.worldpay.service.model.payment;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.TOKENSSL;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

        final List<Object> tokenElements = result.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        assertThat(tokenElements).hasSize(1);
        assertThat(tokenElements.get(0)).isInstanceOf(PaymentTokenID.class);
        assertThat(((PaymentTokenID) tokenElements.get(0)).getvalue()).isEqualTo(PAYMENT_TOKEN_ID);
        assertEquals(MERCHANT, result.getTokenScope());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDWithShopperScope() throws WorldpayModelTransformationException {
        final Token token = new Token(PAYMENT_TOKEN_ID, false);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        final List<Object> tokenElements = result.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        assertThat(tokenElements).hasSize(1);
        assertThat(tokenElements.get(0)).isInstanceOf(PaymentTokenID.class);
        assertThat(((PaymentTokenID) tokenElements.get(0)).getvalue()).isEqualTo(PAYMENT_TOKEN_ID);
        assertEquals(SHOPPER, result.getTokenScope());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDAndPaymentInstrumentWithMerchantScope() throws WorldpayModelTransformationException {
        final CardDetails paymentInstrument = new CardDetails();
        paymentInstrument.setCardNumber(CARD_NUMBER);

        final Token token = new Token(PAYMENT_TOKEN_ID, paymentInstrument, true);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        final List<Object> tokenElements = result.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        assertThat(tokenElements).hasSize(2);
        assertThat(tokenElements.get(0)).isInstanceOf(PaymentTokenID.class);
        assertThat(((PaymentTokenID) tokenElements.get(0)).getvalue()).isEqualTo(PAYMENT_TOKEN_ID);
        assertThat(tokenElements.get(1)).isInstanceOf(PaymentInstrument.class);
        assertThat((((com.worldpay.internal.model.CardDetails) ((PaymentInstrument) tokenElements.get(1)).getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().get(0))).getDerived().getObfuscatedPAN()).isEqualTo(CARD_NUMBER);
        assertEquals(MERCHANT, result.getTokenScope());
    }

    @Test
    public void shouldReturnTokenSSLWithPaymentTokenIDAndPaymentInstrumentWithShopperScope() throws WorldpayModelTransformationException {
        final CardDetails paymentInstrument = new CardDetails();
        paymentInstrument.setCardNumber(CARD_NUMBER);

        final Token token = new Token(PAYMENT_TOKEN_ID, paymentInstrument, false);

        final TOKENSSL result = (TOKENSSL) token.transformToInternalModel();

        final List<Object> tokenElements = result.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        assertThat(tokenElements).hasSize(2);
        assertThat(tokenElements.get(0)).isInstanceOf(PaymentTokenID.class);
        assertThat(((PaymentTokenID) tokenElements.get(0)).getvalue()).isEqualTo(PAYMENT_TOKEN_ID);
        assertThat(tokenElements.get(1)).isInstanceOf(PaymentInstrument.class);
        assertThat((((com.worldpay.internal.model.CardDetails) ((PaymentInstrument) tokenElements.get(1)).getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSL().get(0))).getDerived().getObfuscatedPAN()).isEqualTo(CARD_NUMBER);
        assertEquals(SHOPPER, result.getTokenScope());
    }
}
