package com.worldpay.core.services.impl;

import com.evanlennick.retry4j.config.RetryConfig;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderInquiryServiceTest {

    private static final String PAYMENT_METHOD_CODE = "paymentMethodCode";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES = "worldpayapi.inquiry.max.number.of.retries";
    private static final String WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES = "worldpayapi.inquiry.delay.between.retries";
    private static final int DELAY_BETWEEN_RETRIES = 1;
    private static final int MAX_NUMBER_RETRIES = 3;
    private static final String ERROR_CODE = "5";
    private static final String ERROR_MESSAGE_DETAIL = "Some Error Detail";


    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    @InjectMocks
    private DefaultOrderInquiryService testObj;

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderInquiryServiceRequest orderInquiryServiceRequestMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock, orderInquiryServiceResponseMock1, orderInquiryServiceResponseMock2;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private PaymentInfoModel paymentTransactionPaymentInfoModelMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private WorldpayAPMPaymentInfoModel paymentTransactionAPMPaymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private AbstractOrderModel orderModelMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Captor
    private ArgumentCaptor<RetryConfig> retryConfigArgumentCaptor;
    @Mock
    private KlarnaOrderInquiryServiceRequest klarnaOrderInquiryServiceRequestMock;

    @Before
    public void setUp() throws Exception {
        doReturn(orderInquiryServiceRequestMock).when(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PAYMENT_METHOD_CODE);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionAPMPaymentInfoModelMock.getIsApm()).thenReturn(Boolean.TRUE);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(paymentTransactionAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);

        when(worldpayAPMPaymentInfoModelMock.getPaymentType()).thenReturn(PAYMENT_METHOD_CODE);
        when(modelServiceMock.clone(paymentTransactionAPMPaymentInfoModelMock, WorldpayAPMPaymentInfoModel.class)).thenReturn(worldpayAPMPaymentInfoModelMock);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3)).thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3)).thenReturn(DELAY_BETWEEN_RETRIES);
    }

    @Test
    public void inquirePendingAPMPaymentTransaction() throws Exception {
        testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);

        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test(expected = WorldpayException.class)
    public void inquirePendingAPMPaymentTransactionReThrowsException() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));

        testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);

        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldCreateAPMPaymentInfo() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldNotCreateAPMPaymentInfoWhenNotAPM() throws WorldpayConfigurationException {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldDoNothingWhenInquiryResponseHasErrors() throws WorldpayConfigurationException {
        when(orderInquiryServiceResponseMock.isError()).thenReturn(true);
        when(orderInquiryServiceResponseMock.getErrorDetail().getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock, never()).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    public void shouldInquiryPaymentDetailsOfAnOrderUsingOrderCode() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        final OrderInquiryServiceResponse result = testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);

        assertEquals(result, orderInquiryServiceResponseMock);
    }

    @Test
    public void shouldRetryInquiryPaymentDetailsWhenThereIsAnError() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock))
            .thenReturn(orderInquiryServiceResponseMock)
            .thenReturn(orderInquiryServiceResponseMock1)
            .thenReturn(orderInquiryServiceResponseMock2);

        when(orderInquiryServiceResponseMock2.getPaymentReply()).thenReturn(paymentReplyMock);

        final ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(ERROR_CODE);
        errorDetail.setMessage(ERROR_MESSAGE_DETAIL);

        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(errorDetail);
        when(orderInquiryServiceResponseMock1.getErrorDetail()).thenReturn(errorDetail);
        when(orderInquiryServiceResponseMock2.getErrorDetail()).thenReturn(null);

        final OrderInquiryServiceResponse result = testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock, times(3)).orderInquiry(orderInquiryServiceRequestMock);

        assertEquals(result, orderInquiryServiceResponseMock2);
    }

    @Test
    public void shouldCreateConfigurationUsingConfigurationServiceValues() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);

        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).executeInquiryCallable(any(Callable.class), retryConfigArgumentCaptor.capture());
        final RetryConfig value = retryConfigArgumentCaptor.getValue();
        assertEquals(value.getMaxNumberOfTries().longValue(), MAX_NUMBER_RETRIES);
        assertEquals(value.getDelayBetweenRetries().getSeconds(), DELAY_BETWEEN_RETRIES);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRetryInquiryPaymentDetailsWhenThereIsAnErrorAndFailWhenRetriesNumberAreExhausted() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock))
            .thenReturn(orderInquiryServiceResponseMock)
            .thenReturn(orderInquiryServiceResponseMock1)
            .thenReturn(orderInquiryServiceResponseMock2);

        final ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(ERROR_CODE);
        errorDetail.setMessage(ERROR_MESSAGE_DETAIL);

        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(errorDetail);
        when(orderInquiryServiceResponseMock1.getErrorDetail()).thenReturn(errorDetail);
        when(orderInquiryServiceResponseMock2.getErrorDetail()).thenReturn(errorDetail);

        testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);
    }

    @Test
    public void shouldInquiryKlarnaOrderStatus() throws WorldpayException {
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        final OrderInquiryServiceResponse result = testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        assertThat(result).isEqualTo(orderInquiryServiceResponseMock);
    }

    @Test
    public void shouldCreateRetryConfigurationUsingConfigurationServiceValuesForKlarnaInquiry() throws Exception {
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);

        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).executeInquiryCallable(any(Callable.class), retryConfigArgumentCaptor.capture());

        final RetryConfig value = retryConfigArgumentCaptor.getValue();
        assertEquals(value.getMaxNumberOfTries().longValue(), MAX_NUMBER_RETRIES);
        assertEquals(value.getDelayBetweenRetries().getSeconds(), DELAY_BETWEEN_RETRIES);
    }

    @Test(expected = WorldpayException.class)
    public void shouldRetryKlarnaInquiryPaymentDetailsWhenThereIsAnErrorAndFailWhenRetriesNumberAreExhausted() throws Exception {
        doReturn(klarnaOrderInquiryServiceRequestMock).when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);

        final ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(ERROR_CODE);
        errorDetail.setMessage(ERROR_MESSAGE_DETAIL);

        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(errorDetail);

        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock))
            .thenReturn(orderInquiryServiceResponseMock)
            .thenReturn(orderInquiryServiceResponseMock)
            .thenReturn(orderInquiryServiceResponseMock);

        testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);
    }

}
