package com.worldpay.service;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;


@IntegrationTest
public class CancelServiceRequestIntegrationTest {

    private static final WorldpayConfig WORLD_PAY_CONFIG = WorldpayTestConfigHelper.getWorldpayTestConfig();
    private static final WorldpayServiceGateway GATEWAY = WorldpayServiceGateway.getInstance();
    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final MerchantInfo MERCHANT_INFO = new MerchantInfo(MERCHANT_CODE, MERCHANT_PASSWORD);
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());

    /**
     * Test method for {@link WorldpayServiceGateway#cancel(CancelServiceRequest)}.
     */
    @Test
    public void testCancel() throws WorldpayException {

        WPSGTestHelper.directAuthorise(GATEWAY, WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);

        final CancelServiceRequest request = CancelServiceRequest.createCancelRequest(WORLD_PAY_CONFIG, MERCHANT_INFO, ORDER_CODE);
        final CancelServiceResponse cancel = GATEWAY.cancel(request);

        assertNotNull("Cancel response is null!", cancel);
        assertFalse("Errors returned from cancel request", cancel.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, cancel.getOrderCode());
    }
}

