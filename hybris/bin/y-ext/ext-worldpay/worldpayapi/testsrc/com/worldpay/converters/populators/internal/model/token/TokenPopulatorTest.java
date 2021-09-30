package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.TOKENSSL;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TokenPopulatorTest {

    private static final String MERCHANT = "merchant";
    private static final String SHOPPER = "shopper";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @InjectMocks
    private TokenPopulator testObj;

    @Mock
    private Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverterMock;

    @Mock
    private Token sourceMock;
    @Mock
    private CardDetails cardDetailsMock;
    @Mock
    private com.worldpay.internal.model.CardDetails intCardDetailsMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.TOKENSSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenIsMerchantToken_ShouldPopulateMerchant() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.TRUE);

        final TOKENSSL target = new TOKENSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(MERCHANT);
    }

    @Test
    public void populate_WhenIsNotMerchantToken_ShouldPopulateShopper() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.FALSE);

        final TOKENSSL target = new TOKENSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(SHOPPER);
    }

    @Test
    public void populate_WhenGetPaymentTokenIDIsNull_ShouldNotPopulatePaymentTokenID() {
        when(sourceMock.getPaymentTokenID()).thenReturn(null);

        final TOKENSSL target = new TOKENSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenGetPaymentInstrumentIsNull_ShouldNotPopulatePaymentInstrument() {
        when(sourceMock.getPaymentInstrument()).thenReturn(null);

        final TOKENSSL target = new TOKENSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession()).isEmpty();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.TRUE);
        when(sourceMock.getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
        when(sourceMock.getPaymentInstrument()).thenReturn(cardDetailsMock);

        when(internalCardDetailsConverterMock.convert(cardDetailsMock)).thenReturn(intCardDetailsMock);

        final TOKENSSL target = new TOKENSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(MERCHANT);
        assertThat(target.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession().size()).isEqualTo(2);
    }
}
