package com.worldpay.service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
public class CaptureServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String TRACKING_ID1 = "trackingId1";
    private static final String TRACKING_ID2 = "trackingId2";
    private static final String EXPONENT = "2";
    private static final String EUR = "EUR";

    private MerchantInfo merchantInfo;
    private static final String ORDER_CODE = Instant.now().toString();

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @BeforeEach
    public void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    public void capture_ShouldResponseWithNoErrorsAFullAmount_WhenThereIsACaptureServiceRequestWithoutTrackingIds() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("100");
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, amount, null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull(capture, "Capture response is null!");
        assertFalse(capture.isError(), "Errors returned from capture request");
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amountCapture = capture.getAmount();
        assertNotNull(amountCapture, "Amount in the capture is null!");
        assertEquals("Incorrect amount captured", "100", amountCapture.getValue());
    }

    @Test
    public void capture_ShouldResponseWithNoErrors_WhenThereIsAFullAmountCaptureServiceRequestWithTrackingIds() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("100");
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, amount, null, List.of(TRACKING_ID1, TRACKING_ID2));
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull(capture, "Capture response is null!");
        assertFalse(capture.isError(), "Errors returned from capture request");
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amountCapture = capture.getAmount();
        assertNotNull(amountCapture, "Amount in the capture is null!");
        assertEquals("Incorrect amount captured", "100", amountCapture.getValue());
    }

    @Test
    public void capture_ShouldResponseWithNoErrors_WhenThereIsACorrectPartialAmountCaptureServiceRequest() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("70");
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, amount, null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull(capture, "Capture response is null!");
        assertFalse(capture.isError(), "Errors returned from capture request");
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amountCapture = capture.getAmount();
        assertNotNull(amountCapture, "Amount in the capture is null!");
        assertEquals("Incorrect amount captured", "70", amountCapture.getValue());
    }

    @Test
    public void capture_ShouldNotAllowToCaptureMoreThanAuthorised_WhenExceedingAmountCaptureServiceRequest() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("140");
        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, amount, null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull(capture, "Capture response is null!");
        assertFalse(capture.isError(), "Error returned from capture request");
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
    }
}
