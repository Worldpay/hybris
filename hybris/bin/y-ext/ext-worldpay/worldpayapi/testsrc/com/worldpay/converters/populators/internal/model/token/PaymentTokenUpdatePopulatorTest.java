package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.PaymentTokenUpdate;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.data.token.UpdateTokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenUpdatePopulatorTest {

    private static final String MERCHANT = "merchant";
    private static final String SHOPPER_ID = "shopperId";
    private static final String SHOPPER = "shopper";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";

    @InjectMocks
    private PaymentTokenUpdatePopulator testObj;

    @Mock
    private Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverterMock;

    @Mock
    private UpdateTokenRequest sourceMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private CardDetails cardDetailsMock;
    @Mock
    private com.worldpay.internal.model.CardDetails intCardDetailsMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PaymentTokenUpdate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenIsMerchantToken_ShouldPopulateTokenScopeAndNotPopulateAuthenticatedShopperID() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.TRUE);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(MERCHANT);
        assertThat(target.getAuthenticatedShopperID()).isNull();
    }

    @Test
    public void populate_WhenIsNotMerchantTokenAndAuthenticatedShopperIDIsNull_ShouldNotPopulateAuthenticatedShopperID() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.FALSE);
        when(sourceMock.getAuthenticatedShopperID()).thenReturn(null);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(SHOPPER);
        assertThat(target.getAuthenticatedShopperID()).isNull();
    }

    @Test
    public void populate_WhenIsNotMerchantTokenAndAuthenticatedShopperIDIsNotNull_ShouldPopulateAuthenticatedShopperID() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.FALSE);
        when(sourceMock.getAuthenticatedShopperID()).thenReturn(SHOPPER_ID);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(SHOPPER);
        assertThat(target.getAuthenticatedShopperID().getvalue()).isEqualTo(SHOPPER_ID);
    }

    @Test
    public void populate_WhenGetTokenRequestIsNull_ShouldNotPopulateTokenEventReference() {
        when(sourceMock.getTokenRequest()).thenReturn(null);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenEventReference()).isNull();
    }

    @Test
    public void populate_WhenGetCardDetailsIsNull_ShouldNotPopulatePaymentInstrument() {
        when(sourceMock.getCardDetails()).thenReturn(null);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getPaymentInstrument()).isNull();
    }

    @Test
    public void populate_WhenGetTokenRequestIsNull_ShouldNotPopulateTokenReason() {
        when(sourceMock.getTokenRequest()).thenReturn(null);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenReason()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.FALSE);
        when(sourceMock.getAuthenticatedShopperID()).thenReturn(SHOPPER_ID);
        when(sourceMock.getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID);
        when(sourceMock.getTokenRequest()).thenReturn(tokenRequestMock);
        when(tokenRequestMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(tokenRequestMock.getTokenReason()).thenReturn(TOKEN_REASON);
        when(sourceMock.getCardDetails()).thenReturn(cardDetailsMock);
        when(internalCardDetailsConverterMock.convert(cardDetailsMock)).thenReturn(intCardDetailsMock);

        final PaymentTokenUpdate target = new PaymentTokenUpdate();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(SHOPPER);
        assertThat(target.getAuthenticatedShopperID().getvalue()).isEqualTo(SHOPPER_ID);
        assertThat(target.getPaymentTokenID().getvalue()).isEqualTo(PAYMENT_TOKEN_ID);
        assertThat(target.getTokenEventReference()).isEqualTo(TOKEN_EVENT_REFERENCE);
        assertThat(target.getPaymentInstrument().getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().get(0)).isEqualTo(intCardDetailsMock);
        assertThat(target.getTokenReason().getvalue()).isEqualTo(TOKEN_REASON);
    }
}
