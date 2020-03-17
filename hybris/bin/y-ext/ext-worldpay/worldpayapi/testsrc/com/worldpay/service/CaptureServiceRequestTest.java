package com.worldpay.service;

import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.List;

import static com.worldpay.service.request.CaptureServiceRequest.createCaptureRequest;
import static org.junit.Assert.assertEquals;

@UnitTest
public class CaptureServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";

    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = "orderCode";
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");
    private static final List<String> TRACKING_IDS = List.of("trackingId1", "trackingId2");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndTrackingIds() {
        final com.worldpay.service.model.Date date = new com.worldpay.service.model.Date(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(MERCHANT_INFO, ORDER_CODE, AMOUNT, date, TRACKING_IDS);

        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(AMOUNT, request.getAmount());
        assertEquals(TRACKING_IDS, request.getTrackingIds());
    }

    @Test
    public void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndNoTrackingIds() {
        final com.worldpay.service.model.Date date = new com.worldpay.service.model.Date(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(MERCHANT_INFO, ORDER_CODE, AMOUNT, date, null);

        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(AMOUNT, request.getAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenMerchantInfoIsNull() {
        createCaptureRequest(null, ORDER_CODE, AMOUNT, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenTheOrderCodeIsNull() {
        createCaptureRequest(MERCHANT_INFO, null, AMOUNT, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenAmountIsNull() {
        createCaptureRequest(MERCHANT_INFO, ORDER_CODE, null, null, null);
    }
}
