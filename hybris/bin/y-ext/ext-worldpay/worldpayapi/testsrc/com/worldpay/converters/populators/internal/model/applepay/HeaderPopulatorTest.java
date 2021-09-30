package com.worldpay.converters.populators.internal.model.applepay;

import com.worldpay.data.applepay.Header;
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
public class HeaderPopulatorTest {

    private static final String APPLICATION_DATA = "applicationData";
    private static final String EPHEMERAL_PUBLIC_KEY = "EphemeralPublicKey";
    private static final String PUBLIC_KEY_HASH = "publicKeyHash";
    private static final String TRANSACTION_ID = "transactionId";

    @InjectMocks
    private HeaderPopulator testObj;

    @Mock
    private Header sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Header());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateHeader() {
        when(sourceMock.getApplicationData()).thenReturn(APPLICATION_DATA);
        when(sourceMock.getEphemeralPublicKey()).thenReturn(EPHEMERAL_PUBLIC_KEY);
        when(sourceMock.getPublicKeyHash()).thenReturn(PUBLIC_KEY_HASH);
        when(sourceMock.getTransactionId()).thenReturn(TRANSACTION_ID);

        final com.worldpay.internal.model.Header targetMock = new com.worldpay.internal.model.Header();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getApplicationData()).isEqualTo(APPLICATION_DATA);
        assertThat(targetMock.getEphemeralPublicKey()).isEqualTo(EPHEMERAL_PUBLIC_KEY);
        assertThat(targetMock.getPublicKeyHash()).isEqualTo(PUBLIC_KEY_HASH);
        assertThat(targetMock.getTransactionId()).isEqualTo(TRANSACTION_ID);
    }
}
