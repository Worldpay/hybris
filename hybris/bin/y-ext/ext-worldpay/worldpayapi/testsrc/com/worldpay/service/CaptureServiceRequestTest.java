package com.worldpay.service;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
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
    private static final String ORDER_CODE = "orderCode";
    private static final List<String> TRACKING_IDS = List.of("trackingId1", "trackingId2");

    private MerchantInfo merchantInfo;
    private Amount amount;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final Amount amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue("100");
        this.amount = amount;

        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        this.merchantInfo = merchantInfo;
    }

    @Test
    public void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndTrackingIds() {
        final com.worldpay.data.Date date = WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(merchantInfo, ORDER_CODE, amount, date, TRACKING_IDS);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(amount, request.getAmount());
        assertEquals(TRACKING_IDS, request.getTrackingIds());
    }

    @Test
    public void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndNoTrackingIds() {
        final com.worldpay.data.Date date = WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(merchantInfo, ORDER_CODE, amount, date, null);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(amount, request.getAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenMerchantInfoIsNull() {
        createCaptureRequest(null, ORDER_CODE, amount, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenTheOrderCodeIsNull() {
        createCaptureRequest(merchantInfo, null, amount, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenAmountIsNull() {
        createCaptureRequest(merchantInfo, ORDER_CODE, null, null, null);
    }
}
