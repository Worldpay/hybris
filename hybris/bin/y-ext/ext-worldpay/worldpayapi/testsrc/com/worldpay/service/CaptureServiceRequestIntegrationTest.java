package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.Assert.*;

@IntegrationTest
public class CaptureServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String TRACKING_ID1 = "trackingId1";
    private static final String TRACKING_ID2 = "trackingId2";

    private static final MerchantInfo merchantInfo = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = Instant.now().toString();

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Test
    public void capture_ShouldResponseWithNoErrorsAFullAmount_WhenThereIsACaptureServiceRequestWithoutTrackingIds() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, new Amount("100", "EUR", "2"), null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Errors returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amount = capture.getAmount();
        assertNotNull("Amount in the capture is null!", amount);
        assertEquals("Incorrect amount captured", "100", amount.getValue());
    }

    @Test
    public void capture_ShouldResponseWithNoErrors_WhenThereIsAFullAmountCaptureServiceRequestWithTrackingIds() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, new Amount("100", "EUR", "2"), null, Arrays.asList(TRACKING_ID1, TRACKING_ID2));
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Errors returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amount = capture.getAmount();
        assertNotNull("Amount in the capture is null!", amount);
        assertEquals("Incorrect amount captured", "100", amount.getValue());
    }

    @Test
    public void capture_ShouldResponseWithNoErrors_WhenThereIsACorrectPartialAmountCaptureServiceRequest() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, new Amount("70", "EUR", "2"), null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Errors returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
        final Amount amount = capture.getAmount();
        assertNotNull("Amount in the capture is null!", amount);
        assertEquals("Incorrect amount captured", "70", amount.getValue());
    }

    @Test
    public void capture_ShouldNotAllowToCaptureMoreThanAuthorised_WhenExceedingAmountCaptureServiceRequest() throws WorldpayException {
        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final CaptureServiceRequest request = CaptureServiceRequest.createCaptureRequest(merchantInfo, ORDER_CODE, new Amount("140", "EUR", "2"), null, null);
        final CaptureServiceResponse capture = gateway.capture(request);

        assertNotNull("Capture response is null!", capture);
        assertFalse("Error returned from capture request", capture.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, capture.getOrderCode());
    }
}
