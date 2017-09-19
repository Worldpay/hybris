package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class RefundServiceRequestIntegrationTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final WorldpayServiceGateway GATEWAY = WorldpayServiceGateway.getInstance();
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String REFERENCE = "reference";

    @Test
    public void testRefundFullAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, new Amount("100", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = GATEWAY.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Amount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "100", amount.getValue());
    }

    @Test
    public void testRefundPartialAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, new Amount("70", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = GATEWAY.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Ampount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "70", amount.getValue());
    }

    @Test
    public void testRefundMultiple() throws WorldpayException {

        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, new Amount("70", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = GATEWAY.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Ampount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "70", amount.getValue());

        final RefundServiceRequest request2 = RefundServiceRequest.createRefundRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, new Amount("30", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund2 = GATEWAY.refund(request2);

        assertNotNull("Refund response is null!", refund2);
        assertFalse("Errors returned from refund request", refund2.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund2.getOrderCode());
        final Amount amount2 = refund2.getAmount();
        assertNotNull("Amount in the refund is null!", amount2);
        assertEquals("Incorrect amount refunded", "30", amount2.getValue());
    }

    @Test
    public void testRefundAmountMoreThanCaptured() throws WorldpayException {
        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE, new Amount("140", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = GATEWAY.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("No error should be returned from refund request as it will have been sent for refund", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
    }
}
