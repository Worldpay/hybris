package com.worldpay.service;

import static com.worldpay.service.request.RefundServiceRequest.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

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

        final RefundServiceRequest request = createRefundRequest(merchantInfo, ORDER_CODE, amount, REFERENCE, Boolean.FALSE);

        assertEquals(amount, request.getAmount());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(REFERENCE, request.getReference());
        assertFalse(request.getShopperWebformRefund());
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createRefundRequest(null, ORDER_CODE, amount, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createRefundRequest(merchantInfo, null, amount, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    public void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenAmountIsNull() {
        assertThatThrownBy(() -> createRefundRequest(merchantInfo, ORDER_CODE, null, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }
}
