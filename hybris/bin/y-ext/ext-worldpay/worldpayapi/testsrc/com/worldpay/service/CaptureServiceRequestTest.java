package com.worldpay.service;

import java.time.LocalDateTime;
import java.util.List;

import static com.worldpay.service.request.CaptureServiceRequest.createCaptureRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class CaptureServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";
    private static final List<String> TRACKING_IDS = List.of("trackingId1", "trackingId2");

    private MerchantInfo merchantInfo;
    private Amount amount;

    @BeforeEach
    void setUp() throws Exception {
        amount = new Amount();
        amount.setExponent("2");
        amount.setCurrencyCode("EUR");
        amount.setValue("100");

        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
    }

    @Test
    void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndTrackingIds() {
        final com.worldpay.data.Date date = WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(merchantInfo, ORDER_CODE, amount, date, TRACKING_IDS);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(amount, request.getAmount());
        assertEquals(TRACKING_IDS, request.getTrackingIds());
    }

    @Test
    void createCaptureRequest_ShouldCreateWithNoErrorsAnCaptureServiceRequest_WhenUsingMandatoryFieldsAndNoTrackingIds() {
        final com.worldpay.data.Date date = WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(LocalDateTime.now());
        final CaptureServiceRequest request = createCaptureRequest(merchantInfo, ORDER_CODE, amount, date, null);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(date, request.getDate());
        assertEquals(amount, request.getAmount());
    }

    @Test
    void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createCaptureRequest(null, ORDER_CODE, amount, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenTheOrderCodeIsNull() {
        assertThatThrownBy(() -> createCaptureRequest(merchantInfo, null, amount, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    void createCaptureRequest_ShouldRaiseIllegalArgumentException_WhenAmountIsNull() {
        assertThatThrownBy(() -> createCaptureRequest(merchantInfo, ORDER_CODE, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }
}
