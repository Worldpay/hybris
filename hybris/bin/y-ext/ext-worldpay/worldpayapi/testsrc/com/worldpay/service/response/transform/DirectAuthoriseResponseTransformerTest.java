package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.*;

@UnitTest
public class DirectAuthoriseResponseTransformerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String ECHO_DATA = "echoData";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String AMOUNT = "100";
    private static final String REFERENCE_ID = "referenceId";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_VALUE = "errorValue";
    private static final String REFERENCE_VALUE = "reference_value";

    private final DirectAuthoriseResponseTransformer testObj = new DirectAuthoriseResponseTransformer();

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithRequestInfo() throws Exception {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setRequest3DSecure(new Request3DSecure());
        orderStatusType.add(requestInfo);

        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getRequest3DInfo());
        assertNull(result.getPaymentReply());
        assertNull(result.getRedirectReference());

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getToken().getAuthenticatedShopperID());
        assertEquals(ECHO_DATA, result.getEchoData());
    }

    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithPaymentReply() throws Exception {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final Payment payment = new Payment();
        final com.worldpay.service.model.Amount amount = new com.worldpay.service.model.Amount(AMOUNT, "GBP", "2");
        payment.setAmount((Amount) amount.transformToInternalModel());
        orderStatusType.add(payment);

        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getPaymentReply());
        assertNull(result.getRequest3DInfo());
        assertNull(result.getRedirectReference());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getToken().getAuthenticatedShopperID());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(AMOUNT, result.getPaymentReply().getAmount().getValue());
    }


    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithReference() throws Exception {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        orderStatusType.add(reference);

        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getRedirectReference());
        assertNull(result.getPaymentReply());
        assertNull(result.getRequest3DInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getToken().getAuthenticatedShopperID());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(REFERENCE_ID, result.getRedirectReference().getId());
    }

    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithPaymentAndReference() throws Exception {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final Payment payment = new Payment();
        final com.worldpay.service.model.Amount amount = new com.worldpay.service.model.Amount(AMOUNT, "GBP", "2");
        payment.setAmount((Amount) amount.transformToInternalModel());
        orderStatusType.add(payment);
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setvalue(REFERENCE_VALUE);
        orderStatusType.add(reference);

        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getPaymentReply());
        assertNotNull(result.getRedirectReference());
        assertNull(result.getRequest3DInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getToken().getAuthenticatedShopperID());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(REFERENCE_ID, result.getRedirectReference().getId());
        assertEquals(REFERENCE_VALUE, result.getRedirectReference().getValue());
        assertEquals(AMOUNT, result.getPaymentReply().getAmount().getValue());
    }

    @Test
    public void shouldThrowExceptionWhenUnrecognizedOrderStatusTypeIsReturned() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Order status type returned in Worldpay reply message is not one of the expected types for direct authorise");

        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final BankAccount bankAccount = new BankAccount();
        orderStatusType.add(bankAccount);
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowExceptionWhenUnrecognizedResponseStatusTypeIsReturned() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No order status returned in Worldpay reply message");

        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final BatchStatus batchStatus = new BatchStatus();
        responses.add(batchStatus);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowExceptionWhenOrderStatusTypeIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No order status type returned in Worldpay reply message");

        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        orderStatusType.add(null);
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowExceptionWhenResponseTypeIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No reply message in Worldpay response");

        final PaymentService paymentServiceReply = new PaymentService();

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(null);

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldThrowExceptionWhenResponseTypeNotReply() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("Reply type from Worldpay not the expected type");

        final PaymentService paymentServiceReply = new PaymentService();
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(new Notify());

        testObj.transform(paymentServiceReply);
    }

    @Test
    public void shouldReturnAuthResponseWithErrorDetailsWhenErrorOccurs() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent();
        final Error error = new Error();
        error.setCode(ERROR_CODE);
        error.setvalue(ERROR_VALUE);
        orderStatusType.add(error);
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        final ServiceResponse result = testObj.transform(paymentServiceReply);

        assertEquals(ERROR_VALUE, result.getErrorDetail().getMessage());
        assertEquals(ERROR_CODE, result.getErrorDetail().getCode());
    }
}
