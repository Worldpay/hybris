package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@UnitTest
public class RefundServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final Amount AMOUNT = new Amount("100", "EUR", "2");
    private static final String REFERENCE = "reference";

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRefundFullAmount() throws WorldpayException {

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, AMOUNT, REFERENCE, Boolean.FALSE);

        assertEquals(AMOUNT, request.getAmount());
        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(REFERENCE, request.getReference());
        assertFalse(request.getShopperWebformRefund());
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(null, ORDER_CODE, AMOUNT, REFERENCE, Boolean.FALSE);
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(MERCHANT_INFO, null, AMOUNT, REFERENCE, Boolean.FALSE);
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenAmountIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(MERCHANT_INFO, ORDER_CODE, null, REFERENCE, Boolean.FALSE);
    }
}
