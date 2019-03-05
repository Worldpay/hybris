package com.worldpay.service.model.applepay;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.APPLEPAYSSL;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
public class ApplePayTest {

    private static final String VERSION = "version";
    private static final String TOKEN_REQUESTOR_ID = "tokenRequestorID";
    private static final String SIGNATURE = "signature";
    private static final String DATA = "data";
    private static final String EPHEMERAL_PUBLIC_KEY = "ephemeralPublicKey";
    private static final String PUBLIC_KEY_HASH = "publicKeyHash";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String APPLICATION_DATA = "applicationData";
    private static final Header HEADER = new Header(EPHEMERAL_PUBLIC_KEY, PUBLIC_KEY_HASH, TRANSACTION_ID, APPLICATION_DATA);
    private ApplePay testObj = new ApplePay(HEADER, SIGNATURE, VERSION, DATA, TOKEN_REQUESTOR_ID);

    @Test
    public void shouldSetValuesOnInternalObject() throws WorldpayModelTransformationException {

        final APPLEPAYSSL result = (APPLEPAYSSL) testObj.transformToInternalModel();

        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getHeader().getTransactionId()).isEqualTo(TRANSACTION_ID);
        assertThat(result.getHeader().getPublicKeyHash()).isEqualTo(PUBLIC_KEY_HASH);
        assertThat(result.getHeader().getEphemeralPublicKey()).isEqualTo(EPHEMERAL_PUBLIC_KEY);
        assertThat(result.getHeader().getApplicationData()).isEqualTo(APPLICATION_DATA);
        assertThat(result.getSignature()).isEqualTo(SIGNATURE);
        assertThat(result.getTokenRequestorID()).isEqualTo(TOKEN_REQUESTOR_ID);
        assertThat(result.getVersion()).isEqualTo(VERSION);
    }
}
