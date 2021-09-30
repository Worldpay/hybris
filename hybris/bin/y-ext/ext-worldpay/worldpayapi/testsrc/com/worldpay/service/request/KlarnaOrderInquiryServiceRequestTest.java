package com.worldpay.service.request;

import com.worldpay.data.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class KlarnaOrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MERCHANT_PASSWORD = "merchantPassword";
    private MerchantInfo merchantInfo;
    private static final String ORDER_CODE = "orderCode";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

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
        expectedException.expect(IllegalArgumentException.class);

        OrderInquiryServiceRequest.createOrderInquiryRequest(null, ORDER_CODE);
    }

    @Test
    public void createOrderInquiryRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        OrderInquiryServiceRequest.createOrderInquiryRequest(merchantInfo, null);
    }
}
