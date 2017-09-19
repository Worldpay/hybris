package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@UnitTest
public class CancelServiceRequestTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = "orderCode";

    @SuppressWarnings("PMD.MemberScope")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCancel() throws WorldpayException {

        final CancelServiceRequest request = CancelServiceRequest.createCancelRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        assertEquals(WORLD_PAY_CONFIG, request.getWorldpayConfig());
        assertEquals(MERCHANT_INFO, request.getMerchantInfo());
        assertEquals(ORDER_CODE, request.getOrderCode());
    }

    @Test
    public void createCancelRequestShouldRaiseIllegalArgumentExceptionWhenWorldpayConfigIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CancelServiceRequest.createCancelRequest(null, MERCHANT_INFO, ORDER_CODE);
    }

    @Test
    public void createCancelRequestShouldRaiseIllegalArgumentExceptionWhenMerchantInfoIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CancelServiceRequest.createCancelRequest(WORLD_PAY_CONFIG, null, ORDER_CODE);
    }

    @Test
    public void createCancelRequestShouldRaiseIllegalArgumentExceptionWhenOrderCodeIsNull() {
        expectedException.expect(IllegalArgumentException.class);

        CancelServiceRequest.createCancelRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, null);
    }
}

