package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.model.token.DeleteTokenReply;
import com.worldpay.service.response.DeleteTokenResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
public class DeleteTokenResponseTransformerTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @InjectMocks
    private DeleteTokenResponseTransformer testObj;

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
    private DeleteTokenReceived deleteTokenReceivedMock;
    @Mock
    private Error errorMock;
    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Mock
    private DeleteTokenReply deleteTokenResponseMock;
    @Captor
    private ArgumentCaptor<ServiceResponse> deleteTokenResponseArgumentCaptor;

    @Before
    public void setUp() {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(replyMock));
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken()).thenReturn(singletonList(okReplyMock));
        when(okReplyMock.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryDoneOrVoidSaleReceived()).thenReturn(singletonList(deleteTokenReceivedMock));
        when(deleteTokenReceivedMock.getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
    }

    @Test
    public void shouldTransformPaymentServiceIntoDeleteTokenResponse() throws Exception {
        when(serviceResponseTransformerHelperMock.buildDeleteTokenReply(any(DeleteTokenReceived.class))).thenReturn(deleteTokenResponseMock);

        final DeleteTokenResponse result = (DeleteTokenResponse) testObj.transform(paymentServiceMock);

        assertEquals(deleteTokenResponseMock, result.getDeleteTokenReply());
    }

    @Test
    public void shouldRaiseWorldpayModelTransformationExceptionWhenThereIsNoReplyInThePaymentService() throws Exception {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay delete token response");
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
        thrown.expectMessage("DeleteTokenResponse did not contain an OK object");
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken()).
                thenReturn(singletonList(new Shopper()));

        testObj.transform(paymentServiceMock);
    }

    @Test
    public void shouldReturnDeleteTokenResponseWithErrorDetails() throws Exception {
        when(serviceResponseTransformerHelperMock.checkForError(any(DeleteTokenResponse.class), eq(replyMock))).thenReturn(true);

        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken())
                .thenReturn(singletonList(errorMock));

        final ServiceResponse result = testObj.transform(paymentServiceMock);

        verify(serviceResponseTransformerHelperMock).checkForError(deleteTokenResponseArgumentCaptor.capture(), eq(replyMock));

        assertEquals(result, deleteTokenResponseArgumentCaptor.getValue());

    }
}
