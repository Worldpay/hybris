package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@UnitTest
public class CaptureServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = "orderCode";
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCaptureFullAmount() throws WorldpayException {

        final com.worldpay.service.model.Date date = new com.worldpay.service.model.Date(new Date());
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, AMOUNT, date);

        assertEquals(WORLD_PAY_CONFIG, request.getWorldpayConfig());
        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(AMOUNT, request.getAmount());
    }

    @Test
    public void createCaptureRequestShouldRaiseIllegalArgumentExceptionWhenWorldpayConfigIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CaptureServiceRequest.createCaptureRequest(null, MERCHANT_INFO, ORDER_CODE, AMOUNT, null);
    }

    @Test
    public void createCaptureRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, null, ORDER_CODE, AMOUNT, null);
    }

    @Test
    public void createCaptureRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, null, AMOUNT, null);
    }

    @Test
    public void createCaptureRequestShouldRaiseIllegalArgumentExceptionWhenAmountIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, null, null);
    }
}
