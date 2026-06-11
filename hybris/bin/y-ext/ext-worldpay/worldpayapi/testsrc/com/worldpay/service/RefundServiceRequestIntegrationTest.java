package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerJUnit5BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.annotation.Resource;

import java.util.Date;


@IntegrationTest
class RefundServiceRequestIntegrationTest extends ServicelayerJUnit5BaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());
    private static final String REFERENCE = "reference";
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";

    private MerchantInfo merchantInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @BeforeEach
    void setUp() {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    void testRefundFullAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);
        WPSGTestHelper.capture(gateway, merchantInfo, ORDER_CODE);

        final Amount amountMock = new Amount();
        amountMock.setExponent(EXPONENT);
        amountMock.setCurrencyCode(EUR);
        amountMock.setValue("100");

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, amountMock, REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull(refund, "Refund response is null!");
        assertFalse(refund.isError(), "Errors returned from refund request");
        assertEquals(ORDER_CODE, refund.getOrderCode(), "Order code returned is incorrect");
        final Amount amount = refund.getAmount();
        assertNotNull(amount, "Amount in the refund is null!");
        assertEquals("100", amount.getValue(), "Incorrect amount refunded");
    }

    @Test
    void testRefundPartialAmount() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);
        WPSGTestHelper.capture(gateway, merchantInfo, ORDER_CODE);

        final Amount amountMock = new Amount();
        amountMock.setExponent(EXPONENT);
        amountMock.setCurrencyCode(EUR);
        amountMock.setValue("70");

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, amountMock, REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull(refund, "Refund response is null!");
        assertFalse(refund.isError(), "Errors returned from refund request");
        assertEquals(ORDER_CODE, refund.getOrderCode(), "Order code returned is incorrect");
        final Amount amount = refund.getAmount();
        assertNotNull(amount, "Amount in the refund is null!");
        assertEquals("70", amount.getValue(), "Incorrect amount refunded");
    }

    @Test
    void testRefundMultiple() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);
        WPSGTestHelper.capture(gateway, merchantInfo, ORDER_CODE);

        final Amount amountMock = new Amount();
        amountMock.setExponent(EXPONENT);
        amountMock.setCurrencyCode(EUR);
        amountMock.setValue("70");


        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, amountMock, REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull(refund, "Refund response is null!");
        assertFalse(refund.isError(), "Errors returned from refund request");
        assertEquals(ORDER_CODE, refund.getOrderCode(), "Order code returned is incorrect");
        final Amount amount = refund.getAmount();
        assertNotNull(amount, "Amount in the refund is null!");
        assertEquals("70", amount.getValue(), "Incorrect amount refunded");

        final Amount amountMock2 = new Amount();
        amountMock2.setExponent(EXPONENT);
        amountMock2.setCurrencyCode(EUR);
        amountMock2.setValue("30");

        final RefundServiceRequest request2 = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE,amountMock2, REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund2 = gateway.refund(request2);

        assertNotNull(refund2, "Refund response is null!");
        assertFalse(refund2.isError(), "Errors returned from refund request");
        assertEquals(ORDER_CODE, refund2.getOrderCode(), "Order code returned is incorrect");
        final Amount amount2 = refund2.getAmount();
        assertNotNull(amount2, "Amount in the refund is null!");
        assertEquals("30", amount2.getValue(), "Incorrect amount refunded");
    }

    @Test
    void testRefundAmountMoreThanCaptured() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);
        WPSGTestHelper.capture(gateway, merchantInfo, ORDER_CODE);

        final Amount amountMock = new Amount();
        amountMock.setExponent(EXPONENT);
        amountMock.setCurrencyCode(EUR);
        amountMock.setValue("140");

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, amountMock, REFERENCE, Boolean.FALSE);
        final RefundServiceResponse refund = gateway.refund(request);

        assertNotNull(refund, "Refund response is null!");
        assertFalse(refund.isError(), "No error should be returned from refund request as it will have been sent for refund");
        assertEquals(ORDER_CODE, refund.getOrderCode(), "Order code returned is incorrect");
    }
}
