package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class RedirectAuthoriseResponseTransformerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String REFERENCE_ID = "referenceId";
    private static final String REFERENCE_VALUE = "referenceValue";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_DETAIL = "errorDetail";

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RedirectAuthoriseResponseTransformer testObj = new RedirectAuthoriseResponseTransformer();

    @Test
    public void transformShouldReturnARedirectAuthoriseServiceResponse() throws Exception {

        final PaymentService paymentServiceReply = new PaymentService();

        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply paymentReply = new Reply();
        responseType.add(paymentReply);

        final List<Object> statusElements = paymentReply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus intOrderStatus = new OrderStatus();
        intOrderStatus.setOrderCode(ORDER_CODE);
        final Token token = new Token();
        token.setTokenEventReference(TOKEN_REFERENCE);
        intOrderStatus.setToken(token);
        final List<Object> referenceElements = intOrderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setvalue(REFERENCE_VALUE);
        referenceElements.add(reference);
        statusElements.add(intOrderStatus);

        final RedirectAuthoriseServiceResponse result = (RedirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(TOKEN_REFERENCE, result.getToken().getTokenEventReference());
        assertEquals(REFERENCE_ID, result.getRedirectReference().getId());
        assertEquals(REFERENCE_VALUE, result.getRedirectReference().getValue());
    }


    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenResponseTypeIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay response");
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        responseType.add(null);

        testObj.transform(paymentServiceReply);
    }


    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenResponseTypeIsNotReply() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Reply type from Worldpay not the expected type");
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
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final Error intError = new Error();
        intError.setCode(ERROR_CODE);
        intError.setvalue(ERROR_DETAIL);
        replyElements.add(intError);
        responseType.add(reply);

        final RedirectAuthoriseServiceResponse result = (RedirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNull(result.getOrderCode());
        assertNull(result.getToken());
        assertNull(result.getRedirectReference());
        assertEquals(ERROR_DETAIL, result.getErrorDetail().getMessage());
        assertEquals(ERROR_CODE, result.getErrorDetail().getCode());
    }

    @Test
    public void shouldThrowWorldpayModelTransformationExceptionWhenOrderStatusIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No order status returned in Worldpay reply message");
        final PaymentService paymentServiceReply = new PaymentService();
        final List<Object> responseType = paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        final Reply reply = new Reply();
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
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
        final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        final List<Object> orderStatusElements = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        orderStatusElements.add(new BankAccount());
        replyElements.add(orderStatus);
        responseType.add(reply);

        testObj.transform(paymentServiceReply);
    }

}
