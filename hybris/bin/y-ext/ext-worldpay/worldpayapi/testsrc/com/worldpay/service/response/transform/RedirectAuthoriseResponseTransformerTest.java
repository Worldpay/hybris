package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Error;
import com.worldpay.internal.model.*;
import com.worldpay.data.token.TokenReply;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

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

        final List<Object> statusElements = paymentReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus intOrderStatus = new OrderStatus();
        intOrderStatus.setOrderCode(ORDER_CODE);

        final List<Object> referenceElements = intOrderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();

        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setvalue(REFERENCE_VALUE);
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
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        responseType.add(null);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenResponseTypeIsNotReply() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage(ERROR_MSG_NO_REPLY_OR_NOT_EXPECTED_TYPE);
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        responseType.add(new Submit());

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void responseShouldContainErrorDetailWhenReplyTypeIsError() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final Error intError = new Error();
        intError.setCode(ERROR_CODE);
        intError.setvalue(ERROR_DETAIL);
        replyElements.add(intError);
        responseType.add(reply);

        when(serviceResponseTransformerHelperMock.checkForError(any(ServiceResponse.class), eq(reply))).thenReturn(true);

        final RedirectAuthoriseServiceResponse result = (RedirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        verify(serviceResponseTransformerHelperMock).checkForError(result, reply);
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenOrderStatusIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No order status returned in Worldpay reply message");
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        replyElements.add(null);
        responseType.add(reply);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenOrderStatusTypeIsNotReference() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Order status type returned in Worldpay reply message is not one of the expected types for redirect authorise");
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        final List<Object> orderStatusElements = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
        orderStatusElements.add(new BankAccount());
        replyElements.add(orderStatus);
        responseType.add(reply);

        testObj.transform(paymentServiceReply);
    }

}
