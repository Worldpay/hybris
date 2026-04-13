package com.worldpay.service;

import static com.worldpay.service.request.CancelServiceRequest.createCancelRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class CancelServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @BeforeEach
    void setUp() throws Exception {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
    }

    @Test
    void testCancel() {
        final CancelServiceRequest request = createCancelRequest(merchantInfo, ORDER_CODE);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    void createCancelRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createCancelRequest(null, ORDER_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }

    @Test
    void createCancelRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createCancelRequest(merchantInfo, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo and Order Code cannot be null");
    }
}
