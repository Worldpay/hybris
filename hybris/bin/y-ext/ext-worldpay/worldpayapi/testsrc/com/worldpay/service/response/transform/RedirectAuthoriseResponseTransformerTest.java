package com.worldpay.service.response.transform;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.data.token.TokenReply;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.BankAccount;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reference;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.internal.model.Token;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RedirectAuthoriseResponseTransformerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String REFERENCE_ID = "referenceId";
    private static final String REFERENCE_VALUE = "referenceValue";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_DETAIL = "errorDetail";
    private static final String ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE = "Reply has no reply message or the reply type is not the expected one";

    @InjectMocks
    private RedirectAuthoriseResponseTransformer testObj;

    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Mock
    private TokenReply tokenReplyMock;

    @Test
    public void transformShouldReturnARedirectAuthoriseServiceResponse() throws Exception {
        final PaymentService paymentServiceReply = new PaymentService();

        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply paymentReply = new Reply();
        responseType.add(paymentReply);

        final List<Object> statusElements = paymentReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final OrderStatus intOrderStatus = new OrderStatus();
        intOrderStatus.setOrderCode(ORDER_CODE);

        final List<Object> referenceElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();

        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setValue(REFERENCE_VALUE);
        referenceElements.add(reference);

        final Token token = new Token();
        token.setTokenEventReference(TOKEN_REFERENCE);
        referenceElements.add(token);

        statusElements.add(intOrderStatus);

        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final RedirectAuthoriseServiceResponse result = (RedirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(tokenReplyMock, result.getToken());
        assertEquals(REFERENCE_ID, result.getRedirectReference().getId());
        assertEquals(REFERENCE_VALUE, result.getRedirectReference().getValue());
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenResponseTypeIsNull() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        responseType.add(null);

        assertThatThrownBy(() -> testObj.transform(paymentServiceReply))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);

    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenResponseTypeIsNotReply() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        responseType.add(new Submit());

        assertThatThrownBy(() -> testObj.transform(paymentServiceReply))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
    }

    @Test
    public void responseShouldContainErrorDetailWhenReplyTypeIsError() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final Error intError = new Error();
        intError.setCode(ERROR_CODE);
        intError.setValue(ERROR_DETAIL);
        replyElements.add(intError);
        responseType.add(reply);

        when(serviceResponseTransformerHelperMock.checkForError(any(ServiceResponse.class), eq(reply))).thenReturn(true);

        final RedirectAuthoriseServiceResponse result = (RedirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        verify(serviceResponseTransformerHelperMock).checkForError(result, reply);
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenOrderStatusIsNull() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        replyElements.add(null);
        responseType.add(reply);

        assertThatThrownBy(() -> testObj.transform(paymentServiceReply))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("No order status returned in Worldpay reply message");
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenOrderStatusTypeIsNotReference() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrCurrentBalanceOrCheckCardHolderNameResponseOrCardBinInquiryResponseOrWalletDecryptionResponseOrEcheckVerificationResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        final List<Object> orderStatusElements = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrInstalmentPlanOrRetryDetailsOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrSelectedSchemeOrAuthenticateResponse();
        orderStatusElements.add(new BankAccount());
        replyElements.add(orderStatus);
        responseType.add(reply);

        assertThatThrownBy(() -> testObj.transform(paymentServiceReply))
                .isInstanceOf(WorldpayModelTransformationException.class)
                .hasMessage("Order status type returned in Worldpay reply message is not one of the expected types for redirect authorise");
    }
}
