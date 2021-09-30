package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.data.token.DeleteTokenRequest;
import com.worldpay.data.token.TokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenDeletePopulatorTest {

    private static final String SHOPPER_ID = "shopperId";
    private static final String TOKEN_ID = "tokenId";

    @InjectMocks
    private PaymentTokenDeletePopulator testObj;

    @Mock
    private DeleteTokenRequest sourceMock;
    @Mock
    private TokenRequest tokenRequestMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PaymentTokenDelete());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulatePaymentTokenDelete() {
        when(sourceMock.getTokenRequest()).thenReturn(tokenRequestMock);
        when(sourceMock.getAuthenticatedShopperId()).thenReturn(SHOPPER_ID);
        when(sourceMock.getPaymentTokenId()).thenReturn(TOKEN_ID);

        final com.worldpay.internal.model.PaymentTokenDelete targetMock = new com.worldpay.internal.model.PaymentTokenDelete();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticatedShopperID().getvalue()).isEqualTo(SHOPPER_ID);
        assertThat(targetMock.getPaymentTokenID().getvalue()).isEqualTo(TOKEN_ID);
        assertThat(targetMock.getTokenEventReference()).isEqualTo(tokenRequestMock.getTokenEventReference());
        assertThat(targetMock.getTokenReason().getvalue()).isEqualTo(tokenRequestMock.getTokenReason());
    }
}
