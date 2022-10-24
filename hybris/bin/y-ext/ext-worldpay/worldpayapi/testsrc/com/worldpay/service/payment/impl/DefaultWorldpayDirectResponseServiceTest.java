package com.worldpay.service.payment.impl;

import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectResponseServiceTest {

    private static final String PA_REQUEST = "paRequest";
    private static final String ISSUER_URL = "issuerUrl";
    private static final String ISSUER_PAYLOAD = "issuerPayload";
    private static final String TRANSACTION_ID_3DS = "transactionId3DS";
    private static final String THREEDS_VERSION = "1";

    @InjectMocks
    private DefaultWorldpayDirectResponseService testObj;
    @Mock
    private DirectResponseData directResponseDataMock;

    @Test
    public void isAuthorisedReturnsTrueWhenDirectResponseDataStatusIsAuthorised() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);

        final Boolean result = testObj.isAuthorised(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isCancelledReturnsTrueWhenDirectResponseDataStatusIsCancelled() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.CANCELLED);

        final Boolean result = testObj.isCancelled(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void is3DSecureLegacyFlowReturnsTrueWhenDirectResponseDataIsLegacyFlow() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHENTICATION_REQUIRED);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL);
        when(directResponseDataMock.getPaRequest()).thenReturn(PA_REQUEST);

        final Boolean result = testObj.is3DSecureLegacyFlow(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void is3DSecureLegacyFlowReturnsFalseWhenDirectResponseDataIsLFlexFlow() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHENTICATION_REQUIRED);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL);

        final Boolean result = testObj.is3DSecureLegacyFlow(directResponseDataMock);

        assertThat(result).isFalse();
    }

    @Test
    public void is3DSecureFlexFlowReturnsTrueWhenDirectResponseDataIsFlexFlow() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHENTICATION_REQUIRED);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL);
        when(directResponseDataMock.getIssuerPayload()).thenReturn(ISSUER_PAYLOAD);
        when(directResponseDataMock.getTransactionId3DS()).thenReturn(TRANSACTION_ID_3DS);
        when(directResponseDataMock.getMajor3DSVersion()).thenReturn(THREEDS_VERSION);

        final Boolean result = testObj.is3DSecureFlexFlow(directResponseDataMock);

        assertThat(result).isTrue();
    }

    @Test
    public void is3DSecureFlexFlowReturnsFalseWhenDirectResponseDataIsLegacyFlow() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHENTICATION_REQUIRED);
        when(directResponseDataMock.getIssuerURL()).thenReturn(ISSUER_URL);

        final Boolean result = testObj.is3DSecureFlexFlow(directResponseDataMock);

        assertThat(result).isFalse();
    }

}
