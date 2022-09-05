package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.data.token.TokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TokenRequestPopulatorTest {

    private static final String MERCHANT = "merchant";
    private static final String SHOPPER = "shopper";
    private static final String TOKEN_REASON = "tokenReason";
    private static final String TOKEN_REFERENCE = "tokenReference";

    @InjectMocks
    private TokenRequestPopulator testObj;

    @Mock
    private TokenRequest sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.CreateToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetTokenReasonIsNull_ShouldNotPopulateTokenReason() {
        when(sourceMock.getTokenReason()).thenReturn(null);

        final com.worldpay.internal.model.CreateToken target = new com.worldpay.internal.model.CreateToken();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenReason()).isNull();
    }

    @Test
    public void populate_WhenIsMerchantToken_ShouldPopulateMerchantTokenReference() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.TRUE);

        final com.worldpay.internal.model.CreateToken target = new com.worldpay.internal.model.CreateToken();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(MERCHANT);
    }

    @Test
    public void populate_WhenIsNotMerchantToken_ShouldPopulateShopperTokenReference() {
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.FALSE);

        final com.worldpay.internal.model.CreateToken target = new com.worldpay.internal.model.CreateToken();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenScope()).isEqualTo(SHOPPER);
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getTokenReason()).thenReturn(TOKEN_REASON);
        when(sourceMock.getTokenEventReference()).thenReturn(TOKEN_REFERENCE);
        when(sourceMock.isMerchantToken()).thenReturn(Boolean.TRUE);

        final com.worldpay.internal.model.CreateToken target = new com.worldpay.internal.model.CreateToken();
        testObj.populate(sourceMock, target);

        assertThat(target.getTokenReason().getvalue()).isEqualTo(TOKEN_REASON);
        assertThat(target.getTokenEventReference()).isEqualTo(TOKEN_REFERENCE);
        assertThat(target.getTokenScope()).isEqualTo(MERCHANT);
    }
}
