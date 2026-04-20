package com.worldpay.service.request;

import static com.worldpay.service.request.OrderInquiryServiceRequest.createOrderInquiryRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import com.worldpay.data.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class KlarnaOrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MERCHANT_PASSWORD = "merchantPassword";
    private MerchantInfo merchantInfo;
    private static final String ORDER_CODE = "orderCode";

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        merchantInfo.setMerchantCode(MERCHANT_PASSWORD);
        this.merchantInfo = merchantInfo;
    }

    @Test
    public void testOrderInquiry() {
        final KlarnaOrderInquiryServiceRequest request = KlarnaOrderInquiryServiceRequest.createKlarnaOrderInquiryRequest(merchantInfo, ORDER_CODE);

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
