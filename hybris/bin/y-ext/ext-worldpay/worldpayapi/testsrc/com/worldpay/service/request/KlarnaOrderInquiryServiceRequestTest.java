package com.worldpay.service.request;

import static com.worldpay.service.request.OrderInquiryServiceRequest.createOrderInquiryRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.worldpay.data.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class KlarnaOrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MERCHANT_PASSWORD = "merchantPassword";
    private MerchantInfo merchantInfo;
    private static final String ORDER_CODE = "orderCode";

    @BeforeEach
    void setUp() {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
    }

    @Test
    void testOrderInquiry() {
        final KlarnaOrderInquiryServiceRequest request = KlarnaOrderInquiryServiceRequest.createKlarnaOrderInquiryRequest(merchantInfo, ORDER_CODE);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createOrderInquiryRequest(null, ORDER_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }

    @Test
    void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createOrderInquiryRequest(merchantInfo, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }
}
