package com.worldpay.hostedorderpage.converters;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.data.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RedirectAuthoriseResultPopulatorTest {

    private static final String ORDER_CODE = "00002000-1431098192721";
    private static final String MERCHANTCODE = "MERCHANTCODE";
    private static final String MERCHANT_OWNER = "MERCHANT_OWNER";
    private static final String SEPARATOR = "^";
    private static final String ORDER_KEY = MERCHANT_OWNER + SEPARATOR + MERCHANTCODE + SEPARATOR + ORDER_CODE;
    private static final String STATUS = "AUTHORISED";
    private static final String PAYMENT_AMOUNT = "1000";
    private static final String PAYMENT_CURRENCY = "GBP";

    @InjectMocks
    private RedirectAuthoriseResultPopulator testObj;

    @Mock
    private WorldpayOrderService worldpayOrderService;
    @Mock
    private Amount amountMock;

    private final Map<String, String> paymentStatusSourceMap = new HashMap<>();
    private final Map<String, String> statusSourceMap = new HashMap<>();

    @Test
    public void shouldPopulateRedirectResultForPaymentStatusParameter() {
        populateParametersMap();
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        Mockito.when(worldpayOrderService.createAmount(Currency.getInstance(PAYMENT_CURRENCY), Integer.valueOf(PAYMENT_AMOUNT))).thenReturn(amountMock);
        Mockito.when(amountMock.getValue()).thenReturn("10");

        testObj.populate(paymentStatusSourceMap, result);

        assertEquals(ORDER_KEY, result.getOrderKey());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AuthorisedStatus.AUTHORISED, result.getPaymentStatus());
        assertEquals(new BigDecimal(10), result.getPaymentAmount());
        assertEquals(true, result.getSaveCard());
        assertEquals(false, result.getPending());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionDueToInvalidCurrencyIsoCode() {
        populateParametersMap();
        paymentStatusSourceMap.put("paymentCurrency", "GPB");

        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(paymentStatusSourceMap, result);

        assertEquals(null, result.getPaymentAmount());
    }

    @Test
    public void shouldNotPopulateOrderKeyInRedirectResultForPaymentStatusParameterWhenIsNotPresent() {
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(paymentStatusSourceMap, result);

        assertEquals(null, result.getOrderKey());
        assertEquals(null, result.getOrderCode());
        assertEquals(null, result.getPaymentStatus());
        assertEquals(null, result.getPaymentAmount());
    }

    @Test
    public void shouldPopulateRedirectResultForStatusParameter() {
        populateParametersMap();
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(statusSourceMap, result);

        assertEquals(ORDER_KEY, result.getOrderKey());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(AuthorisedStatus.AUTHORISED, result.getPaymentStatus());
        assertEquals(false, result.getSaveCard());
        assertEquals(false, result.getPending());
    }

    protected void populateParametersMap() {
        paymentStatusSourceMap.put("orderKey", ORDER_KEY);
        paymentStatusSourceMap.put("paymentStatus", STATUS);
        paymentStatusSourceMap.put("savePaymentInfo", Boolean.TRUE.toString());
        paymentStatusSourceMap.put("paymentAmount", PAYMENT_AMOUNT);
        paymentStatusSourceMap.put("paymentCurrency", PAYMENT_CURRENCY);

        statusSourceMap.put("orderKey", ORDER_KEY);
        statusSourceMap.put("status", STATUS);
        statusSourceMap.put("savePaymentInfo", String.valueOf(FALSE));
    }
}
