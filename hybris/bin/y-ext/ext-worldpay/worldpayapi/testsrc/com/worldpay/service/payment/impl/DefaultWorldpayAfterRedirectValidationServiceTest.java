package com.worldpay.service.payment.impl;

import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import com.worldpay.data.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAfterRedirectValidationServiceTest {

    private static final double PAYMENT_AMOUNT = 100d;
    private static final String GBP = "GBP";
    private static final String KEY_MAC = "mac";
    private static final String KEY_MAC2 = "mac2";
    private static final String ORDER_KEY = "orderKey";
    private static final String MAC_SECRET = "macSecret";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String KEY_PAYMENT_AMOUNT = "paymentAmount";
    private static final String KEY_PAYMENT_CURRENCY = "paymentCurrency";

    @InjectMocks
    private DefaultWorldpayAfterRedirectValidationService testObj;

    @Mock
    private MacValidator macValidatorMock;

    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() {
        when(merchantInfoMock.getMacSecret()).thenReturn(MAC_SECRET);
    }

    @Test
    public void testCheckResponseIsValidWhenNotUsingMacValidation() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenRedirectResponseStatusIsOPEN() {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() throws WorldpayMacValidationException {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        when(macValidatorMock.validateResponse(ORDER_KEY, KEY_MAC, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED, MAC_SECRET)).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() throws WorldpayMacValidationException {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        when(macValidatorMock.validateResponse(ORDER_KEY, KEY_MAC, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED, MAC_SECRET)).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertFalse(result);
    }

    @Test
    public void testCheckResponseIsValidWhenUsingMacValidationWithMac2ParameterAndRedirectResultStatusIsAUTHORISED() throws WorldpayMacValidationException {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        when(macValidatorMock.validateResponse(ORDER_KEY, KEY_MAC2, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED, MAC_SECRET)).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponseWithMac2());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenUsingMacValidationWithMac2ParameterAndRedirectResultStatusIsAUTHORISED() throws WorldpayMacValidationException {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        when(macValidatorMock.validateResponse(ORDER_KEY, KEY_MAC2, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED, MAC_SECRET)).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponseWithMac2());

        assertFalse(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenNoOrderKeyPresent() {
        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithNoOrderKey());

        assertFalse(result);
    }

    private Map<String, String> createFullWorldpayResponse() {
        return Map.of(
                PAYMENT_STATUS, AUTHORISED.name(),
                ORDER_KEY, ORDER_KEY,
                KEY_MAC, KEY_MAC,
                KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT),
                KEY_PAYMENT_CURRENCY, GBP
        );
    }

    private Map<String, String> createFullWorldpayResponseWithMac2() {
        return Map.of(
                PAYMENT_STATUS, AUTHORISED.name(),
                ORDER_KEY, ORDER_KEY,
                KEY_MAC2, KEY_MAC2,
                KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT),
                KEY_PAYMENT_CURRENCY, GBP
        );
    }

    private Map<String, String> createWorldpayResponseWithNoOrderKey() {
        return Map.of(
                PAYMENT_STATUS, AUTHORISED.name(),
                KEY_MAC, KEY_MAC,
                KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT),
                KEY_PAYMENT_CURRENCY, GBP
        );
    }

    private Map<String, String> createWorldpayResponseWithOrderKeyOnly() {
        return Map.of(ORDER_KEY, ORDER_KEY);
    }
}
