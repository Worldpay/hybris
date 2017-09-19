package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class CaptureServiceRequestIntegrationTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway gateway = WorldpayServiceGateway.getInstance();
    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String orderCode = String.valueOf(new Date().getTime());

    @Test
    public void testCaptureFullAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, WORLD_PAY_CONFIG, merchantInfo, orderCode);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, merchantInfo, orderCode, new Amount("100", "EUR", "2"), null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Errors returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", orderCode, capture.getOrderCode());
        final Amount amount = capture.getAmount();
        assertNotNull("Amount in the capture is null!", amount);
        assertEquals("Incorrect amount captured", "100", amount.getValue());
    }

    @Test
    public void testCapturePartialAmount() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, WORLD_PAY_CONFIG, merchantInfo, orderCode);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, merchantInfo, orderCode, new Amount("70", "EUR", "2"), null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Errors returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", orderCode, capture.getOrderCode());
        final Amount amount = capture.getAmount();
        assertNotNull("Amount in the capture is null!", amount);
        assertEquals("Incorrect amount captured", "70", amount.getValue());
    }

    @Test
    public void testCaptureMoreThanAuthorised() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, WORLD_PAY_CONFIG, merchantInfo, orderCode);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(WORLD_PAY_CONFIG, merchantInfo, orderCode, new Amount("140", "EUR", "2"), null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Error returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", orderCode, capture.getOrderCode());
    }
}
