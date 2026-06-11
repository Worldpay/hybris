package com.worldpay.core.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.request.KlarnaOrderInquiryServiceRequest;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultOrderInquiryServiceTest {

    private static final String PAYMENT_METHOD_CODE = "paymentMethodCode";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES = "worldpayapi.inquiry.max.number.of.retries";
    private static final String WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES = "worldpayapi.inquiry.delay.between.retries";
    private static final int DELAY_BETWEEN_RETRIES_IN_SECONDS = 1;
    private static final long DELAY_BETWEEN_RETRIES_IN_MILLISECONDS = 1000L;
    private static final int MAX_NUMBER_RETRIES = 3;
    private static final String ERROR_CODE = "5";
    private static final String ERROR_MESSAGE_DETAIL = "Some Error Detail";

    @Spy
    @InjectMocks
    private DefaultOrderInquiryService testObj;

    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;

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
    private AbstractOrderModel orderModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private KlarnaOrderInquiryServiceRequest klarnaOrderInquiryServiceRequestMock;

    @Captor
    private ArgumentCaptor<RetryConfig> retryConfigArgumentCaptor;

    @Test
    void inquirePaymentTransaction_WhenCalledWithPendingAPMPaymentTransaction_ShouldCreateInquiryRequestAndCallWorldpayGateway() throws Exception {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock))
                .thenReturn(orderInquiryServiceResponseMock);

        final OrderInquiryServiceResponse result = testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);

        assertThat(result).isEqualTo(orderInquiryServiceResponseMock);
        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test
    void inquirePaymentTransaction_WhenWorldpayGatewayThrowsWorldpayException_ShouldPropagateException() throws Exception {
        final WorldpayException worldpayException = new WorldpayException(EXCEPTION_MESSAGE);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenThrow(worldpayException);

        assertThatThrownBy(() -> testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock)).isSameAs(worldpayException);

        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test
    void processOrderInquiryServiceResponse_WhenResponseIsSuccessfulAndPaymentInfoIsAPM_ShouldSavePaymentTypeAndCreateAPMPaymentInfo() throws WorldpayConfigurationException {
        when(orderInquiryServiceResponseMock.isError()).thenReturn(false);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PAYMENT_METHOD_CODE);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    void processOrderInquiryServiceResponse_WhenResponseIsSuccessfulAndPaymentInfoIsNotAPM_ShouldSavePaymentTypeAndNotCreateAPMPaymentInfo() throws WorldpayConfigurationException {
        when(orderInquiryServiceResponseMock.isError()).thenReturn(false);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getPaymentMethodCode()).thenReturn(PAYMENT_METHOD_CODE);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    void processOrderInquiryServiceResponse_WhenInquiryResponseHasErrors_ShouldNotUpdatePaymentInfo() throws WorldpayConfigurationException {
        when(orderInquiryServiceResponseMock.isError()).thenReturn(true);
        when(orderInquiryServiceResponseMock.getErrorDetail().getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock, never()).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    void inquireOrder_WhenCalledWithWorldpayOrderCode_ShouldReturnOrderInquiryResponse() throws Exception {
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        final OrderInquiryServiceResponse result = testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);

        assertEquals(result, orderInquiryServiceResponseMock);
    }

    @Test
    void inquireOrder_WhenConfigurationServiceProvidesRetryValues_ShouldUseThemInRetryConfiguration() throws Exception {
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock))
                .thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).executeInquiryCallable(ArgumentMatchers.any(), retryConfigArgumentCaptor.capture());
        final RetryConfig value = retryConfigArgumentCaptor.getValue();
        assertEquals(MAX_NUMBER_RETRIES, value.getMaxAttempts());
        assertEquals(DELAY_BETWEEN_RETRIES_IN_MILLISECONDS, value.getIntervalBiFunction().apply(1, null).longValue());
    }

    @Test
    void inquireOrder_WhenInquiryResponseHasErrors_ShouldRetryUntilSuccess() throws Exception {
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
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
    void inquireOrder_WhenRetriesAreExhausted_ShouldThrowWorldpayException() throws Exception {
        doReturn(orderInquiryServiceRequestMock)
                .when(testObj)
                .createOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
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

        assertThatThrownBy(() -> testObj.inquireOrder(merchantInfoMock, WORLDPAY_ORDER_CODE)).isInstanceOf(WorldpayException.class);
        verify(worldpayServiceGatewayMock, times(3)).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test
    void inquiryKlarnaOrder_WhenCalledWithWorldpayOrderCode_ShouldCreateKlarnaInquiryRequestAndReturnGatewayResponse() throws WorldpayException {
        doReturn(klarnaOrderInquiryServiceRequestMock)
                .when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        final OrderInquiryServiceResponse result = testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        assertThat(result).isEqualTo(orderInquiryServiceResponseMock);
    }

    @Test
    void inquiryKlarnaOrder_WhenConfigurationServiceProvidesRetryValues_ShouldUseThemInRetryConfiguration() throws Exception {
        doReturn(klarnaOrderInquiryServiceRequestMock)
                .when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);
        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(null);

        testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE);

        verify(testObj).executeInquiryCallable(ArgumentMatchers.any(), retryConfigArgumentCaptor.capture());

        final RetryConfig value = retryConfigArgumentCaptor.getValue();
        assertEquals(MAX_NUMBER_RETRIES, value.getMaxAttempts());
        assertEquals(DELAY_BETWEEN_RETRIES_IN_MILLISECONDS, value.getIntervalBiFunction().apply(1, null).longValue());
    }

    @Test
    void inquiryKlarnaOrder_WhenRetriesAreExhausted_ShouldThrowWorldpayException() throws Exception {
        doReturn(klarnaOrderInquiryServiceRequestMock)
                .when(testObj).createKlarnaOrderInquiryServiceRequest(merchantInfoMock, WORLDPAY_ORDER_CODE);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_MAX_NUMBER_OF_RETRIES, 3))
                .thenReturn(MAX_NUMBER_RETRIES);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAYAPI_INQUIRY_DELAY_BETWEEN_RETRIES, 3))
                .thenReturn(DELAY_BETWEEN_RETRIES_IN_SECONDS);
        final ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(ERROR_CODE);
        errorDetail.setMessage(ERROR_MESSAGE_DETAIL);

        when(orderInquiryServiceResponseMock.getErrorDetail()).thenReturn(errorDetail);
        when(worldpayServiceGatewayMock.orderInquiry(klarnaOrderInquiryServiceRequestMock))
                .thenReturn(orderInquiryServiceResponseMock)
                .thenReturn(orderInquiryServiceResponseMock)
                .thenReturn(orderInquiryServiceResponseMock);

        assertThatThrownBy(() -> testObj.inquiryKlarnaOrder(merchantInfoMock, WORLDPAY_ORDER_CODE))
                .isInstanceOf(WorldpayException.class)
                .hasMessageContaining("Unable to retrieve order status");
        verify(worldpayServiceGatewayMock, times(3)).orderInquiry(klarnaOrderInquiryServiceRequestMock);
    }
}
