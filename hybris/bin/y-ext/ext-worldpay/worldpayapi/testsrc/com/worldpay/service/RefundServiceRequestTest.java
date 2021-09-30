package com.worldpay.service;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.RefundServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
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
    private static final String REFERENCE = "reference";
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";

    private Amount amount;
    private MerchantInfo merchantInfo;

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;

        final Amount amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("100");
        this.amount = amount;
    }

    @Test
    public void testRefundFullAmount() {

        final RefundServiceRequest request = RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, amount, REFERENCE, Boolean.FALSE);

        assertEquals(amount, request.getAmount());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(REFERENCE, request.getReference());
        assertFalse(request.getShopperWebformRefund());
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(null, ORDER_CODE, amount, REFERENCE, Boolean.FALSE);
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(merchantInfo, null, amount, REFERENCE, Boolean.FALSE);
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenAmountIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        RefundServiceRequest.createRefundRequest(merchantInfo, ORDER_CODE, null, REFERENCE, Boolean.FALSE);
    }
}
