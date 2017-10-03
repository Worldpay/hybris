package com.worldpay.service.response.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.*;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DirectAuthoriseResponseTransformerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String ECHO_DATA = "echoData";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String REFERENCE_ID = "referenceId";
    private static final String REFERENCE_VALUE = "reference_value";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private DirectAuthoriseResponseTransformer testObj;
    @Mock
    private ServiceResponseTransformerHelper serviceResponseTransformerHelperMock;
    @Captor
    private ArgumentCaptor<ServiceResponse> serviceResponseArgumentCaptor;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private TokenReply tokenReplyMock;

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
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getRequest3DInfo());
        assertNull(result.getPaymentReply());
        assertNull(result.getRedirectReference());

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(tokenReplyMock, result.getToken());
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
        orderStatusType.add(payment);

        final Token token = new Token();
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.buildPaymentReply(payment)).thenReturn(paymentReplyMock);
        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getPaymentReply());
        assertNull(result.getRequest3DInfo());
        assertNull(result.getRedirectReference());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(paymentReplyMock, result.getPaymentReply());
        assertEquals(tokenReplyMock, result.getToken());
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

        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertEquals(tokenReplyMock, result.getToken());
        assertNotNull(result.getRedirectReference());
        assertNull(result.getPaymentReply());
        assertNull(result.getRequest3DInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
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
        orderStatusType.add(payment);
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setvalue(REFERENCE_VALUE);
        orderStatusType.add(reference);

        final Token token = new Token();
        orderStatus.setToken(token);
        final EchoData intEchoData = new EchoData();
        intEchoData.setvalue(ECHO_DATA);
        orderStatus.setEchoData(intEchoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.buildPaymentReply(payment)).thenReturn(paymentReplyMock);
        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertEquals(paymentReplyMock, result.getPaymentReply());
        assertEquals(tokenReplyMock, result.getToken());
        assertNotNull(result.getPaymentReply());
        assertNotNull(result.getRedirectReference());
        assertNull(result.getRequest3DInfo());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(REFERENCE_ID, result.getRedirectReference().getId());
        assertEquals(REFERENCE_VALUE, result.getRedirectReference().getValue());
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
        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.checkForError(any(ServiceResponse.class), eq(reply))).thenReturn(true);

        final ServiceResponse result = testObj.transform(paymentServiceReply);

        verify(serviceResponseTransformerHelperMock).checkForError(serviceResponseArgumentCaptor.capture(), eq(reply));

        assertEquals(result, serviceResponseArgumentCaptor.getValue());
    }
}
