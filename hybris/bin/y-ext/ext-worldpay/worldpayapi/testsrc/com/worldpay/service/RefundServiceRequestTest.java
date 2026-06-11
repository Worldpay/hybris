package com.worldpay.service;

import static com.worldpay.service.request.RefundServiceRequest.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.worldpay.data.Amount;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class RefundServiceRequestTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = "orderCode";
    private static final String REFERENCE = "reference";
    private static final String EUR = "EUR";
    private static final String EXPONENT = "2";

    private Amount amount;
    private MerchantInfo merchantInfo;

    @BeforeEach
    void setUp() {
        merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);

        amount = new Amount();
        amount.setExponent(EXPONENT);
        amount.setCurrencyCode(EUR);
        amount.setValue("100");
    }

    @Test
    void testRefundFullAmount() {
        final RefundServiceRequest request = createRefundRequest(merchantInfo, ORDER_CODE, amount, REFERENCE, Boolean.FALSE);

        assertEquals(amount, request.getAmount());
        assertEquals(merchantInfo, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
        assertEquals(REFERENCE, request.getReference());
        assertFalse(request.getShopperWebformRefund());
    }

    @Test
    void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        assertThatThrownBy(() -> createRefundRequest(null, ORDER_CODE, amount, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        assertThatThrownBy(() -> createRefundRequest(merchantInfo, null, amount, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }

    @Test
    void createRefundRequestShouldRaiseIllegalArgumentExceptionWhenAmountIsNull() {
        assertThatThrownBy(() -> createRefundRequest(merchantInfo, ORDER_CODE, null, REFERENCE, Boolean.FALSE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WorldpayConfig, MerchantInfo, Order Code and Amount cannot be null");
    }
}
