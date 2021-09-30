package com.worldpay.service;

import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class OrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";

    private MerchantInfo merchantInfo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;
    }

    @Test
    public void testOrderInquiry() {

        final OrderInquiryServiceRequest request = OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, ORDER_CODE);

        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        OrderInquiryServiceRequest.createOrderInquiryRequest(null, ORDER_CODE);
    }

    @Test
    public void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, null);
    }
}
