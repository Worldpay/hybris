package com.worldpay.service.response.transform;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.data.token.UpdateTokenReply;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Shopper;
import com.worldpay.internal.model.UpdateTokenReceived;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateTokenResponseTransformerTest {

    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE = "Update Token Response has no reply message or the reply type is not the expected one";

    @InjectMocks
    private UpdateTokenResponseTransformer testObj;

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Reply replyMock;
    @Mock
    private Ok okReplyMock;
    @Mock
    private UpdateTokenReceived updateTokenReceivedMock;
    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelper;
    @Mock
    private UpdateTokenReply updateTokenReplyMock;

    @Before
    public void setUp() {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(replyMock));
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()).thenReturn(singletonList(okReplyMock));
        when(okReplyMock.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrDefenceReceivedOrUpdateTokenReceivedOrDeleteTokenReceivedOrExtendExpiryDateReceivedOrOrderReceivedOrCancelRetryReceivedOrCryptogramReceivedOrVoidSaleReceived()).thenReturn(singletonList(updateTokenReceivedMock));
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
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(null));

        assertThatThrownBy(() -> testObj.transform(paymentServiceMock))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
    }

    @Test
    public void shouldRaiseWorldpayModelTransformationExceptionWhenResponseIsNotAReply() throws Exception {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(new Modify()));

        assertThatThrownBy(() -> testObj.transform(paymentServiceMock))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
    }

    @Test
    public void shouldThrowExceptionWhenResponseIsNotOk() throws Exception {
        when(replyMock.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken()).
                thenReturn(singletonList(new Shopper()));

        assertThatThrownBy(() -> testObj.transform(paymentServiceMock))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("UpdateTokenResponse did not contain an OK object");
    }

    @Test
    public void shouldThrowExceptionWhenErrorInResponse() throws Exception {
        when(serviceResponseTransformerHelper.checkForError(any(ServiceResponse.class), eq(replyMock))).thenReturn(true);

        final ServiceResponse result = testObj.transform(paymentServiceMock);

        verify(serviceResponseTransformerHelper).checkForError(result, replyMock);
    }
}
