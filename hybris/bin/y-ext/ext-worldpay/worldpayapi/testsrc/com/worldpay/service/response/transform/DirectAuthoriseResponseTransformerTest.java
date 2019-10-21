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

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private static final String PA_REQUEST = "paRequest";
    private static final String ISSUER_URL = "issuerURL";

    @Rule
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
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithRequestInfo() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);

        final List<Object> intOrderStatuses = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
        final RequestInfo requestInfo = new RequestInfo();
        final Request3DSecure request3DSecure = new Request3DSecure();

        final IssuerURL issuerURL = new IssuerURL();
        issuerURL.setvalue(ISSUER_URL);
        final PaRequest paRequest = new PaRequest();
        paRequest.setvalue(PA_REQUEST);
        request3DSecure.getPaRequestOrIssuerURLOrMpiRequestOrMpiURL().addAll(Arrays.asList(issuerURL, paRequest));
        requestInfo.setRequest3DSecure(request3DSecure);
        intOrderStatuses.add(requestInfo);

        final Token token = new Token();
        intOrderStatuses.add(token);

        final EchoData echoData = new EchoData();
        echoData.setvalue(ECHO_DATA);
        intOrderStatuses.add(echoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getRequest3DInfo());
        assertNull(result.getPaymentReply());
        assertNull(result.getRedirectReference());

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(tokenReplyMock, result.getToken());
        assertThat(result.getRequest3DInfo().getIssuerUrl()).isEqualTo(ISSUER_URL);
        assertThat(result.getRequest3DInfo().getPaRequest()).isEqualTo(PA_REQUEST);
    }

    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithRequestInfoAndIssuerURLAndPaRequestIsNull() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();

        final RequestInfo requestInfo = new RequestInfo();
        final Request3DSecure request3DSecure = new Request3DSecure();
        requestInfo.setRequest3DSecure(request3DSecure);
        orderStatusType.add(requestInfo);

        final Token token = new Token();
        orderStatusType.add(token);

        final EchoData echoData = new EchoData();
        echoData.setvalue(ECHO_DATA);
        orderStatusType.add(echoData);

        paymentServiceReply.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        when(serviceResponseTransformerHelperMock.buildTokenReply(token)).thenReturn(tokenReplyMock);

        final DirectAuthoriseServiceResponse result = (DirectAuthoriseServiceResponse) testObj.transform(paymentServiceReply);

        assertNotNull(result.getRequest3DInfo());
        assertNull(result.getPaymentReply());
        assertNull(result.getRedirectReference());

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(ECHO_DATA, result.getEchoData());
        assertEquals(tokenReplyMock, result.getToken());
        assertThat(result.getRequest3DInfo().getIssuerUrl()).isNullOrEmpty();
        assertThat(result.getRequest3DInfo().getPaRequest()).isNullOrEmpty();
    }

    @Test
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithPaymentReply() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
        final Payment payment = new Payment();
        orderStatusType.add(payment);

        final Token token = new Token();
        orderStatusType.add(token);

        final EchoData echoData = new EchoData();
        echoData.setvalue(ECHO_DATA);
        orderStatusType.add(echoData);

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
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithReference() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        orderStatusType.add(reference);

        final Token token = new Token();
        token.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        orderStatusType.add(token);

        final EchoData echoData = new EchoData();
        echoData.setvalue(ECHO_DATA);
        orderStatusType.add(echoData);

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
    public void testTransformShouldCreateServiceResponseFromPaymentServiceWithPaymentAndReference() throws WorldpayModelTransformationException {
        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
        final Payment payment = new Payment();
        orderStatusType.add(payment);
        final Reference reference = new Reference();
        reference.setId(REFERENCE_ID);
        reference.setvalue(REFERENCE_VALUE);
        orderStatusType.add(reference);

        final Token token = new Token();
        orderStatusType.add(token);

        final EchoData echoData = new EchoData();
        echoData.setvalue(ECHO_DATA);
        orderStatusType.add(echoData);

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
    public void shouldThrowExceptionWhenOrderStatusTypeIsNull() throws WorldpayModelTransformationException {
        thrown.expect(WorldpayModelTransformationException.class);
        thrown.expectMessage("No order status type returned in Worldpay reply message");

        final PaymentService paymentServiceReply = new PaymentService();
        final Reply reply = new Reply();
        final List<Object> responses = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken();
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderCode(ORDER_CODE);
        responses.add(orderStatus);
        final List<Object> orderStatusType = orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrQrCodeOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrExemptionResponseOrOrderModificationOrJournalOrRequestInfoOrChallengeRequiredOrFxApprovalRequiredOrPbbaRTPOrContentOrJournalTypeDetailOrTokenOrDateOrEchoDataOrPayAsOrderUseNewOrderCodeOrAuthenticateResponse();
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
