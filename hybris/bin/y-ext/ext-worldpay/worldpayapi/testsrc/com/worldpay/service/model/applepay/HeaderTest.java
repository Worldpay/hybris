package com.worldpay.service.model.applepay;

import com.worldpay.exception.WorldpayModelTransformationException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class HeaderTest {

    private static final String EPHEMERAL_PUBLIC_KEY = "ephemeralPublicKey";
    private static final String APPLICATION_DATA = "applicationData";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String PUBLIC_KEY_HASH = "publicKeyHash";
    private Header testObj = new Header(EPHEMERAL_PUBLIC_KEY, PUBLIC_KEY_HASH, TRANSACTION_ID, APPLICATION_DATA);

    @Test
    public void shouldTransformToInternalModel() throws WorldpayModelTransformationException {

        final com.worldpay.internal.model.Header result = (com.worldpay.internal.model.Header) testObj.transformToInternalModel();

        assertThat(result.getApplicationData()).isEqualTo(APPLICATION_DATA);
        assertThat(result.getEphemeralPublicKey()).isEqualTo(EPHEMERAL_PUBLIC_KEY);
        assertThat(result.getPublicKeyHash()).isEqualTo(PUBLIC_KEY_HASH);
        assertThat(result.getTransactionId()).isEqualTo(TRANSACTION_ID);

    }
}
