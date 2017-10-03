package com.worldpay.service.request;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class KlarnaOrderInquiryServiceRequestTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MERCHANT_PASSWORD = "merchantPassword";
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = "orderCode";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testOrderInquiry() throws WorldpayException {

        final KlarnaOrderInquiryServiceRequest request = KlarnaOrderInquiryServiceRequest.createKlarnaOrderInquiryRequest(MERCHANT_INFO, ORDER_CODE);

        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
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

        OrderInquiryServiceRequest.createOrderInquiryRequest(MERCHANT_INFO, null);
    }
}
