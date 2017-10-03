package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.*;

@IntegrationTest
public class RefundServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String REFERENCE = "reference";

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Test
    public void testRefundFullAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(gateway, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, new Amount("100", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Amount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "100", amount.getValue());
    }

    @Test
    public void testRefundPartialAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(gateway, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, new Amount("70", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Ampount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "70", amount.getValue());
    }

    @Test
    public void testRefundMultiple() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(gateway, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, new Amount("70", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("Errors returned from refund request", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
        final Amount amount = refund.getAmount();
        assertNotNull("Ampount in the refund is null!", amount);
        assertEquals("Incorrect amount refunded", "70", amount.getValue());

        final RefundServiceRequest request2 = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, new Amount("30", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund2 = gateway.refund(request2);

        assertNotNull("Refund response is null!", refund2);
        assertFalse("Errors returned from refund request", refund2.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund2.getOrderCode());
        final Amount amount2 = refund2.getAmount();
        assertNotNull("Amount in the refund is null!", amount2);
        assertEquals("Incorrect amount refunded", "30", amount2.getValue());
    }

    @Test
    public void testRefundAmountMoreThanCaptured() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, MERCHANT_INFO, ORDER_CODE);
        WPSGTestHelper.capture(gateway, MERCHANT_INFO, ORDER_CODE);

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, new Amount("140", "EUR", "2"), REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull("Refund response is null!", refund);
        assertFalse("No error should be returned from refund request as it will have been sent for refund", refund.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, refund.getOrderCode());
    }
}
