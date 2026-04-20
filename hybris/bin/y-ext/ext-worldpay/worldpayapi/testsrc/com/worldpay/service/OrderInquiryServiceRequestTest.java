package com.worldpay.service;

import static com.worldpay.service.request.OrderInquiryServiceRequest.createOrderInquiryRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class OrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @BeforeEach
    public void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    public void testOrderInquiry() {

        final OrderInquiryServiceRequest request = createOrderInquiryRequest(merchantInfo, ORDER_CODE);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createOrderInquiryRequest(null, ORDER_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }

    @Test
    public void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createOrderInquiryRequest(merchantInfo, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }
}
