package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.token.UpdateTokenReply;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateTokenResponseTransformerTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_VALUE = "errorValue";

    @InjectMocks
    private UpdateTokenResponseTransformer testObj;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Reply replyMock;
    @Mock
    private Ok okReplyMock;
    @Mock
    private UpdateTokenReceived updateTokenReceivedMock;
    @Mock
    private Error errorMock;
    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelper;
    @Mock
    private UpdateTokenReply updateTokenReplyMock;

    @Before
    public void setUp() {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(replyMock));
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken()).thenReturn(singletonList(okReplyMock));
        when(okReplyMock.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDone()).thenReturn(singletonList(updateTokenReceivedMock));
        when(updateTokenReceivedMock.getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
    }

    @Test
    public void shouldTransformPaymentServiceIntoUpdateTokenResponse() throws Exception {
        when(serviceResponseTransformerHelper.buildUpdateTokenReply(updateTokenReceivedMock)).thenReturn(updateTokenReplyMock);
        when(updateTokenReplyMock.getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID);

        final UpdateTokenResponse result = (UpdateTokenResponse) testObj.transform(paymentServiceMock);

        assertEquals(PAYMENT_TOKEN_ID, result.getUpdateTokenReply().getPaymentTokenId());
    }

    @Test
    public void shouldRaiseWorldpayModelTransformationExceptionWhenThereIsNoReplyInThePaymentService() throws Exception {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay update token response");
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(null));

        testObj.transform(paymentServiceMock);
    }

    @Test
    public void shouldRaiseWorldpayModelTransformationExceptionWhenResponseIsNotAReply() throws Exception {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Reply type from Worldpay not the expected type");
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(new Modify()));

        testObj.transform(paymentServiceMock);
    }

    @Test
    public void shouldThrowExceptionWhenResponseIsNotOk() throws Exception {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("UpdateTokenResponse did not contain an OK object");
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken()).
                thenReturn(singletonList(new Shopper()));

        testObj.transform(paymentServiceMock);
    }

    @Test
    public void shouldThrowExceptionWhenErrorInResponse() throws Exception {
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken())
                .thenReturn(singletonList(errorMock));
        when(errorMock.getCode()).thenReturn(ERROR_CODE);
        when(errorMock.getvalue()).thenReturn(ERROR_VALUE);

        when(serviceResponseTransformerHelper.checkForError(any(ServiceResponse.class), eq(replyMock))).thenReturn(true);

        final ServiceResponse result = testObj.transform(paymentServiceMock);

        verify(serviceResponseTransformerHelper).checkForError(result, replyMock);
    }
}
