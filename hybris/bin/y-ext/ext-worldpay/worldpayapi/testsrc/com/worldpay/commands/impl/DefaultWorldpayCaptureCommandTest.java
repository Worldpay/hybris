package com.worldpay.commands.impl;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCaptureCommandTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String AMOUNT = "100";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String TRACKING_ID_1 = "trackingId1";
    private static final String TRACKING_ID_2 = "trackingId2";

    @InjectMocks
    private DefaultWorldpayCaptureCommand testObj;
    @Mock
    private WorldpayHybrisOrderService worldpayHybrisOrderServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private Converter<CaptureServiceResponse, CaptureResult> captureResponseConverterMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CaptureServiceResponse captureResponseMock;

    @Mock
    private CaptureRequest captureRequestMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CaptureResult captureResultMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private Amount amountMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ConsignmentModel consigment1Mock;
    @Mock
    private ConsignmentModel consigment2Mock;

    @Before
    public void setUp() throws WorldpayException {
        when(captureRequestMock.getRequestToken()).thenReturn(MERCHANT_CODE);
        when(captureRequestMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(captureRequestMock.getTotalAmount()).thenReturn(new BigDecimal(AMOUNT));
        final Currency currency = Currency.getInstance(Locale.UK);
        when(captureRequestMock.getCurrency()).thenReturn(currency);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(worldpayOrderServiceMock.createAmount(currency, Double.parseDouble(AMOUNT))).thenReturn(amountMock);
        when(worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(captureResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());
        when(captureResponseConverterMock.convert(captureResponseMock)).thenReturn(captureResultMock);
        when(worldpayHybrisOrderServiceMock.findOrderByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(orderModelMock);
        when(orderModelMock.getConsignments()).thenReturn(Set.of(consigment1Mock, consigment2Mock));
        when(consigment1Mock.getTrackingID()).thenReturn(TRACKING_ID_1);
        when(consigment2Mock.getTrackingID()).thenReturn(TRACKING_ID_2);
        when(worldpayServiceGatewayMock.capture(any(CaptureServiceRequest.class))).thenReturn(captureResponseMock);
    }

    @Test
    public void perform_WhenCaptureRequestIsReceived_ShouldCreateAndSendCaptureCommandToWorldpay() {
        testObj.perform(captureRequestMock);

        verify(captureResponseConverterMock).convert(captureResponseMock);
        verify(captureResultMock).setRequestToken(MERCHANT_CODE);
    }

    @Test
    public void perform_WhenCaptureRequestIsReceived_ShouldAddTrackingIdFromOrderConsigments() {
        testObj.perform(captureRequestMock);

        verify(worldpayHybrisOrderServiceMock).findOrderByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(orderModelMock).getConsignments();
        verify(consigment1Mock).getTrackingID();
        verify(consigment2Mock).getTrackingID();
    }

    @Test
    public void perform_WhenCaptureIsNull_ShouldReturnCaptureResultWithErrorStatus() throws WorldpayException {
        when(worldpayServiceGatewayMock.capture(any(CaptureServiceRequest.class))).thenReturn(null);

        final CaptureResult result = testObj.perform(captureRequestMock);

        verify(captureResponseConverterMock, never()).convert(captureResponseMock);
        verify(captureResultMock, never()).setRequestToken(MERCHANT_CODE);

        assertEquals(TransactionStatus.ERROR, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.COMMUNICATION_PROBLEM, result.getTransactionStatusDetails());
    }
}
