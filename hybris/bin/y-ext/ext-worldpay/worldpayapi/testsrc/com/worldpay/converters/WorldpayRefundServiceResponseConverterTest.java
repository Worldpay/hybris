package com.worldpay.converters;

import com.worldpay.data.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayRefundServiceResponseConverterTest {

    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private WorldpayRefundServiceResponseConverter testObj;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RefundServiceResponse refundServiceResponseMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    @Mock
    private Amount amountMock;

    private Currency currency;

    @Before
    public void setUp() {
        testObj.setTargetClass(RefundResult.class);
        currency = Currency.getInstance(Locale.UK);

        when(refundServiceResponseMock.getAmount()).thenReturn(amountMock);
        when(refundServiceResponseMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.valueOf(10.99));
        when(refundServiceResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());
    }

    @Test
    public void convertShouldReturnCaptureResult() {

        final RefundResult result = testObj.convert(refundServiceResponseMock);

        assertEquals(currency, result.getCurrency());
        assertEquals(ORDER_CODE, result.getRequestId());
        assertNotNull(result.getRequestTime());
        assertEquals(BigDecimal.valueOf(10.99), result.getTotalAmount());
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }

    @Test
    public void convert_whenResponseHasErrors_shouldSetTransactionStatusAndDetails() {
        when(refundServiceResponseMock.isError()).thenReturn(true);
        when(refundServiceResponseMock.getErrorDetail().getCode()).thenReturn("2");

        final RefundResult result = testObj.convert(refundServiceResponseMock);

        assertEquals(currency, result.getCurrency());
        assertEquals(ORDER_CODE, result.getRequestId());
        assertNotNull(result.getRequestTime());
        assertEquals(BigDecimal.valueOf(10.99), result.getTotalAmount());
        assertEquals(TransactionStatus.ERROR, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.ERROR_PARSE, result.getTransactionStatusDetails());
    }
}
