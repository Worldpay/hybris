package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.*;


@IntegrationTest
public class CancelServiceRequestIntegrationTest extends ServicelayerBaseTest {

    private static final String MERCHANT_CODE = "MERCHANT1ECOM";
    private static final String MERCHANT_PASSWORD = "3l3ph4nt_&_c4st!3";
    private static final String ORDER_CODE = String.valueOf(new Date().getTime());

    private MerchantInfo merchantInfo;

    @Resource(name = "worldpayServiceGateway")
    private WorldpayServiceGateway gateway;

    @Before
    public void setUp() throws Exception {
        final MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setMerchantPassword(MERCHANT_PASSWORD);
        merchantInfo.setMerchantCode(MERCHANT_CODE);
        this.merchantInfo = merchantInfo;
    }

    /**
     * Test method for {@link WorldpayServiceGateway#cancel(CancelServiceRequest)}.
     */
    @Test
    public void testCancel() throws WorldpayException {

        WPSGTestHelper.directAuthorise(gateway, merchantInfo, ORDER_CODE);

        final CancelServiceRequest request = CancelServiceRequest.createCancelRequest(merchantInfo, ORDER_CODE);
        final CancelServiceResponse cancel = gateway.cancel(request);

        assertNotNull("Cancel response is null!", cancel);
        assertFalse("Errors returned from cancel request", cancel.isError());
        assertEquals("Order code returned is incorrect", ORDER_CODE, cancel.getOrderCode());
    }
}

