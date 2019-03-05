package com.worldpay.service.model.payment;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PAYWITHGOOGLESSL;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PayWithGoogleSSLTest {

    private PayWithGoogleSSL testObj = new PayWithGoogleSSL("protocolVersion", "signature", "signedMessage");

    @Test
    public void shouldReturnAPAYWITHGOOGLESSLObjectWithCorrectValuesInMembers() throws WorldpayModelTransformationException {
        final PAYWITHGOOGLESSL result = (PAYWITHGOOGLESSL) testObj.transformToInternalModel();

        assertThat(result.getProtocolVersion()).isEqualTo("protocolVersion");
        assertThat(result.getSignature()).isEqualTo("signature");
        assertThat(result.getSignedMessage()).isEqualTo("signedMessage");

    }
}
