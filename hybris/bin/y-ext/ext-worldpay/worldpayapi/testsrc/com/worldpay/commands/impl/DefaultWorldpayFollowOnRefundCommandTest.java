package com.worldpay.commands.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.response.RefundServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static java.util.Locale.UK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayFollowOnRefundCommandTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String GBP = "GBP";
    private static final String TOTAL_VALUE = "10000";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String PAYMENT_TRANSACTION_ENTRY_CODE = "paymentTransactionEntryCode";

    @Spy
    @InjectMocks
    private DefaultWorldpayFollowOnRefundCommand testObj;

    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private Converter<RefundServiceResponse, RefundResult> worldpayRefundServiceConverterMock;

    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private FollowOnRefundRequest followOnRefundRequestMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private MerchantInfo merchantInfoMock;
    @Mock
    private RefundServiceResponse refundServiceResponseMock;
    @Mock
    private Amount amountMock;
    @Mock
    private RefundResult refundResultMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    private Currency currency = Currency.getInstance(UK);
    private BigDecimal amount = new BigDecimal(TOTAL_VALUE);

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        when(worldpayOrderServiceMock.createAmount(currency, amount.doubleValue())).thenReturn(amountMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRefundServiceConverterMock.convert(refundServiceResponseMock)).thenReturn(refundResultMock);

        when(followOnRefundRequestMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(followOnRefundRequestMock.getTotalAmount()).thenReturn(amount);
        when(followOnRefundRequestMock.getCurrency()).thenReturn(currency);
        when(followOnRefundRequestMock.getMerchantTransactionCode()).thenReturn(PAYMENT_TRANSACTION_ENTRY_CODE);
    }

    @Test
    public void performShouldReturnASuccessfulRefundResult() throws Exception {
        when(worldpayServiceGatewayMock.refund(any(RefundServiceRequest.class))).thenReturn(refundServiceResponseMock);

        testObj.perform(followOnRefundRequestMock);

        verify(testObj).buildRefundRequest(PAYMENT_TRANSACTION_ENTRY_CODE, WORLDPAY_ORDER_CODE, amountMock, merchantInfoMock);
        verify(worldpayServiceGatewayMock).refund(any(RefundServiceRequest.class));
        verify(worldpayRefundServiceConverterMock).convert(refundServiceResponseMock);
    }

    @Test
    public void performShouldReturnAFailingResponseRefundResultWhenExceptionThrownOnCommand() throws WorldpayException {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayServiceGatewayMock).refund(any(RefundServiceRequest.class));

        final RefundResult result = testObj.perform(followOnRefundRequestMock);

        verify(worldpayServiceGatewayMock).refund(any(RefundServiceRequest.class));
        assertEquals(TransactionStatus.ERROR, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.COMMUNICATION_PROBLEM, result.getTransactionStatusDetails());
    }

    @Test
    public void performShouldReturnAFailingResponseRefundResultWhenRefundCommandReturnsNull() throws WorldpayException {
        when(worldpayServiceGatewayMock.refund(any(RefundServiceRequest.class))).thenReturn(null);

        final RefundResult result = testObj.perform(followOnRefundRequestMock);

        verify(worldpayServiceGatewayMock).refund(any(RefundServiceRequest.class));
        assertEquals(TransactionStatus.ERROR, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.COMMUNICATION_PROBLEM, result.getTransactionStatusDetails());
    }
}
