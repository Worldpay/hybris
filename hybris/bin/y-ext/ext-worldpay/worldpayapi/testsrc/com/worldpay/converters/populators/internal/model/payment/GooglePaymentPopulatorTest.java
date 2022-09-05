package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.PAYWITHGOOGLESSL;
import com.worldpay.data.payment.PayWithGoogleSSL;
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
public class GooglePaymentPopulatorTest {

    private static final String PROTOCOL_VERSION = "protocolVersion";
    private static final String SIGNATURE = "signature";
    private static final String SIGNED_MESSAGE = "signedMessage";

    @InjectMocks
    private GooglePaymentPopulator testObj;

    @Mock
    private PayWithGoogleSSL sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PAYWITHGOOGLESSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetProtocolVersionIsNull_ShouldNotPopulateProtocolVersion() {
        when(sourceMock.getProtocolVersion()).thenReturn(null);

        final PAYWITHGOOGLESSL target = new PAYWITHGOOGLESSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getProtocolVersion()).isNull();
    }

    @Test
    public void populate_WhenGetSignatureIsNull_ShouldNotPopulateSignature() {
        when(sourceMock.getSignature()).thenReturn(null);

        final PAYWITHGOOGLESSL target = new PAYWITHGOOGLESSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getSignature()).isNull();
    }

    @Test
    public void populate_WhenGetSignedMessageIsNull_ShouldNotPopulateSignedMessage() {
        when(sourceMock.getSignedMessage()).thenReturn(null);

        final PAYWITHGOOGLESSL target = new PAYWITHGOOGLESSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getSignedMessage()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(sourceMock.getSignature()).thenReturn(SIGNATURE);
        when(sourceMock.getSignedMessage()).thenReturn(SIGNED_MESSAGE);

        final PAYWITHGOOGLESSL target = new PAYWITHGOOGLESSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getProtocolVersion()).isEqualTo(PROTOCOL_VERSION);
        assertThat(target.getSignature()).isEqualTo(SIGNATURE);
        assertThat(target.getSignedMessage()).isEqualTo(SIGNED_MESSAGE);
    }
}
